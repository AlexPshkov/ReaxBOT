package ru.alexpshkov.ReaxBot.Utils;

import com.google.gson.Gson;
import ru.alexpshkov.ReaxBot.Minecraft.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    public boolean initDataBase() {
        boolean flag;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS 'USERS' ('TELEGRAM_ID' VARCHAR(255) NOT NULL, 'USER' VARCHAR(1000) NOT NULL, PRIMARY KEY('TELEGRAM_ID'))");
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

    public List<User> getAllUsers() {
        Connection connection = null;
        List<User> users = new ArrayList<>();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM 'USERS'");
            while (rs.next()) {
                Gson gson = new Gson();
                users.add(gson.fromJson(rs.getString("USER"), User.class));
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
        return users;
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

    public void saveUser(User user) {
        if (isSuchUser(user.getTelegramId())) insertUser(user);
        else {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
                Gson gson = new Gson();
                statement.executeUpdate("INSERT INTO 'USERS' ('TELEGRAM_ID', 'USER') values('" + user.getTelegramId() + "', '" + gson.toJson(user) + "')");
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

    private void insertUser(User user) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            Gson gson = new Gson();
            statement.executeUpdate("UPDATE 'USERS' SET 'USER' = '" + gson.toJson(user) + "' WHERE TELEGRAM_ID = '" + user.getTelegramId() + "'");
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

    public User getUser(String telegramID) {
        if (!isSuchUser(telegramID)) return null;
        Connection connection = null;
        User user = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery("SELECT * FROM 'USERS' WHERE TELEGRAM_ID = '" + telegramID + "'");
            if (rs.next()) {
                Gson gson = new Gson();
                user = gson.fromJson(rs.getString("USER"), User.class);
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
        return user;
    }

}


