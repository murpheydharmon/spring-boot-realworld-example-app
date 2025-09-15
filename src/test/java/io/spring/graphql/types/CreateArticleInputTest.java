package io.spring.graphql.types;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CreateArticleInputTest {

  @Test
  void should_create_create_article_input_with_no_args_constructor() {
    CreateArticleInput input = new CreateArticleInput();

    assertNotNull(input);
    assertNull(input.getBody());
    assertNull(input.getDescription());
    assertNull(input.getTagList());
    assertNull(input.getTitle());
  }

  @Test
  void should_create_create_article_input_with_all_args_constructor() {
    String body = "This is the article body content.";
    String description = "Article description";
    List<String> tagList = Arrays.asList("java", "spring", "testing");
    String title = "Test Article Title";

    CreateArticleInput input = new CreateArticleInput(body, description, tagList, title);

    assertEquals(body, input.getBody());
    assertEquals(description, input.getDescription());
    assertEquals(tagList, input.getTagList());
    assertEquals(title, input.getTitle());
  }

  @Test
  void should_set_and_get_all_properties() {
    CreateArticleInput input = new CreateArticleInput();
    String body = "Updated article body content.";
    String description = "Updated description";
    List<String> tagList = Arrays.asList("updated", "tags");
    String title = "Updated Title";

    input.setBody(body);
    input.setDescription(description);
    input.setTagList(tagList);
    input.setTitle(title);

    assertEquals(body, input.getBody());
    assertEquals(description, input.getDescription());
    assertEquals(tagList, input.getTagList());
    assertEquals(title, input.getTitle());
  }

  @Test
  void should_implement_equals_and_hashcode() {
    List<String> tags = Arrays.asList("tag1", "tag2");
    
    CreateArticleInput input1 = new CreateArticleInput("body", "desc", tags, "title");
    CreateArticleInput input2 = new CreateArticleInput("body", "desc", tags, "title");
    CreateArticleInput input3 = new CreateArticleInput("different", "desc", tags, "title");

    assertEquals(input1, input2);
    assertNotEquals(input1, input3);
    assertEquals(input1.hashCode(), input2.hashCode());
    assertNotEquals(input1.hashCode(), input3.hashCode());
  }

  @Test
  void should_implement_to_string() {
    CreateArticleInput input = new CreateArticleInput();
    input.setTitle("Test Title");
    input.setBody("Test Body");

    String toString = input.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("CreateArticleInput{"));
    assertTrue(toString.contains("Test Title"));
    assertTrue(toString.contains("Test Body"));
  }

  @Test
  void should_create_builder() {
    CreateArticleInput.Builder builder = CreateArticleInput.newBuilder();

    assertNotNull(builder);
  }

  @Test
  void should_build_create_article_input_with_builder() {
    String body = "Builder body content";
    String description = "Builder description";
    List<String> tagList = Arrays.asList("builder", "test");
    String title = "Builder Title";

    CreateArticleInput input = CreateArticleInput.newBuilder()
        .body(body)
        .description(description)
        .tagList(tagList)
        .title(title)
        .build();

    assertEquals(body, input.getBody());
    assertEquals(description, input.getDescription());
    assertEquals(tagList, input.getTagList());
    assertEquals(title, input.getTitle());
  }

  @Test
  void should_build_create_article_input_with_partial_builder() {
    String title = "Partial Title";
    String body = "Partial Body";

    CreateArticleInput input = CreateArticleInput.newBuilder()
        .title(title)
        .body(body)
        .build();

    assertEquals(title, input.getTitle());
    assertEquals(body, input.getBody());
    assertNull(input.getDescription());
    assertNull(input.getTagList());
  }

  @Test
  void should_handle_null_values() {
    CreateArticleInput input = new CreateArticleInput();
    
    input.setBody(null);
    input.setDescription(null);
    input.setTagList(null);
    input.setTitle(null);

    assertNull(input.getBody());
    assertNull(input.getDescription());
    assertNull(input.getTagList());
    assertNull(input.getTitle());
  }

  @Test
  void should_handle_empty_strings() {
    CreateArticleInput input = new CreateArticleInput();
    
    input.setBody("");
    input.setDescription("");
    input.setTitle("");

    assertEquals("", input.getBody());
    assertEquals("", input.getDescription());
    assertEquals("", input.getTitle());
  }

  @Test
  void should_handle_empty_tag_list() {
    CreateArticleInput input = new CreateArticleInput();
    List<String> emptyTags = Collections.emptyList();
    
    input.setTagList(emptyTags);

    assertEquals(emptyTags, input.getTagList());
    assertTrue(input.getTagList().isEmpty());
  }

  @Test
  void should_handle_single_tag() {
    CreateArticleInput input = new CreateArticleInput();
    List<String> singleTag = Collections.singletonList("single-tag");
    
    input.setTagList(singleTag);

    assertEquals(singleTag, input.getTagList());
    assertEquals(1, input.getTagList().size());
    assertEquals("single-tag", input.getTagList().get(0));
  }

  @Test
  void should_handle_multiple_tags() {
    CreateArticleInput input = new CreateArticleInput();
    List<String> multipleTags = Arrays.asList("tag1", "tag2", "tag3", "tag4");
    
    input.setTagList(multipleTags);

    assertEquals(multipleTags, input.getTagList());
    assertEquals(4, input.getTagList().size());
  }

  @Test
  void should_handle_builder_method_chaining() {
    CreateArticleInput.Builder builder = CreateArticleInput.newBuilder();
    
    CreateArticleInput.Builder result = builder
        .title("Chain Title")
        .body("Chain Body")
        .description("Chain Description");
    
    assertSame(builder, result);
    
    CreateArticleInput input = result.build();
    assertEquals("Chain Title", input.getTitle());
    assertEquals("Chain Body", input.getBody());
    assertEquals("Chain Description", input.getDescription());
  }

  @Test
  void should_handle_different_tag_lists_in_equals() {
    List<String> tags1 = Arrays.asList("tag1", "tag2");
    List<String> tags2 = Arrays.asList("tag3", "tag4");
    
    CreateArticleInput input1 = new CreateArticleInput("body", "desc", tags1, "title");
    CreateArticleInput input2 = new CreateArticleInput("body", "desc", tags2, "title");

    assertNotEquals(input1, input2);
    assertNotEquals(input1.hashCode(), input2.hashCode());
  }

  @Test
  void should_handle_long_content() {
    CreateArticleInput input = new CreateArticleInput();
    String longBody = "This is a very long article body that contains multiple paragraphs and extensive content to test how the CreateArticleInput handles large text content without any issues.";
    String longTitle = "This is a Very Long Article Title That Tests How the Input Handles Extended Titles";
    
    input.setBody(longBody);
    input.setTitle(longTitle);

    assertEquals(longBody, input.getBody());
    assertEquals(longTitle, input.getTitle());
  }
}
