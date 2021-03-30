package com.github.arcanjoaq.kefla.csv;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Joiner;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Csv implements Closeable {

  private ICSVWriter writer;

  private char separator = ';';

  private Map<Integer, Function<Object, Object>> converters = new HashMap<>();

  private boolean closed;

  protected Csv() {}

  public Csv(final OutputStream out, final Charset cs) {
    checkArgument(out != null, "Invalid OutputStream: OutputStream is null");
    checkArgument(cs != null, "Invalid Charset: Charset is null");
    this.writer =
        new CSVWriterBuilder(new OutputStreamWriter(out, cs)).withSeparator(separator).build();
    this.closed = false;
  }

  public void writeRow(final Object[] row) {
    checkState(!this.closed, "CSV is closed");
    this.writer.writeNext(coalesce(row, converters));
  }

  public void writeRows(final List<Object[]> rows) throws IOException {
    checkArgument(rows != null && !rows.isEmpty(), "Invalid List: List is null or empty");
    rows.forEach(row -> writeRow(row));
    this.writer.flush();
  }

  @Override
  public void close() throws IOException {
    this.writer.close();
    this.closed = true;
  }

  public void addConverter(final int index, final Function<Object, Object> function) {
    checkArgument(index >= 0, "Index out of bounds");
    checkArgument(function != null, "Invalid function: Function is null");
    this.converters.put(index, function);
  }

  protected String[] coalesce(
      final Object[] row, final Map<Integer, Function<Object, Object>> converters) {
    final List<String> r = new ArrayList<>();
    if (row != null) {
      for (int i = 0; i < row.length; i++) {
        r.add(
            convert(
                row[i],
                converters != null && converters.containsKey(i) ? converters.get(i) : null));
      }
    }
    return r.stream().toArray(String[]::new);
  }

  protected String convert(Object field, final Function<Object, Object> converter) {
    if (converter != null) {
      field = converter.apply(field);
    }
    if (field == null) {
      field = "";
    } else if (field instanceof Iterable) {
      field = Joiner.on(",").skipNulls().join(((Iterable<?>) field));
    } else if (field.toString().trim().isEmpty()) {
      field = "";
    }
    return field.toString();
  }
}
