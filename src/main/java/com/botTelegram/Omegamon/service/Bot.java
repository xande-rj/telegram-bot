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
    private final String lat;
    private final String lon;
    private final String appid;
    private final String WEATHER_URL = "https://api.openweathermap.org/data/2.5";

    private final PriceService priceService;

    public Bot(
            RestClient.Builder builder,
            @Value("${telegram.token}") String token,
            @Value("${chat.id}") String chatId,
            @Value("${latitude}") String lat,
            @Value("${longitude}") String lon,
            @Value("${appid}") String appid, PriceService priceService
    ) {
        this.priceService = priceService;
        this.restClient = builder
                .baseUrl("https://api.telegram.org/bot" + token)
                .build();

        this.chatId = chatId;
        this.lat = lat;
        this.lon = lon;
        this.appid = appid;
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

    public WeatherResponse sendWeather() {
        RestClient weatherClient = RestClient.builder()
                .baseUrl(WEATHER_URL)
                .build();
        return weatherClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", appid).build()
                ).retrieve()
                .body(WeatherResponse.class);


    }
}
