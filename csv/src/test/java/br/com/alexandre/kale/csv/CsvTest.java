package br.com.alexandre.kale.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
    assertEquals("", this.csv.convert(null, null));
    assertEquals("", this.csv.convert("", null));
    assertEquals("", this.csv.convert(" ", null));
    assertEquals("", this.csv.convert("          ", null));
  }

  @Test
  public void shouldCoalesceRow() {
    final Date now = new Date();
    assertArrayEquals(
        new String[] {"1", "Alexandre", now.toString(), "Male", ""},
        this.csv.coalesce(new Object[] {1, "Alexandre", now, "Male", null}, null));

    assertEquals(0, this.csv.coalesce(null, null).length);
  }
}
