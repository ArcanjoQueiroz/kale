package com.github.arcanjoaq.kefla.lang;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Test;

public class IsoDatesTest {

  @Test
  public void test() {
    final ZonedDateTime localDateTime = ZonedDateTime
        .of(2020, 11, 3, 11, 51, 13, 185, ZoneId.of("UTC"));
    final String isoDateTime = "2020-11-03T11:51:13.000000185Z";
    final Instant instant = IsoDates.of(isoDateTime);
    assertThat(instant).isNotNull()
      .isEqualTo(localDateTime.toInstant());
  }

}
