package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.DataBase.UserData;
import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;

import java.util.LinkedList;
import java.util.List;

public class MuteCommand extends AbstractTelegramCommand {

    public MuteCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(commandInfo = "Вклюичить|Выключить любые сообщения от бота", commandHint = "Если включить мут, то бот не будет больше вас беспокоить")
    public void MAIN_COMMAND(TelegramUser sender, String[] args) {
        UserData userData = sender.getUserData();
        if (userData.isMute()) {
            userData.setMute(false);
            sender.sendMessage(DefaultMessages.MUTE_DISABLE());
            return;
        }
        userData.setMute(true);
        sender.sendMessage(DefaultMessages.MUTE_ENABLE());
    }

    @SubCommand(minArgsLength = 2, commandInfo = "Включить|Выключить оповещения от данного игрока")
    public void SOLO_COMMAND(TelegramUser sender, String[] args) {
        String target = args[1];
        UserData userData = sender.getUserData();
        LinkedList<String> blackList = userData.getBlackList();
        if (blackList.contains(target)) {
            sender.sendMessage(DefaultMessages.BLACKLIST_REMOVE(target));
            userData.removeFromBlackList(target);
            return;
        }
        sender.sendMessage(DefaultMessages.BLACKLIST_ADD(target));
        userData.addToBlackList(target);
    }

    @SubCommand(minArgsLength = 2, commandInfo = "Список замьюченных вами игроков")
    public void list(TelegramUser sender, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> blackList = sender.getUserData().getBlackList();
        blackList.forEach(name -> stringBuilder.append("\n  • ").append(name));
        sender.sendMessage(blackList.isEmpty() ? "Вы никого не мутили" : "Список замьюченных игроков:" + stringBuilder);
    }
}
