package com.github.arcanjoaq.kefla.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.xwpf.converter.core.XWPFConverterException;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class ApachePoiDocxToPdfConverter extends OutputStreamPdfConverter {

  public void convert(final File source, final OutputStream out) {
    try (final InputStream doc = new FileInputStream(source);
        final XWPFDocument document = new XWPFDocument(doc)) {
      final PdfOptions options = PdfOptions.create();
      PdfConverter.getInstance().convert(document, out, options);
    } catch (XWPFConverterException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
