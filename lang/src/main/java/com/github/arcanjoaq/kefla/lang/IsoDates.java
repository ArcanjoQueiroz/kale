package com.github.arcanjoaq.kefla.lang;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class IsoDates {

  private IsoDates() { }
  
  public static Instant of(final String isoDateTime) {
    final TemporalAccessor temporalAccessor = DateTimeFormatter
        .ISO_INSTANT.parse(isoDateTime);
    return Instant.from(temporalAccessor);
  }
}
