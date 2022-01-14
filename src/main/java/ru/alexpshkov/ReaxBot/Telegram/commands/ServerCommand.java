package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.DataBase.ServerData;
import ru.alexpshkov.ReaxBot.Minecraft.ServerQuery;
import ru.alexpshkov.ReaxBot.ReaxTelegramBot;
import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;

public class ServerCommand extends AbstractTelegramCommand {

    public ServerCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(minArgsLength = 2, adminLevelReq = 10, commandInfo = "Список всех серверов")
    public void list(TelegramUser sender, String[] args) {
        if (ReaxTelegramBot.getServers().isEmpty()) {
            sender.sendMessage("Список серверов пуст");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("Список серверов:");
        ReaxTelegramBot.getServers().forEach((k, v) -> stringBuilder.append("\n  • ").append(k).append(v.getServerData().isTurnFlag() ? "" : " \uD83D\uDCA4 ").append(" | ").append(v.getServerData().getServerIp()).append(":").append(v.getServerData().getServerPort()).append(" | ").append(v.getServerData().getUpdateDelay()).append("ms"));
        sender.sendMessage(stringBuilder.toString());
    }

    @SubCommand(minArgsLength = 6, adminLevelReq = 10, commandInfo = "Добавить сервер", args = {"[serverName]", "[serverIp]", "[serverPort]", "[updateDelay]"})
    public void add(TelegramUser sender, String[] args) {
        try {
            int serverPort = Integer.parseInt(args[4]);
            int updateDelay = Integer.parseInt(args[5]);
            ReaxTelegramBot.registerNewServer(new ServerData(args[2], args[3], serverPort, updateDelay, true));
            sender.sendMessage("Добавлен сервер " + args[2]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(DefaultMessages.ERROR("[serverPort] и [updateDelay] должны быть числами"));
        }
    }

    @SubCommand(minArgsLength = 3, adminLevelReq = 10, commandInfo = "Удалить сервер", args = "[serverName]")
    public void remove(TelegramUser sender, String[] args) {
        if (!ReaxTelegramBot.getServers().containsKey(args[2])) {
            sender.sendMessage(DefaultMessages.ERROR("Нет сервера с названием " + args[2]));
            return;
        }
        ReaxTelegramBot.getServers().remove(args[2]);
        ReaxTelegramBot.getSQLiteDataBase().removeServer(args[2]);
        sender.sendMessage("Сервер " + args[2] + " удален");
    }

    @SubCommand(minArgsLength = 3, adminLevelReq = 10,commandInfo = "Запустить остановленный сервер", args = "[serverName]")
    public void start(TelegramUser sender, String[] args) {
        if (!ReaxTelegramBot.getServers().containsKey(args[2])) {
            sender.sendMessage(DefaultMessages.ERROR("Нет сервера с названием " + args[2]));
            return;
        }
        ServerQuery serverQuery = ReaxTelegramBot.getServers().get(args[2]);
        ServerData serverData = serverQuery.getServerData();
        if (serverData.isTurnFlag()) {
            sender.sendMessage(DefaultMessages.ERROR("Сервер " + args[2] + " и так уже запущен"));
            return;
        }
        serverData.setTurnFlag(true);
        sender.sendMessage("Сервер " + args[2] + " запущен");
    }

    @SubCommand(minArgsLength = 3, adminLevelReq = 10, commandInfo = "Остановить сервер, но не удалять", args = "[serverName]")
    public void stop(TelegramUser sender, String[] args) {
        if (!ReaxTelegramBot.getServers().containsKey(args[2])) {
            sender.sendMessage(DefaultMessages.ERROR("Нет сервера с названием " + args[2]));
            return;
        }
        ServerQuery serverQuery = ReaxTelegramBot.getServers().get(args[2]);
        ServerData serverData = serverQuery.getServerData();
        if (!serverData.isTurnFlag()) {
            sender.sendMessage(DefaultMessages.ERROR("Сервер " + args[2] + " и так не был запущен"));
            return;
        }
        serverData.setTurnFlag(false);
        sender.sendMessage("Сервер " + args[2] + " остановлен");
    }
}
