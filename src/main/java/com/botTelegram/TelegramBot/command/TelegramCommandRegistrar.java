package com.botTelegram.TelegramBot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramCommandRegistrar {
    public void registerCommands(TelegramClient telegramClient) throws TelegramApiException {
        List<BotCommand> botCommands = List.of(
                new BotCommand("ola","Mande um oi ao bot."),
                new BotCommand("tempo","Veja como esta o tempo agora."),
                new BotCommand("dolar","Cotacao do dolar a partir do real."),
                new BotCommand("euro","Cotacao do euro a partir do real."),
                new BotCommand("iene","Cotacao do iene a partir do real."),
                new BotCommand("yuan","Cotacao do yuan a partir do real."),
                new BotCommand("noticias","Atualizar as noticias.")

        );
        telegramClient.execute(new SetMyCommands(botCommands,new BotCommandScopeDefault(),null));
    }
}
