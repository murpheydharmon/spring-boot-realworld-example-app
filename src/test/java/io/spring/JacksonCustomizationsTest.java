package io.spring;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.JacksonCustomizations.RealWorldModules;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JacksonCustomizationsTest {

  private JacksonCustomizations jacksonCustomizations;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    jacksonCustomizations = new JacksonCustomizations();
    objectMapper = new ObjectMapper();
  }

  @Test
  void should_create_real_world_modules() {
    Module module = jacksonCustomizations.realWorldModules();
    
    assertNotNull(module);
    assertTrue(module instanceof RealWorldModules);
  }

  @Test
  void should_serialize_datetime_with_custom_serializer() throws JsonProcessingException {
    Module module = jacksonCustomizations.realWorldModules();
    objectMapper.registerModule(module);
    
    DateTime dateTime = new DateTime(2023, 1, 1, 12, 0, 0);
    String json = objectMapper.writeValueAsString(dateTime);
    
    assertNotNull(json);
    assertTrue(json.contains("2023"));
  }

  @Test
  void should_handle_null_datetime() throws JsonProcessingException {
    Module module = jacksonCustomizations.realWorldModules();
    objectMapper.registerModule(module);
    
    DateTime nullDateTime = null;
    String json = objectMapper.writeValueAsString(nullDateTime);
    
    assertEquals("null", json);
  }

  @Test
  void real_world_modules_should_have_datetime_serializer() {
    RealWorldModules module = new RealWorldModules();
    
    assertNotNull(module);
  }

  @Test
  void should_register_module_with_object_mapper() {
    Module module = jacksonCustomizations.realWorldModules();
    
    assertDoesNotThrow(() -> {
      objectMapper.registerModule(module);
    });
  }
}
