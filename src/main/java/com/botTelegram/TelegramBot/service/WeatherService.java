package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final String WEATHER_URL;
    private final ClientService restClient;
    private final String lat;
    private final String lon;
    private final String appid;

    public WeatherService(@Value("${weather_url}")String weather_url,
                          @Value("${latitude}") String lat,
                          @Value("${longitude}") String lon,
                                      @Value("${appid}") String appid

                          )
    {
        this.WEATHER_URL = weather_url;
        this.restClient = new ClientService(WEATHER_URL);
        this.lat = lat;
        this.lon = lon;
        this.appid = appid;
    }

    private WeatherResponse sendWeather() {
        return this.restClient.getRestClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", appid).build()
                ).retrieve()
                .body(WeatherResponse.class);
    }
    public String getWeather() {
        WeatherResponse  weather = sendWeather();
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
