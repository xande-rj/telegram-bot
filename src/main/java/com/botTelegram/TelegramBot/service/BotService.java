package com.botTelegram.TelegramBot.service;


import com.botTelegram.TelegramBot.entity.Note;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Service
public class BotService {


    private final PriceService priceService;
    private final WeatherService weatherService;
    private final NewsService newsService;
    private final NoteService noteService;

    public BotService(
            PriceService priceService,
            WeatherService weatherService,
            NewsService newsService,
            NoteService noteService
    ) {
        this.priceService = priceService;
        this.weatherService = weatherService;
        this.newsService = newsService;
        this.noteService = noteService;
    }

    public String getWeather() {
        return weatherService.getWeather();
    }

    public String getPrice(String moeda) {
        return priceService.getPrice(moeda);
    }

    public String getNews() {
        return newsService.getNews();
    }

    public InlineKeyboardMarkup getNoteMarkup(){
        return noteService.getNotes();
    }
    public Note saveNote(String text,Long chatId){
        return noteService.save(text,chatId);
    }
    public String getNotes(Long chatId){
        List<Note> notes = noteService.findAll(chatId);
        StringBuilder mensagem = new StringBuilder("""
                    Notas
                    
                    """);
        for (int i = 0; i < notes.size(); i++) {

            Note note = notes.get(i);

            mensagem.append("""
                        - %d
                        📰 texto: %s

                        """.formatted(
                    i + 1,
                    note.getText()
            ));
        }
        return mensagem.toString();
    }
    public SendMessage sendMessage(Long chatId, String message) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(message)
                .build();

    }
    public SendMessage sendMarkup(Long chatId, String message,InlineKeyboardMarkup markup) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .replyMarkup(markup)
                .build();

    }



}
