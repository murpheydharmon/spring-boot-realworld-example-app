package io.spring.infrastructure.service;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtTokenExpirationTest {

  private JwtService jwtService;
  private JwtService shortExpiryJwtService;

  @BeforeEach
  public void setUp() {
    jwtService = new DefaultJwtService("123123123123123123123123123123123123123123123123123123123123", 3600);
    shortExpiryJwtService = new DefaultJwtService("123123123123123123123123123123123123123123123123123123123123", 1);
  }

  @Test
  public void should_handle_token_expiration_edge_cases() throws InterruptedException {
    User user = new User("email@email.com", "username", "123", "", "");
    String token = shortExpiryJwtService.toToken(user);
    
    Assertions.assertNotNull(token);
    Optional<String> validResult = shortExpiryJwtService.getSubFromToken(token);
    Assertions.assertTrue(validResult.isPresent());
    
    Thread.sleep(2000);
    
    Optional<String> expiredResult = shortExpiryJwtService.getSubFromToken(token);
    Assertions.assertFalse(expiredResult.isPresent());
  }

  @Test
  public void should_validate_token_signature_tampering() {
    User user = new User("email@email.com", "username", "123", "", "");
    String validToken = jwtService.toToken(user);
    
    String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";
    
    Optional<String> result = jwtService.getSubFromToken(tamperedToken);
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  public void should_handle_malformed_token_structure() {
    String malformedToken = "not.a.valid.jwt.token.structure";
    Optional<String> result = jwtService.getSubFromToken(malformedToken);
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  public void should_handle_empty_token() {
    Optional<String> result = jwtService.getSubFromToken("");
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  public void should_handle_null_token() {
    Optional<String> result = jwtService.getSubFromToken(null);
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  public void should_handle_token_with_invalid_signature_algorithm() {
    String tokenWithWrongAlgorithm = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6OTk5OTk5OTk5OX0.invalid_signature";
    Optional<String> result = jwtService.getSubFromToken(tokenWithWrongAlgorithm);
    Assertions.assertFalse(result.isPresent());
  }
}
