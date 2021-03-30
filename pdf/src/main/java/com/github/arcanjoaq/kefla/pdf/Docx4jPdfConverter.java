package com.github.arcanjoaq.kefla.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.docx4j.Docx4J;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class Docx4jPdfConverter extends OutputStreamPdfConverter {

  public void convert(final File source, final OutputStream outputStream) {
    try (final InputStream inputStream = new FileInputStream(source)) {
      final WordprocessingMLPackage wordMlPackage = WordprocessingMLPackage.load(inputStream);
      final FieldUpdater updater = new FieldUpdater(wordMlPackage);
      updater.update(true);

      Docx4J.toPDF(wordMlPackage, outputStream);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}
