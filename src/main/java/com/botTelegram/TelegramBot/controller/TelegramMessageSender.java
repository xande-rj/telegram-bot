package com.botTelegram.TelegramBot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class TelegramMessageSender {
    private final TelegramClient telegramClient;

    public TelegramMessageSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    public void sendMessage(Long chat_id, String text) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chat_id)
                    .text(text)
                    .build());
        } catch (TelegramApiRequestException e) {
            errorTreatment(chat_id, e);
        } catch (TelegramApiException e) {
           log.error("Erro inesperado ao enviar mensagem para chatId={}",chat_id,e);
        }
    }

    private void errorTreatment(Long chat_id, TelegramApiRequestException e) {
        Integer code = e.getErrorCode();
        if (code != null && code == 403) {
            log.warn("Usuário {} bloqueou o bot. Ignorando.", chat_id);
            return;
        }
        if (code != null && code == 429) {
            log.warn("Rate limit do Telegram atingido para chatId={}", chat_id);
            return;
        }
        log.error("Erro da API do Telegram (code={}) para chatId={}", code, chat_id);
    }
}
