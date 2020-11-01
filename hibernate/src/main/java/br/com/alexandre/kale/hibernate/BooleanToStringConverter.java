package br.com.alexandre.kale.hibernate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

  @Override
  public String convertToDatabaseColumn(Boolean value) {        
    return (value != null && value.equals(Boolean.TRUE)) ? "S" : "N";            
  }    

  @Override
  public Boolean convertToEntityAttribute(String value) {
    return "S".equals(value);
  }
}