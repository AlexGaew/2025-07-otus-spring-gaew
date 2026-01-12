package ru.otus.springbatch.chandgelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import ru.otus.springbatch.model.Note;
import ru.otus.springbatch.model.Notebook;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

  @ChangeSet(order = "000", id = "dropDB", author = "GtowA", runAlways = true)
  public void dropDB(MongoDatabase database) {
    database.drop();
  }

  @ChangeSet(order = "001", id = "initNotebooks", author = "GtowA", runAlways = true)
  public void initNotebooks(MongockTemplate template) {
    List<Note> notes = new ArrayList<>();
    notes.add(new Note("купить хлеб"));
    notes.add(new Note("учить язык"));
    notes.add(new Note("идти в спорт зал"));
    notes.add(new Note("смотреть кино"));
    Notebook notebook = new Notebook(notes);
    template.save(notebook);
  }

  @ChangeSet(order = "002", id = "initSecondNotebook", author = "GtowA", runAlways = true)
  public void initSecondNotebook(MongockTemplate template) {
    List<Note> notes = new ArrayList<>();
    notes.add(new Note("позвонить врачу"));
    notes.add(new Note("оплатить счета"));
    notes.add(new Note("купить подарок"));
    notes.add(new Note("прочитать книгу"));
    notes.add(new Note("сделать зарядку"));
    Notebook notebook = new Notebook(notes);
    template.save(notebook);
  }
}
