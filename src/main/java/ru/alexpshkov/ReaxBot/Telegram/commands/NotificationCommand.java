package ru.alexpshkov.ReaxBot.Telegram.commands;

import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.AbstractTelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.SubCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;

import java.util.Locale;

class NotificationType {
    public static int getNotificationLvl(String name) {
        switch (name.toLowerCase(Locale.ROOT)) {
            case "instant":
                return 0;
            case "delayed":
                return 1;
            default:
                return -1;
        }
    }

    public static String getNotificationType(int notificationLvl) {
        switch (notificationLvl) {
            case 0:
                return "INSTANT";
            default:
                return "DELAYED";
        }
    }
}

public class NotificationCommand extends AbstractTelegramCommand {

    public NotificationCommand(String commandName) {
        super.commandName = commandName;
    }

    @SubCommand(commandInfo = "Узнать уровень оповещения", commandHint = "Пока только в тесте")
    public void MAIN_COMMAND(TelegramUser sender, String[] args) {
        sender.sendMessage("У вас стоит " + NotificationType.getNotificationType(sender.getUserData().getNotificationLevel()) + " тип оповещения.");
    }

    @SubCommand(commandInfo = "Выбрать уровень оповещения", minArgsLength = 3, args = "[notificationType]", commandHint = "Если мешают фейковые заходы и выходы, то устнови 1 или больше")
    public void set(TelegramUser sender, String[] args) {
        try {
            int notificationLvl = NotificationType.getNotificationLvl(args[2]);
            if (notificationLvl == -1) notificationLvl = Byte.parseByte(args[2]);
            if (notificationLvl > 5) throw new NumberFormatException();
            sender.getUserData().setNotificationLevel((byte) notificationLvl);
            sender.sendMessage("Вы установили себе " + NotificationType.getNotificationType(notificationLvl) + " тип оповещения");
        } catch (NumberFormatException ex) {
            sender.sendMessage(DefaultMessages.ERROR("В качестве уровня должно быть число от 0 до 5"));
        }
    }

}
