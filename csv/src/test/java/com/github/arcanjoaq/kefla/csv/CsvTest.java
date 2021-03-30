package com.github.arcanjoaq.kefla.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

public class CsvTest {

  private Csv csv;

  @Before
  public void setUp() {
    this.csv = new Csv();
  }

  @Test
  public void shouldConvertNullValues() {
    assertThat(this.csv.convert(null, null)).isEqualTo("");
    assertThat(this.csv.convert("", null)).isEqualTo("");
    assertThat(this.csv.convert(" ", null)).isEqualTo("");
    assertThat(this.csv.convert("          ", null)).isEqualTo("");
  }

  @Test
  public void shouldCoalesceRow() {
    final Date now = new Date();
    assertArrayEquals(
        new String[] {"1", "Alexandre", now.toString(), "Male", ""},
        this.csv.coalesce(new Object[] {1, "Alexandre", now, "Male", null}, null));

    assertThat(this.csv.coalesce(null, null).length).isEqualTo(0);
  }
}
