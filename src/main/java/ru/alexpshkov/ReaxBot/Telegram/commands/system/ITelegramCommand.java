package ru.alexpshkov.ReaxBot.Telegram.commands.system;

import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;

public interface ITelegramCommand {
    /**
     * @param sender Who wants to execute this command
     * @param args Args of command
     * @return Result of command
     */
    boolean execute(TelegramUser sender, String[] args);

    /**
     * Get help of a command
     * @return String with information about command
     */
    String getCommandHelp();
}
