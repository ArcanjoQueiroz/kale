package com.github.arcanjoaq.kefla.xls;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import org.junit.Test;

public class XlsWriterTest {

  @Test
  public void shouldCreateAnEmptyFileIsContentIsEmpty() throws IOException {
    final File file =
        new File(
            String.format(
                "./target/file-%s.xls", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    try (final XlsWriter writer = new XlsWriter(file)) {
      writer.write(Arrays.asList());
    }
    assertThat(file).exists().isFile();
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldCreateAfileWithOneLine() throws IOException {
    final File file =
        new File(
            String.format(
                "./target/file-%s-one-line.xls",
                new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    assertThat(file).doesNotExist();

    try (final XlsWriter writer = new XlsWriter(file)) {
      writer.write(
          Arrays.asList(
              new Object[] {1.0},
              new Object[] {"arcanjoaq"},
              new Object[] {new Date()},
              new Object[] {true},
              new Object[] {new java.sql.Date(2018, 10, 10)}));
    }
    assertThat(file).exists().isFile();
  }

  @Test
  public void shouldCreateAfileWithManyLines() throws IOException {
    final File file =
        new File(
            String.format(
                "./target/file-%s-many-lines.xls",
                new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    assertThat(file).doesNotExist();

    try (final XlsWriter writer = new XlsWriter(file)) {
      writer.write(
          Arrays.asList(
              new Object[] {1.0, "Alexandre"},
              new Object[] {2.0, "Camila"},
              new Object[] {3.0, "Gabriela"},
              new Object[] {4.0, "Felipe"},
              new Object[] {5.0, "Thiago"}));
    }
    assertThat(file).exists().isFile();
  }

  @Test
  public void shouldUseLocalDate() throws IOException {
    final File file =
        new File(
            String.format(
                "./target/file-%s-localdate.xls",
                new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    assertThat(file).doesNotExist();

    try (final XlsWriter writer = new XlsWriter(file)) {
      writer.write(
          Arrays.asList(
              new Object[] {1, "Alexandre", LocalDate.of(2019, 1, 1)},
              new Object[] {2, "Camila", LocalDateTime.of(2018, 12, 31, 10, 10, 20)},
              new Object[] {3, "Gabriela", LocalTime.of(10, 10, 20)}));
    }
    assertThat(file).exists().isFile();
  }
}
