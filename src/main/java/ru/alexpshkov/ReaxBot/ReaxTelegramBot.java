package ru.alexpshkov.ReaxBot;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ru.alexpshkov.ReaxBot.DataBase.SQLiteDataBase;
import ru.alexpshkov.ReaxBot.DataBase.ServerData;
import ru.alexpshkov.ReaxBot.DataBase.UserData;
import ru.alexpshkov.ReaxBot.Minecraft.ServerQuery;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.CommandManager;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;
import ru.alexpshkov.ReaxBot.Telegram.TelegramManager;
import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.*;

import java.util.*;
import java.util.stream.Collectors;

public class ReaxTelegramBot {

    public static final boolean DEBUG_MODE = true;
    public static String botToken;

    private static SQLiteDataBase SQLiteDataBase;
    private static TelegramManager telegramManager;
    private static Timer serverUpdater;

    private static final HashMap<String, TelegramUser> telegramUsers = new HashMap<>();
    private static final HashMap<String, ServerQuery> servers = new HashMap<>();
    private static final HashMap<String, Long> bufferedFirst = new HashMap<>();
    private static final HashMap<String, Long> bufferedSecond = new HashMap<>();


    public static void main(String[] args) {
        SQLiteDataBase = new SQLiteDataBase();
        if (!SQLiteDataBase.initDataBase()) return;

        CommandManager.registerCommand(new StartCommand("/start"));
        CommandManager.registerCommand(new HelpCommand("/help"));
        CommandManager.registerCommand(new MuteCommand("/mute"));
        CommandManager.registerCommand(new WhiteListCommand("/whitelist"));
        CommandManager.registerCommand(new AdminCommand("/admin"));
        CommandManager.registerCommand(new ListCommand("/list"));
        CommandManager.registerCommand(new ServerCommand("/server"));
        CommandManager.registerCommand(new NotificationCommand("/notification"));

        if (botToken == null && (botToken = System.getProperty("token")) == null) {
            System.out.println("Add -Dtoken=<api-token>");
            return;
        }

        serverUpdater = new Timer();
        telegramManager = new TelegramManager(botToken);

        SQLiteDataBase.getAllUsers().forEach(userData -> telegramUsers.put(userData.getTelegramId(), new TelegramUser(userData, telegramManager)));
        SQLiteDataBase.getAllServers().forEach(ReaxTelegramBot::registerServer);

        System.out.println("▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
                        "██ ▄▄▀█ ▄▄█ ▄▄▀█ █ ████ ▄▄▀█▀▄▄▀█▄ ▄█\n" +
                        "██ ▀▀▄█ ▄▄█ ▀▀ █▀▄▀████ ▄▄▀█ ██ ██ ██\n" +
                        "██ ██ █▄▄▄█▄██▄█▄█▄████ ▀▀ ██▄▄███▄██\n" +
                        "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
    }

    public static void registerServer(ServerData serverData) {
        ServerQuery serverQuery = new ServerQuery(serverData);
        serverUpdater.scheduleAtFixedRate(serverQuery, 0, serverData.getUpdateDelay());
        servers.put(serverData.getServerName(), serverQuery);
    }

    public static void registerNewServer(ServerData serverData) {
        registerServer(serverData);
        SQLiteDataBase.saveServer(serverData);
    }

    public static ServerQuery getServerQuery(String serverName) {
        return servers.getOrDefault(serverName, null);
    }

    public static HashMap<String, ServerQuery> getServers() {
        return servers;
    }

    public static List<String> getOnlinePlayers(String serverName) {
        if (!servers.containsKey(serverName)) return new ArrayList<>();
        return servers.get(serverName).getOnlinePlayers();
    }

    public static List<String> getOnlinePlayers() {
        List<String> onlinePlayers = new ArrayList<>();
        servers.forEach((k, v) -> onlinePlayers.addAll(v.getOnlinePlayers().stream().map(name -> name + " | " + k).collect(Collectors.toList())));
        return onlinePlayers;
    }

    public static SQLiteDataBase getSQLiteDataBase() {
        return SQLiteDataBase;
    }

    public static TelegramManager getTelegramManager() {
        return telegramManager;
    }

    public static boolean isSuchTelegramUser(String telegramId) {
        return telegramUsers.containsKey(telegramId);
    }

    public static TelegramUser getTelegramUser(String telegramId) {
        return telegramUsers.getOrDefault(telegramId, null);
    }

    public static HashMap<String, TelegramUser> getAllTelegramUsers() {
        return telegramUsers;
    }

    public static void addNewUser(UserData userData) {
        telegramUsers.put(userData.getTelegramId(), new TelegramUser(userData, telegramManager));
        SQLiteDataBase.saveUser(userData);
    }

    public static void makeAnnounce(TelegramUser telegramUser, String message, String target) {
        telegramUser.sendMessageWithKeyboard(message, Collections.singletonList(new InlineKeyboardButton("\uD83D\uDD15").callbackData("blackList^" + target)));
    }

    public static void makeAnnounce(List<TelegramUser> telegramUser, String message, String target) {
        telegramUser.forEach(t -> makeAnnounce(t, message, target));
    }

    public static List<TelegramUser> sendToCurrentAndGetFuture(String message, String targetPlayer) {
        List<TelegramUser> futureReceivers = new ArrayList<>();
        for (Map.Entry<String, TelegramUser> entry : telegramUsers.entrySet()) {
            TelegramUser telegramUser = entry.getValue();
            UserData userData = telegramUser.getUserData();
            if (!canSend(userData, targetPlayer)) continue;
            if (userData.getNotificationLevel() != 0) futureReceivers.add(telegramUser);
            else makeAnnounce(telegramUser, message, targetPlayer);
        }
        return futureReceivers;
    }

    public static boolean canSend(UserData userData, String target) {
        if (userData.isMute()) return false;
        if (userData.getBlackList().contains(target)) return false;
        return !userData.isWhiteList() || userData.getWhiteList().contains(target);
    }

    public static void onPlayerJoin(String playerName, ServerQuery serverQuery) {
        String message = DefaultMessages.PLAYER_JOIN(serverQuery.getServerData().getServerName(), playerName);
        List<TelegramUser> futureReceivers = sendToCurrentAndGetFuture(message, playerName);
        bufferedFirst.put(playerName, new Date().getTime());

        serverUpdater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (bufferedSecond.containsKey(playerName)) {
                    cancel();
                    return;
                }
                if (serverQuery.getOnlinePlayers().contains(playerName)) {
                    if (new Date().getTime() - bufferedFirst.get(playerName) >= 10000) {
                        bufferedSecond.put(playerName, new Date().getTime());
                        bufferedFirst.remove(playerName);
                        makeAnnounce(futureReceivers, message, playerName);
                        cancel();
                    }
                } else cancel();
            }
        }, 10L, 10L);
    }

    public static void onPlayerQuit(String playerName, ServerQuery serverQuery) {
        String message = DefaultMessages.PLAYER_QUIT(serverQuery.getServerData().getServerName(), playerName);
        List<TelegramUser> futureReceivers = sendToCurrentAndGetFuture(message, playerName);

        bufferedSecond.put(playerName, new Date().getTime());

        serverUpdater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!bufferedSecond.containsKey(playerName)) {
                    cancel();
                    return;
                }
                if (!serverQuery.getOnlinePlayers().contains(playerName)) {
                    if (new Date().getTime() - bufferedSecond.get(playerName) >= 10000) {
                        makeAnnounce(futureReceivers, message, playerName);
                        bufferedSecond.remove(playerName);
                        cancel();
                    }
                } else cancel();
            }
        }, 10L, 10L);
    }
}
