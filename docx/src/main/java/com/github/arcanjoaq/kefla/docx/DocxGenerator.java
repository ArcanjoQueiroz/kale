package com.github.arcanjoaq.kefla.docx;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;

public class DocxGenerator {

  public void fromTemplate(final InputStream template, final Map<String, String> context,
      final OutputStream outputStream) throws Exception {
    final WordprocessingMLPackage wordMlPackage = WordprocessingMLPackage.load(template);
    final MainDocumentPart documentPart = wordMlPackage.getMainDocumentPart();            

    final String xml = XmlUtils.marshaltoString(documentPart.getJaxbElement());
    final Object obj = XmlUtils.unmarshallFromTemplate(xml, context);

    documentPart.setJaxbElement((Document) obj);

    final Save saver = new Save(wordMlPackage);
    saver.save(outputStream);
  }
}
