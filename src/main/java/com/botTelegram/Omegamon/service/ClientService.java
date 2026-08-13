package com.botTelegram.Omegamon.service;


import org.springframework.web.client.RestClient;


public class ClientService {
    private final RestClient restClient;

    public ClientService(String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
