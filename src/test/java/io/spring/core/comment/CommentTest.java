package io.spring.core.comment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommentTest {

  private String testBody;
  private String testUserId;
  private String testArticleId;
  private Comment comment;

  @BeforeEach
  void setUp() {
    testBody = "This is a test comment";
    testUserId = "user-123";
    testArticleId = "article-456";
    comment = new Comment(testBody, testUserId, testArticleId);
  }

  @Test
  void should_create_comment_with_valid_parameters() {
    assertNotNull(comment);
    assertEquals(testBody, comment.getBody());
    assertEquals(testUserId, comment.getUserId());
    assertEquals(testArticleId, comment.getArticleId());
    assertNotNull(comment.getId());
    assertNotNull(comment.getCreatedAt());
  }

  @Test
  void should_generate_unique_ids() {
    Comment comment1 = new Comment(testBody, testUserId, testArticleId);
    Comment comment2 = new Comment(testBody, testUserId, testArticleId);
    
    assertNotEquals(comment1.getId(), comment2.getId());
  }

  @Test
  void should_have_equals_and_hashcode_based_on_id() {
    Comment comment1 = new Comment(testBody, testUserId, testArticleId);
    Comment comment2 = new Comment("different body", "different user", "different article");
    
    assertNotEquals(comment1, comment2);
    assertNotEquals(comment1.hashCode(), comment2.hashCode());
    
    assertEquals(comment1, comment1);
    assertEquals(comment1.hashCode(), comment1.hashCode());
  }

  @Test
  void should_create_comment_with_null_body() {
    Comment commentWithNullBody = new Comment(null, testUserId, testArticleId);
    assertNotNull(commentWithNullBody);
    assertNull(commentWithNullBody.getBody());
    assertEquals(testUserId, commentWithNullBody.getUserId());
    assertEquals(testArticleId, commentWithNullBody.getArticleId());
  }

  @Test
  void should_create_comment_with_empty_body() {
    Comment commentWithEmptyBody = new Comment("", testUserId, testArticleId);
    assertNotNull(commentWithEmptyBody);
    assertEquals("", commentWithEmptyBody.getBody());
  }

  @Test
  void should_create_comment_with_null_user_id() {
    Comment commentWithNullUser = new Comment(testBody, null, testArticleId);
    assertNotNull(commentWithNullUser);
    assertNull(commentWithNullUser.getUserId());
  }

  @Test
  void should_create_comment_with_null_article_id() {
    Comment commentWithNullArticle = new Comment(testBody, testUserId, null);
    assertNotNull(commentWithNullArticle);
    assertNull(commentWithNullArticle.getArticleId());
  }

  @Test
  void should_create_with_no_args_constructor() {
    Comment comment = new Comment();
    assertNotNull(comment);
    assertNull(comment.getId());
    assertNull(comment.getBody());
    assertNull(comment.getUserId());
    assertNull(comment.getArticleId());
    assertNull(comment.getCreatedAt());
  }
}
