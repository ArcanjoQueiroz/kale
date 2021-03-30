package com.github.arcanjoaq.kefla.jdbc;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class FunctionExecutor {

  private final DataSource dataSource;

  private final Logger logger = LoggerFactory.getLogger(FunctionExecutor.class);

  public FunctionExecutor(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public <T> T execute(final String name, final Map<String, Object> params, final Class<T> klass) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
    Preconditions.checkArgument(params != null && !params.isEmpty());
    Preconditions.checkArgument(klass != null);

    final HashMap<String,Object> orderedParams = new LinkedHashMap<>(params);

    final String jdbcQuery = createJdbcQuery(name, orderedParams);
    try (final Connection connection = dataSource.getConnection(); 
        final CallableStatement callableStatement = connection.prepareCall(jdbcQuery);) {
      int inputParamIndex = 2;
      for (final Entry<String,Object> entry : orderedParams.entrySet()) {
        final Object value = entry.getValue();
        if (value != null) {
          if (value instanceof String) {
            callableStatement.setString(inputParamIndex++, (String) value);
          } else if (value instanceof BigDecimal) {
            callableStatement.setBigDecimal(inputParamIndex++, (BigDecimal) value);
          } else if (value instanceof Byte) {
            callableStatement.setByte(inputParamIndex++, (Byte) value);
          } else if (value instanceof Short) {
            callableStatement.setShort(inputParamIndex++, (Short) value);
          } else if (value instanceof Integer) {
            callableStatement.setInt(inputParamIndex++, (Integer) value);
          } else if (value instanceof Long) {
            callableStatement.setLong(inputParamIndex++, (Long) value);
          } else if (value instanceof Float) {
            callableStatement.setFloat(inputParamIndex++, (Float) value);
          } else if (value instanceof Double) {
            callableStatement.setDouble(inputParamIndex++, (Double) value);
          } else if (value instanceof Boolean) {
            callableStatement.setBoolean(inputParamIndex++, (Boolean) value);
          } else if (value instanceof Date) {
            callableStatement.setDate(inputParamIndex++, (Date) value);
          } else if (value instanceof Timestamp) {
            callableStatement.setTimestamp(inputParamIndex++, (Timestamp) value);
          } else {
            throw new RuntimeException(
                String.format("Type %s is not implemented", value.getClass().getName()));
          }
        }
      }

      if (klass.isAssignableFrom(String.class)) {
        callableStatement.registerOutParameter(1, Types.VARCHAR);
      } else if (klass.isAssignableFrom(BigDecimal.class)) {
        callableStatement.registerOutParameter(1, Types.NUMERIC);
      } else if (klass.isAssignableFrom(Byte.class)) {
        callableStatement.registerOutParameter(1, Types.TINYINT);
      } else if (klass.isAssignableFrom(Short.class)) {
        callableStatement.registerOutParameter(1, Types.SMALLINT);
      } else if (klass.isAssignableFrom(Integer.class)) {
        callableStatement.registerOutParameter(1, Types.INTEGER);
      } else if (klass.isAssignableFrom(Long.class)) {
        callableStatement.registerOutParameter(1, Types.BIGINT);
      } else if (klass.isAssignableFrom(Float.class)) {
        callableStatement.registerOutParameter(1, Types.REAL);
      } else if (klass.isAssignableFrom(Double.class)) {
        callableStatement.registerOutParameter(1, Types.DOUBLE);
      } else if (klass.isAssignableFrom(Boolean.class)) {
        callableStatement.registerOutParameter(1, Types.BIT);
      } else if (klass.isAssignableFrom(Date.class)) {
        callableStatement.registerOutParameter(1, Types.DATE);
      } else if (klass.isAssignableFrom(Timestamp.class)) {
        callableStatement.registerOutParameter(1, Types.TIMESTAMP);
      } else {
        throw new RuntimeException(String.format("Type %s is not implemented", klass.getName()));
      }

      callableStatement.execute();

      return callableStatement.getObject(1, klass);      
    } catch (final SQLException e) {
      throw new FunctionExecutorException(e);
    }   
  }

  private String createJdbcQuery(String name, Map<String, Object> params) {
    final List<String> args = new ArrayList<>();
    for (Entry<String,Object> entry : params.entrySet()) {
      args.add(entry.getKey() + " => " + (entry.getValue() != null ? "?" : "null"));
    }    
    final String jdbcQuery = String.format("{ ? = call %s(%s) }", name, String.join(", ", args));
    logger.info("Query: '{}'", jdbcQuery);
    return jdbcQuery;
  }

  public static class FunctionExecutorException extends RuntimeException {

    private static final long serialVersionUID = -5524087787929061850L;

    FunctionExecutorException(Throwable cause) {
      super(cause);
    }    

  }

}

