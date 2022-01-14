package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.CommandManager;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;

public class HelpCommand extends AbstractTelegramCommand {

    public HelpCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(commandInfo = "Получить помощь по всем командам бота")
    public void MAIN_COMMAND(TelegramUser sender, String[] args) {
        StringBuilder stringBuilder = new StringBuilder("Помощь по всем командам бота:");
        CommandManager.getAllCommands().forEach((k, v) -> {if (v.getCommandHelp() != null) stringBuilder.append("\n• ").append(v.getCommandHelp()).append("\n");});
        sender.sendMessage(stringBuilder.toString());
    }
}
