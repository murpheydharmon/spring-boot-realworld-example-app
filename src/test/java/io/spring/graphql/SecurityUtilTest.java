package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class SecurityUtilTest {

  @Mock private SecurityContext securityContext;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_return_current_user_when_authenticated() {
    Authentication authentication = new UsernamePasswordAuthenticationToken(testUser, null);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertTrue(result.isPresent());
    assertEquals(testUser, result.get());
  }

  @Test
  void should_return_empty_when_anonymous_authentication() {
    Authentication anonymousAuth = new AnonymousAuthenticationToken("key", "anonymous", 
        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertFalse(result.isPresent());
  }

  @Test
  void should_return_empty_when_principal_is_null() {
    Authentication authentication = new UsernamePasswordAuthenticationToken(null, null);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    Optional<User> result = SecurityUtil.getCurrentUser();

    assertFalse(result.isPresent());
  }

  @Test
  void should_return_empty_when_authentication_is_null() {
    when(securityContext.getAuthentication()).thenReturn(null);

    assertThrows(NullPointerException.class, () -> {
      SecurityUtil.getCurrentUser();
    });
  }

  @Test
  void should_handle_different_principal_types() {
    Authentication authentication = new UsernamePasswordAuthenticationToken("string-principal", null);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    assertThrows(ClassCastException.class, () -> {
      SecurityUtil.getCurrentUser();
    });
  }

  @Test
  void should_work_with_real_security_context() {
    SecurityContextHolder.clearContext();
    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    
    assertThrows(NullPointerException.class, () -> {
      SecurityUtil.getCurrentUser();
    });
  }
}
