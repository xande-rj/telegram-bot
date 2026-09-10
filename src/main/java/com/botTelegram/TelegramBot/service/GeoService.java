package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.entity.User;
import com.botTelegram.TelegramBot.exception.BotUserException;
import com.botTelegram.TelegramBot.repository.UserRepository;
import com.botTelegram.TelegramBot.response.GeoResponse.GeocodingResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GeoService {
    private final String GEO_URL;
    private final ClientService clientService;
    private final String appid;
    private final UserRepository userRepository;


    public GeoService(
            @Value("${geo_url}") String geoURL,
            @Value("${weather.token}") String appid,
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
        this.GEO_URL = geoURL;
        this.clientService = new ClientService(GEO_URL);
        this.appid = appid;

    }

    public boolean saveGeo(Long chatId, Update update) {
        Map<String, Double> response = getLocation(update.getMessage().getText());
        User user = this.userRepository.findById(chatId).orElseGet(() -> {
            User novo = new User();
            novo.setChatId(chatId);
            return novo;
        });
        user.setLatitude(response.get("latitude"));
        user.setLongitude(response.get("longitude"));
        userRepository.save(user);
        return true;
    }

    private Map<String, Double> getLocation(String cidade) {
        Map<String, Double> result = new HashMap<>();
        GeocodingResponse[] geo = this.clientService.getRestClient().get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", cidade)
                        .queryParam("appid", appid)
                        .build()).retrieve().body(GeocodingResponse[].class);
        if (geo.length == 0) {
            throw new BotUserException("Location not found");
        }
        GeocodingResponse response = geo[0];
        result.put("latitude", response.lat());
        result.put("longitude", response.lon());
        return result;
    }
}
