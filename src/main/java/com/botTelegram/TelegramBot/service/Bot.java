package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.response.TelegramResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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


    public TelegramResponse getMessages(Long offset) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUpdates")
                        .queryParam("offset", offset)
                        .queryParam("timeout", 10).build())

                .retrieve()
                .body(TelegramResponse.class);


    }


    public void sendMessage(Long chatId, String message) {

        restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sendMessage")
                        .queryParam("chat_id", chatId)
                        .queryParam("text", message)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }


}
