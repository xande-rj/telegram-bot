package com.botTelegram.TelegramBot.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tb_user")
public class User {
    @Id
    private Long chatId;

    private Double latitude;
    private Double longitude;

    private boolean resumoDiarioAtivo = false;
}
