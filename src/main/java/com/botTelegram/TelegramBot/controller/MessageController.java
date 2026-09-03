package com.botTelegram.TelegramBot.controller;

import com.botTelegram.TelegramBot.Enum.Coins;
import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.entity.User;
import com.botTelegram.TelegramBot.exception.BotUserException;
import com.botTelegram.TelegramBot.repository.UserRepository;
import com.botTelegram.TelegramBot.service.BotService;

import com.botTelegram.TelegramBot.service.ConversationStateService;
import com.botTelegram.TelegramBot.service.RateLimiteService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.longpolling.exceptions.TelegramApiErrorResponseException;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Component
public class MessageController implements LongPollingSingleThreadUpdateConsumer {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final TelegramMessageSender telegramMessageSender;
    private final Map<String, BotCommandHandler> handlers;

    public MessageController(
                             TelegramMessageSender telegramMessageSender,
                             List<BotCommandHandler> handlerList) {

        Map<String, BotCommandHandler> handlerMap = new HashMap<>();
        for (BotCommandHandler handler : handlerList) {
            for (String command : handler.getCommand()) {
                handlerMap.put(command, handler);
            }
        }
        this.handlers = handlerMap;
        this.telegramMessageSender = telegramMessageSender;
    }



    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        Long chatId = update.getMessage().getChatId();
        try {

            processarComando(update, chatId);

        } catch (BotUserException e) {
            telegramMessageSender.sendMessage(chatId, e.getMessage());

        } catch (Exception e) {
            log.error("Erro não tratado processando update de chatId={}", chatId, e);
            telegramMessageSender.sendMessage(chatId, "⚠️ Ocorreu um erro inesperado. Tente novamente.");
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


