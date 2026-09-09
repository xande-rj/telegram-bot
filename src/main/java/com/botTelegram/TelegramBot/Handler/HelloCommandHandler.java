package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class HelloCommandHandler implements BotCommandHandler {

    private final TelegramMessageSender telegramMessageSender;

    public HelloCommandHandler(TelegramMessageSender telegramMessageSender) {
        this.telegramMessageSender = telegramMessageSender;
    }
    @Override
    public List<String> getCommand() {
        return List.of("/ola");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        telegramMessageSender.sendMessage(update.getMessage().getChatId(), "Olá! Como posso ajudar?");
    }
}
