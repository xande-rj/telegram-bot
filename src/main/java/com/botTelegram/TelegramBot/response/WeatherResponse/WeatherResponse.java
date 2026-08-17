package com.botTelegram.TelegramBot.response.WeatherResponse;

import com.botTelegram.TelegramBot.response.CurrencyResponse.Coord;

import java.util.List;

public record WeatherResponse(
        Coord coord,
        List<Weather> weather,
        String base,
        Main main,
        Integer visibility,
        Wind wind,
        Rain rain,
        Clouds clouds,
        Long dt,
        Sys sys,
        Integer timezone,
        Long id,
        String name,
        Integer cod
) {
}