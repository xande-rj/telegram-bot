package com.botTelegram.TelegramBot.response;

import java.util.List;

public record GeminiResponse(
        List<Candidate> candidates
) {
}