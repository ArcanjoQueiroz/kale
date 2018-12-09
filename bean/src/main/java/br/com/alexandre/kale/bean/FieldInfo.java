package br.com.alexandre.kale.bean;

import java.io.Serializable;

public class FieldInfo implements Serializable {

  private static final long serialVersionUID = 2631219851547865770L;

  private String field;
  private String operator;
  private Object value;

  public FieldInfo() { }

  public FieldInfo(final String field, final String operator, final Object value) {
    this.field = field;
    this.operator = operator;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public String getOperator() {
    return operator;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + ((operator == null) ? 0 : operator.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FieldInfo other = (FieldInfo) obj;
    if (field == null) {
      if (other.field != null)
        return false;
    } else if (!field.equals(other.field))
      return false;
    if (operator == null) {
      if (other.operator != null)
        return false;
    } else if (!operator.equals(other.operator))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return field + " " + operator + " " + value;
  }

}
