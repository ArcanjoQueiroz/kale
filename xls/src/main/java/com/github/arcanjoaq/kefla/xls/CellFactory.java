package com.github.arcanjoaq.kefla.xls;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

class CellFactory {

  private static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm:ss";
  private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
  private static final String DEFAULT_TIME_FORMAT = "hh:mm:ss";

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final Object value) {
    if (value == null) {
      return createCell(workbook, row, number);
    } else if (value instanceof Date) {
      return createCell(workbook, row, number, (Date) value);
    } else if (value instanceof LocalDate) {
      return createCell(workbook, row, number, (LocalDate) value);
    } else if (value instanceof LocalTime) {
      return createCell(workbook, row, number, (LocalTime) value);
    } else if (value instanceof LocalDateTime) {
      return createCell(workbook, row, number, (LocalDateTime) value);
    } else if (value instanceof Boolean) {
      return createCell(workbook, row, number, (Boolean) value);
    } else if (value instanceof Number) {
      return createCell(workbook, row, number, (Number) value);
    } else {
      return createCell(workbook, row, number, (String) value);
    }
  }

  public static Cell createCell(final Workbook workbook, final Row row, final int number) {
    final Cell cell = row.createCell(number);
    return cell;
  }

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final String value) {
    final Cell cell = row.createCell(number);
    cell.setCellValue(value.toString().trim());
    return cell;
  }

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final Boolean value) {
    final Cell cell = row.createCell(number);
    cell.setCellValue((Boolean) value);
    return cell;
  }

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final Number value) {
    final Cell cell = row.createCell(number);
    cell.setCellValue(((Number) value).doubleValue());
    return cell;
  }

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final LocalDateTime value) {
    final Date date = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
    return createCell(workbook, row, number, date, DEFAULT_DATE_TIME_FORMAT);
  }

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final LocalDate value) {
    final Date date =
        Date.from(((LocalDate) value).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    return createCell(workbook, row, number, date, DEFAULT_DATE_FORMAT);
  }

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final LocalTime value) {
    final Date date =
        Date.from(
            ((LocalTime) value).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
    return createCell(workbook, row, number, date, DEFAULT_TIME_FORMAT);
  }

  public static Cell createCell(
      final Workbook workbook, final Row row, final int number, final Date value) {
    return createCell(workbook, row, number, value, DEFAULT_DATE_FORMAT);
  }

  public static Cell createCell(
      final Workbook workbook,
      final Row row,
      final int number,
      final Date value,
      final String format) {
    final Cell cell = row.createCell(number);
    cell.setCellValue(value);
    cell.setCellStyle(createCellStyle(workbook, format));
    return cell;
  }

  private static CellStyle createCellStyle(final Workbook workbook, final String format) {
    final CreationHelper createHelper = workbook.getCreationHelper();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat(format));
    return cellStyle;
  }
}
