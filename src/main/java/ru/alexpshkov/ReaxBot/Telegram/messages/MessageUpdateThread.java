package ru.alexpshkov.ReaxBot.Telegram.messages;

import ru.alexpshkov.ReaxBot.Telegram.TelegramManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class MessageUpdateThread extends TimerTask {
    private final TelegramManager telegramManager;

    public MessageUpdateThread(TelegramManager telegramManager) {
        this.telegramManager = telegramManager;
    }

    @Override
    public void run() {
        List<UpdatableMessage> messagesToRemove = new ArrayList<>();
        telegramManager.getUpdatableMessages().forEach(msg -> { if(!msg.update()) messagesToRemove.add(msg); });
        messagesToRemove.forEach(telegramManager::removeMessage);
    }
}
