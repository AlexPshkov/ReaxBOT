package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.DataBase.UserData;
import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;

import java.util.LinkedList;

public class WhiteListCommand extends AbstractTelegramCommand {

    public WhiteListCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(commandInfo = "Включить|Выключить режим белого списка", commandHint = "Вам будут приходить уведомления только об игроках из белого списка")
    public void MAIN_COMMAND(TelegramUser sender, String[] args) {
        UserData userData = sender.getUserData();
        if (userData.isWhiteList()) {
            userData.setWhiteList(false);
            sender.sendMessage(DefaultMessages.WHITELIST_DISABLE());
            return;
        }
        userData.setWhiteList(true);
        sender.sendMessage(DefaultMessages.WHITELIST_ENABLE());
    }

    @SubCommand(minArgsLength = 2, commandInfo = "Добавить|Удалить игрока из списка", args = "[playerName]")
    public void SOLO_COMMAND(TelegramUser sender, String[] args) {
        String target = args[1];
        UserData userData = sender.getUserData();
        LinkedList<String> whiteList = userData.getWhiteList();
        if (whiteList.contains(target)) {
            sender.sendMessage(DefaultMessages.WHITELIST_REMOVE(target));
            userData.removeFromWhiteList(target);
            return;
        }
        sender.sendMessage(DefaultMessages.WHITELIST_ADD(target));
        userData.addToWhiteList(target);
    }

    @SubCommand(minArgsLength = 2, commandInfo = "Список игроков в вашем белом списке")
    public void list(TelegramUser sender, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        LinkedList<String> whiteList = sender.getUserData().getWhiteList();
        whiteList.forEach(name -> stringBuilder.append("\n  • ").append(name));
        sender.sendMessage(whiteList.isEmpty() ? "Ваш белый список пуст" : "Список игроков в вашем белом списке:" + stringBuilder);
    }
}
