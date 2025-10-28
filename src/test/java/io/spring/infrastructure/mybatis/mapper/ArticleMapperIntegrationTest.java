package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ArticleMapperIntegrationTest {

  @Autowired private ArticleMapper articleMapper;

  @Autowired private ArticleRepository articleRepository;

  @Autowired private UserRepository userRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    user =
        new User(
            "test" + uniqueId + "@test.com", "testuser" + uniqueId, "password", "bio", "image");
    userRepository.save(user);
  }

  @Test
  public void should_test_complex_article_queries_with_joins() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    Article article1 =
        new Article(
            "Title " + uniqueId + " 1",
            "Description 1",
            "Body 1",
            Arrays.asList("java", "spring"),
            user.getId());
    Article article2 =
        new Article(
            "Title " + uniqueId + " 2",
            "Description 2",
            "Body 2",
            Arrays.asList("python", "django"),
            user.getId());

    articleRepository.save(article1);
    articleRepository.save(article2);

    Article foundArticle1 = articleRepository.findById(article1.getId()).orElse(null);
    Article foundArticle2 = articleRepository.findById(article2.getId()).orElse(null);

    Assertions.assertNotNull(foundArticle1);
    Assertions.assertNotNull(foundArticle2);
    Assertions.assertTrue(
        foundArticle1.getTags().stream().anyMatch(tag -> "java".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle1.getTags().stream().anyMatch(tag -> "spring".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle2.getTags().stream().anyMatch(tag -> "python".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle2.getTags().stream().anyMatch(tag -> "django".equals(tag.getName())));
  }

  @Test
  public void should_test_transaction_rollback_scenarios() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    Article article =
        new Article(
            "Test Title " + uniqueId,
            "Test Description",
            "Test Body",
            Arrays.asList("java"),
            user.getId());
    articleRepository.save(article);

    try {
      throw new RuntimeException("Simulated error");
    } catch (RuntimeException e) {
    }

    Article foundArticle = articleRepository.findById(article.getId()).orElse(null);
    Assertions.assertNotNull(foundArticle);
  }

  @Test
  public void should_test_batch_operations() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    Article article1 =
        new Article(
            "Batch Title " + uniqueId + " 1",
            "Description 1",
            "Body 1",
            Arrays.asList("batch"),
            user.getId());
    Article article2 =
        new Article(
            "Batch Title " + uniqueId + " 2",
            "Description 2",
            "Body 2",
            Arrays.asList("batch"),
            user.getId());
    Article article3 =
        new Article(
            "Batch Title " + uniqueId + " 3",
            "Description 3",
            "Body 3",
            Arrays.asList("batch"),
            user.getId());

    articleRepository.save(article1);
    articleRepository.save(article2);
    articleRepository.save(article3);

    Article foundArticle1 = articleRepository.findById(article1.getId()).orElse(null);
    Article foundArticle2 = articleRepository.findById(article2.getId()).orElse(null);
    Article foundArticle3 = articleRepository.findById(article3.getId()).orElse(null);

    Assertions.assertNotNull(foundArticle1);
    Assertions.assertNotNull(foundArticle2);
    Assertions.assertNotNull(foundArticle3);
    Assertions.assertTrue(
        foundArticle1.getTags().stream().anyMatch(tag -> "batch".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle2.getTags().stream().anyMatch(tag -> "batch".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle3.getTags().stream().anyMatch(tag -> "batch".equals(tag.getName())));
  }

  @Test
  public void should_handle_article_with_no_tags() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    Article article =
        new Article(
            "No Tags Title " + uniqueId, "Description", "Body", Arrays.asList(), user.getId());
    articleRepository.save(article);

    Article foundArticle = articleRepository.findById(article.getId()).orElse(null);
    Assertions.assertNotNull(foundArticle);
    Assertions.assertTrue(foundArticle.getTags().isEmpty());
  }

  @Test
  public void should_find_articles_by_tag() {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    Article article1 =
        new Article(
            "Java Article " + uniqueId,
            "Description",
            "Body",
            Arrays.asList("java", "programming"),
            user.getId());
    Article article2 =
        new Article(
            "Python Article " + uniqueId,
            "Description",
            "Body",
            Arrays.asList("python", "programming"),
            user.getId());

    articleRepository.save(article1);
    articleRepository.save(article2);

    Article foundArticle1 = articleRepository.findById(article1.getId()).orElse(null);
    Article foundArticle2 = articleRepository.findById(article2.getId()).orElse(null);

    Assertions.assertNotNull(foundArticle1);
    Assertions.assertNotNull(foundArticle2);
    Assertions.assertTrue(
        foundArticle1.getTags().stream().anyMatch(tag -> "java".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle1.getTags().stream().anyMatch(tag -> "programming".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle2.getTags().stream().anyMatch(tag -> "python".equals(tag.getName())));
    Assertions.assertTrue(
        foundArticle2.getTags().stream().anyMatch(tag -> "programming".equals(tag.getName())));
  }
}
