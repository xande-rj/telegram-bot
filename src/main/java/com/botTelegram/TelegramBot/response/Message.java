package com.botTelegram.TelegramBot.response;

public record Message(
        Chat chat,
        String text
) {}