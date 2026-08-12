package com.botTelegram.Omegamon.response;

public record UsdBrl(
        String code,
        String codein,
        String name,
        String high,
        String low,
        String varBid,
        String pctChange,
        String bid,
        String ask,
        String timestamp,
        String create_date
) {
}
