package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.service.NewsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class NewsCommandHandler implements BotCommandHandler {
    private final NewsService newsService;
    private final TelegramMessageSender telegramMessageSender;

    public NewsCommandHandler(NewsService newsService, TelegramMessageSender telegramMessageSender) {
        this.newsService = newsService;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    public String getCommand() {
        return "/noticias";
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String news = this.newsService.getNews();
        this.telegramMessageSender.sendMessage(chatId, news);

    }
}
