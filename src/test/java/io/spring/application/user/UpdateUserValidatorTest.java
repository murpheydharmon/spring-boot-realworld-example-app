package io.spring.application.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Optional;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateUserValidatorTest {

  @Mock private UserRepository userRepository;
  @Mock private ConstraintValidatorContext context;
  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

  @InjectMocks private UpdateUserValidator validator;

  private User targetUser;
  private UpdateUserParam updateUserParam;

  @BeforeEach
  void setUp() {
    targetUser = new User("original@example.com", "originaluser", "password", "bio", "image");
    updateUserParam = UpdateUserParam.builder()
        .email("new@example.com")
        .username("newuser")
        .build();
  }

  @Test
  void should_return_true_when_email_and_username_are_available() {
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    UpdateUserCommand command = new UpdateUserCommand(targetUser, updateUserParam);
    boolean result = validator.isValid(command, context);

    assertTrue(result);
  }

  @Test
  void should_return_true_when_email_belongs_to_same_user() {
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(targetUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    UpdateUserCommand command = new UpdateUserCommand(targetUser, updateUserParam);
    boolean result = validator.isValid(command, context);

    assertTrue(result);
  }

  @Test
  void should_return_false_when_email_belongs_to_different_user() {
    User differentUser = new User("different@example.com", "differentuser", "password", "bio", "image");
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(differentUser));
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
    when(context.buildConstraintViolationWithTemplate("email already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("email")).thenReturn(nodeBuilder);

    UpdateUserCommand command = new UpdateUserCommand(targetUser, updateUserParam);
    boolean result = validator.isValid(command, context);

    assertFalse(result);
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("email already exist");
  }

  @Test
  void should_return_false_when_username_belongs_to_different_user() {
    User differentUser = new User("different@example.com", "differentuser", "password", "bio", "image");
    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(differentUser));
    when(context.buildConstraintViolationWithTemplate("username already exist")).thenReturn(violationBuilder);
    when(violationBuilder.addPropertyNode("username")).thenReturn(nodeBuilder);

    UpdateUserCommand command = new UpdateUserCommand(targetUser, updateUserParam);
    boolean result = validator.isValid(command, context);

    assertFalse(result);
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("username already exist");
  }
}
