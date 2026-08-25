package com.botTelegram.TelegramBot.response.GeminiResponse;

import java.util.List;

public record GeminiResponse(
        List<Candidate> candidates
) {
}