package ru.alexpshkov.ReaxBot.DataBase;

import javafx.util.Pair;
import ru.alexpshkov.ReaxBot.ReaxTelegramBot;

import java.util.LinkedList;
import java.util.List;

public class UserData {
    private final String telegramId;
    private final String playerName;
    private LinkedList<String> blackList;
    private LinkedList<String> whiteList;
    private boolean mute;
    private boolean isWhiteList;
    private byte notificationLevel;
    private byte administrationLevel;

    /**
     * User data object
     * @param telegramId ChatID of user
     * @param playerName Name of user
     * @param blackList List of blocked users
     * @param mute Muted or not
     * @param notificationLevel Level of notification
     * @param administrationLevel Level of administration
     * @param whiteList list of "white" players
     * @param isWhiteList is whitelist enabled
     */
    public UserData(String telegramId, String playerName, LinkedList<String> blackList, boolean mute, byte notificationLevel, byte administrationLevel, LinkedList<String> whiteList, boolean isWhiteList) {
        this.telegramId = telegramId;
        this.playerName = playerName;
        this.blackList = blackList;
        this.whiteList = whiteList;
        this.mute = mute;
        this.notificationLevel = notificationLevel;
        this.administrationLevel = administrationLevel;
        this.isWhiteList = isWhiteList;
    }

    public LinkedList<String> getBlackList() {
        return blackList == null ? new LinkedList<>() : blackList;
    }

    public LinkedList<String> getWhiteList() {
        return whiteList == null ? new LinkedList<>() : whiteList;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean isWhiteList() {
        return isWhiteList;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void addToBlackList(String playerName) {
        if (blackList == null) blackList = new LinkedList<>();
        this.blackList.add(playerName);
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }

    public void addToWhiteList(String playerName) {
        if (whiteList == null) whiteList = new LinkedList<>();
        this.whiteList.add(playerName);
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }

    public void removeFromBlackList(String playerName) {
        if (blackList == null) blackList = new LinkedList<>();
        this.blackList.remove(playerName);
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }

    public void removeFromWhiteList(String playerName) {
        if (whiteList == null) whiteList = new LinkedList<>();
        this.whiteList.remove(playerName);
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }

    public void setMute(boolean flag) {
        this.mute = flag;
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }

    public void setWhiteList(boolean flag) {
        this.isWhiteList = flag;
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }

    public int getNotificationLevel() {
        return notificationLevel;
    }

    public void setNotificationLevel(byte notificationLevel) {
        this.notificationLevel = notificationLevel;
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }

    public byte getAdministrationLevel() {
        return administrationLevel;
    }

    public void setAdministrationLevel(byte administrationLevel) {
        this.administrationLevel = administrationLevel;
        ReaxTelegramBot.getSQLiteDataBase().saveUser(this);
    }
}