package com.botTelegram.Omegamon.service;

import com.botTelegram.Omegamon.Enum.Coins;
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

    private Map<String, CurrencyResponse> sendCoin(String moeda){

        if (moeda.equals("BRL")) {
            moeda = "USD";
        }
        return restClient.getRestClient()
                .get()
                .uri("/{moeda}-BRL",moeda)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, CurrencyResponse>>() {});

    }

    public  String getPrice(String moeda){
        Map<String, CurrencyResponse> currencies = sendCoin(moeda);
        CurrencyResponse price = currencies.get(moeda + "BRL");

        String mensagem = """
                            💵 %s → Real
                            
                            💰 Compra: R$ %s
                            💰 Venda: R$ %s
                            📈 Máxima: R$ %s
                            📉 Mínima: R$ %s
                            📊 Variação: %s%%
                            """.formatted(
                moeda,
                price.bid(),
                price.ask(),
                price.high(),
                price.low(),
                price.pctChange());
        return mensagem;
    }
}
