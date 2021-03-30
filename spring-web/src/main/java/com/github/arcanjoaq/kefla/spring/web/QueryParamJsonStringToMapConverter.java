package com.github.arcanjoaq.kefla.spring.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

public class QueryParamJsonStringToMapConverter implements Converter<String, Map<String, Object>> {

  private Logger logger = LoggerFactory.getLogger(QueryParamJsonStringToMapConverter.class);

  private ObjectMapper objectMapper;

  public QueryParamJsonStringToMapConverter(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Map<String, Object> convert(final String source) {
    if (Strings.isNullOrEmpty(source)) {
      return new HashMap<>();
    }
    final String trimmedSource = source.trim();
    if (trimmedSource.startsWith("[") || trimmedSource.startsWith("{")) {
      try {
        return objectMapper.readValue(source, new TypeReference<HashMap<String,Object>>() {});
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    } else {
      final String[] parts = trimmedSource.split(",");
      final Map<String, Object> collect = Arrays.stream(parts)
          .map(p -> {
            final String[] c = p.split(":");
            if (c.length == 0) {
              throw new IllegalArgumentException(String.format("Invalid pair: %s", p));
            }
            final String key = c[0];
            if (Strings.isNullOrEmpty(key)) {
              throw new IllegalArgumentException(String.format("Invalid key in pair: %s", p));
            }        
            if (c.length > 2) {
              throw new IllegalArgumentException(String.format("Invalid key value pair: %s", p));
            }

            final String value = c.length == 2 && c[1] != null ? c[1].trim() : null;

            return Pair.of(key.trim(), value);
          })
          .filter(d -> d.getRight() != null)
          .collect(Collectors.toMap(x -> x.getKey().toString(), x -> convertValue(x.getValue())));
      logger.debug("Map generated: '{}'", collect);      
      return collect;
    }    
  }

  private Object convertValue(final String value) {
    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
      return Boolean.parseBoolean(value);
    }
    return value;
  }

}

