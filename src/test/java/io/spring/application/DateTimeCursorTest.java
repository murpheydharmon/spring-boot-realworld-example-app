package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class DateTimeCursorTest {

  @Test
  void should_create_datetime_cursor_with_timestamp() {
    DateTime now = DateTime.now();
    DateTimeCursor cursor = new DateTimeCursor(now);

    assertNotNull(cursor);
    assertEquals(now, cursor.getData());
  }

  @Test
  void should_convert_to_string() {
    DateTime now = DateTime.now();
    DateTimeCursor cursor = new DateTimeCursor(now);

    String cursorString = cursor.toString();
    assertNotNull(cursorString);
    assertFalse(cursorString.isEmpty());
    assertEquals(String.valueOf(now.getMillis()), cursorString);
  }

  @Test
  void should_parse_from_string() {
    DateTime now = DateTime.now();
    DateTimeCursor originalCursor = new DateTimeCursor(now);
    String cursorString = originalCursor.toString();

    DateTime parsedDateTime = DateTimeCursor.parse(cursorString);

    assertNotNull(parsedDateTime);
    assertEquals(originalCursor.getData().getMillis(), parsedDateTime.getMillis());
  }

  @Test
  void should_handle_null_cursor_string() {
    DateTime result = DateTimeCursor.parse(null);
    assertNull(result);
  }

  @Test
  void should_handle_invalid_cursor_string() {
    assertThrows(NumberFormatException.class, () -> {
      DateTimeCursor.parse("invalid-cursor-string");
    });
  }
}
