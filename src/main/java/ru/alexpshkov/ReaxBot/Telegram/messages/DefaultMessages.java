package ru.alexpshkov.ReaxBot.Telegram.messages;

import ru.alexpshkov.ReaxBot.ReaxTelegramBot;

import java.util.List;

public class DefaultMessages {

    public static String WELCOME_MESSAGE() {
        return "Привет! Я буду сообщать если кто-то зайдет или выйдет с сервера. \n" +
                "  - В случае, если тебе надоедят мои оповещения, то ты можешь замутить меня командой /mute.\n" +
                "  - Если же тебе надоедят оповещения о заходе и выходе конкретного игрока, то ты можешь убрать их командой /mute [ник игрока] и больше ты не будешь их получать.";
    }

    public static String ERROR(String message) {
        return "Ошибка! " + message;
    }

    public static String NO_SUCH_COMMAND() {
        return "❗️ Данной команды нет. Проверьте правильность написания";
    }

    public static String NO_PERMS(byte administrationLevel) {
        return "⛔️ Для этого нужен уровень доступа не ниже " + administrationLevel;
    }

    public static String COMMAND_USAGE(String command, List<String> args) {
        StringBuilder stringBuilder = new StringBuilder("Используй: ").append(command);
        for (String arg : args)
            stringBuilder.append(" ").append(arg);
        return stringBuilder.toString();
    }

    public static String COMMAND_USAGE(String command, List<String> args, String hint) {
        if (hint.isEmpty()) return COMMAND_USAGE(command, args);
        return COMMAND_USAGE(command, args) + "\n   ➥ " + hint;
    }

    public static String MUTE_ENABLE() {
        return "\uD83D\uDCDB Вы выключили все сообщения. Включить - /mute";
    }

    public static String MUTE_DISABLE() {
        return "❇️ Вы включили все сообщения. Выключить - /mute";
    }

    public static String BLACKLIST_ADD(String playerName) {
        return "🚫 Вы заглушили игрока " + playerName + " \nПосмотреть список всех заглушенных игроков - /mute list";
    }

    public static String BLACKLIST_REMOVE(String playerName) {
        return "❇️ Вы сняли заглушку с игрока <strong>" + playerName + "</strong> ";
    }

    public static String BLACKLIST_ALREADY() {
        return "⁉️ Этот игрок уже заглушен. Посмотреть список - /mute list";
    }

    public static String WHITELIST_ADD(String playerName) {
        return " Вы добавили игрока " + playerName + " в свой белый список \n Посмотреть игроков в своем белом списке - /whitelist list";
    }

    public static String WHITELIST_REMOVE(String playerName) {
        return " Вы удалили игрока " + playerName + " из своего белого списка";
    }

    public static String WHITELIST_ENABLE() {
        return "Включен режим белого списка. Вам будут приходить уведомления только об игроках, которые есть в вашем белом списке.";
    }

    public static String WHITELIST_DISABLE() {
        return "Режим белого списка выключен.";
    }

    public static String WHITELIST_ALREADY() {
        return "⁉️ Этот игрок уже в вашем белом списке. Посмотреть список - /whitelist list";
    }

    public static String PLAYER_JOIN(String serverName, String playerName) {
        StringBuilder stringBuilder = new StringBuilder(serverName + " | ➕ <strong>" + playerName + "</strong>");
        stringBuilder.append("\nСейчас на сервере:");
        for (String player : ReaxTelegramBot.getOnlinePlayers(serverName))
            stringBuilder.append("\n • ").append(player);
        return stringBuilder.toString();
    }

    public static String PLAYER_QUIT(String serverName, String playerName) {
        StringBuilder stringBuilder = new StringBuilder(serverName + " | ➖ <strong>" + playerName + "</strong>");
        List<String> onlinePlayers = ReaxTelegramBot.getOnlinePlayers(serverName);
        if (!onlinePlayers.isEmpty()) {
            stringBuilder.append("\nСейчас на сервере:");
            for (String player : onlinePlayers)
                stringBuilder.append("\n • ").append(player);
        } else
            stringBuilder.append("\nТеперь сервер пустой");
        return stringBuilder.toString();
    }

    public static String GLOBAL_PLAYER_LIST() {
        if (ReaxTelegramBot.getOnlinePlayers().isEmpty())
            return "Сейчас игроков нет";
        StringBuilder stringBuilder = new StringBuilder("Список всех игроков:");
        ReaxTelegramBot.getOnlinePlayers().forEach(name -> stringBuilder.append("\n  • ").append(name));
        return stringBuilder.toString();
    }

    public static String SERVER_PLAYER_LIST(String serverName) {
        List<String> onlinePlayers = ReaxTelegramBot.getOnlinePlayers(serverName);
        if (onlinePlayers.isEmpty())
            return "На данном сервере нет игроков";
        StringBuilder stringBuilder = new StringBuilder("Список игроков на сервере " + serverName + ":");
        onlinePlayers.forEach(name -> stringBuilder.append("\n  • ").append(name));
        return stringBuilder.toString();
    }

}
