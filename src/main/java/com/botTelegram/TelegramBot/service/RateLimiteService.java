package com.botTelegram.TelegramBot.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiteService {
    private final Map<Long, Deque<Instant>> historico = new ConcurrentHashMap<>();

    private static final int LIMIT_REQUESTS = 10;
    private static final Duration JANELA = Duration.ofMinutes(10);

    public boolean permitir (Long chat_id){
        Instant now = Instant.now();
        Deque<Instant> timestamps = historico.computeIfAbsent(chat_id, id-> new ArrayDeque<>());
        synchronized (timestamps){
            limpar(timestamps,now);
            if(timestamps.size() >= LIMIT_REQUESTS){
                return false;
            }
            timestamps.addLast(now);
            return true;
        }

    }
    public void limpar(Deque<Instant> timestamps, Instant now){
        Instant limite = now.minus(JANELA);
        while(!timestamps.isEmpty() && timestamps.peekFirst().isBefore(limite)){
            timestamps.pollFirst();
        }
    }
}
