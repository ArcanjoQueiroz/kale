package com.github.arcanjoaq.kefla.lang;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dates {
  private static Logger logger = LoggerFactory.getLogger(Date.class);

  public static Date atStartOfDay(final Date date) {
    return (date != null) 
        ? DateUtils.truncate(date, Calendar.DATE) 
        : null;
  }

  public static Date atEndOfDay(final Date date) {
    return (date != null) 
        ? DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1) 
        : null;
  }

  public static Date from(final Object value, final String pattern) {
    logger.debug("Value to be converted to: '{}'", value);
    if (value == null) {
      return null;
    } else if (value instanceof Date) {
      return (Date) value;
    } else if (value instanceof LocalDate) {
      return Date.from(
          ((LocalDate) value).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    } else if (value instanceof LocalDateTime) {
      return Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
    } else if (value instanceof Instant) {
      return Date.from(((Instant) value));
    } else if (value instanceof Calendar) {
      return ((Calendar) value).getTime();
    } else if (value instanceof Long) {
      return new Date((Long) value);
    } else if (value instanceof String) {
      try {
        return Date.from(Instant.parse(value.toString()));
      } catch (final DateTimeParseException e4) {
        try {
          return new SimpleDateFormat(pattern).parse(value.toString());
        } catch (final ParseException e1) {
          throw new IllegalArgumentException(
              String.format("Invalid value format: '%s' used in conversion", value.toString()), e4);
        }
      }
    }
    throw new IllegalArgumentException(
        String.format("Invalid value type: '%s' used in conversion", value.getClass().getName()));
  }
}
