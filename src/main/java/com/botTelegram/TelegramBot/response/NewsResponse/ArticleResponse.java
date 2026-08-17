package com.botTelegram.TelegramBot.response.NewsResponse;

import java.time.OffsetDateTime;

public record ArticleResponse(
        SourceResponse source,
        String author,
        String title,
        String description,
        String url,
        String urlToImage,
        OffsetDateTime publishedAt,
        String content
) {
}