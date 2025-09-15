package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PageTest {

  @Test
  void should_create_page_with_default_values() {
    Page page = new Page();

    assertNotNull(page);
    assertEquals(0, page.getOffset());
    assertEquals(20, page.getLimit());
  }

  @Test
  void should_create_page_with_custom_values() {
    Page page = new Page(10, 50);

    assertNotNull(page);
    assertEquals(10, page.getOffset());
    assertEquals(50, page.getLimit());
  }

  @Test
  void should_limit_max_limit_to_100() {
    Page page = new Page(0, 200);

    assertEquals(100, page.getLimit());
  }

  @Test
  void should_ignore_negative_offset() {
    Page page = new Page(-5, 20);

    assertEquals(0, page.getOffset());
    assertEquals(20, page.getLimit());
  }

  @Test
  void should_ignore_negative_limit() {
    Page page = new Page(0, -10);

    assertEquals(0, page.getOffset());
    assertEquals(20, page.getLimit());
  }
}
