package ru.otus.hw.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.otus.hw.services.BookService;


@SpringBootTest
@AutoConfigureMockMvc

public class BookServiceSecurityTest {

  @Autowired
  private BookService bookService;


  @ParameterizedTest
  @MethodSource("getTestDataDelete")
  void shouldDeleteBookThisRoleAdminAnyRoleAccessDenied(String userName, String[] roles, boolean shouldFail) {
    setSecurityContext(userName, roles);
    if (shouldFail) {
      assertThrows(AccessDeniedException.class, () -> {
        bookService.deleteById(1L);
      });
    } else {
      bookService.deleteById(1L);
    }
  }

  @ParameterizedTest
  @MethodSource("getTestDataUpdateInsert")
  void shouldUpdateBookThisRoleAdminAndMANAGERAnyRoleAccessDenied(String userName, String[] roles, boolean shouldFail) {
    setSecurityContext(userName, roles);
    if (shouldFail) {
      assertThrows(AccessDeniedException.class, () -> {
        bookService.update(1L, "Updated Book", 2L, List.of(2L));
      });
    } else {
      bookService.update(1L, "Updated Book", 2L, List.of(2L));
    }
  }

  @ParameterizedTest
  @MethodSource("getTestDataUpdateInsert")
  void shouldInsertBookThisRoleAdminAndMANAGERAnyRoleAccessDenied(String userName, String[] roles, boolean shouldFail) {
    setSecurityContext(userName, roles);
    if (shouldFail) {
      assertThrows(AccessDeniedException.class, () -> {
        bookService.insert("New Book", 2L, List.of(2L));
      });
    } else {
      bookService.insert("New Book", 2L, List.of(2L));
    }
  }

  private static Stream<Arguments> getTestDataDelete() {
    return Stream.of(
        Arguments.of("user", new String[]{"USER"}, true),
        Arguments.of("manager", new String[]{"MANAGER", "USER"}, true),
        Arguments.of("admin", new String[]{"ADMIN"}, false)
    );
  }

  private static Stream<Arguments> getTestDataUpdateInsert() {
    return Stream.of(
        Arguments.of("user", new String[]{"USER"}, true),
        Arguments.of("manager", new String[]{"MANAGER", "USER"}, false),
        Arguments.of("admin", new String[]{"ADMIN"}, false)
    );
  }

  private void setSecurityContext(String username, String[] roles) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            username,
            "password",
            Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList()
        );
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);
  }
}

