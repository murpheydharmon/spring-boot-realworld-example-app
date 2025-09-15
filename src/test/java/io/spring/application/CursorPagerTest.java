package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class CursorPagerTest {

  @Test
  void should_create_pager_with_next_direction_and_extra_data() {
    List<Node> data = Arrays.asList(createMockNode("1"), createMockNode("2"));
    
    CursorPager<Node> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, true);
    
    assertEquals(data, pager.getData());
    assertTrue(pager.hasNext());
    assertFalse(pager.hasPrevious());
    assertTrue(pager.isNext());
    assertFalse(pager.isPrevious());
  }

  @Test
  void should_create_pager_with_next_direction_and_no_extra_data() {
    List<Node> data = Arrays.asList(createMockNode("1"), createMockNode("2"));
    
    CursorPager<Node> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);
    
    assertEquals(data, pager.getData());
    assertFalse(pager.hasNext());
    assertFalse(pager.hasPrevious());
    assertFalse(pager.isNext());
    assertFalse(pager.isPrevious());
  }

  @Test
  void should_create_pager_with_prev_direction_and_extra_data() {
    List<Node> data = Arrays.asList(createMockNode("1"), createMockNode("2"));
    
    CursorPager<Node> pager = new CursorPager<>(data, CursorPager.Direction.PREV, true);
    
    assertEquals(data, pager.getData());
    assertFalse(pager.hasNext());
    assertTrue(pager.hasPrevious());
    assertFalse(pager.isNext());
    assertTrue(pager.isPrevious());
  }

  @Test
  void should_create_pager_with_prev_direction_and_no_extra_data() {
    List<Node> data = Arrays.asList(createMockNode("1"), createMockNode("2"));
    
    CursorPager<Node> pager = new CursorPager<>(data, CursorPager.Direction.PREV, false);
    
    assertEquals(data, pager.getData());
    assertFalse(pager.hasNext());
    assertFalse(pager.hasPrevious());
    assertFalse(pager.isNext());
    assertFalse(pager.isPrevious());
  }

  @Test
  void should_return_start_cursor_for_non_empty_data() {
    PageCursor mockCursor = Mockito.mock(PageCursor.class);
    Node firstNode = createMockNode("1");
    Mockito.when(firstNode.getCursor()).thenReturn(mockCursor);
    
    List<Node> data = Arrays.asList(firstNode, createMockNode("2"));
    CursorPager<Node> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);
    
    assertEquals(mockCursor, pager.getStartCursor());
  }

  @Test
  void should_return_end_cursor_for_non_empty_data() {
    PageCursor mockCursor = Mockito.mock(PageCursor.class);
    Node lastNode = createMockNode("2");
    Mockito.when(lastNode.getCursor()).thenReturn(mockCursor);
    
    List<Node> data = Arrays.asList(createMockNode("1"), lastNode);
    CursorPager<Node> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, false);
    
    assertEquals(mockCursor, pager.getEndCursor());
  }

  @Test
  void should_return_null_cursors_for_empty_data() {
    List<Node> emptyData = Collections.emptyList();
    CursorPager<Node> pager = new CursorPager<>(emptyData, CursorPager.Direction.NEXT, false);
    
    assertNull(pager.getStartCursor());
    assertNull(pager.getEndCursor());
  }

  @Test
  void should_handle_single_item_data() {
    PageCursor mockCursor = Mockito.mock(PageCursor.class);
    Node singleNode = createMockNode("1");
    Mockito.when(singleNode.getCursor()).thenReturn(mockCursor);
    
    List<Node> data = Arrays.asList(singleNode);
    CursorPager<Node> pager = new CursorPager<>(data, CursorPager.Direction.NEXT, true);
    
    assertEquals(mockCursor, pager.getStartCursor());
    assertEquals(mockCursor, pager.getEndCursor());
    assertTrue(pager.hasNext());
    assertFalse(pager.hasPrevious());
  }

  private Node createMockNode(String id) {
    Node node = Mockito.mock(Node.class);
    return node;
  }
}
