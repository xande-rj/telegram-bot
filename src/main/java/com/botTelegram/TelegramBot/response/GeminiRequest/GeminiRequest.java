package com.botTelegram.TelegramBot.response.GeminiRequest;

import java.util.List;

public record GeminiRequest(
        List<Content> contents
) {
}