package com.botTelegram.TelegramBot.controller;

import com.botTelegram.TelegramBot.Enum.Coins;
import com.botTelegram.TelegramBot.entity.User;
import com.botTelegram.TelegramBot.exception.BotUserException;
import com.botTelegram.TelegramBot.repository.UserRepository;
import com.botTelegram.TelegramBot.service.BotService;

import com.botTelegram.TelegramBot.service.ConversationStateService;
import com.botTelegram.TelegramBot.service.RateLimiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.longpolling.exceptions.TelegramApiErrorResponseException;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;


@Slf4j
@Component
public class MessageController extends DefaultLongPollingUpdateConsumer {
    private final TelegramClient telegramClient;
    private final String[] MOEDAS_STRING = new String[]{"dolar", "euro", "iene", "yuan"};
    private final RateLimiteService rateLimiteService;
    private final String MENSAGEM_LIMITE = "⏳ Você atingiu o limite de comandos. Tente novamente em alguns segundos.";

    private final TelegramMessageSender telegramMessageSender;
    private final UserRepository  userRepository;

    public MessageController(TelegramClient telegramClient, RateLimiteService rateLimiteService, TelegramMessageSender telegramMessageSender, UserRepository userRepository) {
        this.telegramClient = telegramClient;
        this.rateLimiteService = rateLimiteService;
        this.telegramMessageSender = telegramMessageSender;
        this.userRepository = userRepository;

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

                } else if (message_text.equalsIgnoreCase("resumodiario")) {
                    resumoDiario(chat_id);

                }
                for (String moeda : MOEDAS_STRING) {
                    if (message_text.equalsIgnoreCase(moeda)) {
                        coins(chat_id, moeda);
                    }
                }


            }
        }
    }
    private void resumoDiario(long chat_id) {
        if(userRepository.findById(chat_id).isPresent()) {
            User user = userRepository.findById(chat_id).get();
            user.setResumoDiarioAtivo(!user.isResumoDiarioAtivo());
            userRepository.save(user);
            String status = user.isResumoDiarioAtivo() ? "ativado ✅" : "desativado ❌";
            telegramMessageSender.sendMessage(chat_id,status);
        }
        else {
            User user = new User();
            user.setChatId(chat_id);
            user.setResumoDiarioAtivo(true);
            userRepository.save(user);
            String status = user.isResumoDiarioAtivo() ? "ativado ✅" : "desativado ❌";
            telegramMessageSender.sendMessage(chat_id, status);
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
            log.error("Erro inesperado ao traduzir", e);
            throw new BotUserException("⚠\uFE0F Algo deu errado. Tente novamente.");
        }


    }

    public void notes(long chat_id) {
        try {
            InlineKeyboardMarkup markup = bot.getNoteMarkup();
            SendMessage message = bot.sendMarkup(chat_id, "Notas: ", markup);
            telegramClient.execute(message);
        } catch (TelegramApiErrorResponseException e) {
            log.error("Erro inesperado na resposta da api", e);
            throw new BotUserException("⚠\uFE0F Algo deu errado. Tente novamente.");
        } catch (TelegramApiException e) {
            log.error("Erro inesperado na api", e);
            throw new BotUserException("⚠\uFE0F Algo deu errado. Tente novamente.");
        }
    }

    public void avisarLimite(long chat_id) {
        telegramMessageSender.sendMessage(chat_id, MENSAGEM_LIMITE);

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


