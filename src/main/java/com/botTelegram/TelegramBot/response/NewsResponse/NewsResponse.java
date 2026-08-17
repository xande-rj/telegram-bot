package com.botTelegram.TelegramBot.response.NewsResponse;

import java.util.List;

public record NewsResponse(
        String status,
        Integer totalResults,
        List<ArticleResponse> articles
) {
}