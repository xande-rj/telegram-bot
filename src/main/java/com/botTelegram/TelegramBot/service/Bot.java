package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.response.TelegramResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class Bot {

    private final RestClient restClient;
    private final PriceService priceService;
    private final WeatherService weatherService;

    public Bot(
            RestClient.Builder builder,
            @Value("${telegram.token}") String token,
            PriceService priceService,
            WeatherService weatherService
    ) {
        this.priceService = priceService;
        this.weatherService = weatherService;
        this.restClient = builder
                .baseUrl("https://api.telegram.org/bot" + token)
                .build();
    }

    public String getWeather() {
        return weatherService.getWeather();
    }

    public String getPrice(String moeda) {
        return priceService.getPrice(moeda);
    }


    public SendMessage sendMessage(Long chatId, String message) {
        return   SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(message)
                .build();

    }


}
