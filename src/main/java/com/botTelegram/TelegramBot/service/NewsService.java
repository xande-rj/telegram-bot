package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.response.NewsResponse.ArticleResponse;
import com.botTelegram.TelegramBot.response.NewsResponse.NewsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NewsService {
    private final String NEWS_URL;
    private final ClientService restClient;
    private final String NEW_TOKEN;

    public NewsService(
            @Value("${news_url}") String newsUrl,
            @Value("${news.token}") String newsToken)
    {
        this.NEWS_URL = newsUrl;
        this.restClient = new ClientService(NEWS_URL);
        this.NEW_TOKEN = newsToken;
    }

    private NewsResponse sendNews(){
        LocalDate date = LocalDate.now();
        return  restClient.getRestClient().get().uri(
                uriBuilder -> uriBuilder
                        .queryParam("from",date)
                        .queryParam("to",date)
                        .queryParam("pageSize",5)
                        .queryParam("apiKey",NEW_TOKEN)
                        .build()
                ).retrieve()
                .body(NewsResponse.class);
    }

    public String getNews() {
        StringBuilder mensagem = new StringBuilder("""
        📰 *Principais notícias*

        """);
        NewsResponse newsResponse = sendNews();
        for (int i = 0; i < newsResponse.articles().size(); i++) {

            ArticleResponse article = newsResponse.articles().get(i);

            mensagem.append("""
            - *%d. %s*

            📰 Fonte: %s
            ✍️ Autor: %s
            📝 %s
            🔗 %s

            """.formatted(
                    i + 1,
                    article.title(),
                    article.source().name(),
                    article.author(),
                    article.description(),
                    article.url()
            ));
        }
        return mensagem.toString();
    }

}
