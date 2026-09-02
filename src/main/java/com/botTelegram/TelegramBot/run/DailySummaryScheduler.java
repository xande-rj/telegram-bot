package com.botTelegram.TelegramBot.run;

import com.botTelegram.TelegramBot.Enum.Coins;
import com.botTelegram.TelegramBot.controller.TelegramMessageSender;
import com.botTelegram.TelegramBot.entity.User;
import com.botTelegram.TelegramBot.repository.UserRepository;
import com.botTelegram.TelegramBot.response.WeatherResponse.Weather;
import com.botTelegram.TelegramBot.service.NewsService;
import com.botTelegram.TelegramBot.service.PriceService;
import com.botTelegram.TelegramBot.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailySummaryScheduler {
    private static final Logger log = LoggerFactory.getLogger(DailySummaryScheduler.class);

    private final WeatherService weatherService;
    private final PriceService priceService;
    private final NewsService newsService;
    private final TelegramMessageSender messageSender;
    private final UserRepository userRepository;

    public DailySummaryScheduler(WeatherService weatherService, PriceService priceService,  NewsService newsService, TelegramMessageSender messageSender, UserRepository userRepository) {
        this.weatherService = weatherService;
        this.priceService = priceService;
        this.newsService = newsService;
        this.messageSender = messageSender;
        this.userRepository = userRepository;
    }

    //@Scheduled(fixedRate = 10000)
    public void resumoDiario(){
    log.info("iniciando resumo diario...");
    List<User> usuarios = userRepository.findAllByResumoDiarioAtivoTrue();
    for(User user: usuarios) {
        try {
            String resumo = montarResumo(user);
            messageSender.sendMessage(user.getChatId(), resumo);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    log.info("Resumo diário enviado para {} usuários.", usuarios.size());
    }
    public String montarResumo(User usuario){
        String news = this.newsService.getNews();
        String Price = this.priceService.getPrice(Coins.DOLAR.getCoin());
        String weather = this.weatherService.getWeather();
        return """
            ☀️ Bom dia! Aqui está seu resumo:

            📰 %s

            🌤️ %s

            💵 %s
            """.formatted(news, weather, Price);
    }
}
