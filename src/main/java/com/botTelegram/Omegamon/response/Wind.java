package com.botTelegram.Omegamon.response;

public record Wind(
        Double speed,
        Integer deg,
        Double gust
) {
}