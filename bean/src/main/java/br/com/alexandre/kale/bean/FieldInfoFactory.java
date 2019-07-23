package br.com.alexandre.kale.bean;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;

public class FieldInfoFactory {
  private ObjectMapper objectMapper;

  public FieldInfoFactory() {
    this.objectMapper = defaultObjectMapper();
  }

  public FieldInfoFactory(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private ObjectMapper defaultObjectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    return objectMapper;
  }

  @SuppressWarnings("unchecked")
  public List<FieldInfo> createFieldInfo(final Object o) {
    checkArgument(o != null, "Illegal Argument: value is null");
    final Map<String, Object> value = objectMapper.convertValue(o, Map.class);
    return value.entrySet().stream()
        .map(entry -> createField(o, entry))
        .collect(Collectors.toList());
  }

  private FieldInfo createField(final Object o, final Entry<String, Object> entry) {
    final Field field = FieldUtils.getField(o.getClass(), entry.getKey(), true);
    String operator;
    if (field.isAnnotationPresent(LessThan.class)) {
      operator = "<";
    } else if (field.isAnnotationPresent(GreaterThan.class)) {
      operator = ">";
    } else if (field.isAnnotationPresent(LessThanOrEqualTo.class)) {
      operator = "<=";
    } else if (field.isAnnotationPresent(GreaterThanOrEqualTo.class)) {
      operator = ">=";
    } else {
      operator = "=";
    }
    return new FieldInfo(entry.getKey(), operator, entry.getValue());
  }

  public void setObjectMapper(final ObjectMapper objectMapper) {
    if (objectMapper != null) {
      this.objectMapper = objectMapper;
    }
  }
}
