package ru.alexpshkov.ReaxBot.Telegram.commands.system;

import ru.alexpshkov.ReaxBot.Telegram.TelegramUser;
import ru.alexpshkov.ReaxBot.Telegram.messages.DefaultMessages;
import ru.alexpshkov.ReaxBot.Utils.Logger;

import java.lang.reflect.Method;
import java.util.*;

public class AbstractTelegramCommand implements ITelegramCommand {
    private final HashMap<String, Method> subCommands = new HashMap<>();
    public String commandName;

    {
        Arrays.stream(this.getClass().getMethods()).filter(method -> method.isAnnotationPresent(SubCommand.class)).forEach(method -> subCommands.put(method.getName().toLowerCase(Locale.ROOT), method));
    }

    @Override
    public boolean execute(TelegramUser sender, String[] args) {
        Method method;
        if (args.length < 2) {
            method = subCommands.getOrDefault("main_command", null);
            if(method == null) {
                String helpString = getCommandHelp();
                if (helpString != null) sender.sendMessage(helpString);
                return false;
            }
        } else {
            method = subCommands.getOrDefault(args[1].toLowerCase(Locale.ROOT), null);
            if (method == null && ((method = subCommands.getOrDefault("solo_command", null)) == null)) {
                sender.sendMessage(getCommandHelp());
                return false;
            }
        }
        SubCommand subCommand = method.getAnnotation(SubCommand.class);
        if (args.length < subCommand.minArgsLength()) {
            List<String> usage = new ArrayList<>();
            usage.add(method.getName());
            Collections.addAll(usage, subCommand.args());
            sender.sendMessage(DefaultMessages.COMMAND_USAGE(args[0], usage, subCommand.commandHint()));
            return false;
        }
        if (sender.getUserData().getAdministrationLevel() < subCommand.adminLevelReq()) {
            sender.sendMessage(DefaultMessages.NO_PERMS(subCommand.adminLevelReq()));
            return false;
        }
        try {
            method.invoke(this, sender, args);
            return true;
        } catch (Exception ex) {
            Logger.printError(ex);
            return false;
        }
    }

    @Override
    public String getCommandHelp() {
        StringBuilder stringBuilder = new StringBuilder();
        subCommands.forEach((k, v) -> {
            SubCommand subCommand = v.getAnnotation(SubCommand.class);
            if(!subCommand.commandInfo().isEmpty()) {
                stringBuilder.append("\n  - ").append(commandName).append(" ").append((Objects.equals(k, "main_command") || Objects.equals(k, "solo_command")) ? "" : k);
                for (String arg : subCommand.args()) stringBuilder.append(" ").append(arg);
                stringBuilder.append(" - ").append(subCommand.commandInfo());
            }
        });
        return stringBuilder.toString().isEmpty() ? null : "Помощь по команде " + commandName + ":" + stringBuilder;
    }
}
