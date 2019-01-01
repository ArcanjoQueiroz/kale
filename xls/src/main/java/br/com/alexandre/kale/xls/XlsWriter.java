package br.com.alexandre.kale.xls;

import static br.com.alexandre.kale.xls.CellFactory.createCell;
import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.exists;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;

public class XlsWriter {

  private Path path;
  private String sheetName;
  private boolean autoSize;

  private Logger logger = LoggerFactory.getLogger(XlsWriter.class);

  public XlsWriter(final Path path) {
    this.setPath(path);
    this.autoSize = true;
  }

  public XlsWriter(final Path path, final String sheetName) {
    this(path);
    this.setSheetName(sheetName);
  }

  public void write(final List<Object[]> rows) {
    checkArgument(rows != null, "Rows is null");
    try (final Workbook workbook = path.getFileName().endsWith(".xls") ? new HSSFWorkbook() : new XSSFWorkbook();
        final FileOutputStream fileOutputStream = new FileOutputStream(path.toAbsolutePath().toString())) {
      logger.debug("Starting writing process. File: '{}'", this.path.toAbsolutePath().toString());
      final Sheet sheet = Strings.isNullOrEmpty(sheetName) ? workbook.createSheet(): workbook.createSheet(sheetName);
      int lastColumnNumber = 0;
      int rowNumber = 0;
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
      logger.debug("Writing to output stream");
      workbook.write(fileOutputStream);
      logger.debug("Writing process finished successfully. File: '{}'", this.path.toAbsolutePath().toString());
    } catch (final IOException e) {
      throw new RuntimeException("Error on Write Excel file: " + e.getMessage(), e);
    }

  }

  public void setAutoSize(boolean autoSize) {
    this.autoSize = autoSize;
  }

  private void setSheetName(final String sheetName) {
    checkArgument(!Strings.isNullOrEmpty(sheetName), "Sheet name must be different from null or empty");
    this.sheetName = sheetName;
  }

  private void setPath(final Path path) {
    checkArgument(path != null, "Destination file is null");
    checkArgument(!exists(path), "Destination file exists: " + path.getFileName().toString());
    checkArgument(path.getFileName().toString().endsWith(".xls") || path.getFileName().toString().endsWith(".xlsx"), "Destination file is not a XLS or XLSX file: " +  path.getFileName().toString());
    this.path = path;
  }

}
