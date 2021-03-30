package com.github.arcanjoaq.kefla.zip;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipExtractor {
  private Path zip;

  private Logger logger = LoggerFactory.getLogger(ZipExtractor.class);

  private static final int BUFFER_SIZE = 4096;

  public ZipExtractor(final Path zip) {
    checkArgument(zip != null, "Invalid source file");
    checkArgument(exists(zip), "Source file does not exist");
    checkArgument(isRegularFile(zip), "Source is not a regular file");
    this.zip = zip;
  }

  public void extractTo(final Path path) {
    checkArgument(path != null, "Invalid target directory");
    checkArgument(exists(zip), "Target does not exist");
    checkArgument(isDirectory(path), "Target is not a directory");

    try (ZipInputStream is =
        new ZipInputStream(new BufferedInputStream(new FileInputStream(zip.toFile())))) {
      logger.debug("Extracting '{}' zip file", this.zip.toFile().getName());
      ZipEntry entry = null;
      while ((entry = is.getNextEntry()) != null) {
        final Path output = path.resolve(entry.getName());
        if (!exists(output)) {
          if (entry.isDirectory()) {
            logger.debug("Creating directory: '{}'", output.toString());
            createDirectories(output);
          } else {
            logger.debug("Creating file: '{}'", output.toString());
            extractFile(is, new FileOutputStream(output.toFile()));
          }
        }
      }
      logger.debug("Zip file '{}' extracted successfully", this.zip.toFile().getName());
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void extractFile(final InputStream inputStream, final FileOutputStream outputStream)
      throws IOException {
    int count = -1;
    byte[] buffer = new byte[BUFFER_SIZE];
    try (BufferedOutputStream out = new BufferedOutputStream(outputStream, BUFFER_SIZE)) {
      while ((count = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
        out.write(buffer, 0, count);
      }
    }
  }
}
