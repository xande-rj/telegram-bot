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


            long chat_id = update.getMessage().getChatId();
            Optional<String> estado = conversationStateService.getEstado(chat_id);

            if (estado.isPresent() && estado.get().equals("AGUARDANDO_TEXTO_NOTA")) {
                String text = update.getMessage().getText();
                bot.saveNote(text, chat_id);
                conversationStateService.limparEstado(chat_id);
                execute(chat_id,"✅ Nota salva!");
            } else if (estado.isPresent() && estado.get().equals("AGUARDANDO_NUMERO_NOTA")) {
                String text = update.getMessage().getText();
                bot.deleteNote(text,chat_id);
                conversationStateService.limparEstado(chat_id);
                execute(chat_id,"✅ Nota Deletada!");

            }
            if (update.getMessage().getText().contains("/")) {
                String message_text = update.getMessage().getText().split("/")[1];

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
    }

    public void onUpdateReceived(CallbackQuery update) {
        String callBack = update.getData();
        Long chatId = update.getMessage().getChatId();

        if (callBack.equalsIgnoreCase("save")) {
            conversationStateService.definirEstado(chatId, "AGUARDANDO_TEXTO_NOTA");
            execute(chatId,"📝 Digite o texto da sua nota:");

        } else if (callBack.equalsIgnoreCase("get")) {
            execute(chatId,bot.getNotes(chatId));

        } else if (callBack.equalsIgnoreCase("delete")) {
            conversationStateService.definirEstado(chatId, "AGUARDANDO_NUMERO_NOTA");
            execute(chatId,"📝 Digite o numero da nota que deseja deletar:");
            execute(chatId,bot.getNotes(chatId));

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
        execute(chat_id, mensagem);

    }

    private void news(long chat_id) {
        execute(
                chat_id,
                bot.getNews()
        );
    }

    private void helloWorld(long chat_id) {
        execute(
                chat_id,
                "Olá! Como posso ajudar?"
        );
    }

    private void weather(long chat_id) {
        execute(
                chat_id,
                bot.getWeather()
        );
    }

    private void coins(long chat_id, String moeda) {
        execute(
                chat_id,
                bot.getPrice(Coins.valueOf(moeda.toUpperCase()).getCoin())
        );
    }

    private void execute(Long chat_id, String text) {
        try {
            SendMessage message = bot.sendMessage(
                    chat_id,
                    text
            );
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}


