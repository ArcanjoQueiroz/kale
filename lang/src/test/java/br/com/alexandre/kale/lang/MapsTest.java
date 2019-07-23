package br.com.alexandre.kale.lang;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class MapsTest {

  @Test
  public void shouldGetValueUsingTheSameClass() {
    final Date now = new Date();

    final Map<String, Object> map = new HashMap<>();
    map.put("name", "Camila");
    map.put("age", 31);
    map.put("pass", true);
    map.put("date", now);
    map.put("creditCard", "");
    map.put("id", null);

    assertThat(Maps.getValue("name", map, String.class).get()).isEqualTo("Camila");
    assertThat(Maps.getValue("age", map, Integer.class).get()).isEqualTo(31);
    assertThat(Maps.getValue("pass", map, Boolean.class).get()).isEqualTo(true);
    assertThat(Maps.getValue("date", map, Date.class).get()).isEqualTo(now);
    assertThat(Maps.getValue("creditCard", map, String.class)).isNotPresent();
    assertThat(Maps.getValue("id", map, String.class)).isNotPresent();
  }

  @Test
  public void shouldGetValueUsingCompatibleClass() {
    final Date now = new Date();
    final Instant instant = now.toInstant();
    final LocalDate localDate = LocalDate.ofInstant(instant, ZoneOffset.UTC);
    final LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

    final Map<String, Object> map = new HashMap<>();
    map.put("name", "Camila");
    map.put("age", "31");
    map.put("pass", "true");
    map.put("date", instant.toString());

    assertThat(Maps.getValue("age", map, Integer.class).get()).isEqualTo(31);
    assertThat(Maps.getValue("pass", map, Boolean.class).get()).isEqualTo(true);
    assertThat(Maps.getValue("date", map, Date.class).get()).isEqualTo(now);
    assertThat(Maps.getValue("date", map, LocalDate.class).get()).isEqualTo(localDate);
    assertThat(Maps.getValue("date", map, LocalDateTime.class).get()).isEqualTo(localDateTime);
  }
}
