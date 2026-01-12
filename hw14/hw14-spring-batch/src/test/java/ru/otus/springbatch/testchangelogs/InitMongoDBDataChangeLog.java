package ru.otus.springbatch.testchangelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.springbatch.model.Note;
import ru.otus.springbatch.model.Notebook;

import java.util.ArrayList;
import java.util.List;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

    @ChangeSet(order = "000", id = "dropDB", author = "GtwoA", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initNotebooks", author = "GtwoA", runAlways = true)
    public void initNotebooks(MongockTemplate template) {
        List<Note> notes1 = new ArrayList<>();
        notes1.add(new Note("тестовая заметка 1"));
        notes1.add(new Note("тестовая заметка 2"));
        notes1.add(new Note("тестовая заметка 3"));
        Notebook notebook1 = new Notebook(notes1);
        template.save(notebook1);

        List<Note> notes2 = new ArrayList<>();
        notes2.add(new Note("купить молоко"));
        notes2.add(new Note("позвонить другу"));
        notes2.add(new Note("сделать домашку"));
        notes2.add(new Note("пойти на прогулку"));
        Notebook notebook2 = new Notebook(notes2);
        template.save(notebook2);

        List<Note> notes3 = new ArrayList<>();
        notes3.add(new Note("изучить Spring Batch"));
        notes3.add(new Note("написать тесты"));
        notes3.add(new Note("проверить миграцию"));
        Notebook notebook3 = new Notebook(notes3);
        template.save(notebook3);
    }
}