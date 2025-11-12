package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/comment/{id}")
  public String getComment(@PathVariable("id") long id, Model model) {
    CommentDto comment = commentService.findCommentById(id)
        .orElseThrow(() -> new NotFoundException("Comment not found"));
    model.addAttribute("comment", comment);
    return "comment";
  }
}
