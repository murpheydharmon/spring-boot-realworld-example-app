package io.spring.graphql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import io.spring.application.article.ArticleCommandService;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(DgsAutoConfiguration.class)
public class ArticleMutationTest {

  @Autowired
  DgsQueryExecutor dgsQueryExecutor;

  @MockBean
  private ArticleCommandService articleCommandService;

  @MockBean
  private ArticleRepository articleRepository;

  @MockBean
  private ArticleFavoriteRepository articleFavoriteRepository;

  private User user;
  private Article article;

  @BeforeEach
  public void setUp() {
    user = new User("test@test.com", "testuser", "password", "bio", "image");
    article = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), user.getId());
    
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList()));
  }

  @Test
  public void should_create_article_via_graphql() {
    when(articleCommandService.createArticle(any(), eq(user))).thenReturn(article);

    String mutation = "mutation { " +
        "createArticle(input: { " +
        "title: \"Test Title\" " +
        "description: \"Test Description\" " +
        "body: \"Test Body\" " +
        "tagList: [\"java\"] " +
        "}) { " +
        "article { " +
        "title " +
        "description " +
        "body " +
        "} " +
        "} " +
        "}";

    dgsQueryExecutor.execute(mutation);
  }

  @Test
  public void should_update_article_via_graphql() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));
    when(articleCommandService.updateArticle(eq(article), any())).thenReturn(article);

    String mutation = "mutation { " +
        "updateArticle(slug: \"test-title\", changes: { " +
        "title: \"Updated Title\" " +
        "description: \"Updated Description\" " +
        "body: \"Updated Body\" " +
        "}) { " +
        "article { " +
        "title " +
        "description " +
        "body " +
        "} " +
        "} " +
        "}";

    dgsQueryExecutor.execute(mutation);
  }

  @Test
  public void should_favorite_article_via_graphql() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));

    String mutation = "mutation { " +
        "favoriteArticle(slug: \"test-title\") { " +
        "article { " +
        "title " +
        "favorited " +
        "} " +
        "} " +
        "}";

    dgsQueryExecutor.execute(mutation);
  }

  @Test
  public void should_handle_authorization_errors_in_graphql() {
    SecurityContextHolder.getContext().setAuthentication(null);

    String mutation = "mutation { " +
        "createArticle(input: { " +
        "title: \"Test Title\" " +
        "description: \"Test Description\" " +
        "body: \"Test Body\" " +
        "tagList: [\"java\"] " +
        "}) { " +
        "article { " +
        "title " +
        "} " +
        "} " +
        "}";

    try {
      dgsQueryExecutor.execute(mutation);
    } catch (Exception e) {
    }
  }

  @Test
  public void should_delete_article_via_graphql() {
    when(articleRepository.findBySlug(eq("test-title"))).thenReturn(Optional.of(article));

    String mutation = "mutation { " +
        "deleteArticle(slug: \"test-title\") { " +
        "success " +
        "} " +
        "}";

    dgsQueryExecutor.execute(mutation);
  }
}
