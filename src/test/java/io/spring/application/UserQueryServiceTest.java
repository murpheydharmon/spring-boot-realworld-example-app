package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.application.data.UserData;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

  @Mock private UserReadService userReadService;

  @InjectMocks private UserQueryService userQueryService;

  private UserData testUserData;

  @BeforeEach
  void setUp() {
    testUserData = new UserData("user-id", "test@example.com", "testuser", "Test bio", "test-image.jpg");
  }

  @Test
  void should_find_user_by_id_when_user_exists() {
    when(userReadService.findById("user-id")).thenReturn(testUserData);

    Optional<UserData> result = userQueryService.findById("user-id");

    assertTrue(result.isPresent());
    UserData userData = result.get();
    assertEquals("test@example.com", userData.getEmail());
    assertEquals("testuser", userData.getUsername());
    assertEquals("Test bio", userData.getBio());
    assertEquals("test-image.jpg", userData.getImage());
  }

  @Test
  void should_return_empty_when_user_not_found() {
    when(userReadService.findById("nonexistent-id")).thenReturn(null);

    Optional<UserData> result = userQueryService.findById("nonexistent-id");

    assertFalse(result.isPresent());
  }

  @Test
  void should_handle_user_with_null_bio_and_image() {
    UserData userWithNulls = new UserData("user-id", "test@example.com", "testuser", null, null);
    when(userReadService.findById("user-id")).thenReturn(userWithNulls);

    Optional<UserData> result = userQueryService.findById("user-id");

    assertTrue(result.isPresent());
    UserData userData = result.get();
    assertEquals("test@example.com", userData.getEmail());
    assertEquals("testuser", userData.getUsername());
    assertNull(userData.getBio());
    assertNull(userData.getImage());
  }

  @Test
  void should_handle_empty_strings_for_bio_and_image() {
    UserData userWithEmptyStrings = new UserData("user-id", "test@example.com", "testuser", "", "");
    when(userReadService.findById("user-id")).thenReturn(userWithEmptyStrings);

    Optional<UserData> result = userQueryService.findById("user-id");

    assertTrue(result.isPresent());
    UserData userData = result.get();
    assertEquals("test@example.com", userData.getEmail());
    assertEquals("testuser", userData.getUsername());
    assertEquals("", userData.getBio());
    assertEquals("", userData.getImage());
  }
}
