package br.com.alexandre.kale.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigDecimals {
  private static Logger logger = LoggerFactory.getLogger(BigDecimals.class);

  public static BigDecimal from(final Object value) {
    logger.debug("Value to be converted: '{}'", value);
    if (value == null) {
      return null;
    } else if (value instanceof BigDecimal) {
      return (BigDecimal) value;
    } else if (value instanceof String) {
      return new BigDecimal(value.toString());
    }
    if (value instanceof Integer) {
      return new BigDecimal((Integer) value);
    } else if (value instanceof Long) {
      return new BigDecimal((Long) value);
    } else if (value instanceof Double) {
      return new BigDecimal((Double) value);
    } else if (value instanceof BigInteger) {
      return new BigDecimal((BigInteger) value);
    }
    throw new IllegalArgumentException(
        String.format("Invalid value type: '%s' used in conversion", value.getClass().getName()));
  }
}
