package ru.alexpshkov.ReaxBot.Utils;

import ru.alexpshkov.ReaxBot.ReaxTelegramBot;

import java.util.Date;

public class Logger {
    public static void printError(Exception exception) {
        System.out.println(new Date() + " | " + exception.getMessage());
        if(ReaxTelegramBot.DEBUG_MODE) exception.printStackTrace();
    }
}
