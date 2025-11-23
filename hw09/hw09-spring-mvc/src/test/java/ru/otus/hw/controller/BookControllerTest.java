package ru.otus.hw.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

@WebMvcTest(BookController.class)
public class BookControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockitoBean
  private BookService bookService;
  @MockitoBean
  private AuthorService authorService;
  @MockitoBean
  private GenreService genreService;
  @MockitoBean
  private CommentService commentService;

  private BookDto bookDto;
  private AuthorDto authorDto;
  private GenreDto genreDto;
  private CommentDto commentDto;

  @BeforeEach
  void setUp() {
    authorDto = new AuthorDto(1L, "Peter Parker");
    genreDto = new GenreDto(1L, "Fantasy");
    bookDto = new BookDto(1L, "book1", authorDto, List.of(genreDto));
    commentDto = new CommentDto(1L, "comment1", 1L);

  }

  @Test
  void shouAllBooks() throws Exception {
    List<BookDto> bookDtos = List.of(bookDto);

    given(bookService.findAll()).willReturn(bookDtos);

    mvc.perform(get("/books"))
        .andExpect(status().isOk())
        .andExpect(view().name("list-books"))
        .andExpect(model().attributeExists("books"));
  }

  @Test
  void showCommentsBook() throws Exception {

    given(commentService.findAllById(1L)).willReturn(List.of(commentDto));
    given(bookService.findById(1L)).willReturn(bookDto);

    mvc.perform(get("/comments-book/{bookId}", 1L))
        .andExpect(status().isOk())
        .andExpect(view().name("book-info"))
        .andExpect(model().attributeExists("book"))
        .andExpect(model().attributeExists("comments"));
  }

  @Test
  void showCommentsBookThrowExceptionWhenBookNotFound() throws Exception {

    given(commentService.findAllById(1L)).willReturn(List.of(commentDto));
    given(bookService.findById(1L)).willThrow(NotFoundException.class);

    mvc.perform(get("/comments-book/{bookId}", 1))
        .andExpect(status().isOk())
        .andExpect(view().name("not-found"));
  }

  @Test
  void createBook() throws Exception {
    given(bookService.insert("title", 1L, List.of(1L))).willReturn(bookDto);

    mvc.perform(post("/book/create")
            .param("title", "title")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books"));
  }

  @Test
  void updateBook() throws Exception {

    given(bookService.update(1L, "book_1", 1L, List.of(1L))).willReturn(bookDto);

    mvc.perform(post("/book/{id}/edit", 1L)
            .param("title", "book_1")
            .param("authorId", "1")
            .param("genreIds", "1")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books"));
  }

  @Test
  void deleteBook() throws Exception {

    willDoNothing().given(bookService).deleteById(1L);

    mvc.perform(post("/book/{id}/delete", 1L)
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/books"));
  }

  @Test
  void showEditForm() throws Exception {

    given(bookService.findById(1L)).willReturn(bookDto);
    given(authorService.findAll()).willReturn(List.of(authorDto));
    given(genreService.findAll()).willReturn(List.of(genreDto));

    mvc.perform(get("/book/{id}/edit", 1L))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-book"))
        .andExpect(model().attributeExists("bookRequest"))
        .andExpect(model().attributeExists("authors"))
        .andExpect(model().attributeExists("genres"));
  }

  @Test
  void showEditFormWillThrowException() throws Exception {

    given(bookService.findById(1L)).willThrow(NotFoundException.class);

    mvc.perform(get("/comments-book/{bookId}", 1))
        .andExpect(status().isOk())
        .andExpect(view().name("not-found"));
  }
}
