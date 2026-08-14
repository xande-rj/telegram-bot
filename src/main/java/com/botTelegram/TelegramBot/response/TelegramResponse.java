package com.botTelegram.TelegramBot.response;

import java.util.List;

public record TelegramResponse(
        boolean ok,
        List<Update> result) {
}
