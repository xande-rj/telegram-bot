package com.botTelegram.TelegramBot.response;

public record Weather(
        Integer id,
        String main,
        String description,
        String icon
) {
}