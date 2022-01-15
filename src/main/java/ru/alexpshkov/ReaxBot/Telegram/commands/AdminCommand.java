package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.ReaxTelegramBot;
import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;

public class AdminCommand extends AbstractTelegramCommand {

    public AdminCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(minArgsLength = 3, args = "[BotToken]", commandInfo = "Получить админку у бота", commandHint = "Для подтверждения укажите API токен данного бота")
    public void makeMeAdmin(TelegramUser sender, String[] args) {
        if (args[2].equals(ReaxTelegramBot.botToken)) {
            sender.getUserData().setAdministrationLevel((byte) 10);
            sender.sendMessage("Вам установлен максимальный уровень доступа");
            return;
        }
        sender.sendMessage("Код неверен. Проверьте, не ошиблись ли Вы.");
    }

    @SubCommand(minArgsLength = 4, adminLevelReq = 10, args = {"[TelegramID]", "[AdminLVL]"}, commandInfo = "Поставить уровень доступа другому", commandHint = "Уровень админки от 1 до 10 (10 - полный доступ)")
    public void setLevel(TelegramUser sender, String[] args) {
        if(!ReaxTelegramBot.isSuchTelegramUser(args[2])) {
            sender.sendMessage(DefaultMessages.ERROR("Пользователя с таким ID не найдено"));
            return;
        }
        TelegramUser target = ReaxTelegramBot.getTelegramUser(args[2]);
        try {
            byte administrationLevel = Byte.parseByte(args[3]);
            target.getUserData().setAdministrationLevel(administrationLevel);
            sender.sendMessage("Вы установили " + target.getUserData().getPlayerName() + " уровень доступа " + administrationLevel);
        } catch (NumberFormatException ex) {
            sender.sendMessage(DefaultMessages.ERROR("В качестве уровня должно быть число от 0 до 10"));
        }
    }

    @SubCommand(minArgsLength = 2, adminLevelReq = 5, commandInfo = "Посмотреть список всех пользователей")
    public void userList(TelegramUser sender, String[] args) {
        StringBuilder userList = new StringBuilder("Список пользователей:");
        ReaxTelegramBot.getAllTelegramUsers().forEach((k, v) -> userList.append("\n  • ").append(v.getUserData().getPlayerName()).append(" | ").append(k));
        sender.sendMessage(userList.toString());
    }
}
