package io.spring.api.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtTokenFilterTest {

  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @InjectMocks private JwtTokenFilter jwtTokenFilter;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_authenticate_user_with_valid_token() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Token valid-jwt-token");
    when(jwtService.getSubFromToken("valid-jwt-token")).thenReturn(Optional.of("user-id"));
    when(userRepository.findById("user-id")).thenReturn(Optional.of(testUser));

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(userRepository).findById("user-id");
  }

  @Test
  void should_continue_without_authentication_when_no_token() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(jwtService, never()).getSubFromToken(anyString());
  }

  @Test
  void should_continue_without_authentication_when_invalid_token_format() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Bearer invalid-format");

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void should_continue_without_authentication_when_token_invalid() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Token invalid-jwt-token");
    when(jwtService.getSubFromToken("invalid-jwt-token")).thenReturn(Optional.empty());

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(userRepository, never()).findById(anyString());
  }

  @Test
  void should_continue_without_authentication_when_user_not_found() throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Token valid-jwt-token");
    when(jwtService.getSubFromToken("valid-jwt-token")).thenReturn(Optional.of("nonexistent-user-id"));
    when(userRepository.findById("nonexistent-user-id")).thenReturn(Optional.empty());

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(userRepository).findById("nonexistent-user-id");
  }
}
