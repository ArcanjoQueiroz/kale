package com.github.arcanjoaq.kefla.xls;

import static com.github.arcanjoaq.kefla.xls.CellFactory.createCell;
import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsWriter implements Closeable {

  private boolean autoSize;
  private Workbook workbook;
  private Sheet sheet;
  private OutputStream outputStream;
  private int rowNumber;

  private Logger logger = LoggerFactory.getLogger(XlsWriter.class);

  public XlsWriter(final OutputStream outputStream, final Format format) {
    this(outputStream, format, null);
  }

  public XlsWriter(final File file, final Format format, final String sheetName)
      throws IOException {
    this(new FileOutputStream(file), format, sheetName);
  }

  public XlsWriter(final File file) throws IOException {
    this(file, Format.XLSX, null);
  }

  public XlsWriter(final OutputStream outputStream, final Format format, final String sheetName) {
    checkArgument(outputStream != null, "OutputStream is null");
    this.autoSize = true;
    this.outputStream = outputStream;
    this.workbook = (format == Format.XLS) ? new HSSFWorkbook() : new XSSFWorkbook();
    this.sheet =
        Strings.isNullOrEmpty(sheetName) ? workbook.createSheet() : workbook.createSheet(sheetName);
    this.rowNumber = 0;
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
              logger.debug(
                  "Writing '{}' value into cell", cell.getClass().getSimpleName(), cellNumber);
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
