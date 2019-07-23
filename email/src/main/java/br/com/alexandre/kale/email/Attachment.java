package br.com.alexandre.kale.email;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Attachment implements Serializable {

  private static final long serialVersionUID = 7892157450458231156L;

  private InputStream source;
  private String mimeType;
  private String fileName;

  public Attachment() {}

  public Attachment(final InputStream source, final String mimeType, final String fileName) {
    this.source = source;
    this.mimeType = mimeType;
    this.fileName = fileName;
  }

  public InputStream getSource() {
    return source;
  }

  public void setSource(InputStream source) {
    this.source = source;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Attachment other = (Attachment) obj;
    if (fileName == null) {
      if (other.fileName != null) {
        return false;
      }
    } else if (!fileName.equals(other.fileName)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Attachment [source="
        + source
        + ", mimeType="
        + mimeType
        + ", fileName="
        + fileName
        + "]";
  }

  public static List<Attachment> from(
      final InputStream inputStream, final String mimeType, final String fileName) {
    return Arrays.asList(new Attachment(inputStream, mimeType, fileName));
  }
}
