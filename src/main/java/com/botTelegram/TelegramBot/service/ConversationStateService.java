package com.botTelegram.TelegramBot.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationStateService {
    private final Map<Long,String> estado = new ConcurrentHashMap<>();

    public void definirEstado(Long id,String estado){
        this.estado.put(id,estado);
    }
    public Optional<String> getEstado(Long id){
        return Optional.ofNullable(this.estado.get(id));
    }
    public void limparEstado(long id){
        this.estado.remove(id);
    }
}
