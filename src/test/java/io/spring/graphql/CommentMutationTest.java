package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.graphql.types.CommentPayload;
import io.spring.graphql.types.DeletionStatus;
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
public class CommentMutationTest {

  @Mock private CommentRepository commentRepository;
  @Mock private ArticleRepository articleRepository;
  @Mock private CommentQueryService commentQueryService;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  @InjectMocks private CommentMutation commentMutation;

  private User testUser;
  private Article testArticle;
  private Comment testComment;
  private CommentData commentData;

  @BeforeEach
  void setUp() {
    testUser = new User("test@example.com", "testuser", "password", "bio", "image");
    testArticle = new Article("Test Title", "Test Description", "Test Body", Arrays.asList("java"), testUser.getId());
    testComment = new Comment("Test comment body", testUser.getId(), testArticle.getId());
    commentData = mock(CommentData.class);
  }

  @Test
  void should_create_comment_successfully() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(testArticle));
    when(commentQueryService.findById(anyString(), any(User.class))).thenReturn(Optional.of(commentData));

    DataFetcherResult<CommentPayload> result = commentMutation.createComment("test-title", "Test comment body");

    assertNotNull(result);
    assertNotNull(result.getData());
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  void should_throw_exception_when_article_not_found() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      commentMutation.createComment("nonexistent", "Test comment");
    });
    
    verify(commentRepository, never()).save(any());
  }

  @Test
  void should_delete_comment_successfully() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(testArticle));
    when(commentRepository.findById(testArticle.getId(), "comment-id")).thenReturn(Optional.of(testComment));

    DeletionStatus result = commentMutation.removeComment("test-title", "comment-id");

    assertNotNull(result);
    assertTrue(result.getSuccess());
    verify(commentRepository).remove(testComment);
  }

  @Test
  void should_throw_exception_when_comment_not_found() {
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(testArticle));
    when(commentRepository.findById(testArticle.getId(), "nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      commentMutation.removeComment("test-title", "nonexistent");
    });
    
    verify(commentRepository, never()).remove(any());
  }
}
