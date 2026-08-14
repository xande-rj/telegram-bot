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
                new BotCommand("oi","mande um oi")
        );
        telegramClient.execute(new SetMyCommands(botCommands,new BotCommandScopeDefault(),null));
    }
}
