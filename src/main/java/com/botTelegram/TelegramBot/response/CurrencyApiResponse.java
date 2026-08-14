package com.botTelegram.TelegramBot.response;

import java.util.Map;

public record CurrencyApiResponse(
        Map<String, CurrencyResponse> currencies

) {
}