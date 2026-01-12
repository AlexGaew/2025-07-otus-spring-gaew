package ru.otus.springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {

  @Id
  private String id;

  private String note;

  public Note(String note) {
    this.note = note;
  }

}
