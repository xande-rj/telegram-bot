package com.botTelegram.TelegramBot.response;

public record Sys(
        String country,
        Long sunrise,
        Long sunset
) {
}