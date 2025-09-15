package io.spring.core.service;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceTest {

  private User user1;
  private User user2;
  private Article article;
  private Comment comment;

  @BeforeEach
  void setUp() {
    user1 = new User("user1@example.com", "user1", "password", "bio", "image");
    user2 = new User("user2@example.com", "user2", "password", "bio", "image");
    article = new Article("Title", "Description", "Body", Arrays.asList("tag"), user1.getId());
    comment = new Comment("Comment body", user1.getId(), article.getId());
  }

  @Test
  void should_allow_article_author_to_write_article() {
    boolean canWrite = AuthorizationService.canWriteArticle(user1, article);
    assertTrue(canWrite);
  }

  @Test
  void should_not_allow_non_author_to_write_article() {
    boolean canWrite = AuthorizationService.canWriteArticle(user2, article);
    assertFalse(canWrite);
  }

  @Test
  void should_allow_comment_author_to_write_comment() {
    boolean canWrite = AuthorizationService.canWriteComment(user1, article, comment);
    assertTrue(canWrite);
  }

  @Test
  void should_not_allow_non_author_to_write_comment() {
    boolean canWrite = AuthorizationService.canWriteComment(user2, article, comment);
    assertFalse(canWrite);
  }

  @Test
  void should_allow_article_author_to_write_comment_on_own_article() {
    Comment commentByOther = new Comment("Comment by other", user2.getId(), article.getId());
    boolean canWrite = AuthorizationService.canWriteComment(user1, article, commentByOther);
    assertTrue(canWrite);
  }
}
