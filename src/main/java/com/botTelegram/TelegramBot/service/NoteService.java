package com.botTelegram.TelegramBot.service;

import com.botTelegram.TelegramBot.entity.Note;
import com.botTelegram.TelegramBot.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    public InlineKeyboardMarkup getNotes() {
        InlineKeyboardButton save = InlineKeyboardButton.builder()
                .text("Salvar Notas \uD83D\uDDC2\uFE0F")
                .callbackData("save")
                .build();
        InlineKeyboardButton get = InlineKeyboardButton.builder()
                .text("Listar Notas \uD83D\uDDD2\uFE0F")
                .callbackData("get")
                .build();
        InlineKeyboardButton delete = InlineKeyboardButton.builder()
                .text("Deletar Notas \uD83D\uDDD1\uFE0F ")
                .callbackData("delete")
                .build();

        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(save);
        row.add(get);

        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(delete);
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(row)
                .keyboardRow(row1)
                .build();
        return markup;
    }

    public Note save(String text,Long chatId) {
        Note savedNote =new Note();
        savedNote.setChatId(chatId);
        savedNote.setText(text);
        savedNote.setCreatedAt(LocalDateTime.now());
        return noteRepository.save(savedNote);
    }

    public void delete(String text,Long chatId) {
        List<Note> notes = noteRepository.findAllByChatId(chatId);
        Note note = notes.get(Integer.parseInt(text)-1);
        noteRepository.delete(note);
        System.out.println("Note Deletado!" + note.getChatId());

        System.out.println("Note Deletado!" + note.getText());
    }

    public String findAll(Long chatId) {
        List<Note> notes = noteRepository.findAllByChatId(chatId);

        StringBuilder mensagem = new StringBuilder("""
                    Notas
                    
                    """);
        if(notes.isEmpty()){
            return mensagem.append("Sem notas").toString();
        }
        for (int i = 0; i < notes.size(); i++) {

            Note note = notes.get(i);

            mensagem.append("""
                        - %d
                        📰 Nota: %s

                        """.formatted(
                    i + 1,
                    note.getText()
            ));
        }
        return mensagem.toString();
    }
}
