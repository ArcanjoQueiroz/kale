package br.com.alexandre.kale.xls;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.junit.Test;

public class XlsWriterTest {

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowAnIllegalArgumentExceptionIfPathIsNull() {
    final XlsWriter writer = new XlsWriter(null);
    writer.write(null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowAnIllegalArgumentExceptionContentIsNull() {
    final Path path = Paths.get(String.format("./target/file-%s.xls", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    new XlsWriter(path)
    .write(null);
  }

  @Test()
  public void shouldCreateAnEmptyFileIsContentIsEmpty() {
    final Path path = Paths.get(String.format("./target/file-%s.xls", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    new XlsWriter(path)
    .write(Arrays.asList());
    assertTrue(exists(path) && isRegularFile(path));
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowAnIllegalArgumentExceptionIfDestinationPathIsNotAnExcelFile() {
    final Path path = Paths.get(String.format("./target/file-%s.txt", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    new XlsWriter(path);
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldCreateAfileWithOneLine() {
    final Path path = Paths.get(String.format("./target/file-%s-one-line.xls", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    assertFalse(exists(path));
    
    final XlsWriter writer = new XlsWriter(path);
    writer.write(Arrays.asList(
        new Object[] { 1.0 },
        new Object[] { "Alexandre Arcanjo de Queiroz" },
        new Object[] { new Date() },
        new Object[] { true },
        new Object[] { new java.sql.Date(2018, 10, 10) }
    ));
    assertTrue(exists(path) && isRegularFile(path));
  }

  @Test
  public void shouldCreateAfileWithManyLines() {
    final Path path = Paths.get(String.format("./target/file-%s-many-lines.xls", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    assertFalse(exists(path));

    final XlsWriter writer = new XlsWriter(path);
    writer.write(Arrays.asList(
        new Object[] { 1.0 , "Alexandre" },
        new Object[] { 2.0 , "Camila" },
        new Object[] { 3.0 , "Gabriela" },
        new Object[] { 4.0 , "Felipe" },
        new Object[] { 5.0 , "Thiago" }
    ));
    assertTrue(exists(path) && isRegularFile(path));
  }

  @Test
  public void shouldntCreateAfileIfWriteWasNotInvoked() {
    final Path path = Paths.get(String.format("./target/file-%s-write-method.xls", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    new XlsWriter(path);
    assertFalse(exists(path));
  }
}
