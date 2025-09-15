package io.spring.application.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UserService userService;

  private RegisterParam registerParam;
  private UpdateUserParam updateUserParam;
  private User testUser;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, "default-image.png", passwordEncoder);
    
    registerParam = new RegisterParam("test@example.com", "testuser", "password");
    updateUserParam = UpdateUserParam.builder()
        .email("updated@example.com")
        .username("updateduser")
        .password("newpassword")
        .bio("Updated bio")
        .image("updated-image.png")
        .build();
    
    testUser = new User("test@example.com", "testuser", "encodedpassword", "bio", "image");
  }

  @Test
  void should_create_user_successfully() {
    when(passwordEncoder.encode("password")).thenReturn("encodedpassword");

    User result = userService.createUser(registerParam);

    assertNotNull(result);
    assertEquals("test@example.com", result.getEmail());
    assertEquals("testuser", result.getUsername());
    assertEquals("encodedpassword", result.getPassword());
    assertEquals("", result.getBio());
    assertEquals("default-image.png", result.getImage());
    
    verify(userRepository).save(any(User.class));
    verify(passwordEncoder).encode("password");
  }

  @Test
  void should_update_user_successfully() {
    UpdateUserCommand command = new UpdateUserCommand(testUser, updateUserParam);
    
    userService.updateUser(command);

    verify(userRepository).save(testUser);
  }

  @Test
  void should_handle_null_values_in_update() {
    UpdateUserParam partialUpdate = UpdateUserParam.builder()
        .email("newemail@example.com")
        .build();
    UpdateUserCommand command = new UpdateUserCommand(testUser, partialUpdate);
    
    userService.updateUser(command);

    verify(userRepository).save(testUser);
  }
}
