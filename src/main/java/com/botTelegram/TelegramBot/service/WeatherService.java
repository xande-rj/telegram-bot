package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.entity.User;
import com.botTelegram.TelegramBot.exception.BotUserException;
import com.botTelegram.TelegramBot.repository.UserRepository;
import com.botTelegram.TelegramBot.response.WeatherResponse.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeatherService {
    private final String WEATHER_URL;
    private final ClientService restClient;
    private final String appid;
    private final UserRepository userRepository;

    public WeatherService(@Value("${weather_url}")String weather_url,
                          @Value("${weather.token}") String appid,
                          UserRepository userRepository

                          )
    {
        this.WEATHER_URL = weather_url;
        this.restClient = new ClientService(WEATHER_URL);
        this.appid = appid;
        this.userRepository = userRepository;
    }

    private WeatherResponse sendWeather(Long chatId) throws BotUserException {
        User user = userRepository.findById(chatId).orElseThrow(()-> new BotUserException("Usuario nao cadastrado, se cadastre no comando /localizacao "));
        try {
            return this.restClient.getRestClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("lat", user.getLatitude())
                            .queryParam("lon", user.getLongitude())
                            .queryParam("appid", appid).build()
                    ).retrieve()
                    .body(WeatherResponse.class);
        } catch (IllegalArgumentException e) {
        log.error("Erro inesperado na resposta da api", e);
        throw new BotUserException("⚠\uFE0F Algo deu errado. Tente novamente mais tarde.");
    } catch (Exception e) {
        log.error("Erro inesperado na api de Weather", e);
        throw new BotUserException("⚠\uFE0F Algo deu errado. Tente novamente.");
    }
    }

    public String getWeather(Long chatId) {
        WeatherResponse  weather = sendWeather(chatId);
        String mensagem = """
                        🌎 Cidade: %s
                        🌡️ Temperatura: %.1f°C
                        🤔 Sensação: %.1f°C
                        💧 Umidade: %d%%
                        ☁️ Condição: %s
                        💨 Vento: %.1f m/s
                        """.formatted(
                weather.name(),
                weather.main().temp(),
                weather.main().feels_like(),
                weather.main().humidity(),
                weather.weather().get(0).description(),
                weather.wind().speed()
        );
        return mensagem;
    }
}
