package ru.alexpshkov.ReaxBot.Telegram.commands.system;

import java.util.HashMap;
import java.util.Locale;

public class CommandManager {

    private static final HashMap<String, ITelegramCommand> telegramCommandHashMap = new HashMap<>();

    public static void registerCommand(AbstractTelegramCommand telegramCommand) {
        telegramCommandHashMap.put(telegramCommand.commandName.toLowerCase(Locale.ROOT), telegramCommand);
    }

    public static ITelegramCommand getCommand(String commandName) {
        return telegramCommandHashMap.getOrDefault(commandName.toLowerCase(Locale.ROOT), null);
    }

    public static HashMap<String, ITelegramCommand> getAllCommands() {
        return telegramCommandHashMap;
    }
}
