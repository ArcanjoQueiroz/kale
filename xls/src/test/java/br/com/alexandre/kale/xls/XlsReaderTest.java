package br.com.alexandre.kale.xls;

import org.junit.Test;

public class XlsReaderTest {

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnIllegalArgumentExceptionIfPathIsNull() {
    new XlsReader(null);
  }
}
