package ru.alexpshkov.ReaxBot.DataBase;

import ru.alexpshkov.ReaxBot.ReaxTelegramBot;

public class ServerData {

    private final String serverName;
    private final String serverIp;
    private final int serverPort;
    private final int updateDelay;
    private boolean turnFlag;

    public ServerData(String serverName, String serverIp, int serverPort, int updateDelay, boolean turnFlag) {
        this.serverName = serverName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.updateDelay = updateDelay;
        this.turnFlag = turnFlag;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getUpdateDelay() {
        return updateDelay;
    }

    public boolean isTurnFlag() {
        return turnFlag;
    }

    public void setTurnFlag(boolean turnFlag) {
        this.turnFlag = turnFlag;
        ReaxTelegramBot.getSQLiteDataBase().saveServer(this);
    }
}