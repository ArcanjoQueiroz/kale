package br.com.alexandre.kale.docx;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocxAppender implements Closeable {

  private OutputStream output;
  private List<File> inputs;
  private XWPFDocument root;
  private boolean closed;

  private Logger logger = LoggerFactory.getLogger(DocxAppender.class);

  public DocxAppender(final OutputStream output) {
    checkArgument(output != null, "Invalid output");
    this.output = output;
    this.inputs = new ArrayList<>();
    this.closed = false;
  }

  public DocxAppender add(final File input) {
    checkArgument(input != null, "Invalid input");
    checkState(!this.closed, "Appender is already closed");
    inputs.add(input);
    return this;
  }

  public void append() throws IOException {
    if (inputs.size() > 0) {
      try (final XWPFDocument left = new XWPFDocument(OPCPackage.open(inputs.get(0)))) {
        root = left;
        if (inputs.size() > 1) {
          for (int i = 1; i < inputs.size(); i++) {
            try (final XWPFDocument right = new XWPFDocument(OPCPackage.open(inputs.get(i)))) {
              root.getDocument().addNewBody().set(right.getDocument().getBody());                        
            }
          }
        }
        root.write(output);
      } catch (InvalidFormatException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void close() throws IOException {
    logger.debug("Closing resources");
    try {
      output.close();
    } catch (final RuntimeException e) {
      logger.info("Error on close OutputStream: ", e);
    }
    this.closed = true;
  }

}