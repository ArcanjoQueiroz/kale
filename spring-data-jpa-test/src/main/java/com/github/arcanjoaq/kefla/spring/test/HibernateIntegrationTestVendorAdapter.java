package com.github.arcanjoaq.kefla.spring.test;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

public class HibernateIntegrationTestVendorAdapter implements IntegrationTestVendorAdapter {

  @Value("${hibernate.proc.param_null_passing:true}") 
  private boolean nullPassing;
  
  @Value("${hibernate.show_sql:true}") 
  private boolean showSql;
  
  @Value("${hibernate.format_sql:true}") 
  private boolean formatSql;
  
  @Value("${hibernate.generate_ddl:false}") 
  private boolean generateDdl;
  
  @Value("${hibernate.use_sql_comments:true}") 
  private boolean useSqlComments;
  
  @Value("${hibernate.jdbc.lob.non_contextual_creation:true}") 
  private boolean lobNonContextualCreation;
  
  @Value("${hibernate.temp.use_jdbc_metadata_defaults:false}") 
  private boolean useJdbcMetadataDefaults;

  private final Logger logger = LoggerFactory
      .getLogger(HibernateIntegrationTestVendorAdapter.class);
  
  @Override
  public JpaVendorAdapter getVendorAdapter() {
    return new HibernateJpaVendorAdapter();
  }

  @Override
  public Map<String, Object> getProperties() {
    var properties = new HashMap<String, Object>();
    properties.put("hibernate.proc.param_null_passing", nullPassing);
    properties.put("hibernate.show_sql", showSql);
    properties.put("hibernate.format_sql", formatSql);
    properties.put("hibernate.generate_ddl", generateDdl);
    properties.put("hibernate.use_sql_comments", useSqlComments);
    properties.put("hibernate.jdbc.lob.non_contextual_creation", lobNonContextualCreation);
    properties.put("hibernate.hibernate.temp.use_jdbc_metadata_defaults", 
        useJdbcMetadataDefaults);    
    logger.debug("Using Hibernate Properties: '{}'", properties);
    return properties;
  }

}
