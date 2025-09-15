package io.spring.core.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "Test bio", "test-image.jpg");
  }

  @Test
  void should_create_user_with_valid_parameters() {
    assertNotNull(testUser);
    assertEquals("test@example.com", testUser.getEmail());
    assertEquals("testuser", testUser.getUsername());
    assertEquals("password", testUser.getPassword());
    assertEquals("Test bio", testUser.getBio());
    assertEquals("test-image.jpg", testUser.getImage());
    assertNotNull(testUser.getId());
  }

  @Test
  void should_update_user_profile() {
    String newEmail = "updated@example.com";
    String newUsername = "updateduser";
    String newPassword = "newpassword";
    String newBio = "Updated bio";
    String newImage = "updated-image.jpg";

    testUser.update(newEmail, newUsername, newPassword, newBio, newImage);

    assertEquals(newEmail, testUser.getEmail());
    assertEquals(newUsername, testUser.getUsername());
    assertEquals(newPassword, testUser.getPassword());
    assertEquals(newBio, testUser.getBio());
    assertEquals(newImage, testUser.getImage());
  }

  @Test
  void should_handle_null_values_in_update() {
    String originalEmail = testUser.getEmail();
    String originalUsername = testUser.getUsername();
    String originalPassword = testUser.getPassword();
    String originalBio = testUser.getBio();
    String originalImage = testUser.getImage();

    testUser.update(null, null, null, null, null);

    assertEquals(originalEmail, testUser.getEmail());
    assertEquals(originalUsername, testUser.getUsername());
    assertEquals(originalPassword, testUser.getPassword());
    assertEquals(originalBio, testUser.getBio());
    assertEquals(originalImage, testUser.getImage());
  }

  @Test
  void should_not_update_with_empty_strings() {
    String originalEmail = testUser.getEmail();
    String originalUsername = testUser.getUsername();
    String originalPassword = testUser.getPassword();
    String originalBio = testUser.getBio();
    String originalImage = testUser.getImage();

    testUser.update("", "", "", "", "");

    assertEquals(originalEmail, testUser.getEmail());
    assertEquals(originalUsername, testUser.getUsername());
    assertEquals(originalPassword, testUser.getPassword());
    assertEquals(originalBio, testUser.getBio());
    assertEquals(originalImage, testUser.getImage());
  }

  @Test
  void should_create_user_with_null_bio_and_image() {
    User userWithNulls = new User("test@example.com", "testuser", "password", null, null);
    
    assertNotNull(userWithNulls);
    assertEquals("test@example.com", userWithNulls.getEmail());
    assertEquals("testuser", userWithNulls.getUsername());
    assertEquals("password", userWithNulls.getPassword());
    assertNull(userWithNulls.getBio());
    assertNull(userWithNulls.getImage());
  }

  @Test
  void should_create_user_with_null_email() {
    User user = new User(null, "testuser", "password", "bio", "image");
    assertNotNull(user);
    assertNull(user.getEmail());
  }

  @Test
  void should_create_user_with_empty_email() {
    User user = new User("", "testuser", "password", "bio", "image");
    assertNotNull(user);
    assertEquals("", user.getEmail());
  }

  @Test
  void should_create_user_with_null_username() {
    User user = new User("test@example.com", null, "password", "bio", "image");
    assertNotNull(user);
    assertNull(user.getUsername());
  }

  @Test
  void should_create_user_with_empty_username() {
    User user = new User("test@example.com", "", "password", "bio", "image");
    assertNotNull(user);
    assertEquals("", user.getUsername());
  }

  @Test
  void should_create_user_with_null_password() {
    User user = new User("test@example.com", "testuser", null, "bio", "image");
    assertNotNull(user);
    assertNull(user.getPassword());
  }

  @Test
  void should_create_user_with_empty_password() {
    User user = new User("test@example.com", "testuser", "", "bio", "image");
    assertNotNull(user);
    assertEquals("", user.getPassword());
  }
}
