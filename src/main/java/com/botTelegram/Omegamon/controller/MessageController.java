package com.botTelegram.Omegamon.controller;

import com.botTelegram.Omegamon.Enum.Coins;
import com.botTelegram.Omegamon.response.*;
import com.botTelegram.Omegamon.service.Bot;
import com.botTelegram.Omegamon.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    private Long offset = 0L;
    private final String[] moedasString = new String[]{"dolar", "real", "euro", "iene", "yuan"};
    @Autowired
    private Bot bot;

    @Scheduled(fixedRate = 5000)
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

            for (String moeda : moedasString) {
                if (mensagem.equalsIgnoreCase(moeda)) {
                    bot.sendMessage(
                            update.message().chat().id(),
                            bot.getPrice(Coins.valueOf(moeda.toUpperCase()).getCoin())
                    );
                    offset = update.update_id() + 1;
                    break;
                }
            }

            if (mensagem.equalsIgnoreCase("dia") || mensagem.equalsIgnoreCase("tempo")) {

                WeatherResponse weather = bot.sendWeather(

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
                offset = update.update_id() + 1;
            }


        }
    }
}

