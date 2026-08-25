package com.botTelegram.TelegramBot.response.GeminiRequest;

import java.util.List;

public record Content(
        String role,
        List<Part> parts
) {
}