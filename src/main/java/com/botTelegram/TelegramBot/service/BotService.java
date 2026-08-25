package com.botTelegram.TelegramBot.service;


import com.botTelegram.TelegramBot.entity.Note;
import com.botTelegram.TelegramBot.response.GeminiResponse.GeminiResponse;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class BotService {


    private final PriceService priceService;
    private final WeatherService weatherService;
    private final NewsService newsService;
    private final NoteService noteService;
    private final TranslateService translateService;

    public BotService(
            PriceService priceService,
            WeatherService weatherService,
            NewsService newsService,
            NoteService noteService,
            TranslateService translateService
    ) {
        this.priceService = priceService;
        this.weatherService = weatherService;
        this.newsService = newsService;
        this.noteService = noteService;
        this.translateService = translateService;
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

    public InlineKeyboardMarkup getNoteMarkup() {
        return noteService.getNotes();
    }

    public Note saveNote(String text, Long chatId) {
        return noteService.save(text, chatId);
    }

    public boolean deleteNote(String text, Long chatId) {
        return noteService.delete(text, chatId);
    }

    public String getNotes(Long chatId) {

        return noteService.findAll(chatId);
    }

    public String translate(String idioma, String texto) {
        GeminiResponse response =  translateService.translate(idioma,texto);
        return response.candidates().getFirst().content().parts().getFirst().text();
    }

    public SendMessage sendMessage(Long chatId, String message) {
        return SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(message)
                .build();

    }

    public SendMessage sendMarkup(Long chatId, String message, InlineKeyboardMarkup markup) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .replyMarkup(markup)
                .build();

    }


}
