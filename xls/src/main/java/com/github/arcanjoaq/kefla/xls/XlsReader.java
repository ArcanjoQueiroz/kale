package com.github.arcanjoaq.kefla.xls;

import static com.google.common.base.Preconditions.checkArgument;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsReader {

  private Path path;
  private String sheetName;

  private Logger logger = LoggerFactory.getLogger(XlsReader.class);

  public XlsReader(final Path path) {
    setPath(path);
  }

  public XlsReader(final Path path, final String sheetName) {
    this(path);
    this.setSheetName(sheetName);
  }

  public List<Object[]> read() {
    logger.debug("Starting reading process. File: '{}'", this.path.toAbsolutePath().toString());
    final List<Object[]> list = new ArrayList<Object[]>();
    try (final Workbook workbook =
        this.path.getFileName().toString().toLowerCase().endsWith(".xls") ? new HSSFWorkbook()
            : new XSSFWorkbook();
        final FileInputStream fileInputStream = new FileInputStream(this.path.toFile())) {
      final Sheet sheet = (Strings.isNullOrEmpty(sheetName)) ? workbook.getSheetAt(0)
          : workbook.getSheet(sheetName);
      final Iterator<Row> iterator = sheet.iterator();
      while (iterator.hasNext()) {
        final Row row = iterator.next();
        final Object[] cells = getCells(row);
        logger.info("Read '{}' cells from row: '{}'", cells.length, row.getRowNum());
        if (logger.isDebugEnabled()) {
          logger.debug("Cell read: '{}'", Joiner.on(",").useForNull("").join(cells));
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException("Error on Read Excel File: " + e.getMessage(), e);
    }
    logger.info(
        "Read '{}' rows from file: '{}'", list.size(), this.path.toAbsolutePath().toString());
    return list;
  }

  private Object[] getCells(final Row row) {
    final List<Object> cells = new ArrayList<>();
    final Iterator<Cell> cellIterator = row.cellIterator();
    while (cellIterator.hasNext()) {
      final Cell cell = cellIterator.next();
      switch (cell.getCellType()) {
        case BOOLEAN: {
          cells.add(cell.getBooleanCellValue());
          break;
        }
        case STRING: {
          cells.add(cell.getStringCellValue());
          break;
        }
        case NUMERIC: {
          if (DateUtil.isCellDateFormatted(cell)) {
            cells.add(cell.getDateCellValue());
          } else {
            cells.add(cell.getNumericCellValue());
          }
          break;
        }
        case BLANK: {
          cells.add("");
          break;
        }
        default:
          break;
      }
    }
    return cells.stream().toArray(Object[]::new);
  }

  private void setPath(final Path path) {
    checkArgument(path != null, "Source is null");
    checkArgument(exists(path), "Source file does not exist");
    checkArgument(isRegularFile(path), "File is not a regular file");
    checkArgument(
        path.getFileName().toString().toLowerCase().endsWith(".xls")
        || path.getFileName().toString().toLowerCase().endsWith(".xlsx"),
        "Source file is not a XLS or XLSX file: " + path.getFileName().toString());
    this.path = path;
  }

  private void setSheetName(final String sheetName) {
    checkArgument(
        !Strings.isNullOrEmpty(sheetName), "Sheet name must be different from null or empty");
    this.sheetName = sheetName;
  }
}
