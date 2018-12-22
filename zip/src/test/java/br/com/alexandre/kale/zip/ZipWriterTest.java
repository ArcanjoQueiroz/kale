package br.com.alexandre.kale.zip;

import static java.nio.file.Files.exists;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

public class ZipWriterTest {

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowAndIllegalArgumentExceptionIfFileIsNull() {
    final ZipWriter writer = new ZipWriter(null);
    writer.write();
  }

  @Test(expected=IllegalArgumentException.class)
  public void shouldThrowAnIllegalArgumentExceptionIfFileIsAdirectory() {
    final ZipWriter writer = new ZipWriter(Paths.get("src/test/resources"));
    writer.write();
  }

  @Test
  public void shouldCreateAzipFileUsingTheTestFiles() {
    final String fileName = String.format("target/my-zip-created-at-%s-with-more-than-one-file.zip", 
        new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));

    final Path destination = Paths.get(fileName);

    assertFalse(exists(destination));

    final ZipWriter writer = new ZipWriter(destination);
    writer.write(Paths.get("src/test/resources/customers.xls"),
        Paths.get("src/test/resources/dummy.txt"), 
        Paths.get("src/test/resources/empty.xls"));

    assertTrue(exists(destination));
  }

  @Test
  public void shouldCreateAzipFileWithOnlyOneFile() {
    final String fileName = String.format("target/my-zip-created-at-%s-with-one-file.zip", 
        new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));

    final Path destination = Paths.get(fileName);

    assertFalse(exists(destination));

    final ZipWriter writer = new ZipWriter(destination);
    writer.write(Paths.get("src/test/resources/customers.xls"));

    assertTrue(exists(destination));
  }
  
  @Test
  public void shouldCreateAzipFileWithAdirectory() {
    final String fileName = String.format("target/my-zip-created-at-%s-with-a-directory.zip", 
        new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));

    final Path destination = Paths.get(fileName);

    assertFalse(exists(destination));

    final ZipWriter writer = new ZipWriter(destination);
    writer.write(Paths.get("src/test/resources"));

    assertTrue(exists(destination));
  }
}
