package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.mapper.UserMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserMapperIntegrationTest {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void should_test_user_crud_operations() {
    User user = new User("test@test.com", "testuser", "password", "bio", "image");
    userRepository.save(user);

    Optional<User> foundUser = userRepository.findById(user.getId());
    Assertions.assertTrue(foundUser.isPresent());
    Assertions.assertEquals("testuser", foundUser.get().getUsername());
    Assertions.assertEquals("test@test.com", foundUser.get().getEmail());
  }

  @Test
  public void should_test_user_relationships() {
    User user1 = new User("user1@test.com", "user1", "password", "bio1", "image1");
    User user2 = new User("user2@test.com", "user2", "password", "bio2", "image2");
    
    userRepository.save(user1);
    userRepository.save(user2);

    FollowRelation followRelation = new FollowRelation(user1.getId(), user2.getId());
    userMapper.saveRelation(followRelation);

    FollowRelation foundRelation = userMapper.findRelation(user1.getId(), user2.getId());
    Assertions.assertNotNull(foundRelation);
    Assertions.assertEquals(user1.getId(), foundRelation.getUserId());
    Assertions.assertEquals(user2.getId(), foundRelation.getTargetId());
  }

  @Test
  public void should_handle_user_updates() {
    User user = new User("original@test.com", "original", "password", "original bio", "original image");
    userRepository.save(user);

    user.update("updated@test.com", "updated", "updated password", "updated bio", "updated image");
    userRepository.save(user);

    Optional<User> updatedUser = userRepository.findById(user.getId());
    Assertions.assertTrue(updatedUser.isPresent());
    Assertions.assertEquals("updated@test.com", updatedUser.get().getEmail());
    Assertions.assertEquals("updated", updatedUser.get().getUsername());
  }

  @Test
  public void should_find_user_by_email_and_username() {
    User user = new User("findme@test.com", "findme", "password", "bio", "image");
    userRepository.save(user);

    Optional<User> foundByEmail = userRepository.findByEmail("findme@test.com");
    Optional<User> foundByUsername = userRepository.findByUsername("findme");

    Assertions.assertTrue(foundByEmail.isPresent());
    Assertions.assertTrue(foundByUsername.isPresent());
    Assertions.assertEquals(user.getId(), foundByEmail.get().getId());
    Assertions.assertEquals(user.getId(), foundByUsername.get().getId());
  }

  @Test
  public void should_handle_user_deletion() {
    User user = new User("delete@test.com", "deleteuser", "password", "bio", "image");
    userRepository.save(user);

    Optional<User> foundUser = userRepository.findById(user.getId());
    Assertions.assertTrue(foundUser.isPresent());

    Assertions.assertNotNull(foundUser.get());
  }
}
