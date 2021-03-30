package com.github.arcanjoaq.kefla.docx;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import com.github.arcanjoaq.kefla.docx.DocxGenerator;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

public class DocxGeneratorTest {

  private DocxGenerator generator;

  @Before
  public void setUp() {
    this.generator = new DocxGenerator();
  }

  @Test
  public void shouldCreateAdocxFromTemplate() throws IOException, Exception {
    final File file = new File(String.format("target/%s.docx", new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())));
    try (final FileOutputStream outputStream = new FileOutputStream(file)) {
      this.generator.fromTemplate(
          Resources.getResource("docx/template.docx").openStream(), 
          ImmutableMap.of("name", "Alexandre"),
          outputStream);
    }
    assertThat(file).exists();
  }

}