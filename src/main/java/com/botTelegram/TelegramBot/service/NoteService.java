package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.entity.Note;
import com.botTelegram.TelegramBot.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    public Note save(Note note) {
        return noteRepository.save(note);
    }
    public void delete(Note note) {
        noteRepository.delete(note);
    }
    public List<Note> findAll() {
        return noteRepository.findAll();
    }
}
