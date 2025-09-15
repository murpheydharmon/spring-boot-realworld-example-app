package io.spring.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
public class CustomizeExceptionHandlerTest {

  @Mock private WebRequest webRequest;
  @Mock private BindingResult bindingResult;

  @InjectMocks private CustomizeExceptionHandler exceptionHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void should_handle_invalid_request_exception() {
    FieldError fieldError = new FieldError("user", "email", null, false, new String[]{"Email"}, null, "Email is invalid");
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
    
    InvalidRequestException exception = new InvalidRequestException(bindingResult);

    ResponseEntity<Object> response = exceptionHandler.handleInvalidRequest(exception, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody() instanceof ErrorResource);
    
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertEquals(1, errorResource.getFieldErrors().size());
    
    FieldErrorResource fieldErrorResource = errorResource.getFieldErrors().get(0);
    assertEquals("user", fieldErrorResource.getResource());
    assertEquals("email", fieldErrorResource.getField());
    assertNotNull(fieldErrorResource.getCode());
    assertEquals("Email is invalid", fieldErrorResource.getMessage());
  }

  @Test
  void should_handle_invalid_authentication_exception() {
    InvalidAuthenticationException exception = new InvalidAuthenticationException();

    ResponseEntity<Object> response = exceptionHandler.handleInvalidAuthentication(exception, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody() instanceof HashMap);
    
    @SuppressWarnings("unchecked")
    HashMap<String, Object> body = (HashMap<String, Object>) response.getBody();
    assertNotNull(body.get("message"));
  }

  @Test
  void should_handle_method_argument_not_valid_exception() {
    FieldError fieldError = new FieldError("article", "title", null, false, new String[]{"NotBlank"}, null, "Title cannot be blank");
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));
    
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<Object> response = exceptionHandler.handleMethodArgumentNotValid(
        exception, null, HttpStatus.BAD_REQUEST, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody() instanceof ErrorResource);
    
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertEquals(1, errorResource.getFieldErrors().size());
    
    FieldErrorResource fieldErrorResource = errorResource.getFieldErrors().get(0);
    assertEquals("article", fieldErrorResource.getResource());
    assertEquals("title", fieldErrorResource.getField());
    assertNotNull(fieldErrorResource.getCode());
    assertEquals("Title cannot be blank", fieldErrorResource.getMessage());
  }

  @Test
  void should_handle_constraint_violation_exception() {
    Set<ConstraintViolation<?>> violations = Set.of();
    ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

    ErrorResource response = exceptionHandler.handleConstraintViolation(exception, webRequest);

    assertNotNull(response);
    assertEquals(0, response.getFieldErrors().size());
  }

  @Test
  void should_handle_multiple_field_errors() {
    FieldError emailError = new FieldError("user", "email", null, false, new String[]{"Email"}, null, "Email is invalid");
    FieldError usernameError = new FieldError("user", "username", null, false, new String[]{"NotBlank"}, null, "Username is required");
    when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(emailError, usernameError));
    
    InvalidRequestException exception = new InvalidRequestException(bindingResult);

    ResponseEntity<Object> response = exceptionHandler.handleInvalidRequest(exception, webRequest);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    ErrorResource errorResource = (ErrorResource) response.getBody();
    assertEquals(2, errorResource.getFieldErrors().size());
  }

}
