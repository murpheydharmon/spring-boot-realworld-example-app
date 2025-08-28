package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.UserQueryService;
import io.spring.application.user.UserService;
import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersApi.class)
@Import({
  WebSecurityConfig.class,
  UserQueryService.class,
  BCryptPasswordEncoder.class,
  JacksonCustomizations.class
})
public class UsersApiEdgeCasesTest {
  @Autowired private MockMvc mvc;

  @MockBean private UserRepository userRepository;

  @MockBean private JwtService jwtService;

  @MockBean private UserReadService userReadService;

  @MockBean private UserService userService;

  @Autowired private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() throws Exception {
    RestAssuredMockMvc.mockMvc(mvc);
    when(userRepository.findByUsername(eq("validuser"))).thenReturn(Optional.empty());
    when(userRepository.findByEmail(eq("valid@test.com"))).thenReturn(Optional.empty());
  }

  @Test
  public void should_show_error_for_password_too_short() throws Exception {
    Map<String, Object> param = prepareRegisterParameter("valid@test.com", "validuser", "");

    given()
        .contentType("application/json")
        .body(param)
        .when()
        .post("/users")
        .then()
        .statusCode(422)
        .body("errors.password[0]", equalTo("can't be empty"));
  }

  @Test
  public void should_show_error_for_empty_username() throws Exception {
    Map<String, Object> param = prepareRegisterParameter("valid@test.com", "", "password123");

    given()
        .contentType("application/json")
        .body(param)
        .when()
        .post("/users")
        .then()
        .statusCode(422)
        .body("errors.username[0]", equalTo("can't be empty"));
  }

  @Test
  public void should_show_error_for_email_without_domain() throws Exception {
    Map<String, Object> param = prepareRegisterParameter("invalidemail", "validuser", "password123");

    given()
        .contentType("application/json")
        .body(param)
        .when()
        .post("/users")
        .then()
        .statusCode(422)
        .body("errors.email[0]", equalTo("should be an email"));
  }

  @Test
  public void should_show_error_for_empty_request_body() throws Exception {
    given()
        .contentType("application/json")
        .body("{}")
        .when()
        .post("/users")
        .then()
        .statusCode(400);
  }

  @Test
  public void should_show_error_for_null_user_object() throws Exception {
    given()
        .contentType("application/json")
        .body("{\"user\": null}")
        .when()
        .post("/users")
        .then()
        .statusCode(400);
  }

  @Test
  public void should_show_error_for_missing_required_fields() throws Exception {
    Map<String, Object> param = new HashMap<String, Object>() {
      {
        put("user", new HashMap<String, Object>() {
          {
            put("email", "test@test.com");
          }
        });
      }
    };

    given()
        .contentType("application/json")
        .body(param)
        .when()
        .post("/users")
        .then()
        .statusCode(422);
  }

  private HashMap<String, Object> prepareRegisterParameter(
      final String email, final String username, final String password) {
    return new HashMap<String, Object>() {
      {
        put(
            "user",
            new HashMap<String, Object>() {
              {
                put("email", email);
                put("password", password);
                put("username", username);
              }
            });
      }
    };
  }
}
