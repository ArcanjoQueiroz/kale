package br.com.alexandre.kale.docx;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class DocxAppender implements Closeable {

  private OutputStream output;
  private List<InputStream> inputs;
  private XWPFDocument root;
  private boolean closed;
  
  private Logger logger = LoggerFactory.getLogger(DocxAppender.class);

  public DocxAppender(final OutputStream output) {
    checkArgument(output != null, "Invalid output");
    this.output = output;
    this.inputs = new ArrayList<>();
    this.closed = false;
  }

  public DocxAppender add(final InputStream input) throws IOException, InvalidFormatException {
    checkArgument(input != null, "Invalid input");
    checkState(!this.closed, "Appender is already closed");
    inputs.add(input);
    final OPCPackage opcPackage = OPCPackage.open(input);
    final XWPFDocument docx = new XWPFDocument(opcPackage);
    logger.debug("Append document body");
    if(inputs.size() == 1) {
      root = docx;
    } else {
      final CTBody body = docx.getDocument().getBody();
      root.getDocument().addNewBody().set(body);            
    }
    return this;
  }
  
  public void append() throws IOException {
    logger.debug("Writing output");
    root.write(output);
    logger.debug("Flushing results");
    output.flush();
  }

  @Override
  public void close() throws IOException {
    logger.debug("Closing resources");
    try {
      output.close();
    } catch (final RuntimeException e) {
      logger.info("Error on close OutputStream: ", e);
    }
    for (final InputStream input: inputs) {      
      try {
        input.close();
      } catch (final RuntimeException e) {
        logger.info("Error on close InputStream: ", e);
      }
    }
    this.closed = true;
  }

}
