package io.spring.api.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class WebSecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  public void should_protect_authenticated_endpoints() throws Exception {
    mockMvc.perform(get("/user")).andExpect(status().isUnauthorized());

    mockMvc
        .perform(
            post("/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"article\":{\"title\":\"test\",\"description\":\"test\",\"body\":\"test\"}}"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/profiles/testuser/follow")).andExpect(status().isUnauthorized());
  }

  @Test
  public void should_allow_public_endpoints() throws Exception {
    mockMvc.perform(get("/tags")).andExpect(status().isOk());

    mockMvc.perform(get("/articles")).andExpect(status().isOk());

    mockMvc
        .perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"user\":{\"email\":\"invalid-email\",\"username\":\"test\",\"password\":\"password\"}}"))
        .andExpect(status().isUnprocessableEntity());

    mockMvc
        .perform(
            post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user\":{\"email\":\"test@test.com\",\"password\":\"password\"}}"))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void should_handle_cors_configuration() throws Exception {
    mockMvc
        .perform(get("/tags").header("Origin", "http://localhost:3000"))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/articles").header("Origin", "https://example.com"))
        .andExpect(status().isOk());
  }

  @Test
  public void should_validate_jwt_filter_chain() throws Exception {
    mockMvc
        .perform(get("/user").header("Authorization", "Token invalid-token"))
        .andExpect(status().isUnauthorized());

    mockMvc
        .perform(get("/user").header("Authorization", "Bearer invalid-token"))
        .andExpect(status().isUnauthorized());

    mockMvc
        .perform(get("/user").header("Authorization", "invalid-format"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void should_handle_missing_authorization_header() throws Exception {
    mockMvc
        .perform(
            post("/articles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"article\":{\"title\":\"test\",\"description\":\"test\",\"body\":\"test\"}}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void should_handle_malformed_authorization_header() throws Exception {
    mockMvc.perform(get("/user").header("Authorization", "")).andExpect(status().isUnauthorized());

    mockMvc
        .perform(get("/user").header("Authorization", "Token"))
        .andExpect(status().isUnauthorized());
  }
}
