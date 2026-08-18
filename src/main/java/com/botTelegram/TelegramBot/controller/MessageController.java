package com.botTelegram.TelegramBot.controller;

import com.botTelegram.TelegramBot.Enum.Coins;
import com.botTelegram.TelegramBot.service.BotService;

import com.botTelegram.TelegramBot.service.RateLimiteService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;


@Component
public class MessageController extends DefaultLongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String[] MOEDAS_STRING = new String[]{"dolar", "euro", "iene", "yuan"};
    private final RateLimiteService rateLimiteService;

    public MessageController(TelegramClient telegramClient, RateLimiteService rateLimiteService) {
        this.telegramClient = telegramClient;
        this.rateLimiteService = rateLimiteService;

    }


    @Autowired
    private BotService bot;

    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            onUpdateReceived(update.getCallbackQuery());
            return;
        }
        if (update.hasMessage() && update.getMessage().hasText()) {

            if (update.getMessage().getText().length() <= 2) {
                return;
            }
            String message_text = update.getMessage().getText().split("/")[1];
            System.out.println("Mensagem : " + message_text);
            long chat_id = update.getMessage().getChatId();
            if (!rateLimiteService.permitir(chat_id)) {
                avisarLimite(chat_id);
                return;
            }
            if (message_text.equalsIgnoreCase("notas")) {
                notes(chat_id);
            }
            if (message_text.equalsIgnoreCase("ola")) {
                helloWorld(chat_id);
            }
            if (message_text.equalsIgnoreCase("Tempo")) {
                weather(chat_id);
            }
            for (String moeda : MOEDAS_STRING) {
                if (message_text.equalsIgnoreCase(moeda)) {
                    coins(chat_id, moeda);
                }
            }
            if (message_text.equalsIgnoreCase("noticias")) {
                news(chat_id);
            }
        }
    }

    public void onUpdateReceived(CallbackQuery update) {
        System.out.println("Mensagem de Call Back : " + update.getData());
    }

    public void notes(long chat_id) {
        try {
            InlineKeyboardMarkup markup = bot.getNoteMarkup();
            SendMessage message = bot.sendMarkup(chat_id, "Notas: ",markup);
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void avisarLimite(long chat_id) {
        String mensagem =
                "⏳ Você atingiu o limite de comandos. Tente novamente em alguns segundos.";
        try {
            SendMessage message = bot.sendMessage(chat_id, mensagem);
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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

    private void coins(long chat_id, String moeda) {
        try {
            SendMessage message = bot.sendMessage(
                    chat_id,
                    bot.getPrice(Coins.valueOf(moeda.toUpperCase()).getCoin())
            );
            telegramClient.execute(message); // Sending our message object to user

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}






