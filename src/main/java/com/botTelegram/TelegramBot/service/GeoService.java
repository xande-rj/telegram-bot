package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.response.GeoResponse.GeocodingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GeoService {
    private final String GEO_URL;
    private final ClientService clientService;
    private final String appid;

    public GeoService(
            @Value("${geo_url}") String geoURL,
            @Value("${weather.token}") String appid

    ) {
        this.GEO_URL = geoURL;
        this.clientService = new ClientService(GEO_URL);
        this.appid = appid;

    }

    public Map<String,Double> getLocation(String cidade){
        Map<String,Double> result = new HashMap<>();
        GeocodingResponse[] geo = this.clientService.getRestClient().get()
                .uri( uriBuilder -> uriBuilder
                        .queryParam("q",cidade)
                        .queryParam("appid",appid)
                        .build()).retrieve().body(GeocodingResponse[].class);
GeocodingResponse response = geo[0];
        result.put("latitude",response.lat());
        result.put("longitude",response.lon());
        return   result;
    }
}
