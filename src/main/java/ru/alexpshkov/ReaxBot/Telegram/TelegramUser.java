package ru.alexpshkov.ReaxBot.Telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import ru.alexpshkov.ReaxBot.Minecraft.User;

import java.util.List;

public class TelegramUser {

    private final User user;
    private final String telegramId;
    private final TelegramManager telegramManager;

    public TelegramUser(User user, TelegramManager telegramManager) {
        this.user = user;
        this.telegramId = user.getTelegramId();
        this.telegramManager = telegramManager;
    }

    public User getUser() {
        return user;
    }

    public boolean sendMessage(String message) {
        return telegramManager.sendMessage(message, telegramId);
    }

    public int sendMessageAndGetId(String message) {
        return telegramManager.sendMessageAndGetId(message, telegramId);
    }

    public boolean sendMessageWithKeyboard(String message, List<InlineKeyboardButton> buttons) {
        return telegramManager.sendMessageWithKeyboard(message, buttons, telegramId);
    }

    public boolean removeMessage(int messageId) {
        return telegramManager.removeMessage(messageId, telegramId);
    }

    public boolean editMessage(int messageId, String newMessage) {
        return telegramManager.editMessage(telegramId, messageId, newMessage);
    }
}
