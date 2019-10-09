package br.com.alexandre.kale.spring.test;

import java.util.Map;
import org.springframework.orm.jpa.JpaVendorAdapter;

public interface IntegrationTestVendorAdapter {
  public JpaVendorAdapter getVendorAdapter();
  
  public Map<String, Object> getProperties();
}
