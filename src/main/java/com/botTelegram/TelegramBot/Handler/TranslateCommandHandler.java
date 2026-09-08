package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.response.GeminiResponse.GeminiResponse;
import com.botTelegram.TelegramBot.service.TranslateService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class TranslateCommandHandler implements BotCommandHandler {
    private final TelegramMessageSender telegramMessageSender;
    private final TranslateService translateService;
    private final String MENSAGEM =
            "Use assim: /traduzir <idioma> <texto>\nEx: /traduzir inglês Bom dia!";

    public TranslateCommandHandler(TelegramMessageSender telegramMessageSender, TranslateService translateService) {
        this.telegramMessageSender = telegramMessageSender;
        this.translateService = translateService;
    }

    @Override
    public List<String> getCommand() {
        return List.of("/traduzir");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        String textoCompleto = text.replaceFirst("/traduzir\\s*", "").trim();

        if (textoCompleto.isEmpty()) {
            telegramMessageSender.sendMessage(chatId, MENSAGEM);
        }

        String[] partes = textoCompleto.split(" ", 2);

        if (partes.length < 2) {
            telegramMessageSender.sendMessage(chatId, MENSAGEM);
        }

        String idioma = partes[0];
        String texto = partes[1];

        Message temp = telegramMessageSender.sendTemp(chatId, "🌐 Traduzindo para " + idioma + "...");

        String traducao = translate(idioma, texto);
        telegramMessageSender.editTemp(chatId, traducao, temp.getMessageId());
    }

    private String translate(String idioma, String texto) {
        GeminiResponse response = this.translateService.translate(idioma, texto);
        return response.candidates().getFirst().content().parts().getFirst().text();
    }
}
