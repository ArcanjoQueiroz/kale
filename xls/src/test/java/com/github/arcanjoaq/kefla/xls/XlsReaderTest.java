package com.github.arcanjoaq.kefla.xls;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;

public class XlsReaderTest {

  @Test
  public void shouldThrowAnIllegalArgumentExceptionIfPathIsNull() {
    assertThatThrownBy(() -> {
      new XlsReader(null);
    }).isInstanceOf(IllegalArgumentException.class);
    
  }
}
