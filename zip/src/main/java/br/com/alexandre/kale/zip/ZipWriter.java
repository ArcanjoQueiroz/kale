package br.com.alexandre.kale.zip;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.compress.utils.IOUtils.copy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newDirectoryStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipWriter {

  private final Path path;

  private final Logger logger = LoggerFactory.getLogger(ZipWriter.class);

  public ZipWriter(final Path file) {
    checkArgument(file != null, "Destination file is null");
    checkArgument(!exists(file), "Destination file exists");
    this.path = file;
  }

  public void write(final Path...paths) {
    checkArgument(paths != null && paths.length > 0, "There is no input resources");
    try (final FileOutputStream zos = new FileOutputStream(path.toFile());
        final ArchiveOutputStream zip = new ArchiveStreamFactory()
            .createArchiveOutputStream(ArchiveStreamFactory.ZIP, zos)) {
      logger.debug("Creating '{}' zip file", this.path.getFileName().toString());
      write(zip, paths);
      logger.debug("Zip file '{}' finished successfully", this.path.getFileName().toString());
      zip.finish();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void write(final ArchiveOutputStream zip, final Path... paths) throws IOException, FileNotFoundException {
    for (final Path path: paths) {
      if (isRegularFile(path)) {
        logger.debug("Archiving file '{}'", path.getFileName().toString());
        zip.putArchiveEntry(new ZipArchiveEntry(path.toFile(), path.getFileName().toString()));
        copy(new FileInputStream(path.toFile()), zip);
        zip.closeArchiveEntry();
      } else if (isDirectory(path)) {
        try (DirectoryStream<Path> directoryStream = newDirectoryStream(path)) {
          final List<Path> directoryPaths = StreamSupport.stream(directoryStream.spliterator(), false)
              .collect(Collectors.toList());
          if (directoryPaths.size() > 0) {
            final Path[] param = new Path[directoryPaths.size()];
            logger.debug("Invoking write with subdirectory '{}' content", path.toString());
            write(zip, directoryPaths.toArray(param));
          } else {
            logger.debug("There is no content to iterate");
          }
        } 
      }
    }
  }
}
