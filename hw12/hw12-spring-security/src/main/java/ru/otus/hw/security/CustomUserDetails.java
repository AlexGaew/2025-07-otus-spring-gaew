package ru.otus.hw.security;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = userRepository.findByUsername(username).orElseThrow(() ->
        new UsernameNotFoundException("User not found" + username));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .authorities(mapRolesToAuthorities(user))
        .build();
  }

  private Collection<? extends GrantedAuthority> mapRolesToAuthorities(User user) {
    return user.getAuthorities().stream()
        .map(SimpleGrantedAuthority::new)
        .toList();
  }
}
