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
import ru.alexpshkov.ReaxBot.DataBase.UserData;
import ru.alexpshkov.ReaxBot.ReaxTelegramBot;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.CommandManager;
import ru.alexpshkov.ReaxBot.Telegram.commands.StartCommand;
import ru.alexpshkov.ReaxBot.Telegram.commands.system.ITelegramCommand;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;
import ru.alexpshkov.ReaxBot.Telegram.messages.MessageUpdateThread;
import ru.alexpshkov.ReaxBot.Telegram.messages.UpdatableMessage;
import ru.alexpshkov.ReaxBot.Utils.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

public class TelegramManager extends TelegramBot {
    Timer messageUpdater = new Timer();
    private final List<UpdatableMessage> updatableMessages = new ArrayList<>();

    public TelegramManager(String BOT_TOKEN) {
        super(BOT_TOKEN);
        messageUpdater.scheduleAtFixedRate(new MessageUpdateThread(this), 0, 5000);
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

    public List<UpdatableMessage> getUpdatableMessages() {
        return updatableMessages;
    }

    public void addUpdatableMessage(Runnable updateMethod, int messageId, String chatId,  int amountOfUpdates) {
        updatableMessages.add(new UpdatableMessage(updateMethod, messageId, chatId, amountOfUpdates));
    }

    public void removeMessage(UpdatableMessage message) {
        updatableMessages.remove(message);
        removeMessage(message.getMessageId(), message.getChatId());
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
        UserData userData = ReaxTelegramBot.getTelegramUser(chatId).getUserData();
        if(args[0].equalsIgnoreCase("blackList")) {
            LinkedList<String> blackList = userData.getBlackList();
            if(blackList.contains(args[1])) {
                editMessage(chatId, callback.message().messageId(), DefaultMessages.BLACKLIST_ALREADY());
            } else {
                userData.addToBlackList(args[1]);
                editMessage(chatId, callback.message().messageId(), DefaultMessages.BLACKLIST_ADD(args[1]));
            }
        }
        execute(new AnswerCallbackQuery(callback.id()));
    }

    private void onMessageEvent(Message message) {
        String msg = message.text();
        String chatId = message.chat().id().toString();
        if (!ReaxTelegramBot.isSuchTelegramUser(chatId)) {
            ReaxTelegramBot.addNewUser(new UserData(chatId, message.from().username(), new LinkedList<>(), false, (byte) 0, (byte) 0, new LinkedList<>(), false));
            new StartCommand("/start").execute(ReaxTelegramBot.getTelegramUser(chatId), null);
        }
        String[] args = msg.split(" ");
        TelegramUser telegramUser = ReaxTelegramBot.getTelegramUser(chatId);

        if (!args[0].startsWith("/")) return;
        ITelegramCommand telegramCommand = CommandManager.getCommand(args[0]);
         if (telegramCommand == null) {
             sendMessage(DefaultMessages.NO_SUCH_COMMAND(), chatId);
             return;
         }
         CommandManager.getCommand(args[0]).execute(telegramUser, args);
    }
}