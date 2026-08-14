package com.botTelegram.TelegramBot.controller;

import com.botTelegram.TelegramBot.service.BotService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;



@Component
public class MessageController implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    public MessageController(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }



    @Autowired
    private BotService bot;

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println("Mensagem : " + update.getMessage().getText());

            String message_text = update.getMessage().getText();

            long chat_id = update.getMessage().getChatId();

            if (message_text.equalsIgnoreCase("oi")) {
                helloWorld(chat_id);
            }


        }
    }

    private void helloWorld(long chat_id) {
        try {
            SendMessage message = bot.sendMessage(
                    chat_id,
                    "Olá! Como posso ajudar?"
            );
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    private void coins(){
//        for (String moeda : moedasString) {
//                try {
//                    if (mensagem.equalsIgnoreCase(moeda)) {
//                        bot.sendMessage(
//                                update.message().chat().id(),
//                                bot.getPrice(Coins.valueOf(moeda.toUpperCase()).getCoin())
//                        );
//                        offset = update.update_id() + 1;
//                        break;
//                    }
//                }catch (IllegalArgumentException e) {
//                    System.out.println(e.getMessage());
//                }
//
//            }
//    }
}
//    @Scheduled(fixedRate = 5000)
//    public void viewerMessage() {
//
//
//
//            for (String moeda : moedasString) {
//                try {
//                    if (mensagem.equalsIgnoreCase(moeda)) {
//                        bot.sendMessage(
//                                update.message().chat().id(),
//                                bot.getPrice(Coins.valueOf(moeda.toUpperCase()).getCoin())
//                        );
//                        offset = update.update_id() + 1;
//                        break;
//                    }
//                }catch (IllegalArgumentException e) {
//                    System.out.println(e.getMessage());
//                }
//
//            }
//
//            if (mensagem.equalsIgnoreCase("dia") || mensagem.equalsIgnoreCase("tempo")) {
//                bot.sendMessage(
//                        update.message().chat().id(),
//                        bot.getWeather()
//                );
//                offset = update.update_id() + 1;
//            }
//
//
//        }
//    }





