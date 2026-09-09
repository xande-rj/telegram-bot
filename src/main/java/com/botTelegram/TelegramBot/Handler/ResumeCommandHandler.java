package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResumeCommandHandler implements BotCommandHandler {

    private final TelegramMessageSender telegramMessageSender;
    private final List<String> commands = new ArrayList<>();
    public ResumeCommandHandler(TelegramMessageSender telegramMessageSender,List<BotCommandHandler> handlerList) {
        this.telegramMessageSender = telegramMessageSender;
        for (BotCommandHandler handler : handlerList) {
            for (String command : handler.getCommand()) {
                commands.add(command);
            }
            }
    }
    @Override
    public List<String> getCommand() {
        return List.of("/start");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        StringBuilder mensagem = new StringBuilder("""
                O bot tem Opcoes :
                """);
        for (String handler : commands) {
            mensagem.append("""
                    
                    """);
            mensagem.append(handler);
        }
        this.telegramMessageSender.sendMessage(update.getMessage().getChatId(),  mensagem.toString());

    }
}
