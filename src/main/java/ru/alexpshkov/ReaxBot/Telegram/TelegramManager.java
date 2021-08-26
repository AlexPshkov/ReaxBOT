package ru.alexpshkov.ReaxBot.Telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import ru.alexpshkov.ReaxBot.Minecraft.User;
import ru.alexpshkov.ReaxBot.ReaxTelegramBot;
import ru.alexpshkov.ReaxBot.Utils.Logger;

import java.util.LinkedList;
import java.util.List;

public class TelegramManager extends TelegramBot {

    public TelegramManager(String BOT_TOKEN) {
        super(BOT_TOKEN);
        setUpdatesListener(updates -> {
            updates.forEach(update -> {
                try {
                    if (update.callbackQuery() != null) onCommandEvent(update.callbackQuery());
                    if (update.message() != null) {
                        Message message = update.message();
                        if (update.message().text() != null) onMessageEvent(message);
                    }
                } catch (Exception e) {
                    Logger.printError(e);
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public boolean removeMessage(int messageId, String chatId) {
        return execute(new DeleteMessage(chatId, messageId)).isOk();
    }

    public boolean sendMessage(String message, String chatId) {
        if (message.isEmpty()) return true;
        if (message.length() > 4000) {
            sendMessage(message.substring(0, 4000), chatId);
            sendMessage(message.substring(4000), chatId);
        }
        return execute(new SendMessage(chatId, message).parseMode(ParseMode.HTML)).isOk();
    }

    public int sendMessageAndGetId(String message, String chatId) {
        if (message.length() > 4000) {
            sendMessage(message.substring(0, 4000), chatId);
            sendMessage(message.substring(4000), chatId);
        }
        return execute(new SendMessage(chatId, message).parseMode(ParseMode.HTML)).message().messageId();
    }

    public boolean editMessage(String chatId, int messageId, String text) {
        return execute(new EditMessageText(chatId, messageId, text).parseMode(ParseMode.HTML)).isOk();
    }

    public boolean sendMessageWithKeyboard(String message, Keyboard keyboard, String chatId) {
        if (message.isEmpty()) return true;
        return execute(new SendMessage(chatId, message).parseMode(ParseMode.HTML).replyMarkup(keyboard)).isOk();
    }

    public boolean sendMessageWithKeyboard(String message, List<InlineKeyboardButton> buttons, String chatId) {
        if (message.isEmpty()) return true;
        InlineKeyboardButton[] buttonsArr = new InlineKeyboardButton[buttons.size()];
        buttons.toArray(buttonsArr);
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(buttonsArr);
        return execute(new SendMessage(chatId, message).parseMode(ParseMode.HTML).replyMarkup(inlineKeyboard)).isOk();
    }

    private void onCommandEvent(CallbackQuery callback) {
        String chatId = callback.message().chat().id().toString();
        String[] args = callback.data().split("\\^");
        if(args[0].equalsIgnoreCase("blackList")) {
            User user = ReaxTelegramBot.getTelegramUser(chatId).getUser();
            LinkedList<String> blackList = user.getBlackList();
            if(blackList.contains(args[1])) {
                editMessage(chatId, callback.message().messageId(), DefaultMessages.MUTE_ALREADY_ENABLE(args[1]));
            } else {
                user.addToBlackList(args[1]);
                editMessage(chatId, callback.message().messageId(), DefaultMessages.MUTE_ENABLE(args[1]));
            }
        }
        execute(new AnswerCallbackQuery(callback.id()));
    }

    private void onMessageEvent(Message message) {
        String msg = message.text();
        String chatId = message.chat().id().toString();
        if (!ReaxTelegramBot.isSuchTelegramUser(chatId)) {
            ReaxTelegramBot.addNewUser(new User(chatId, message.from().username(), "Никогда", new LinkedList<>(), false));
            if (msg.equalsIgnoreCase("/start"))
                sendMessage(DefaultMessages.WELCOME_MESSAGE(), chatId);
        }
        String[] args = msg.split(" ");

        if (args[0].startsWith("/")) {
//            if(args[0].equalsIgnoreCase("/list")) {
//                sendMessage(DefaultMessages.PLAYERS_LIST(ReaxTelegramBot.getOnlinePlayers()), chatId);
//                return;
//            }
            User user = ReaxTelegramBot.getTelegramUser(chatId).getUser();
            if(args[0].equalsIgnoreCase("/mute")) {
                if(args.length == 1) {
                    if(user.isMute()) {
                        user.setMute(false);
                        sendMessage(DefaultMessages.MUTE_DISABLE(), chatId);
                    } else {
                        user.setMute(true);
                        sendMessage(DefaultMessages.MUTE_ENABLE(), chatId);
                    }
                } else {
                    String target = args[1];
                    LinkedList<String> blackList = user.getBlackList();
                    if(target.equalsIgnoreCase("list")){
                        sendMessage(DefaultMessages.BLACK_LIST(blackList), chatId);
                        return;
                    }
                    if(blackList.contains(target)) {
                        sendMessage(DefaultMessages.MUTE_DISABLE(target), chatId);
                        user.removeFromBlackList(target);
                    } else {
                        sendMessage(DefaultMessages.MUTE_ENABLE(target), chatId);
                        user.addToBlackList(target);
                    }
                }
                return;
            }
            sendMessage(DefaultMessages.NO_SUCH_COMMAND(), chatId);
        }
    }

}
