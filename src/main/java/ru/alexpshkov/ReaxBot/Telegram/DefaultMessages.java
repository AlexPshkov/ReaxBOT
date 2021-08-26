package ru.alexpshkov.ReaxBot.Telegram;

import java.util.List;

public class DefaultMessages {

    public static String WELCOME_MESSAGE() {
        return "\uD83D\uDD05 Бот будет сообщать о заходах и выходах игроков с сервера. Если вам будет это мешать, то Вы всегда можете отключить оповещения от конкретного игрока, или все оповещения разом";
    }

    public static String NO_SUCH_COMMAND() {
        return "❗️ Данной команды нет. Проверьте правильность написания";
    }

    public static String MUTE_ENABLE() {
        return "\uD83D\uDCDB Вы выключили все сообщения. Включить - /mute";
    }

    public static String MUTE_DISABLE() {
        return "❇️ Вы включили все сообщения. Выключить - /mute";
    }

    public static String MUTE_ENABLE(String playerName) {
        return "🚫 Вы заглушили игрока <strong>" + playerName + "</strong> \nПосмотреть список всех заглушенных игроков - /mute list";
    }

    public static String MUTE_DISABLE(String playerName) {
        return "❇️ Вы сняли заглушку с игрока <strong>" + playerName +"</strong> ";
    }

    public static String MUTE_ALREADY_ENABLE(String playerName) {
        return "⁉️ Этот игрок уже заглушен. Посмотреть список - /mute list";
    }

    public static String PLAYER_JOIN(String playerName, List<String> allPlayers) {
        StringBuilder stringBuilder = new StringBuilder("➕ <strong>"+ playerName + "</strong>");
        stringBuilder.append("\nСейчас на сервере:");
        for (String player : allPlayers)
            stringBuilder.append("\n • " + player);
        return stringBuilder.toString();
    }

    public static String PLAYER_QUIT(String playerName, List<String> allPlayers) {
        StringBuilder stringBuilder = new StringBuilder("➖ <strong>" + playerName + "</strong>");
        if(!allPlayers.isEmpty()) {
            stringBuilder.append("\nСейчас на сервере:");
            for (String player : allPlayers)
                stringBuilder.append("\n • " + player);
        } else
            stringBuilder.append("\nТеперь сервер пустой");
        return stringBuilder.toString();
    }

    public static String PLAYERS_LIST(List<String> currentPlayers) {
        if(currentPlayers.size() == 0) return "\uD83D\uDCAC Сейчас на сервере никого нет";
        StringBuilder stringBuilder = new StringBuilder("\uD83D\uDCAC На данный момент на сервере играют: ");
        currentPlayers.forEach(currentPlayer -> stringBuilder.append("\n  • ").append(currentPlayer));
        return stringBuilder.toString();
    }

    public static String BLACK_LIST(List<String> blackList) {
        if(blackList.size() == 0) return "\uD83C\uDF1A В вашем черном списке никого нет. Добавить - /mute [ник]";
        StringBuilder stringBuilder = new StringBuilder("♨️ Вы заглушили игроков:");
        blackList.forEach(currentPlayer -> stringBuilder.append("\n  • ").append(currentPlayer));
        stringBuilder.append("\nСнять заглушку - /mute [ник]");
        return stringBuilder.toString();
    }
}
