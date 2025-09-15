package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.application.ArticleQueryService;
import io.spring.application.article.ArticleCommandService;
import io.spring.application.article.NewArticleParam;
import io.spring.application.article.UpdateArticleParam;
import io.spring.application.data.ArticleData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import io.spring.graphql.types.ArticlePayload;
import io.spring.graphql.types.CreateArticleInput;
import io.spring.graphql.types.DeletionStatus;
import io.spring.graphql.types.UpdateArticleInput;
import io.spring.api.exception.ResourceNotFoundException;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class ArticleMutationTest {

  @Mock private ArticleCommandService articleCommandService;
  @Mock private ArticleQueryService articleQueryService;
  @Mock private ArticleRepository articleRepository;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  @InjectMocks private ArticleMutation articleMutation;

  private User testUser;
  private Article testArticle;
  private ArticleData testArticleData;
  private CreateArticleInput createArticleInput;
  private UpdateArticleInput updateArticleInput;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java", "spring"), testUser.getId());
    
    createArticleInput = CreateArticleInput.newBuilder()
        .title("Test Title")
        .description("Test Description")
        .body("Test Body")
        .tagList(Arrays.asList("java", "spring"))
        .build();
        
    updateArticleInput = UpdateArticleInput.newBuilder()
        .title("Updated Title")
        .description("Updated Description")
        .body("Updated Body")
        .build();
  }

  @Test
  void should_create_article_successfully() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleCommandService.createArticle(any(NewArticleParam.class), eq(testUser))).thenReturn(testArticle);

    DataFetcherResult<ArticlePayload> result = articleMutation.createArticle(createArticleInput);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(testArticle, result.getLocalContext());
    verify(articleCommandService).createArticle(any(NewArticleParam.class), eq(testUser));
  }

  @Test
  void should_update_article_successfully() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(testArticle));
    when(articleCommandService.updateArticle(eq(testArticle), any(UpdateArticleParam.class))).thenReturn(testArticle);

    DataFetcherResult<ArticlePayload> result = articleMutation.updateArticle("test-title", updateArticleInput);

    assertNotNull(result);
    assertNotNull(result.getData());
    assertEquals(testArticle, result.getLocalContext());
    verify(articleCommandService).updateArticle(eq(testArticle), any(UpdateArticleParam.class));
  }

  @Test
  void should_delete_article_successfully() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(testArticle));

    DeletionStatus result = articleMutation.deleteArticle("test-title");

    assertNotNull(result);
    assertTrue(result.getSuccess());
    verify(articleRepository).remove(testArticle);
  }

  @Test
  void should_throw_exception_when_article_not_found_for_update() {
    when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      articleMutation.updateArticle("nonexistent", updateArticleInput);
    });
    
    verify(articleCommandService, never()).updateArticle(any(), any());
  }

  @Test
  void should_throw_exception_when_article_not_found_for_delete() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      articleMutation.deleteArticle("nonexistent");
    });
    
    verify(articleRepository, never()).remove(any());
  }
}
