package com.botTelegram.Omegamon.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DollarResponse(
        @JsonProperty("USDBRL")
        UsdBrl  usdBrl

) {
}