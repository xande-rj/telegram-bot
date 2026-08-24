package com.botTelegram.TelegramBot.response;

import java.util.List;

public record Content(
        String role,
        List<Part> parts
) {
}