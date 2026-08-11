package com.botTelegram.Omegamon.controller;

import com.botTelegram.Omegamon.response.TelegramResponse;
import com.botTelegram.Omegamon.response.Update;
import com.botTelegram.Omegamon.response.WeatherResponse;
import com.botTelegram.Omegamon.service.Bot;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/message")
public class MessageController {

    private Long offset = 0L;

    @Autowired
    private Bot bot;

    public void verifyBot() {
        bot.verify("Ola verificando Bot no DATA: " + LocalDateTime.now().toString() + ", " + UUID.randomUUID().toString());
    }


    @Scheduled(fixedRate = 1000)
    public void viewerMessage() {
        TelegramResponse response = bot.getMessages(offset);
        if (response == null || response.result() == null) {
            return;
        }
        for (Update update : response.result()) {

            if (update.message() == null) {
                offset = update.update_id() + 1;
                continue;
            }
            String mensagem = update.message().text();

            System.out.println("Mensagem recebida: " + mensagem);

            if (mensagem.equalsIgnoreCase("oi")) {

                bot.sendMessage(
                        update.message().chat().id(),
                        "Olá! Como posso ajudar?"
                );
            }
            if (mensagem.equalsIgnoreCase("dia")) {

                WeatherResponse weather = bot.sendWeather(
                        update.message().chat().id()
                );
                String men = """
                        🌎 Cidade: %s
                        🌡️ Temperatura: %.1f°C
                        🤔 Sensação: %.1f°C
                        💧 Umidade: %d%%
                        ☁️ Condição: %s
                        💨 Vento: %.1f m/s
                        """.formatted(
                        weather.name(),
                        weather.main().temp(),
                        weather.main().feels_like(),
                        weather.main().humidity(),
                        weather.weather().get(0).description(),
                        weather.wind().speed()
                );
                bot.sendMessage(
                        update.message().chat().id(),
                        men
                );
            }

            offset = update.update_id() + 1;
        }
    }
}

