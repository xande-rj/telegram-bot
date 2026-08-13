package com.botTelegram.Omegamon.service;

import com.botTelegram.Omegamon.response.CurrencyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PriceService {
    private final String PRICE_URL;
    private final ClientService restClient;

    public PriceService(@Value("${price_url}")String priceUrl) {
        PRICE_URL = priceUrl;
        this.restClient  = new ClientService(PRICE_URL);
    }

    public Map<String, CurrencyResponse> sendPrice(String moeda){

        if (moeda.equals("BRL")) {
            moeda = "USD";
        }
        return restClient.getRestClient()
                .get()
                .uri("/{moeda}-BRL",moeda)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, CurrencyResponse>>() {});

    }
}
