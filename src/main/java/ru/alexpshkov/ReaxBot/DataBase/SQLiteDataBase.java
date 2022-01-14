package ru.alexpshkov.ReaxBot.DataBase;

import com.google.gson.Gson;
import ru.alexpshkov.ReaxBot.Minecraft.ServerQuery;
import ru.alexpshkov.ReaxBot.Utils.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDataBase {

    public boolean initDataBase() {
        boolean flag;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS 'USERS' ('TELEGRAM_ID' VARCHAR(255) NOT NULL, 'USER' VARCHAR(1000) NOT NULL, PRIMARY KEY('TELEGRAM_ID'))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS 'SERVERS' ('SERVER_NAME' VARCHAR(255) NOT NULL, 'SERVER' VARCHAR(1000) NOT NULL, PRIMARY KEY('SERVER_NAME'))");
            flag = true;
        } catch (SQLException e) {
            Logger.printError(e);
            flag = false;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
        return flag;
    }

    public List<UserData> getAllUsers() {
        Connection connection = null;
        List<UserData> userData = new ArrayList<>();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM 'USERS'");
            while (rs.next()) {
                Gson gson = new Gson();
                userData.add(gson.fromJson(rs.getString("USER"), UserData.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
        return userData;
    }

    public List<ServerData> getAllServers() {
        Connection connection = null;
        List<ServerData> serverData = new ArrayList<>();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM 'SERVERS'");
            while (rs.next()) {
                Gson gson = new Gson();
                serverData.add(gson.fromJson(rs.getString("SERVER"), ServerData.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
        return serverData;
    }

    public void saveServer(ServerData serverData) {
        if (isSuchServer(serverData.getServerName())) insertServer(serverData);
        else {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                Gson gson = new Gson();
                statement.executeUpdate("INSERT INTO 'SERVERS' ('SERVER_NAME', 'SERVER') values('" + serverData.getServerName() + "', '" + gson.toJson(serverData) + "')");
            } catch (SQLException e) {
                Logger.printError(e);
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    Logger.printError(e);
                }
            }
        }
    }

    private void insertServer(ServerData serverData) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            Gson gson = new Gson();
            statement.executeUpdate("UPDATE 'SERVERS' SET 'SERVER' = '" + gson.toJson(serverData) + "' WHERE SERVER_NAME = '" + serverData.getServerName() + "'");
        } catch (SQLException e) {
            Logger.printError(e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
    }

    public Boolean isSuchServer(String serverName) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM 'SERVERS' WHERE SERVER_NAME = '" + serverName + "'");
            flag = rs.next();
        } catch (SQLException e) {
            Logger.printError(e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
        return flag;
    }

    public void removeServer(String serverName) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("DELETE FROM 'SERVERS' WHERE SERVER_NAME = '" + serverName + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean isSuchUser(String telegramID) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM 'USERS' WHERE TELEGRAM_ID = '" + telegramID + "'");
            flag = rs.next();
        } catch (SQLException e) {
            Logger.printError(e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
        return flag;
    }

    public void saveUser(UserData userData) {
        if (isSuchUser(userData.getTelegramId())) insertUser(userData);
        else {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                Gson gson = new Gson();
                statement.executeUpdate("INSERT INTO 'USERS' ('TELEGRAM_ID', 'USER') values('" + userData.getTelegramId() + "', '" + gson.toJson(userData) + "')");
            } catch (SQLException e) {
                Logger.printError(e);
            } finally {
                try {
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    Logger.printError(e);
                }
            }
        }
    }

    private void insertUser(UserData userData) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            Gson gson = new Gson();
            statement.executeUpdate("UPDATE 'USERS' SET 'USER' = '" + gson.toJson(userData) + "' WHERE TELEGRAM_ID = '" + userData.getTelegramId() + "'");
        } catch (SQLException e) {
            Logger.printError(e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
    }

    public UserData getUser(String telegramID) {
        if (!isSuchUser(telegramID)) return null;
        Connection connection = null;
        UserData userData = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM 'USERS' WHERE TELEGRAM_ID = '" + telegramID + "'");
            if (rs.next()) {
                Gson gson = new Gson();
                userData = gson.fromJson(rs.getString("USER"), UserData.class);
            }
        } catch (SQLException e) {
            Logger.printError(e);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Logger.printError(e);
            }
        }
        return userData;
    }

}
