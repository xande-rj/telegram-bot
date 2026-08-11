package com.botTelegram.Omegamon.controller;

import com.botTelegram.Omegamon.response.TelegramResponse;
import com.botTelegram.Omegamon.response.Update;
import com.botTelegram.Omegamon.service.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/message")
public class MessageController {


    @Autowired
    private Bot bot;

    public void verifyBot() {
        bot.verify("Ola verificando Bot no DATA: " + LocalDateTime.now().toString() + ", " + UUID.randomUUID().toString());
    }

    @Scheduled(fixedRate = 15000)
    public void viewerMessage() {
        TelegramResponse response = bot.getMessages();
        for (Update update : response.result()) {
            if (update.message() == null) {
                continue;
            }
            String mensagem = update.message().text();

            System.out.println("Recebi: " + mensagem);

            if (mensagem.equalsIgnoreCase("oi")) {

                bot.sendMessage(
                        update.message().chat().id(),
                        "Olá! Como posso ajudar?"
                );
            }
        }


    }
}
