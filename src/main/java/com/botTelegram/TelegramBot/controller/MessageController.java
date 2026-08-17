package com.botTelegram.TelegramBot.controller;

import com.botTelegram.TelegramBot.Enum.Coins;
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
    private final String[] MOEDAS_STRING = new String[]{"dolar", "euro", "iene", "yuan"};

    public MessageController(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }


    @Autowired
    private BotService bot;

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {


            String message_text = update.getMessage().getText().split("/")[1];
            System.out.println("Mensagem : " + message_text);
            long chat_id = update.getMessage().getChatId();

            if (message_text.equalsIgnoreCase("ola")) {
                helloWorld(chat_id);
            }
            if (message_text.equalsIgnoreCase("Tempo")) {
                weather(chat_id);
            }
            for (String moeda : MOEDAS_STRING) {
                if (message_text.equalsIgnoreCase(moeda)) {
                    coins(chat_id,moeda);
                }
            }
            if (message_text.equalsIgnoreCase("noticias")) {
                news(chat_id);
            }
        }
    }
    private void news(long chat_id) {

        try {
            SendMessage message = bot.sendMessage(
                    chat_id,
                    bot.getNews()
            );
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
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

    private void weather(long chat_id) {
        try {
            SendMessage message = bot.sendMessage(
                    chat_id,
                    bot.getWeather()
            );
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void coins(long chat_id,String moeda) {
                try {
                    SendMessage message = bot.sendMessage(
                                chat_id,
                                bot.getPrice(Coins.valueOf(moeda.toUpperCase()).getCoin())
                        );
                    telegramClient.execute(message); // Sending our message object to user

                }catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
    }






