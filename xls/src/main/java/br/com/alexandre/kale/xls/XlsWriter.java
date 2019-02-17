package br.com.alexandre.kale.xls;

import static br.com.alexandre.kale.xls.CellFactory.createCell;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import static java.util.Arrays.asList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;

public class XlsWriter implements Closeable {

  private boolean autoSize;
  private Workbook workbook;
  private Sheet sheet;
  private OutputStream outputStream;
  private int rowNumber;

  private Logger logger = LoggerFactory.getLogger(XlsWriter.class);

  public XlsWriter(final File file, final String sheetName) throws IOException {
    checkArgument(file != null, "File is null");
    final String extension = getExtension(file);
    checkArgument(asList("xls", "xlsx").contains(extension), "File is not a regular Excel file");    
    this.autoSize = true;
    this.outputStream = new FileOutputStream(file);
    this.workbook = "xls".equalsIgnoreCase(extension) ? new HSSFWorkbook() : new XSSFWorkbook();
    this.sheet = Strings.isNullOrEmpty(sheetName) ? workbook.createSheet(): workbook.createSheet(sheetName);
    this.rowNumber = 0;
  }

  private String getExtension(final File file) {
    final String[] strings = file.getName().split("\\.");
    return (strings.length > 1) ? Strings.nullToEmpty(strings[strings.length - 1]).toLowerCase(): null; 
  }

  public XlsWriter(final File file) throws IOException {
    this(file, null);
  }

  public void write(final List<Object[]> rows) {
    if (rows != null) {
      logger.debug("Starting writing process.");
      int lastColumnNumber = 0;
      if (!rows.isEmpty()) {
        for (final Object[] cells : rows) {
          logger.debug("Writing row: '{}'", rowNumber);
          final Row ro = sheet.createRow(rowNumber++);
          int cellNumber = 0;
          if (cells != null && cells.length > 0) {
            logger.debug("Writing cell: '{}'", cellNumber);
            for (final Object cell : cells) {
              createCell(workbook, ro, cellNumber, cell);
              logger.debug("Writing '{}' value into cell", cell.getClass().getSimpleName(), cellNumber);
              cellNumber++;
            }
            if (cellNumber > lastColumnNumber) {
              lastColumnNumber = cellNumber;
            }
          } else {
            logger.warn("There are not cells to write");
          }
        }
      } else {
        logger.warn("There are not rows to write");
      }
      if (this.autoSize) {
        logger.debug("Auto sizing columns");
        for (int i = 0; i < lastColumnNumber; i++) {
          sheet.autoSizeColumn(i, true);
        }
      }
    }
  }

  public void setAutoSize(boolean autoSize) {
    this.autoSize = autoSize;
  }

  @Override
  public void close() throws IOException {        
    logger.debug("Writing to output stream");
    try {
      workbook.write(outputStream);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    workbook.close();
  }

}