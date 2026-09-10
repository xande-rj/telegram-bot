package com.botTelegram.TelegramBot.controller;

import com.botTelegram.TelegramBot.Enum.Coins;
import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.entity.User;
import com.botTelegram.TelegramBot.exception.BotUserException;


import com.botTelegram.TelegramBot.service.ConversationStateService;
import com.botTelegram.TelegramBot.service.GeoService;
import com.botTelegram.TelegramBot.service.NoteService;

import com.botTelegram.TelegramBot.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Component;

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class MessageController implements LongPollingSingleThreadUpdateConsumer {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final TelegramMessageSender telegramMessageSender;
    private final Map<String, BotCommandHandler> handlers;
    private final NoteService noteService;
    private final GeoService geoService;
private final WeatherService weatherService;
    @Autowired
    private ConversationStateService conversationStateService;

    public MessageController(
            TelegramMessageSender telegramMessageSender,
            List<BotCommandHandler> handlerList,
            NoteService noteService,
            GeoService geoService,
            WeatherService weatherService
    ) {
        this.noteService = noteService;
        Map<String, BotCommandHandler> handlerMap = new HashMap<>();
        for (BotCommandHandler handler : handlerList) {
            for (String command : handler.getCommand()) {
                handlerMap.put(command, handler);
            }
        }
        this.handlers = handlerMap;
        this.telegramMessageSender = telegramMessageSender;
        this.geoService = geoService;
        this.weatherService = weatherService;
    }


    @Override
    public void consume(Update update) {
        if (update.hasCallbackQuery()) {
            onUpdateReceived(update.getCallbackQuery());
            return;
        }
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        try {
            Optional<String> estado = conversationStateService.getEstado(chatId);
            if (estado.isPresent() && estado.get().equals("AGUARDANDO_TEXTO_NOTA")) {
                saveNote(chatId, update);
                return;
            } else if (estado.isPresent() && estado.get().equals("AGUARDANDO_NUMERO_NOTA")) {
                deleteNote(chatId, update);
                return;
            } else if (estado.isPresent() && estado.get().equals("AGUARDANDO_CIDADE")) {
                saveGeo(chatId,update);
                conversationStateService.limparEstado(chatId);
                return;
            }
            processarComando(update, chatId);

        } catch (BotUserException e) {
            telegramMessageSender.sendMessage(chatId, e.getMessage());

        } catch (Exception e) {
            log.error("Erro não tratado processando update de chatId={}", chatId, e);
            telegramMessageSender.sendMessage(chatId, "⚠️ Ocorreu um erro inesperado. Tente novamente.");
        }

    }

    private void saveGeo(Long chatId, Update update) {
        if(this.geoService.saveGeo(chatId,update)){
            telegramMessageSender.sendMessage(chatId, "✅ Criado com sucesso!");
            telegramMessageSender.sendMessage(chatId, this.weatherService.getWeather(chatId));
        }
        else {
            telegramMessageSender.sendMessage(chatId, "❌ Erro ao cadastra localizacao. Tente novamente em alguns segundos.!");
        }

    }

    public void saveNote(Long chatId, Update update) {
        String text = update.getMessage().getText();
        noteService.save(text, chatId);
        conversationStateService.limparEstado(chatId);
        telegramMessageSender.sendMessage(chatId, "✅ Nota salva!");

    }

    public void deleteNote(Long chatId, Update update) {
        String text = update.getMessage().getText();
        noteService.delete(text, chatId);
        conversationStateService.limparEstado(chatId);
        telegramMessageSender.sendMessage(chatId, "✅ Nota deletada!");
    }

    public void onUpdateReceived(CallbackQuery update) {
        String callBack = update.getData();
        Long chatId = update.getMessage().getChatId();

        if (callBack.equalsIgnoreCase("save")) {
            conversationStateService.definirEstado(chatId, "AGUARDANDO_TEXTO_NOTA");
            telegramMessageSender.sendMessage(chatId, "📝 Digite o text  o da sua nota:");

        } else if (callBack.equalsIgnoreCase("get")) {
            telegramMessageSender.sendMessage(chatId, noteService.findAll(chatId));

        } else if (callBack.equalsIgnoreCase("delete")) {
            conversationStateService.definirEstado(chatId, "AGUARDANDO_NUMERO_NOTA");
            telegramMessageSender.sendMessage(chatId, noteService.findAll(chatId));

        }
    }

    private void processarComando(Update update, Long chatId) throws Exception {
        String texto = update.getMessage().getText();
        String comando = texto.split(" ")[0];
        BotCommandHandler handler = handlers.get(comando);


        if (handler == null) {
            telegramMessageSender.sendMessage(chatId, "Comando não reconhecido. Use /start pra ver as opções.");
            return;
        }

        handler.handle(update, null);
        
    }

}


