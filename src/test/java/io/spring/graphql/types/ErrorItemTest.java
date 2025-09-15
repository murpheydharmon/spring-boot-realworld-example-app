package io.spring.graphql.types;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ErrorItemTest {

  @Test
  void should_create_error_item_with_no_args_constructor() {
    ErrorItem errorItem = new ErrorItem();

    assertNotNull(errorItem);
    assertNull(errorItem.getKey());
    assertNull(errorItem.getValue());
  }

  @Test
  void should_create_error_item_with_all_args_constructor() {
    String key = "validation_error";
    List<String> value = Arrays.asList("Field is required", "Invalid format");

    ErrorItem errorItem = new ErrorItem(key, value);

    assertEquals(key, errorItem.getKey());
    assertEquals(value, errorItem.getValue());
  }

  @Test
  void should_set_and_get_all_properties() {
    ErrorItem errorItem = new ErrorItem();
    String key = "updated_error";
    List<String> value = Arrays.asList("Updated error message");

    errorItem.setKey(key);
    errorItem.setValue(value);

    assertEquals(key, errorItem.getKey());
    assertEquals(value, errorItem.getValue());
  }

  @Test
  void should_implement_equals_and_hashcode() {
    List<String> messages = Arrays.asList("Error message");
    
    ErrorItem errorItem1 = new ErrorItem("key", messages);
    ErrorItem errorItem2 = new ErrorItem("key", messages);
    ErrorItem errorItem3 = new ErrorItem("different-key", messages);

    assertEquals(errorItem1, errorItem2);
    assertNotEquals(errorItem1, errorItem3);
    assertEquals(errorItem1.hashCode(), errorItem2.hashCode());
    assertNotEquals(errorItem1.hashCode(), errorItem3.hashCode());
  }

  @Test
  void should_implement_to_string() {
    ErrorItem errorItem = new ErrorItem();
    errorItem.setKey("test_error");

    String toString = errorItem.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("ErrorItem{"));
    assertTrue(toString.contains("test_error"));
  }

  @Test
  void should_create_builder() {
    ErrorItem.Builder builder = ErrorItem.newBuilder();

    assertNotNull(builder);
  }

  @Test
  void should_build_error_item_with_builder() {
    String key = "builder_error";
    List<String> value = Arrays.asList("Builder error message");

    ErrorItem errorItem = ErrorItem.newBuilder()
        .key(key)
        .value(value)
        .build();

    assertEquals(key, errorItem.getKey());
    assertEquals(value, errorItem.getValue());
  }

  @Test
  void should_build_error_item_with_partial_builder() {
    String key = "partial_error";

    ErrorItem errorItem = ErrorItem.newBuilder()
        .key(key)
        .build();

    assertEquals(key, errorItem.getKey());
    assertNull(errorItem.getValue());
  }

  @Test
  void should_handle_null_values() {
    ErrorItem errorItem = new ErrorItem();
    
    errorItem.setKey(null);
    errorItem.setValue(null);

    assertNull(errorItem.getKey());
    assertNull(errorItem.getValue());
  }

  @Test
  void should_handle_empty_key() {
    ErrorItem errorItem = new ErrorItem();
    
    errorItem.setKey("");

    assertEquals("", errorItem.getKey());
  }

  @Test
  void should_handle_empty_value_list() {
    ErrorItem errorItem = new ErrorItem();
    List<String> emptyList = Collections.emptyList();
    
    errorItem.setValue(emptyList);

    assertEquals(emptyList, errorItem.getValue());
    assertTrue(errorItem.getValue().isEmpty());
  }

  @Test
  void should_handle_single_error_message() {
    ErrorItem errorItem = new ErrorItem();
    List<String> singleMessage = Collections.singletonList("Single error message");
    
    errorItem.setValue(singleMessage);

    assertEquals(singleMessage, errorItem.getValue());
    assertEquals(1, errorItem.getValue().size());
    assertEquals("Single error message", errorItem.getValue().get(0));
  }

  @Test
  void should_handle_multiple_error_messages() {
    ErrorItem errorItem = new ErrorItem();
    List<String> multipleMessages = Arrays.asList(
        "First error message",
        "Second error message", 
        "Third error message"
    );
    
    errorItem.setValue(multipleMessages);

    assertEquals(multipleMessages, errorItem.getValue());
    assertEquals(3, errorItem.getValue().size());
  }

  @Test
  void should_handle_builder_method_chaining() {
    ErrorItem.Builder builder = ErrorItem.newBuilder();
    
    ErrorItem.Builder result = builder
        .key("chain_error")
        .value(Arrays.asList("Chain error message"));
    
    assertSame(builder, result);
    
    ErrorItem errorItem = result.build();
    assertEquals("chain_error", errorItem.getKey());
    assertEquals(Arrays.asList("Chain error message"), errorItem.getValue());
  }

  @Test
  void should_handle_different_values_in_equals() {
    List<String> messages1 = Arrays.asList("Message 1");
    List<String> messages2 = Arrays.asList("Message 2");
    
    ErrorItem errorItem1 = new ErrorItem("key", messages1);
    ErrorItem errorItem2 = new ErrorItem("key", messages2);

    assertNotEquals(errorItem1, errorItem2);
    assertNotEquals(errorItem1.hashCode(), errorItem2.hashCode());
  }

  @Test
  void should_handle_validation_error_scenario() {
    ErrorItem errorItem = new ErrorItem();
    errorItem.setKey("email");
    errorItem.setValue(Arrays.asList("is required", "must be valid email format"));

    assertEquals("email", errorItem.getKey());
    assertEquals(2, errorItem.getValue().size());
    assertTrue(errorItem.getValue().contains("is required"));
    assertTrue(errorItem.getValue().contains("must be valid email format"));
  }

  @Test
  void should_handle_authentication_error_scenario() {
    ErrorItem errorItem = new ErrorItem("authentication", 
        Collections.singletonList("Invalid credentials"));

    assertEquals("authentication", errorItem.getKey());
    assertEquals(1, errorItem.getValue().size());
    assertEquals("Invalid credentials", errorItem.getValue().get(0));
  }
}
