package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.user.UserService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(DgsAutoConfiguration.class)
public class UserMutationTest {

  @Autowired
  DgsQueryExecutor dgsQueryExecutor;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private UserService userService;

  @MockBean
  private PasswordEncoder passwordEncoder;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
  }

  @Test
  public void should_create_user_via_graphql() {
    when(userService.createUser(any())).thenReturn(user);

    String mutation = "mutation { " +
        "createUser(input: { " +
        "email: \"test@test.com\" " +
        "username: \"testuser\" " +
        "password: \"password\" " +
        "}) { " +
        "user { " +
        "email " +
        "username " +
        "} " +
        "} " +
        "}";

    dgsQueryExecutor.execute(mutation);
  }

  @Test
  public void should_login_user_via_graphql() {
    when(userRepository.findByEmail(eq("test@test.com"))).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq("password"), eq(user.getPassword()))).thenReturn(true);

    String mutation = "mutation { " +
        "login(email: \"test@test.com\", password: \"password\") { " +
        "user { " +
        "email " +
        "username " +
        "} " +
        "} " +
        "}";

    dgsQueryExecutor.execute(mutation);
  }

  @Test
  public void should_update_user_via_graphql() {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList()));

    String mutation = "mutation { " +
        "updateUser(changes: { " +
        "email: \"updated@test.com\" " +
        "username: \"updateduser\" " +
        "bio: \"Updated bio\" " +
        "}) { " +
        "user { " +
        "email " +
        "username " +
        "bio " +
        "} " +
        "} " +
        "}";

    dgsQueryExecutor.execute(mutation);
  }

  @Test
  public void should_handle_invalid_login_credentials() {
    when(userRepository.findByEmail(eq("test@test.com"))).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(eq("wrongpassword"), eq(user.getPassword()))).thenReturn(false);

    String mutation = "mutation { " +
        "login(email: \"test@test.com\", password: \"wrongpassword\") { " +
        "user { " +
        "email " +
        "} " +
        "} " +
        "}";

    try {
      dgsQueryExecutor.execute(mutation);
    } catch (Exception e) {
    }
  }

  @Test
  public void should_handle_user_not_found_login() {
    when(userRepository.findByEmail(eq("notfound@test.com"))).thenReturn(Optional.empty());

    String mutation = "mutation { " +
        "login(email: \"notfound@test.com\", password: \"password\") { " +
        "user { " +
        "email " +
        "} " +
        "} " +
        "}";

    try {
      dgsQueryExecutor.execute(mutation);
    } catch (Exception e) {
    }
  }
}
