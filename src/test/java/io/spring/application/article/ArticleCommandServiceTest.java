package io.spring.application.article;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleCommandServiceTest {

  @Mock private ArticleRepository articleRepository;

  @InjectMocks private ArticleCommandService articleCommandService;

  private User testUser;
  private NewArticleParam newArticleParam;
  private UpdateArticleParam updateArticleParam;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    
    List<String> tags = Arrays.asList("java", "spring");
    newArticleParam = new NewArticleParam("Test Title", "Test Description", "Test Body", tags);
    
    updateArticleParam = new UpdateArticleParam("Updated Title", "Updated Description", "Updated Body");
  }

  @Test
  void should_create_article_successfully() {
    Article result = articleCommandService.createArticle(newArticleParam, testUser);

    assertNotNull(result);
    assertEquals("Test Title", result.getTitle());
    assertEquals("Test Description", result.getDescription());
    assertEquals("Test Body", result.getBody());
    assertEquals(testUser.getId(), result.getUserId());
    assertEquals(2, result.getTags().size());
    
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  void should_update_article_successfully() {
    Article existingArticle = new Article("Old Title", "Old Description", "Old Body", Arrays.asList("old"), testUser.getId());
    
    Article result = articleCommandService.updateArticle(existingArticle, updateArticleParam);

    assertNotNull(result);
    assertEquals(existingArticle, result);
    
    verify(articleRepository).save(existingArticle);
  }

  @Test
  void should_handle_null_tags_in_create_article() {
    List<String> emptyTags = Arrays.asList();
    NewArticleParam paramWithEmptyTags = new NewArticleParam("Title", "Description", "Body", emptyTags);
    
    Article result = articleCommandService.createArticle(paramWithEmptyTags, testUser);

    assertNotNull(result);
    assertEquals("Title", result.getTitle());
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  void should_handle_partial_update() {
    Article existingArticle = new Article("Old Title", "Old Description", "Old Body", Arrays.asList("old"), testUser.getId());
    UpdateArticleParam partialUpdate = new UpdateArticleParam("New Title", null, null);
    
    Article result = articleCommandService.updateArticle(existingArticle, partialUpdate);

    assertNotNull(result);
    assertEquals("New Title", result.getTitle());
    verify(articleRepository).save(existingArticle);
  }
}
