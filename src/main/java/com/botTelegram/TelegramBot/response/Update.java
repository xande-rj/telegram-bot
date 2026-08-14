package com.botTelegram.TelegramBot.response;

public record Update(
        Long update_id,
        Message message
) {}