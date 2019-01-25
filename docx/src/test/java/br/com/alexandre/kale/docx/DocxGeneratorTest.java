package br.com.alexandre.kale.docx;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Before;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class DocxGeneratorTest {

  private DocxGenerator generator;
  
  @Before
  public void setUp() {
    this.generator = new DocxGenerator();
  }
  
  @Test
  public void shouldCreateAdocxFromTemplate() throws IOException, Exception {
    try (OutputStream outputStream = this.generator.fromTemplate(Resources.getResource("docx/template.docx").openStream(), ImmutableMap.of("name", "Alexandre"))) {
      final File file = new File("target/birthday.docx");
      Files.write(((ByteArrayOutputStream) outputStream).toByteArray(), file);
      assertThat(file).exists();
    }
  }

}
