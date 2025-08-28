package io.spring.core.service;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceTest {

  private User articleAuthor;
  private User commentAuthor;
  private User otherUser;
  private Article article;
  private Comment comment;

  @BeforeEach
  public void setUp() {
    articleAuthor = new User("author@test.com", "author", "password", "bio", "image");
    commentAuthor = new User("commenter@test.com", "commenter", "password", "bio", "image");
    otherUser = new User("other@test.com", "other", "password", "bio", "image");
    
    article = new Article("Test Article", "Description", "Body", Arrays.asList("java"), articleAuthor.getId());
    comment = new Comment("Test comment", commentAuthor.getId(), article.getId());
  }

  @Test
  public void should_allow_article_author_to_write() {
    boolean canWrite = AuthorizationService.canWriteArticle(articleAuthor, article);
    Assertions.assertTrue(canWrite);
  }

  @Test
  public void should_deny_non_author_article_write() {
    boolean canWrite = AuthorizationService.canWriteArticle(otherUser, article);
    Assertions.assertFalse(canWrite);
  }

  @Test
  public void should_allow_comment_author_to_write() {
    boolean canWrite = AuthorizationService.canWriteComment(commentAuthor, article, comment);
    Assertions.assertTrue(canWrite);
  }

  @Test
  public void should_allow_article_author_to_write_comments() {
    boolean canWrite = AuthorizationService.canWriteComment(articleAuthor, article, comment);
    Assertions.assertTrue(canWrite);
  }

  @Test
  public void should_deny_unauthorized_comment_write() {
    boolean canWrite = AuthorizationService.canWriteComment(otherUser, article, comment);
    Assertions.assertFalse(canWrite);
  }

  @Test
  public void should_handle_null_user_for_article() {
    boolean canWrite = AuthorizationService.canWriteArticle(null, article);
    Assertions.assertFalse(canWrite);
  }

  @Test
  public void should_handle_null_article() {
    boolean canWrite = AuthorizationService.canWriteArticle(articleAuthor, null);
    Assertions.assertFalse(canWrite);
  }

  @Test
  public void should_handle_null_user_for_comment() {
    boolean canWrite = AuthorizationService.canWriteComment(null, article, comment);
    Assertions.assertFalse(canWrite);
  }

  @Test
  public void should_handle_null_comment() {
    boolean canWrite = AuthorizationService.canWriteComment(commentAuthor, article, null);
    Assertions.assertFalse(canWrite);
  }

  @Test
  public void should_handle_different_user_ids() {
    User userWithDifferentId = new User("different@test.com", "different", "password", "bio", "image");
    
    boolean canWriteArticle = AuthorizationService.canWriteArticle(userWithDifferentId, article);
    boolean canWriteComment = AuthorizationService.canWriteComment(userWithDifferentId, article, comment);
    
    Assertions.assertFalse(canWriteArticle);
    Assertions.assertFalse(canWriteComment);
  }
}
