package br.com.alexandre.kale.pdf;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OutputStreamPdfConverter implements PdfConverter {

  private Logger logger = LoggerFactory.getLogger(OutputStreamPdfConverter.class);

  @Override
  public File convertAsync(final File source) {
    logger.debug("Converting the file: '{}'", source.getAbsolutePath());
    checkArgument(source != null && source.exists() && source.isFile(), "source file");

    final File destination =
        new File(
            String.format(
                "%s%s%s.pdf",
                System.getProperty("java.io.tmpdir"),
                File.separator,
                RandomStringUtils.randomAlphabetic(25)));
    try {
      convert(source, new FileOutputStream(destination));
    } catch (final FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return destination;
  }

  @Override
  public synchronized byte[] convertSync(final File source) {
    logger.debug("Converting the file: '{}'", source.getAbsolutePath());
    checkArgument(source != null && source.exists() && source.isFile(), "source file");

    try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      convert(source, outputStream);
      return outputStream.toByteArray();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public abstract void convert(final File source, final OutputStream outputStream);
}
