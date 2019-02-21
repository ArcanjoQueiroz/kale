package br.com.alexandre.kale.zip;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

public class ZipWriterTest {

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowAndIllegalArgumentExceptionIfFileIsNull() throws IOException {
    final OutputStream outputStream = null;
    try (final ZipWriter writer = new ZipWriter(outputStream)) {
      writer.write();
    }
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowAnIllegalArgumentExceptionIfFileIsAdirectory() throws IOException {
    try(final ZipWriter writer = new ZipWriter(new File("src/test/resources"))) {
      writer.write();
    }
  }

  @Test
  public void shouldCreateAzipFileUsingTheTestFiles() throws IOException {
    final String fileName = String.format("target/my-zip-created-at-%s-with-more-than-one-file.zip", 
        new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));

    final File destination = new File(fileName);

    assertFalse(destination.exists());

    try (final ZipWriter writer = new ZipWriter(destination)) {
      writer.write(new File("src/test/resources/customers.xls"),
          new File("src/test/resources/dummy.txt"), 
          new File("src/test/resources/empty.xls"));
    }

    assertTrue(destination.exists());
  }

  @Test
  public void shouldCreateAzipFileWithOnlyOneFile() throws IOException {
    final String fileName = String.format("target/my-zip-created-at-%s-with-one-file.zip", 
        new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));

    final File destination = new File(fileName);

    assertFalse(destination.exists());

    try (final ZipWriter writer = new ZipWriter(destination)) {
      writer.write(new File("src/test/resources/customers.xls"));
    }

    assertTrue(destination.exists());
  }

  @Test
  public void shouldCreateAzipFileWithAdirectory() throws IOException {
    final String fileName = String.format("target/my-zip-created-at-%s-with-a-directory.zip", 
        new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));

    final File destination = new File(fileName);

    assertFalse(destination.exists());

    try (final ZipWriter writer = new ZipWriter(destination)) {
      writer.write(new File("src/test/resources"));
    }

    assertTrue(destination.exists());
  }
}
