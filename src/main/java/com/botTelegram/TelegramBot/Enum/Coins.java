package com.botTelegram.TelegramBot.Enum;

public enum Coins {
    IENE("JPY"),
    REAL("BRL"),
    DOLAR("USD"),
    YUAN("CNY"),
    EURO("EUR")
    ;

    private String coin;

    Coins(String coin) {
        this.coin = coin;
    }
    public String getCoin() {
        return coin;
    }
}
