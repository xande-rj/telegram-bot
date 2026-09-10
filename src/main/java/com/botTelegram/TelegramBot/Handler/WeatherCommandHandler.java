package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.service.PriceService;
import com.botTelegram.TelegramBot.service.WeatherService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class WeatherCommandHandler implements BotCommandHandler {
    private final TelegramMessageSender telegramMessageSender;
    private final WeatherService weatherService;

    public WeatherCommandHandler(TelegramMessageSender telegramMessageSender,WeatherService weatherService ) {
        this.telegramMessageSender = telegramMessageSender;
        this.weatherService = weatherService;
    }
    @Override
    public List<String> getCommand() {
        return List.of("/tempo");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {
        String weather = this.weatherService.getWeather(update.getMessage().getChatId());
        this.telegramMessageSender.sendMessage(update.getMessage().getChatId(), weather);

    }
}
