package com.botTelegram.Omegamon.service;

import com.botTelegram.Omegamon.Enum.Coins;
import com.botTelegram.Omegamon.response.CurrencyApiResponse;

import com.botTelegram.Omegamon.response.CurrencyResponse;
import com.botTelegram.Omegamon.response.TelegramResponse;

import com.botTelegram.Omegamon.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class Bot {

    private final RestClient restClient;
    private final String chatId;

    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5";


    private final PriceService priceService;
    private final WeatherService weatherService;

    public Bot(
            RestClient.Builder builder,
            @Value("${telegram.token}") String token,
            @Value("${chat.id}") String chatId,
            PriceService priceService,
            WeatherService weatherService
    ) {
        this.priceService = priceService;
        this.weatherService = weatherService;
        this.restClient = builder
                .baseUrl("https://api.telegram.org/bot" + token)
                .build();

        this.chatId = chatId;

    }

    public String getWeather() {

return weatherService.getWeather();
    }

public String getPrice(String moeda){
    return priceService.getPrice(moeda);
}

    public void verify(String message) {

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
