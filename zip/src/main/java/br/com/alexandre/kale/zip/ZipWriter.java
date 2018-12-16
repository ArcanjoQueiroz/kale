package br.com.alexandre.kale.zip;


import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.compress.utils.IOUtils.copy;

public class ZipWriter {

  private final File file;

  private final Logger logger = LoggerFactory.getLogger(ZipWriter.class);
  
  public ZipWriter(final File file) {
    checkArgument(file != null, "Destination file is null");
    checkArgument(!file.exists(), "Destination file exists");
    this.file = file;
  }

  public void write(final File...files) {
    checkArgument(files != null && files.length > 0, "There is no input files");
    try (final FileOutputStream zos = new FileOutputStream(file);
        final ArchiveOutputStream zip = new ArchiveStreamFactory()
            .createArchiveOutputStream(ArchiveStreamFactory.ZIP, zos)) {
      logger.debug("Starting '{}' zip file", this.file.getName());
      for (final File file: files) {
        if (file.isFile()) {
          logger.debug("Archiving file '{}'", file.getName());
          zip.putArchiveEntry(new ZipArchiveEntry(file, file.getName()));
          copy(new FileInputStream(file), zip);
          zip.closeArchiveEntry();
        } else {
          logger.warn("Resource '{}' is not a file", file.getName());
        }
      }
      logger.debug("Finishing '{}' zip file", this.file.getName());
      zip.finish();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
