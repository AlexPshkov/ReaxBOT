package ru.alexpshkov.ReaxBot.Telegram.messages;

public class UpdatableMessage {
    private final int messageId;
    private final String chatId;
    private int updatesAmount;
    private final int updatesMax;
    private final Runnable updateCommand;

    public UpdatableMessage(Runnable updateCommand, int messageId, String chatId, int updatesMax) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.updatesAmount = 0;
        this.updatesMax = updatesMax;
        this.updateCommand = updateCommand;
    }

    /**
     * Update message
     * @return will next update or not
     */
    public boolean update() {
        if (updatesAmount++ >= updatesMax || updatesMax == -1) return false;
        updateCommand.run();
        return true;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getChatId() {
        return chatId;
    }
}
