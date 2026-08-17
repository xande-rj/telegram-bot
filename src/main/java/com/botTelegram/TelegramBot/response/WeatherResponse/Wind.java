package com.botTelegram.TelegramBot.response.WeatherResponse;

public record Wind(
        Double speed,
        Integer deg,
        Double gust
) {
}