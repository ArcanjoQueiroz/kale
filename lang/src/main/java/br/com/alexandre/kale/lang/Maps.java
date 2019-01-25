package br.com.alexandre.kale.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class Maps {

  public static <T>Optional<T> getValue(final String key, final Map<String, Object> map, final Class<T> klass) {
    checkArgument(!isNullOrEmpty(key), "Invalid 'key' parameter: key is null or empty");
    checkArgument(klass != null, "Invalid 'klass' parameter: klass is null or empty");
    if (map == null || map.isEmpty()) {
      return Optional.empty();
    }
    final Object value = map.containsKey(key.trim()) ?
        map.get(key.trim()) :
          map.get(key.toLowerCase().trim());
        if (value == null) {
          return Optional.empty();
        }
        if (value instanceof String && isNullOrEmpty(value.toString())) {
          return Optional.empty();
        }
        if (klass.isInstance(value)) {
          return Optional.of(klass.cast(value));
        } else if (value instanceof String) {
          return convertFromString(klass, value.toString());            
        }        
        throw new IllegalArgumentException("Incompatible classes: '" + value.getClass().getName() +  "' and '" + klass.getName() + "'");
  }

  @SuppressWarnings("unchecked")
  private static <T> Optional<T> convertFromString(final Class<T> klass, final String value) {
    if (Boolean.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Boolean.parseBoolean(value));
    } else if (Byte.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Byte.parseByte(value));  
    } else if (Short.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Short.parseShort(value));
    } else if (Integer.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Integer.parseInt(value));
    } else if (Long.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Long.parseLong(value));
    } else if (Float.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Float.parseFloat(value));
    } else if (Double.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Double.parseDouble(value));
    } else if (BigInteger.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(new BigInteger(value));
    } else if (BigDecimal.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(new BigDecimal(value));
    } else if (LocalDateTime.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(LocalDateTime.ofInstant(Instant.parse(value), ZoneOffset.UTC)); 
    } else if (LocalDate.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(LocalDate.ofInstant(Instant.parse(value), ZoneOffset.UTC));
    } else if (Date.class.isAssignableFrom(klass)) {
      return (Optional<T>) Optional.of(Date.from(Instant.parse(value)));
    }
    throw new IllegalArgumentException("Unimplemented conversion from 'java.lang.String' to '" + klass.getName() + "'");
  }
}
