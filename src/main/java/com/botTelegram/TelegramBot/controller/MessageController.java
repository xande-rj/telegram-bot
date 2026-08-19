package com.botTelegram.TelegramBot.controller;

import com.botTelegram.TelegramBot.Enum.Coins;
import com.botTelegram.TelegramBot.entity.Note;
import com.botTelegram.TelegramBot.response.NewsResponse.ArticleResponse;
import com.botTelegram.TelegramBot.service.BotService;

import com.botTelegram.TelegramBot.service.ConversationStateService;
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
import java.util.Optional;


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
    private ConversationStateService conversationStateService;
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

            long chat_id = update.getMessage().getChatId();
            Optional<String> estado = conversationStateService.getEstado(chat_id);

            if (estado.isPresent() && estado.get().equals("AGUARDANDO_TEXTO_NOTA")) {
                String text = update.getMessage().getText();

                bot.saveNote(text, chat_id);
                conversationStateService.limparEstado(chat_id);
                try {
                    telegramClient.execute(SendMessage.builder()
                            .chatId(chat_id)
                            .text("✅ Nota salva!")
                            .build());
                    return;
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                // não processa como comando normal
            }
            String message_text = update.getMessage().getText().split("/")[1];
            System.out.println("Mensagem : " + message_text);

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
        String callBack = update.getData();
        Long chatId = update.getMessage().getChatId();
        if (callBack.equalsIgnoreCase("save")) {
            conversationStateService.definirEstado(chatId, "AGUARDANDO_TEXTO_NOTA");
            try {

                telegramClient.execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("📝 Digite o texto da sua nota:")
                        .build()
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (callBack.equalsIgnoreCase("get")) {

            List<Note> notes = bot.getNotes(chatId);
            StringBuilder mensagem = new StringBuilder("""
                    📰 *Notas*
                    
                    """);
            for (int i = 0; i < notes.size(); i++) {

                Note note = notes.get(i);

                mensagem.append("""
                        - *%d*
                        
                        📰 texto: %s
                        
                        
                        """.formatted(
                        i + 1,
                        note.getText()
                ));
            }
            System.out.println("Mensagem de Call Back : " + notes);
            try {

                telegramClient.execute(SendMessage.builder()
                        .chatId(chatId)
                        .text("📝")
                                .text(mensagem.toString())
                        .build()
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else if (callBack.equalsIgnoreCase("delete")) {

        }
    }

    public void notes(long chat_id) {
        try {
            InlineKeyboardMarkup markup = bot.getNoteMarkup();
            SendMessage message = bot.sendMarkup(chat_id, "Notas: ", markup);
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






