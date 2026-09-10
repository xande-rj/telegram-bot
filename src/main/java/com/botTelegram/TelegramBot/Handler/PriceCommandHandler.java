package com.botTelegram.TelegramBot.Handler;

import com.botTelegram.TelegramBot.Enum.Coins;
import com.botTelegram.TelegramBot.Interfaces.BotCommandHandler;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.service.PriceService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class PriceCommandHandler implements BotCommandHandler {
    private final PriceService priceService;
    private final TelegramMessageSender telegramMessageSender;

    public PriceCommandHandler(PriceService priceService, TelegramMessageSender telegramMessageSender) {
        this.priceService = priceService;
        this.telegramMessageSender = telegramMessageSender;
    }

    @Override
    public List<String> getCommand() {
        return List.of("/dolar", "/euro", "/iene", "/yuan");
    }

    @Override
    public void handle(Update update, TelegramClient telegramClient) throws TelegramApiException {

        String coin = Coins.valueOf(update.getMessage().getText().split("/")[1].toUpperCase()).getCoin();
        String price = this.priceService.getPrice(coin);
        this.telegramMessageSender.sendMessage(update.getMessage().getChatId(), price);

    }
}
