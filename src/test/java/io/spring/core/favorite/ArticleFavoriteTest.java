package io.spring.core.favorite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArticleFavoriteTest {

  private String testUserId;
  private String testArticleId;
  private ArticleFavorite articleFavorite;

  @BeforeEach
  void setUp() {
    testUserId = "user-123";
    testArticleId = "article-456";
    articleFavorite = new ArticleFavorite(testArticleId, testUserId);
  }

  @Test
  void should_create_article_favorite_with_valid_parameters() {
    assertNotNull(articleFavorite);
    assertEquals(testArticleId, articleFavorite.getArticleId());
    assertEquals(testUserId, articleFavorite.getUserId());
  }

  @Test
  void should_create_article_favorite_with_null_article_id() {
    ArticleFavorite favorite = new ArticleFavorite(null, testUserId);
    assertNotNull(favorite);
    assertNull(favorite.getArticleId());
    assertEquals(testUserId, favorite.getUserId());
  }

  @Test
  void should_create_article_favorite_with_null_user_id() {
    ArticleFavorite favorite = new ArticleFavorite(testArticleId, null);
    assertNotNull(favorite);
    assertEquals(testArticleId, favorite.getArticleId());
    assertNull(favorite.getUserId());
  }

  @Test
  void should_create_article_favorite_with_empty_strings() {
    ArticleFavorite favorite = new ArticleFavorite("", "");
    assertNotNull(favorite);
    assertEquals("", favorite.getArticleId());
    assertEquals("", favorite.getUserId());
  }

  @Test
  void should_have_equals_and_hashcode() {
    ArticleFavorite favorite1 = new ArticleFavorite(testArticleId, testUserId);
    ArticleFavorite favorite2 = new ArticleFavorite(testArticleId, testUserId);
    ArticleFavorite favorite3 = new ArticleFavorite("different-article", "different-user");
    
    assertEquals(favorite1, favorite2);
    assertEquals(favorite1.hashCode(), favorite2.hashCode());
    assertNotEquals(favorite1, favorite3);
    assertNotEquals(favorite1.hashCode(), favorite3.hashCode());
  }

  @Test
  void should_create_with_no_args_constructor() {
    ArticleFavorite favorite = new ArticleFavorite();
    assertNotNull(favorite);
    assertNull(favorite.getArticleId());
    assertNull(favorite.getUserId());
  }
}
