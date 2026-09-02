package com.botTelegram.TelegramBot.response.GeoResponse;

public record GeocodingResponse(
        String name,
        Double lat,
        Double lon,
        String country,
        String state
) {
}