package ru.alexpshkov.ReaxBot.Minecraft;

import ru.alexpshkov.ReaxBot.ReaxTelegramBot;

import java.util.LinkedList;

public class User {
    private final String telegramId;
    private String playerName;
    private String lastJoinDate;
    private LinkedList<String> blackList;
    private boolean mute;

    public User(String telegramId, String playerName, String lastJoinDate, LinkedList<String> blackList, boolean mute) {
        this.telegramId = telegramId;
        this.playerName = playerName;
        this.lastJoinDate = lastJoinDate;
        this.blackList = blackList;
        this.mute = mute;
    }

    public LinkedList<String> getBlackList() {
        return blackList;
    }

    public boolean isMute() {
        return mute;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getLastJoinDate() {
        return lastJoinDate;
    }

    public void addToBlackList(String playerName) {
        this.blackList.add(playerName);
        ReaxTelegramBot.dataBase.saveUser(this);
    }

    public void removeFromBlackList(String playerName) {
        this.blackList.remove(playerName);
        ReaxTelegramBot.dataBase.saveUser(this);
    }

    public void setMute(boolean flag) {
        this.mute = flag;
        ReaxTelegramBot.dataBase.saveUser(this);
    }

    public void setLastJoinDate(String lastJoinDate) {
        this.lastJoinDate = lastJoinDate;
        ReaxTelegramBot.dataBase.saveUser(this);
    }

    public void setPlayerName(String playerName) {
        this.lastJoinDate = playerName;
        ReaxTelegramBot.dataBase.saveUser(this);
    }
}
