package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.application.TagsQueryService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TagDatafetcherTest {

  @Mock private TagsQueryService tagsQueryService;

  @InjectMocks private TagDatafetcher tagDatafetcher;

  @BeforeEach
  void setUp() {
  }

  @Test
  void should_return_all_tags_when_tags_exist() {
    List<String> expectedTags = Arrays.asList("java", "spring", "testing", "api");
    when(tagsQueryService.allTags()).thenReturn(expectedTags);

    List<String> result = tagDatafetcher.getTags();

    assertEquals(expectedTags, result);
    assertEquals(4, result.size());
    assertTrue(result.contains("java"));
    assertTrue(result.contains("spring"));
    assertTrue(result.contains("testing"));
    assertTrue(result.contains("api"));
    verify(tagsQueryService).allTags();
  }

  @Test
  void should_return_empty_list_when_no_tags_exist() {
    when(tagsQueryService.allTags()).thenReturn(Collections.emptyList());

    List<String> result = tagDatafetcher.getTags();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(tagsQueryService).allTags();
  }

  @Test
  void should_return_single_tag_when_only_one_exists() {
    List<String> singleTag = Arrays.asList("java");
    when(tagsQueryService.allTags()).thenReturn(singleTag);

    List<String> result = tagDatafetcher.getTags();

    assertEquals(1, result.size());
    assertEquals("java", result.get(0));
    verify(tagsQueryService).allTags();
  }

  @Test
  void should_preserve_tag_order_from_service() {
    List<String> orderedTags = Arrays.asList("z-tag", "a-tag", "m-tag");
    when(tagsQueryService.allTags()).thenReturn(orderedTags);

    List<String> result = tagDatafetcher.getTags();

    assertEquals(orderedTags, result);
    assertEquals("z-tag", result.get(0));
    assertEquals("a-tag", result.get(1));
    assertEquals("m-tag", result.get(2));
    verify(tagsQueryService).allTags();
  }

  @Test
  void should_handle_tags_with_special_characters() {
    List<String> specialTags = Arrays.asList("spring-boot", "real_world", "api-design", "test.coverage");
    when(tagsQueryService.allTags()).thenReturn(specialTags);

    List<String> result = tagDatafetcher.getTags();

    assertEquals(specialTags, result);
    assertTrue(result.contains("spring-boot"));
    assertTrue(result.contains("real_world"));
    assertTrue(result.contains("api-design"));
    assertTrue(result.contains("test.coverage"));
    verify(tagsQueryService).allTags();
  }
}
