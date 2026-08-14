package com.botTelegram.TelegramBot.run;

import com.botTelegram.TelegramBot.command.TelegramCommandRegistrar;
import com.botTelegram.TelegramBot.controller.MessageController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class TelegramBotRunner implements CommandLineRunner {
    @Value("${telegram.token}")
    private String token;
    private final MessageController messageController;
    private final TelegramClient telegramClient;
    private final TelegramCommandRegistrar command;

    public TelegramBotRunner(
            @Value("${telegram.token}") String token,
            MessageController messageController,
            TelegramClient telegramClient,
            TelegramCommandRegistrar telegramCommandRegistrar

    ) {
        this.token = token;
        this.messageController = messageController;
        this.telegramClient = telegramClient;
        this.command = telegramCommandRegistrar;
    }

    @Override
    public void run(String... args) throws Exception {
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(token, messageController);
            command.registerCommands(telegramClient);

            System.out.println("rodando Bot...");
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
