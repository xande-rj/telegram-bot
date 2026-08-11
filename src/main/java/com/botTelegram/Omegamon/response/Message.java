package com.botTelegram.Omegamon.response;

public record Message(
        Chat chat,
        String text
) {}