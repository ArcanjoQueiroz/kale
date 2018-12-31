package br.com.alexandre.kale.xls;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.exists;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;

public class XlsWriter {

  private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
  private static final String EMPTY_VALUE = "";
  
  private Path path;
  private String sheetName;
  private boolean autoSize;
  private String dateFormat;

  private Logger logger = LoggerFactory.getLogger(XlsWriter.class);

  public XlsWriter(final Path path) {
    this.setPath(path);
    this.autoSize = true;
    this.setDateFormat(DEFAULT_DATE_FORMAT);
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
        for (final Object[] row : rows) {
          logger.debug("Writing row: '{}'", rowNumber);
          final org.apache.poi.ss.usermodel.Row ro = sheet.createRow(rowNumber++);
          int cellNumber = 0;
          if (row != null && row.length > 0) {
            logger.debug("Writing cell: '{}'", cellNumber);
            for (final Object value : row) {
              createCell(value, cellNumber++, ro, workbook);
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

  private org.apache.poi.ss.usermodel.Cell createCell(final Object value, final int number, final org.apache.poi.ss.usermodel.Row row, final Workbook workbook) {
    final org.apache.poi.ss.usermodel.Cell cell = row.createCell(number);
    if (value == null) {
      cell.setCellValue(EMPTY_VALUE);      
      logger.debug("Writing null value into cell: '{}'", number);
    } else {
      if (value instanceof java.util.Date) {
        cell.setCellValue((java.util.Date) value);
        cell.setCellStyle(createDateCellStyle(workbook, this.dateFormat));
      } else if (value instanceof Boolean) {
        cell.setCellValue((Boolean) value);
      } else if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue());
      } else {
        cell.setCellValue(value.toString());
      }
      logger.debug("Writing '{}' value into cell: '{}'", value.getClass().getSimpleName(), number);
    }
    return cell;
  }

  private CellStyle createDateCellStyle(final Workbook workbook, final String dateFormat) {
    final CellStyle cellStyle = workbook.createCellStyle();
    final DataFormat dataFormat = workbook.createDataFormat();
    final short df = dataFormat.getFormat(dateFormat);
    cellStyle.setDataFormat(df);
    return cellStyle;
  }

  public void setAutoSize(boolean autoSize) {
    this.autoSize = autoSize;
  }

  public void setDateFormat(String dateFormat) {
    checkArgument(!Strings.isNullOrEmpty(dateFormat), "Date format be different from null or empty");
    this.dateFormat = dateFormat;
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
