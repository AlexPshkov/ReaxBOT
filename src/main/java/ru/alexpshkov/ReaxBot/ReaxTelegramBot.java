package ru.alexpshkov.ReaxBot;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ru.alexpshkov.ReaxBot.Minecraft.QueryThread;
import ru.alexpshkov.ReaxBot.Minecraft.User;
import ru.alexpshkov.ReaxBot.Telegram.DefaultMessages;
import ru.alexpshkov.ReaxBot.Telegram.TelegramManager;
import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Utils.DataBase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaxTelegramBot {

    public static final boolean DEBUG_MODE = false;
    public static String botToken;

    public static DataBase dataBase;
    public static TelegramManager telegramManager;

    private static final HashMap<String, TelegramUser> telegramUsers = new HashMap<>();


    public static void main(String[] args) {
        dataBase = new DataBase();
        if(!dataBase.initDataBase()) return;

        botToken = System.getProperty("token");
        if (botToken == null) {
            System.out.println("Add -Dtoken=<api-token>");
            return;
        }

        telegramManager = new TelegramManager(botToken);
        new QueryThread("1.5.2", "s2.reaxlab.ru", 54300).start();
        new QueryThread("1.12.2","s2.reaxlab.ru", 54200).start();

        for (User user : dataBase.getAllUsers()) {
            System.out.println(user.getTelegramId() + " -> " + user.getPlayerName());
            telegramUsers.put(user.getTelegramId(), new TelegramUser(user, telegramManager));
        }
        System.out.println("Bot started");
    }

    public static boolean isSuchTelegramUser(String telegramId) {
        return telegramUsers.containsKey(telegramId);
    }

    public static TelegramUser getTelegramUser(String telegramId) {
        return telegramUsers.get(telegramId);
    }

    public static void addNewUser(User user) {
        telegramUsers.put(user.getTelegramId(), new TelegramUser(user, telegramManager));
        dataBase.saveUser(user);
    }

    public static void broadcastMessage(String message) {
        for (Map.Entry<String, TelegramUser> entry : telegramUsers.entrySet()) {
            TelegramUser telegramUser = entry.getValue();
            if(!telegramUser.getUser().isMute()) telegramUser.sendMessage(message);
        }
    }

    public static void broadcastPlayerJoin(String prefix, String playerName, List<String> allPlayers) {
        for (Map.Entry<String, TelegramUser> entry : telegramUsers.entrySet()) {
            TelegramUser telegramUser = entry.getValue();
            User user = telegramUser.getUser();
            if(!user.isMute() && !user.getBlackList().contains(playerName))
                telegramUser.sendMessageWithKeyboard(prefix + DefaultMessages.PLAYER_JOIN(playerName, allPlayers), Arrays.asList(new InlineKeyboardButton("\uD83D\uDD15").callbackData("blackList^" + playerName)));
        }
    }

    public static void broadcastPlayerQuit(String prefix, String playerName, List<String> allPlayers) {
        for (Map.Entry<String, TelegramUser> entry : telegramUsers.entrySet()) {
            TelegramUser telegramUser = entry.getValue();
            User user = telegramUser.getUser();
            if(!user.isMute() && !user.getBlackList().contains(playerName))
                telegramUser.sendMessageWithKeyboard(prefix + DefaultMessages.PLAYER_QUIT(playerName, allPlayers), Arrays.asList(new InlineKeyboardButton("\uD83D\uDD15").callbackData("blackList^" + playerName)));
        }
    }
}
