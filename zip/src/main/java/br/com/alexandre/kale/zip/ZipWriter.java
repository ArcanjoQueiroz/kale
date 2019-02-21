package br.com.alexandre.kale.zip;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.newDirectoryStream;
import static org.apache.commons.compress.utils.IOUtils.copy;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipWriter implements Closeable {

  private final OutputStream destination;
  private final ArchiveOutputStream zip;

  private final Logger logger = LoggerFactory.getLogger(ZipWriter.class);

  public ZipWriter(final OutputStream destination) {
    checkArgument(destination != null, "Destination is null");
    this.destination = destination;
    this.zip = createZipOutputStream(this.destination);        
  }

  public ZipWriter(final File file) {
    checkArgument(file != null, "Destination file is null");
    checkArgument(!file.exists(), "Destination file exists");
    try {
      this.destination = new FileOutputStream(file);
    } catch (final FileNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    this.zip = createZipOutputStream(this.destination);       
  }

  private ArchiveOutputStream createZipOutputStream(OutputStream out) {
    try {
      return new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, out);
    } catch (final ArchiveException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public void write(final File...paths) {
    checkArgument(paths != null && paths.length > 0, "There is no input resources");
    try {
      write(zip, paths);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void finish() throws IOException {
    zip.finish();
  }

  private void write(final ArchiveOutputStream zip, final File... files) throws IOException {
    for (final File file: files) {
      if (file.isFile()) {
        logger.debug("Archiving file '{}'", file.getName().toString());
        zip.putArchiveEntry(new ZipArchiveEntry(file, file.getName().toString()));
        copy(new FileInputStream(file), zip);
        zip.closeArchiveEntry();
      } else if (file.isDirectory()) {
        try (DirectoryStream<Path> directoryStream = newDirectoryStream(Paths.get(file.getAbsolutePath()))) {
          final List<File> directoryPaths = StreamSupport
              .stream(directoryStream.spliterator(), false)
              .map(f -> f.toFile())
              .collect(Collectors.toList());
          if (directoryPaths.size() > 0) {
            final File[] param = new File[directoryPaths.size()];
            logger.debug("Invoking write with subdirectory '{}' content", file.toString());
            write(zip, directoryPaths.toArray(param));
          } else {
            logger.debug("There is no content to iterate");
          }
        } 
      }
    }
  }

  @Override
  public void close() throws IOException {     
    this.zip.close();        
  }
}

