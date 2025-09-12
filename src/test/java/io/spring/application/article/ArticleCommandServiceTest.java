package io.spring.application.article;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Arrays;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArticleCommandServiceTest {

  @Autowired
  private ArticleCommandService articleCommandService;

  @Autowired
  private ArticleRepository articleRepository;

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    user = new User("test" + uniqueId + "@test.com", "testuser" + uniqueId, "password", "bio", "image");
    userRepository.save(user);
  }

  @Test
  public void should_create_article_with_tags_integration() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Test Article")
        .description("Test Description")
        .body("Test Body")
        .tagList(Arrays.asList("java", "spring", "test"))
        .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertNotNull(article);
    Assertions.assertEquals("Test Article", article.getTitle());
    Assertions.assertEquals("Test Description", article.getDescription());
    Assertions.assertEquals("Test Body", article.getBody());
    Assertions.assertEquals(user.getId(), article.getUserId());
    Assertions.assertEquals(3, article.getTags().size());

    Article savedArticle = articleRepository.findById(article.getId()).orElse(null);
    Assertions.assertNotNull(savedArticle);
    Assertions.assertEquals(article.getTitle(), savedArticle.getTitle());
  }

  @Test
  public void should_update_article_content_integration() {
    Article originalArticle = new Article("Original Title", "Original Description", "Original Body", 
        Arrays.asList("java"), user.getId());
    articleRepository.save(originalArticle);

    UpdateArticleParam updateParam = new UpdateArticleParam("Updated Title", "Updated Body", "Updated Description");
    Article updatedArticle = articleCommandService.updateArticle(originalArticle, updateParam);

    Assertions.assertEquals("Updated Title", updatedArticle.getTitle());
    Assertions.assertEquals("Updated Body", updatedArticle.getBody());
    Assertions.assertEquals("Updated Description", updatedArticle.getDescription());

    Article savedArticle = articleRepository.findById(originalArticle.getId()).orElse(null);
    Assertions.assertNotNull(savedArticle);
    Assertions.assertEquals("Updated Title", savedArticle.getTitle());
  }

  @Test
  public void should_validate_duplicate_title_constraint() {
    NewArticleParam param1 = NewArticleParam.builder()
        .title("Unique Title")
        .description("Description 1")
        .body("Body 1")
        .tagList(Arrays.asList("java"))
        .build();

    NewArticleParam param2 = NewArticleParam.builder()
        .title("Unique Title")
        .description("Description 2")
        .body("Body 2")
        .tagList(Arrays.asList("spring"))
        .build();

    Article article1 = articleCommandService.createArticle(param1, user);
    Assertions.assertNotNull(article1);

    Assertions.assertThrows(javax.validation.ConstraintViolationException.class, () -> {
      articleCommandService.createArticle(param2, user);
    });
  }

  @Test
  public void should_validate_article_constraints() {
    NewArticleParam param = NewArticleParam.builder()
        .title("Valid Title")
        .description("Valid Description")
        .body("Valid Body")
        .tagList(Collections.emptyList())
        .build();

    Article article = articleCommandService.createArticle(param, user);

    Assertions.assertNotNull(article);
    Assertions.assertEquals("Valid Title", article.getTitle());
    Assertions.assertTrue(article.getTags().isEmpty());
  }

  @Test
  public void should_preserve_user_id_on_update() {
    Article originalArticle = new Article("Title", "Description", "Body", 
        Arrays.asList("java"), user.getId());
    articleRepository.save(originalArticle);

    UpdateArticleParam updateParam = new UpdateArticleParam("New Title", "New Body", "New Description");
    Article updatedArticle = articleCommandService.updateArticle(originalArticle, updateParam);

    Assertions.assertEquals(user.getId(), updatedArticle.getUserId());
  }

  @Test
  public void should_update_timestamps_on_modification() {
    Article originalArticle = new Article("Title", "Description", "Body", 
        Arrays.asList("java"), user.getId());
    articleRepository.save(originalArticle);

    UpdateArticleParam updateParam = new UpdateArticleParam("New Title", "New Body", "New Description");
    Article updatedArticle = articleCommandService.updateArticle(originalArticle, updateParam);

    Assertions.assertTrue(updatedArticle.getUpdatedAt().isAfter(originalArticle.getCreatedAt()) || 
                         updatedArticle.getUpdatedAt().isEqual(originalArticle.getCreatedAt()));
  }
}
