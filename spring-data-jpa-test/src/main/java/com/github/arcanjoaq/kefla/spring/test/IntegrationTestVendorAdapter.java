package com.github.arcanjoaq.kefla.spring.test;

import java.util.Map;
import org.springframework.orm.jpa.JpaVendorAdapter;

public interface IntegrationTestVendorAdapter {
  public JpaVendorAdapter getVendorAdapter();
  
  public Map<String, Object> getProperties();
}
