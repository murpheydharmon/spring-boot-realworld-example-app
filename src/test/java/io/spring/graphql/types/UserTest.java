package io.spring.graphql.types;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  void should_create_user_with_no_args_constructor() {
    User user = new User();

    assertNotNull(user);
    assertNull(user.getEmail());
    assertNull(user.getProfile());
    assertNull(user.getToken());
    assertNull(user.getUsername());
  }

  @Test
  void should_create_user_with_all_args_constructor() {
    String email = "test@example.com";
    Profile profile = new Profile();
    String token = "jwt-token-123";
    String username = "testuser";

    User user = new User(email, profile, token, username);

    assertEquals(email, user.getEmail());
    assertEquals(profile, user.getProfile());
    assertEquals(token, user.getToken());
    assertEquals(username, user.getUsername());
  }

  @Test
  void should_set_and_get_all_properties() {
    User user = new User();
    String email = "updated@example.com";
    Profile profile = new Profile();
    String token = "updated-jwt-token";
    String username = "updateduser";

    user.setEmail(email);
    user.setProfile(profile);
    user.setToken(token);
    user.setUsername(username);

    assertEquals(email, user.getEmail());
    assertEquals(profile, user.getProfile());
    assertEquals(token, user.getToken());
    assertEquals(username, user.getUsername());
  }

  @Test
  void should_implement_equals_and_hashcode() {
    Profile profile = new Profile();
    
    User user1 = new User("email", profile, "token", "user");
    User user2 = new User("email", profile, "token", "user");
    User user3 = new User("different", profile, "token", "user");

    assertEquals(user1, user2);
    assertNotEquals(user1, user3);
    assertEquals(user1.hashCode(), user2.hashCode());
    assertNotEquals(user1.hashCode(), user3.hashCode());
  }

  @Test
  void should_implement_to_string() {
    User user = new User();
    user.setEmail("test@example.com");
    user.setUsername("testuser");

    String toString = user.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("User{"));
    assertTrue(toString.contains("test@example.com"));
    assertTrue(toString.contains("testuser"));
  }

  @Test
  void should_create_builder() {
    User.Builder builder = User.newBuilder();

    assertNotNull(builder);
  }

  @Test
  void should_build_user_with_builder() {
    String email = "builder@example.com";
    Profile profile = new Profile();
    String token = "builder-token";
    String username = "builderuser";

    User user = User.newBuilder()
        .email(email)
        .profile(profile)
        .token(token)
        .username(username)
        .build();

    assertEquals(email, user.getEmail());
    assertEquals(profile, user.getProfile());
    assertEquals(token, user.getToken());
    assertEquals(username, user.getUsername());
  }

  @Test
  void should_build_user_with_partial_builder() {
    String email = "partial@example.com";
    String username = "partialuser";

    User user = User.newBuilder()
        .email(email)
        .username(username)
        .build();

    assertEquals(email, user.getEmail());
    assertEquals(username, user.getUsername());
    assertNull(user.getProfile());
    assertNull(user.getToken());
  }

  @Test
  void should_handle_null_values() {
    User user = new User();
    
    user.setEmail(null);
    user.setProfile(null);
    user.setToken(null);
    user.setUsername(null);

    assertNull(user.getEmail());
    assertNull(user.getProfile());
    assertNull(user.getToken());
    assertNull(user.getUsername());
  }

  @Test
  void should_handle_empty_strings() {
    User user = new User();
    
    user.setEmail("");
    user.setToken("");
    user.setUsername("");

    assertEquals("", user.getEmail());
    assertEquals("", user.getToken());
    assertEquals("", user.getUsername());
  }

  @Test
  void should_handle_builder_method_chaining() {
    User.Builder builder = User.newBuilder();
    
    User.Builder result = builder
        .email("chain@example.com")
        .username("chainuser")
        .token("chain-token");
    
    assertSame(builder, result);
    
    User user = result.build();
    assertEquals("chain@example.com", user.getEmail());
    assertEquals("chainuser", user.getUsername());
    assertEquals("chain-token", user.getToken());
  }

  @Test
  void should_handle_different_tokens_in_equals() {
    Profile profile = new Profile();
    
    User user1 = new User("email", profile, "token1", "user");
    User user2 = new User("email", profile, "token2", "user");

    assertNotEquals(user1, user2);
    assertNotEquals(user1.hashCode(), user2.hashCode());
  }

  @Test
  void should_handle_different_profiles_in_equals() {
    Profile profile1 = new Profile();
    profile1.setUsername("profile1");
    Profile profile2 = new Profile();
    profile2.setUsername("profile2");
    
    User user1 = new User("email", profile1, "token", "user");
    User user2 = new User("email", profile2, "token", "user");

    assertNotEquals(user1, user2);
    assertNotEquals(user1.hashCode(), user2.hashCode());
  }

  @Test
  void should_handle_long_token_values() {
    User user = new User();
    String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    
    user.setToken(longToken);

    assertEquals(longToken, user.getToken());
  }

  @Test
  void should_handle_email_validation_format() {
    User user = new User();
    String validEmail = "user.name+tag@example.com";
    
    user.setEmail(validEmail);

    assertEquals(validEmail, user.getEmail());
  }
}
