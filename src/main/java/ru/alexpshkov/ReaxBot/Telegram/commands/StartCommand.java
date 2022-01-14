package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;

public class StartCommand extends AbstractTelegramCommand {

    public StartCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(commandInfo = "Начать пользование ботом")
    public void MAIN_COMMAND(TelegramUser sender, String[] args) {
        sender.sendMessage(DefaultMessages.WELCOME_MESSAGE());
    }

}
