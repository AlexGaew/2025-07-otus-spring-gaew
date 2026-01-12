package ru.otus.springbatch.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.otus.springbatch.model.NoteEntity;
import ru.otus.springbatch.model.Notebook;
import ru.otus.springbatch.model.NotebookEntity;

@Service
public class NotebookMapperService {

    public NotebookEntity map(Notebook notebook) {
        NotebookEntity notebookEntity = new NotebookEntity();
        List<NoteEntity> notes = notebook.getNotes().stream()
            .map(note -> {
                NoteEntity noteEntity = new NoteEntity();
                noteEntity.setNote(note.getNote());
                return noteEntity;
            }).toList();

        notebookEntity.setNotes(notes);
        return notebookEntity;
    }
}
