package com.botTelegram.TelegramBot.response.CurrencyResponse;

import java.util.Map;

public record CurrencyApiResponse(
        Map<String, CurrencyResponse> currencies

) {
}