package com.botTelegram.TelegramBot.service;


import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class BotService {


    private final PriceService priceService;
    private final WeatherService weatherService;
    private final NewsService newsService;

    public BotService(
            PriceService priceService,
            WeatherService weatherService,
            NewsService newsService
    ) {
        this.priceService = priceService;
        this.weatherService = weatherService;
        this.newsService = newsService;
    }

    public String getWeather() {
        return weatherService.getWeather();
    }

    public String getPrice(String moeda) {
        return priceService.getPrice(moeda);
    }


    public SendMessage sendMessage(Long chatId, String message) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(message)
                .build();

    }


    public String getNews() {
         return newsService.getNews();
    }
}
