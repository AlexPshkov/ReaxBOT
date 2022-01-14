package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.Minecraft.ServerQuery;
import ru.alexpshkov.ReaxBot.ReaxTelegramBot;
import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;

public class ListCommand extends AbstractTelegramCommand {

    public ListCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(minArgsLength = 2, commandInfo = "Список игроков со всех серверов")
    public void all(TelegramUser sender, String[] args) {
        int messageId = sender.sendMessageAndGetId(DefaultMessages.GLOBAL_PLAYER_LIST());
        ReaxTelegramBot.getTelegramManager().addUpdatableMessage(() -> sender.editMessage(messageId, DefaultMessages.GLOBAL_PLAYER_LIST()), messageId, sender.getUserData().getTelegramId(), 5000);
    }

    @SubCommand(minArgsLength = 2, commandInfo = "Список игроков с определенного сервера", args = "[serverName]")
    public void SOLO_COMMAND(TelegramUser sender, String[] args) {
        ServerQuery serverQuery = ReaxTelegramBot.getServerQuery(args[1]);
        if (serverQuery == null) {
            sender.sendMessage(DefaultMessages.ERROR("Сервера с названием " + args[1] + " не существует. Перепроверьте название"));
            return;
        }
        int messageId = sender.sendMessageAndGetId(DefaultMessages.SERVER_PLAYER_LIST(args[1]));
        ReaxTelegramBot.getTelegramManager().addUpdatableMessage(() -> sender.editMessage(messageId, DefaultMessages.SERVER_PLAYER_LIST(args[1])), messageId, sender.getUserData().getTelegramId(), 5000);
    }
}
