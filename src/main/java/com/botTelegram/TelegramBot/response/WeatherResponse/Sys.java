package com.botTelegram.TelegramBot.response.WeatherResponse;

public record Sys(
        String country,
        Long sunrise,
        Long sunset
) {
}