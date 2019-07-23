package br.com.alexandre.kale.pdf;

import java.io.File;

public interface PdfConverter {
  public File convertAsync(final File source);

  public byte[] convertSync(final File source);
}
