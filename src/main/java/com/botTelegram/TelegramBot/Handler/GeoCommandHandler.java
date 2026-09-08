package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.service.ConversationStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class GeoCommandHandler implements BotCommandHandler {
    private final TelegramMessageSender telegramMessageSender;
    @Autowired
    private ConversationStateService conversationStateService;
    public  GeoCommandHandler(TelegramMessageSender telegramMessageSender) {
        this.telegramMessageSender = telegramMessageSender;
    }
    @Override
    public List<String> getCommand() {
        return List.of("/localizacao");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        telegramMessageSender.sendMessage(chatId, "🏙️ Digite o nome da sua cidade (ex: São Paulo):");
        conversationStateService.definirEstado(chatId,"AGUARDANDO_CIDADE");
    }


}
