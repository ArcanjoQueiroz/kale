package br.com.alexandre.kale.docx;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

public class DocxGenerator {

  public OutputStream fromTemplate(final InputStream template, final Map<String, String> context) throws Exception {
    final WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(template);
    final MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
    VariablePrepare.prepare(wordMLPackage);
    documentPart.variableReplace(context);
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    wordMLPackage.save(outputStream);
    return outputStream;
  }
}
