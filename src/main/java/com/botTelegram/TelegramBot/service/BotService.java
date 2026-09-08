package com.botTelegram.TelegramBot.service;


import com.botTelegram.TelegramBot.entity.Note;
import com.botTelegram.TelegramBot.entity.User;
import com.botTelegram.TelegramBot.repository.UserRepository;
import com.botTelegram.TelegramBot.response.GeminiResponse.GeminiResponse;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Map;

@Service
public class BotService {


    private final WeatherService weatherService;

    private final GeoService geoService;
    private final UserRepository userRepository;

    public BotService(
            WeatherService weatherService,
            GeoService geoService,
            UserRepository userRepository
    ) {
        this.weatherService = weatherService;
        this.geoService = geoService;
        this.userRepository = userRepository;
    }

//    public String getWeather() {
//        return weatherService.getWeather();
//    }



}
