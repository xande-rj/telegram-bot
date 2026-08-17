package com.botTelegram.TelegramBot.response.WeatherResponse;

public record Weather(
        Integer id,
        String main,
        String description,
        String icon
) {
}