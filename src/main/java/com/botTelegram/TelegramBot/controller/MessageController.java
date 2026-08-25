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
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
public class MessageController extends DefaultLongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String[] MOEDAS_STRING = new String[]{"dolar", "euro", "iene", "yuan"};
    private final RateLimiteService rateLimiteService;

    private final TelegramMessageSender telegramMessageSender;
    public MessageController(TelegramClient telegramClient, RateLimiteService rateLimiteService, TelegramMessageSender telegramMessageSender) {
        this.telegramClient = telegramClient;
        this.rateLimiteService = rateLimiteService;
        this.telegramMessageSender = telegramMessageSender;

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
                telegramMessageSender.sendMessage(chat_id, "✅ Nota salva!");
            } else if (estado.isPresent() && estado.get().equals("AGUARDANDO_NUMERO_NOTA")) {
                String text = update.getMessage().getText();
                boolean sucesso = bot.deleteNote(text, chat_id);
                if (sucesso) {
                    conversationStateService.limparEstado(chat_id);
                    telegramMessageSender.sendMessage(chat_id, "✅ Nota Deletada!");
                } else {
                    telegramMessageSender.sendMessage(chat_id, "⚠️ Número inválido. Digite o número correspondente à nota que deseja deletar:");
                }
            }

            if (update.getMessage().getText().contains("/")) {
                String message_text = update.getMessage().getText().split("/")[1];


                if (!rateLimiteService.permitir(chat_id)) {
                    avisarLimite(chat_id);
                    return;
                }
                if (message_text.equalsIgnoreCase("notas")) {
                    notes(chat_id);
                } else if (message_text.equalsIgnoreCase("ola")) {
                    helloWorld(chat_id);
                } else if (message_text.equalsIgnoreCase("tempo")) {
                    weather(chat_id);
                } else if (message_text.equalsIgnoreCase("noticias")) {
                    news(chat_id);
                } else if (message_text.contains("traduzir")) {
                    translate(chat_id, update.getMessage().getText());

                }
                for (String moeda : MOEDAS_STRING) {
                    if (message_text.equalsIgnoreCase(moeda)) {
                        coins(chat_id, moeda);
                    }
                }


            }
        }
    }

    public void onUpdateReceived(CallbackQuery update) {
        String callBack = update.getData();
        Long chatId = update.getMessage().getChatId();

        if (callBack.equalsIgnoreCase("save")) {
            conversationStateService.definirEstado(chatId, "AGUARDANDO_TEXTO_NOTA");
            telegramMessageSender.sendMessage(chatId, "📝 Digite o texto da sua nota:");

        } else if (callBack.equalsIgnoreCase("get")) {
            telegramMessageSender.sendMessage(chatId, bot.getNotes(chatId));

        } else if (callBack.equalsIgnoreCase("delete")) {
            conversationStateService.definirEstado(chatId, "AGUARDANDO_NUMERO_NOTA");
            telegramMessageSender.sendMessage(chatId, "📝 Digite o numero da nota que deseja deletar:");
            telegramMessageSender.sendMessage(chatId, bot.getNotes(chatId));

        }
    }

    public void translate(long chat_id, String text) {

        String textoCompleto = text.replaceFirst("/traduzir\\s*", "").trim();
        String mensagem =
                "Use assim: /traduzir <idioma> <texto>\nEx: /traduzir inglês Bom dia!";
        if (textoCompleto.isEmpty()) {
            telegramMessageSender.sendMessage(chat_id, mensagem);
        }
        String[] partes = textoCompleto.split(" ", 2);
        if (partes.length < 2) {
            telegramMessageSender.sendMessage(chat_id, mensagem);
        }
        String idioma = partes[0];
        String texto = partes[1];

        try {


            Message temp = telegramClient.execute(SendMessage.builder()
                    .chatId(chat_id)
                    .text("🌐 Traduzindo para " + idioma + "...")
                    .build());
            String traducao = bot.translate(idioma, texto);


            telegramClient.execute(EditMessageText.builder()
                    .chatId(chat_id)
                    .messageId(temp.getMessageId())
                    .text(traducao)
                    .build());

        } catch (TelegramApiException e) {
            e.printStackTrace();
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
        telegramMessageSender.sendMessage(chat_id, mensagem);

    }

    private void news(long chat_id) {
        telegramMessageSender.sendMessage(
                chat_id,
                bot.getNews()
        );
    }

    private void helloWorld(long chat_id) {
        telegramMessageSender.sendMessage(
                chat_id,
                "Olá! Como posso ajudar?"
        );
    }

    private void weather(long chat_id) {
        telegramMessageSender.sendMessage(
                chat_id,
                bot.getWeather()
        );
    }

    private void coins(long chat_id, String moeda) {
        telegramMessageSender.sendMessage(
                chat_id,
                bot.getPrice(Coins.valueOf(moeda.toUpperCase()).getCoin())
        );
    }

}


