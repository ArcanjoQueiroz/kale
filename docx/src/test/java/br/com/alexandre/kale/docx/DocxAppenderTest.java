package br.com.alexandre.kale.docx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;
import com.google.common.io.Resources;

public class DocxAppenderTest {

  @Test
  public void test() throws IOException, Exception {
    final File file = new File("target/out.docx");
    try (final DocxAppender appender = new DocxAppender(new FileOutputStream(file))) {
      appender
        .add(Resources.getResource("docx/fileOne.docx").openStream())
        .add(Resources.getResource("docx/fileTwo.docx").openStream())
        .add(Resources.getResource("docx/fileThree.docx").openStream())
        .append();
    }
  }

}
