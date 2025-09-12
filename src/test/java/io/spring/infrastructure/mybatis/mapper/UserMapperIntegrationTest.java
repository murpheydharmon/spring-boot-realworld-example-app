package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.mapper.UserMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserMapperIntegrationTest {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void should_test_user_crud_operations() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    User user = new User("test" + uniqueId + "@test.com", "testuser" + uniqueId, "password", "bio", "image");
    userRepository.save(user);

    Optional<User> foundUser = userRepository.findById(user.getId());
    Assertions.assertTrue(foundUser.isPresent());
    Assertions.assertEquals("testuser" + uniqueId, foundUser.get().getUsername());
    Assertions.assertEquals("test" + uniqueId + "@test.com", foundUser.get().getEmail());
  }

  @Test
  public void should_test_user_relationships() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    User user1 = new User("user1" + uniqueId + "@test.com", "user1" + uniqueId, "password", "bio1", "image1");
    User user2 = new User("user2" + uniqueId + "@test.com", "user2" + uniqueId, "password", "bio2", "image2");
    
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
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    User user = new User("original" + uniqueId + "@test.com", "original" + uniqueId, "password", "original bio", "original image");
    userRepository.save(user);

    user.update("updated" + uniqueId + "@test.com", "updated" + uniqueId, "updated password", "updated bio", "updated image");
    userRepository.save(user);

    Optional<User> updatedUser = userRepository.findById(user.getId());
    Assertions.assertTrue(updatedUser.isPresent());
    Assertions.assertEquals("updated" + uniqueId + "@test.com", updatedUser.get().getEmail());
    Assertions.assertEquals("updated" + uniqueId, updatedUser.get().getUsername());
  }

  @Test
  public void should_find_user_by_email_and_username() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    User user = new User("findme" + uniqueId + "@test.com", "findme" + uniqueId, "password", "bio", "image");
    userRepository.save(user);

    Optional<User> foundByEmail = userRepository.findByEmail("findme" + uniqueId + "@test.com");
    Optional<User> foundByUsername = userRepository.findByUsername("findme" + uniqueId);

    Assertions.assertTrue(foundByEmail.isPresent());
    Assertions.assertTrue(foundByUsername.isPresent());
    Assertions.assertEquals(user.getId(), foundByEmail.get().getId());
    Assertions.assertEquals(user.getId(), foundByUsername.get().getId());
  }

  @Test
  public void should_handle_user_deletion() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    User user = new User("delete" + uniqueId + "@test.com", "deleteuser" + uniqueId, "password", "bio", "image");
    userRepository.save(user);

    Optional<User> foundUser = userRepository.findById(user.getId());
    Assertions.assertTrue(foundUser.isPresent());

    Assertions.assertNotNull(foundUser.get());
  }
}
