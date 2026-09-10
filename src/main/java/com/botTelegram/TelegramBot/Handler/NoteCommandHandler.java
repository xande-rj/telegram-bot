package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.service.NoteService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class NoteCommandHandler implements BotCommandHandler {
    private final TelegramMessageSender telegramMessageSender;
    private final NoteService noteService;

    public NoteCommandHandler(TelegramMessageSender telegramMessageSender, NoteService noteService) {
        this.telegramMessageSender = telegramMessageSender;
        this.noteService = noteService;
    }

    @Override
    public List<String> getCommand() {
        return List.of("/notas");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        InlineKeyboardMarkup markup= this.noteService.getNotes();
        sendMarkup(update.getMessage().getChatId(), "Notas :",markup);
    }

    private void sendMarkup(Long chatId, String message, InlineKeyboardMarkup markup) {
        this.telegramMessageSender.sendMarkupNote(chatId, message, markup);
    }

}
