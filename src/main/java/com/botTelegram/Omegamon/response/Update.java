package com.botTelegram.Omegamon.response;

public record Update(
        Long update_id,
        Message message
) {}