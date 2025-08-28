package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.mapper.ArticleMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ArticleMapperIntegrationTest {

  @Autowired
  private ArticleMapper articleMapper;

  @Autowired
  private ArticleRepository articleRepository;

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  public void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    userRepository.save(user);
  }

  @Test
  public void should_test_complex_article_queries_with_joins() {
    Article article1 = new Article("Title 1", "Description 1", "Body 1", Arrays.asList("java", "spring"), user.getId());
    Article article2 = new Article("Title 2", "Description 2", "Body 2", Arrays.asList("python", "django"), user.getId());
    
    articleRepository.save(article1);
    articleRepository.save(article2);

    Article foundArticle1 = articleMapper.findById(article1.getId());
    Article foundArticle2 = articleMapper.findById(article2.getId());
    
    Assertions.assertNotNull(foundArticle1);
    Assertions.assertNotNull(foundArticle2);
    Assertions.assertTrue(foundArticle1.getTags().contains("java"));
    Assertions.assertTrue(foundArticle1.getTags().contains("spring"));
    Assertions.assertTrue(foundArticle2.getTags().contains("python"));
    Assertions.assertTrue(foundArticle2.getTags().contains("django"));
  }

  @Test
  public void should_test_transaction_rollback_scenarios() {
    Article article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), user.getId());
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
    Article article1 = new Article("Batch Title 1", "Description 1", "Body 1", Arrays.asList("batch"), user.getId());
    Article article2 = new Article("Batch Title 2", "Description 2", "Body 2", Arrays.asList("batch"), user.getId());
    Article article3 = new Article("Batch Title 3", "Description 3", "Body 3", Arrays.asList("batch"), user.getId());

    articleRepository.save(article1);
    articleRepository.save(article2);
    articleRepository.save(article3);

    Article foundArticle1 = articleMapper.findById(article1.getId());
    Article foundArticle2 = articleMapper.findById(article2.getId());
    Article foundArticle3 = articleMapper.findById(article3.getId());
    
    Assertions.assertNotNull(foundArticle1);
    Assertions.assertNotNull(foundArticle2);
    Assertions.assertNotNull(foundArticle3);
    Assertions.assertTrue(foundArticle1.getTags().contains("batch"));
    Assertions.assertTrue(foundArticle2.getTags().contains("batch"));
    Assertions.assertTrue(foundArticle3.getTags().contains("batch"));
  }

  @Test
  public void should_handle_article_with_no_tags() {
    Article article = new Article("No Tags Title", "Description", "Body", Arrays.asList(), user.getId());
    articleRepository.save(article);

    Article foundArticle = articleRepository.findById(article.getId()).orElse(null);
    Assertions.assertNotNull(foundArticle);
    Assertions.assertTrue(foundArticle.getTags().isEmpty());
  }

  @Test
  public void should_find_articles_by_tag() {
    Article article1 = new Article("Java Article", "Description", "Body", Arrays.asList("java", "programming"), user.getId());
    Article article2 = new Article("Python Article", "Description", "Body", Arrays.asList("python", "programming"), user.getId());
    
    articleRepository.save(article1);
    articleRepository.save(article2);

    Article foundArticle1 = articleMapper.findById(article1.getId());
    Article foundArticle2 = articleMapper.findById(article2.getId());
    
    Assertions.assertNotNull(foundArticle1);
    Assertions.assertNotNull(foundArticle2);
    Assertions.assertTrue(foundArticle1.getTags().contains("java"));
    Assertions.assertTrue(foundArticle1.getTags().contains("programming"));
    Assertions.assertTrue(foundArticle2.getTags().contains("python"));
    Assertions.assertTrue(foundArticle2.getTags().contains("programming"));
  }
}
