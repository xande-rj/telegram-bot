package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.service.PriceService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class TranslateCommandHandler implements BotCommandHandler {
    private final TelegramMessageSender telegramMessageSender;

    public TranslateCommandHandler(TelegramMessageSender telegramMessageSender) {
        this.telegramMessageSender = telegramMessageSender;
    }
    @Override
    public List<String> getCommand() {
        return List.of("/traduzir");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        String text = update.getMessage().getText();
        String textoCompleto = text.replaceFirst("/traduzir\\s*", "").trim();
        String mensagem =
                "Use assim: /traduzir <idioma> <texto>\nEx: /traduzir inglês Bom dia!";
        if (textoCompleto.isEmpty()) {
            telegramMessageSender.sendMessage(update.getMessage().getChatId(),  mensagem);
        }
        System.out.println(textoCompleto);

    }
}
