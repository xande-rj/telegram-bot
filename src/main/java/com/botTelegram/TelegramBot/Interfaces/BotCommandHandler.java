package com.botTelegram.TelegramBot.Interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface BotCommandHandler {
    String getCommand();
    void handle(Update update, TelegramClient telegramClient) throws TelegramApiException;
}
