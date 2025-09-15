package io.spring.infrastructure.mybatis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DateTimeHandlerTest {

  @Mock private PreparedStatement preparedStatement;
  @Mock private ResultSet resultSet;
  @Mock private CallableStatement callableStatement;

  private DateTimeHandler dateTimeHandler;
  private DateTime testDateTime;
  private Timestamp testTimestamp;

  @BeforeEach
  void setUp() {
    dateTimeHandler = new DateTimeHandler();
    testDateTime = new DateTime(2023, 9, 15, 10, 30, 0, DateTimeZone.UTC);
    testTimestamp = new Timestamp(testDateTime.getMillis());
  }

  @Test
  void should_set_parameter_with_valid_datetime() throws SQLException {
    dateTimeHandler.setParameter(preparedStatement, 1, testDateTime, JdbcType.TIMESTAMP);

    verify(preparedStatement).setTimestamp(eq(1), any(Timestamp.class), any(Calendar.class));
  }

  @Test
  void should_set_parameter_with_null_datetime() throws SQLException {
    dateTimeHandler.setParameter(preparedStatement, 1, null, JdbcType.TIMESTAMP);

    verify(preparedStatement).setTimestamp(eq(1), eq(null), any(Calendar.class));
  }

  @Test
  void should_get_result_by_column_name() throws SQLException {
    when(resultSet.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(testTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertNotNull(result);
    assertEquals(testDateTime.getMillis(), result.getMillis());
  }

  @Test
  void should_get_null_result_by_column_name_when_timestamp_is_null() throws SQLException {
    when(resultSet.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertNull(result);
  }

  @Test
  void should_get_result_by_column_index() throws SQLException {
    when(resultSet.getTimestamp(eq(1), any(Calendar.class))).thenReturn(testTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, 1);

    assertNotNull(result);
    assertEquals(testDateTime.getMillis(), result.getMillis());
  }

  @Test
  void should_get_null_result_by_column_index_when_timestamp_is_null() throws SQLException {
    when(resultSet.getTimestamp(eq(1), any(Calendar.class))).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(resultSet, 1);

    assertNull(result);
  }

  @Test
  void should_get_result_from_callable_statement() throws SQLException {
    when(callableStatement.getTimestamp(eq(1), any(Calendar.class))).thenReturn(testTimestamp);

    DateTime result = dateTimeHandler.getResult(callableStatement, 1);

    assertNotNull(result);
    assertEquals(testDateTime.getMillis(), result.getMillis());
  }

  @Test
  void should_get_null_result_from_callable_statement_when_timestamp_is_null() throws SQLException {
    when(callableStatement.getTimestamp(eq(1), any(Calendar.class))).thenReturn(null);

    DateTime result = dateTimeHandler.getResult(callableStatement, 1);

    assertNull(result);
  }

  @Test
  void should_handle_different_timezones_correctly() throws SQLException {
    DateTime utcDateTime = new DateTime(2023, 9, 15, 10, 30, 0, DateTimeZone.UTC);
    Timestamp utcTimestamp = new Timestamp(utcDateTime.getMillis());
    
    when(resultSet.getTimestamp(eq("created_at"), any(Calendar.class))).thenReturn(utcTimestamp);

    DateTime result = dateTimeHandler.getResult(resultSet, "created_at");

    assertNotNull(result);
    assertEquals(utcDateTime.getMillis(), result.getMillis());
  }

  @Test
  void should_use_utc_calendar_for_all_operations() throws SQLException {
    dateTimeHandler.setParameter(preparedStatement, 1, testDateTime, JdbcType.TIMESTAMP);

    verify(preparedStatement).setTimestamp(eq(1), any(Timestamp.class), argThat(calendar -> 
        calendar.getTimeZone().equals(TimeZone.getTimeZone("UTC"))));
  }
}
