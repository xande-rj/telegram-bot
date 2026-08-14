package com.botTelegram.TelegramBot.response;

public record Wind(
        Double speed,
        Integer deg,
        Double gust
) {
}