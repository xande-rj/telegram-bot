package com.botTelegram.TelegramBot.response;

import java.util.List;

public record GeminiRequest(
        List<Content> contents
) {
}