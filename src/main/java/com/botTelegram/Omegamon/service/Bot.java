package com.botTelegram.Omegamon.service;

import com.botTelegram.Omegamon.response.TelegramResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class Bot {

    private final RestClient restClient;
    private final String chatId;

    public Bot(
            RestClient.Builder builder,
            @Value("${telegram.token}") String token,
            @Value("${chat.id}") String chatId
    ) {
        this.restClient = builder
                .baseUrl("https://api.telegram.org/bot" + token)
                .build();

        this.chatId = chatId;
    }

    public void verify(String message) {

        restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sendMessage")
                        .queryParam("chat_id", chatId)
                        .queryParam("text", message)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }
    public TelegramResponse getMessages() {

        return restClient
                .get()
                .uri("/getUpdates")
                .retrieve()
                .body(TelegramResponse .class);
    }
}
