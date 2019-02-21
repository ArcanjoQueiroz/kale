package br.com.alexandre.kale.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class Doc4jFOPDFConverter extends OutputStreamPDFConverter {

  @Override
  public void convert(final File source, final OutputStream outputStream) {
    try (final InputStream inputStream = new FileInputStream(source)) {
      final WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
      FieldUpdater updater = new FieldUpdater(wordMLPackage);
      updater.update(true);

      final FOSettings foSettings = Docx4J.createFOSettings();
      foSettings.setWmlPackage(wordMLPackage);

      Docx4J.toFO(foSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }        
  }

}
