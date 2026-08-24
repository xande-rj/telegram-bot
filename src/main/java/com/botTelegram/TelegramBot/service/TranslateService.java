package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.response.Content;
import com.botTelegram.TelegramBot.response.GeminiRequest;
import com.botTelegram.TelegramBot.response.GeminiResponse;
import com.botTelegram.TelegramBot.response.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranslateService {

    private String apiKey;
    private final RestClient restClient;
    private final static String URL = "https://generativelanguage.googleapis.com";

    TranslateService(@Value("${gemini.api.key}") String apiKey, RestClient.Builder builder) {
        this.apiKey = apiKey;
        this.restClient = builder.baseUrl(URL).build();
    }

    public GeminiResponse translate(String idioma, String texto) {
        String prompt = String.format("Traduza o seguinte texto para %s. Responda APENAS com a tradução, sem explicações: \"%s\"",
                idioma,
                texto
        );

        GeminiRequest request = new GeminiRequest(
          List.of(
                  new Content(
                          "user",
                          List.of(
                                  new Part(prompt)
                          )
                  )
          )
        );


           GeminiResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-3.5-flash-lite:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);


        return response;
    }
}
