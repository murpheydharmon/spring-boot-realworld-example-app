package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.infrastructure.mybatis.readservice.TagReadService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagsQueryServiceTest {

  @Mock private TagReadService tagReadService;

  @InjectMocks private TagsQueryService tagsQueryService;

  @Test
  void should_return_all_tags_when_tags_exist() {
    List<String> expectedTags = Arrays.asList("java", "spring", "testing", "api");
    when(tagReadService.all()).thenReturn(expectedTags);

    List<String> result = tagsQueryService.allTags();

    assertEquals(expectedTags, result);
    assertEquals(4, result.size());
    assertTrue(result.contains("java"));
    assertTrue(result.contains("spring"));
    assertTrue(result.contains("testing"));
    assertTrue(result.contains("api"));
  }

  @Test
  void should_return_empty_list_when_no_tags_exist() {
    when(tagReadService.all()).thenReturn(Collections.emptyList());

    List<String> result = tagsQueryService.allTags();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void should_return_single_tag_when_only_one_exists() {
    List<String> singleTag = Arrays.asList("java");
    when(tagReadService.all()).thenReturn(singleTag);

    List<String> result = tagsQueryService.allTags();

    assertEquals(1, result.size());
    assertEquals("java", result.get(0));
  }

  @Test
  void should_preserve_tag_order_from_read_service() {
    List<String> orderedTags = Arrays.asList("z-tag", "a-tag", "m-tag");
    when(tagReadService.all()).thenReturn(orderedTags);

    List<String> result = tagsQueryService.allTags();

    assertEquals(orderedTags, result);
    assertEquals("z-tag", result.get(0));
    assertEquals("a-tag", result.get(1));
    assertEquals("m-tag", result.get(2));
  }

  @Test
  void should_handle_tags_with_special_characters() {
    List<String> specialTags = Arrays.asList("spring-boot", "real_world", "api-design", "test.coverage");
    when(tagReadService.all()).thenReturn(specialTags);

    List<String> result = tagsQueryService.allTags();

    assertEquals(specialTags, result);
    assertTrue(result.contains("spring-boot"));
    assertTrue(result.contains("real_world"));
    assertTrue(result.contains("api-design"));
    assertTrue(result.contains("test.coverage"));
  }
}
