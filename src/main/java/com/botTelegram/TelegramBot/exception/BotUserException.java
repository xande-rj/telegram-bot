package com.botTelegram.TelegramBot.exception;

public class BotUserException extends RuntimeException{
    public BotUserException(String message) {
        super(message);
    }
    public BotUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
