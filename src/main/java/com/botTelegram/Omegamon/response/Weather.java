package com.botTelegram.Omegamon.response;

public record Weather(
        Integer id,
        String main,
        String description,
        String icon
) {
}