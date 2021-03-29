package br.com.alexandre.kale.lang;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalTime;
import org.junit.Test;

public class TimeRangeTest {

  @Test
  public void testIsBetween() {
    final TimeRange timeRange = TimeRange.of(LocalTime.of(10, 10), LocalTime.of(15, 15));
    
    assertThat(timeRange.isBetween(LocalTime.of(10, 9))).isFalse();
    assertThat(timeRange.isBetween(LocalTime.of(10, 11))).isTrue();
    assertThat(timeRange.isBetween(LocalTime.of(15, 14))).isTrue();
    assertThat(timeRange.isBetween(LocalTime.of(15, 16))).isFalse();
  }
  
  @Test
  public void testIsBetweenReverseRange() {
    final TimeRange timeRange = TimeRange.of(LocalTime.of(15, 15), LocalTime.of(10, 10));
    
    assertThat(timeRange.isBetween(LocalTime.of(10, 9))).isTrue();
    assertThat(timeRange.isBetween(LocalTime.of(10, 11))).isFalse();
    assertThat(timeRange.isBetween(LocalTime.of(15, 14))).isFalse();
    assertThat(timeRange.isBetween(LocalTime.of(15, 16))).isTrue();
  }

}
