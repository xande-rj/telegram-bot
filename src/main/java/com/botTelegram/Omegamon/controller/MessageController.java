package com.botTelegram.Omegamon.controller;

import com.botTelegram.Omegamon.service.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping
    public void verifyBot() {
        bot.verify("Ola verificando Bot no DATA: " + LocalDateTime.now().toString() + ", " + UUID.randomUUID().toString());
    }


}
