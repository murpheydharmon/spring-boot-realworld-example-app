package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.application.data.ProfileData;
import io.spring.application.data.UserData;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProfileQueryServiceTest {

  @Mock private UserReadService userReadService;
  @Mock private UserRelationshipQueryService userRelationshipQueryService;

  @InjectMocks private ProfileQueryService profileQueryService;

  private UserData testUserData;
  private User currentUser;

  @BeforeEach
  void setUp() {
    testUserData = new UserData("user-id", "test@example.com", "testuser", "Test bio", "test-image.jpg");
    currentUser = new User("current@example.com", "currentuser", "password", "Current bio", "current-image.jpg");
  }

  @Test
  void should_find_profile_by_username_when_user_exists() {
    when(userReadService.findByUsername("testuser")).thenReturn(testUserData);
    when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), testUserData.getId())).thenReturn(false);

    Optional<ProfileData> result = profileQueryService.findByUsername("testuser", currentUser);

    assertTrue(result.isPresent());
    ProfileData profile = result.get();
    assertEquals("testuser", profile.getUsername());
    assertEquals("Test bio", profile.getBio());
    assertEquals("test-image.jpg", profile.getImage());
    assertFalse(profile.isFollowing());
  }

  @Test
  void should_return_empty_when_user_not_found() {
    when(userReadService.findByUsername("nonexistent")).thenReturn(null);

    Optional<ProfileData> result = profileQueryService.findByUsername("nonexistent", currentUser);

    assertFalse(result.isPresent());
    verify(userRelationshipQueryService, never()).isUserFollowing(any(), any());
  }

  @Test
  void should_show_following_status_when_current_user_follows_target() {
    when(userReadService.findByUsername("testuser")).thenReturn(testUserData);
    when(userRelationshipQueryService.isUserFollowing(currentUser.getId(), testUserData.getId())).thenReturn(true);

    Optional<ProfileData> result = profileQueryService.findByUsername("testuser", currentUser);

    assertTrue(result.isPresent());
    assertTrue(result.get().isFollowing());
  }

  @Test
  void should_handle_null_current_user() {
    when(userReadService.findByUsername("testuser")).thenReturn(testUserData);

    Optional<ProfileData> result = profileQueryService.findByUsername("testuser", null);

    assertTrue(result.isPresent());
    assertFalse(result.get().isFollowing());
    verify(userRelationshipQueryService, never()).isUserFollowing(any(), any());
  }

  @Test
  void should_handle_user_with_null_bio_and_image() {
    UserData userWithNulls = new UserData("user-id", "test@example.com", "testuser", null, null);
    when(userReadService.findByUsername("testuser")).thenReturn(userWithNulls);

    Optional<ProfileData> result = profileQueryService.findByUsername("testuser", null);

    assertTrue(result.isPresent());
    ProfileData profile = result.get();
    assertEquals("testuser", profile.getUsername());
    assertNull(profile.getBio());
    assertNull(profile.getImage());
    assertFalse(profile.isFollowing());
  }

  @Test
  void should_handle_empty_strings_for_bio_and_image() {
    UserData userWithEmptyStrings = new UserData("user-id", "test@example.com", "testuser", "", "");
    when(userReadService.findByUsername("testuser")).thenReturn(userWithEmptyStrings);

    Optional<ProfileData> result = profileQueryService.findByUsername("testuser", null);

    assertTrue(result.isPresent());
    ProfileData profile = result.get();
    assertEquals("testuser", profile.getUsername());
    assertEquals("", profile.getBio());
    assertEquals("", profile.getImage());
    assertFalse(profile.isFollowing());
  }
}
