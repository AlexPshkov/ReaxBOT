package ru.alexpshkov.ReaxBot.Telegram.commands.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.METHOD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface SubCommand {
    String commandHint() default "";
    String commandInfo() default "";
    byte adminLevelReq() default 0;
    String[] args() default {};
    int minArgsLength() default 0;
}
