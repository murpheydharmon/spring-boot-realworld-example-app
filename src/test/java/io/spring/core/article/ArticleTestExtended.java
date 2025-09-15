package io.spring.core.article;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArticleTestExtended {

  private String testUserId;
  private Article testArticle;

  @BeforeEach
  void setUp() {
    testUserId = "user-123";
    List<String> tags = Arrays.asList("java", "spring");
    testArticle = new Article("Test Title", "Test description", "Test body", tags, testUserId);
  }

  @Test
  void should_create_article_with_valid_parameters() {
    assertNotNull(testArticle);
    assertEquals("Test Title", testArticle.getTitle());
    assertEquals("Test description", testArticle.getDescription());
    assertEquals("Test body", testArticle.getBody());
    assertEquals(testUserId, testArticle.getUserId());
    assertNotNull(testArticle.getId());
    assertNotNull(testArticle.getSlug());
    assertNotNull(testArticle.getCreatedAt());
    assertNotNull(testArticle.getUpdatedAt());
  }

  @Test
  void should_generate_slug_from_title() {
    String slug = testArticle.getSlug();
    assertNotNull(slug);
    assertEquals("test-title", slug);
  }

  @Test
  void should_have_tags() {
    assertEquals(2, testArticle.getTags().size());
    assertTrue(testArticle.getTags().stream().anyMatch(tag -> "java".equals(tag.getName())));
    assertTrue(testArticle.getTags().stream().anyMatch(tag -> "spring".equals(tag.getName())));
  }

  @Test
  void should_update_article_content() {
    String newTitle = "Updated Title";
    String newDescription = "Updated description";
    String newBody = "Updated body";

    testArticle.update(newTitle, newDescription, newBody);

    assertEquals(newTitle, testArticle.getTitle());
    assertEquals(newDescription, testArticle.getDescription());
    assertEquals(newBody, testArticle.getBody());
    assertNotNull(testArticle.getUpdatedAt());
  }

  @Test
  void should_not_update_with_null_title() {
    String originalTitle = testArticle.getTitle();
    
    testArticle.update(null, "New description", "New body");

    assertEquals(originalTitle, testArticle.getTitle());
    assertEquals("New description", testArticle.getDescription());
    assertEquals("New body", testArticle.getBody());
  }

  @Test
  void should_not_update_with_empty_title() {
    String originalTitle = testArticle.getTitle();
    
    testArticle.update("", "New description", "New body");

    assertEquals(originalTitle, testArticle.getTitle());
    assertEquals("New description", testArticle.getDescription());
    assertEquals("New body", testArticle.getBody());
  }

  @Test
  void should_not_update_with_null_description() {
    String originalDescription = testArticle.getDescription();
    
    testArticle.update("New title", null, "New body");

    assertEquals("New title", testArticle.getTitle());
    assertEquals(originalDescription, testArticle.getDescription());
    assertEquals("New body", testArticle.getBody());
  }

  @Test
  void should_not_update_with_null_body() {
    String originalBody = testArticle.getBody();
    
    testArticle.update("New title", "New description", null);

    assertEquals("New title", testArticle.getTitle());
    assertEquals("New description", testArticle.getDescription());
    assertEquals(originalBody, testArticle.getBody());
  }

  @Test
  void should_create_article_with_empty_tags() {
    Article articleWithoutTags = new Article("Title", "Description", "Body", Arrays.asList(), testUserId);
    
    assertNotNull(articleWithoutTags);
    assertTrue(articleWithoutTags.getTags().isEmpty());
  }

  @Test
  void should_handle_duplicate_tags() {
    List<String> duplicateTags = Arrays.asList("java", "spring", "java", "testing");
    Article article = new Article("Title", "Description", "Body", duplicateTags, testUserId);
    
    assertEquals(3, article.getTags().size());
    assertTrue(article.getTags().stream().anyMatch(tag -> "java".equals(tag.getName())));
    assertTrue(article.getTags().stream().anyMatch(tag -> "spring".equals(tag.getName())));
    assertTrue(article.getTags().stream().anyMatch(tag -> "testing".equals(tag.getName())));
  }

  @Test
  void should_generate_correct_slug_for_complex_title() {
    Article article = new Article("What? The Hell, World!", "desc", "body", Arrays.asList(), testUserId);
    assertEquals("what-the-hell-world!", article.getSlug());
  }
}
