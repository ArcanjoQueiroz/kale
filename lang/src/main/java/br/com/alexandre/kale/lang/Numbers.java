package br.com.alexandre.kale.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public class Numbers {

  private static Logger logger = LoggerFactory.getLogger(Numbers.class);
  
  public static Number from(final Object value) {
      logger.debug("Value to be converted: '{}'", value);
      if (value == null || Strings.isNullOrEmpty(value.toString())) {
          return null;
      } else if (value instanceof Number) {
          return (Number) value;
      } else if (value instanceof String) {
          final String v = value.toString();
          final Integer i = Ints.tryParse(v);
          if (i != null) {
              return i;
          }
          final Long l = Longs.tryParse(v);
          if (l != null) {
              return l;
          }
          final Double d = Doubles.tryParse(v);
          if (d != null) {
              return d;
          }
      }
      throw new IllegalArgumentException(String.format("Invalid value type: '%s' used in conversion", value.getClass().getName()));
  }

}
