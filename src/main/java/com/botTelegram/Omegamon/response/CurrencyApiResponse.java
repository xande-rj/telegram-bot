package com.botTelegram.Omegamon.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record CurrencyApiResponse(
        Map<String, CurrencyResponse> currencies

) {
}