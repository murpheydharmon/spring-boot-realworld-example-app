package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.InvalidAuthenticationException;
import io.spring.application.user.RegisterParam;
import io.spring.application.user.UpdateUserCommand;
import io.spring.application.user.UserService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.CreateUserInput;
import io.spring.graphql.types.UpdateUserInput;
import io.spring.graphql.types.UserPayload;
import io.spring.graphql.types.UserResult;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserMutationTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserService userService;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  @InjectMocks private UserMutation userMutation;

  private User testUser;
  private CreateUserInput createUserInput;
  private UpdateUserInput updateUserInput;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    createUserInput = CreateUserInput.newBuilder()
        .email("test@example.com")
        .username("testuser")
        .password("password")
        .build();
    updateUserInput = UpdateUserInput.newBuilder()
        .email("updated@example.com")
        .username("updateduser")
        .bio("Updated bio")
        .password("newpassword")
        .image("new-image.png")
        .build();
  }

  @Test
  void should_create_user_successfully() {
    when(userService.createUser(any(RegisterParam.class))).thenReturn(testUser);

    DataFetcherResult<UserResult> result = userMutation.createUser(createUserInput);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(testUser, result.getLocalContext());
    verify(userService).createUser(any(RegisterParam.class));
  }

  @Test
  void should_handle_constraint_violation_in_create_user() {
    ConstraintViolationException exception = mock(ConstraintViolationException.class);
    when(userService.createUser(any(RegisterParam.class))).thenThrow(exception);

    DataFetcherResult<UserResult> result = userMutation.createUser(createUserInput);

    assertNotNull(result);
    assertNotNull(result.getData());
    verify(userService).createUser(any(RegisterParam.class));
  }

  @Test
  void should_login_successfully_with_valid_credentials() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("password", testUser.getPassword())).thenReturn(true);

    DataFetcherResult<UserPayload> result = userMutation.login("password", "test@example.com");

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(testUser, result.getLocalContext());
  }

  @Test
  void should_throw_exception_for_invalid_email() {
    when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

    assertThrows(InvalidAuthenticationException.class, () -> {
      userMutation.login("password", "invalid@example.com");
    });
  }

  @Test
  void should_throw_exception_for_invalid_password() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

    assertThrows(InvalidAuthenticationException.class, () -> {
      userMutation.login("wrongpassword", "test@example.com");
    });
  }

  @Test
  void should_update_user_successfully() {
    Authentication userAuth = new UsernamePasswordAuthenticationToken(testUser, null);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(userAuth);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(updateUserInput);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(testUser, result.getLocalContext());
    verify(userService).updateUser(any(UpdateUserCommand.class));
  }

  @Test
  void should_return_null_for_anonymous_user() {
    Authentication anonymousAuth = new AnonymousAuthenticationToken("key", "anonymous", 
        java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(updateUserInput);

    assertNull(result);
    verify(userService, never()).updateUser(any(UpdateUserCommand.class));
  }

  @Test
  void should_return_null_for_null_principal() {
    Authentication nullPrincipalAuth = new UsernamePasswordAuthenticationToken(null, null);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(nullPrincipalAuth);

    DataFetcherResult<UserPayload> result = userMutation.updateUser(updateUserInput);

    assertNull(result);
    verify(userService, never()).updateUser(any(UpdateUserCommand.class));
  }
}
