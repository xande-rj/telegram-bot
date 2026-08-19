package com.botTelegram.TelegramBot.repository;

import com.botTelegram.TelegramBot.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByChatId(Long chatId);
}
