package com.github.arcanjoaq.kefla.spring.test;

import java.sql.SQLException;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import com.google.common.base.Strings;

@TestConfiguration
@AutoConfigureJdbc
public abstract class JpaIntegrationTestConfig {

  private final Logger logger = LoggerFactory.getLogger(JpaIntegrationTestConfig.class);
  
  @Bean
  @ConditionalOnMissingBean(LocalContainerEntityManagerFactoryBean.class)
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Value("${spring.jpa.persistence-unit-name:default}") final String persistenceUnitName,
      @Value("${spring.jpa.properties.hibernate.dialect:}") final String dialect,
      @Value("${spring.jpa.properties.hibernate.jdbc.use_scrollable_resultset:}") 
      final Boolean useScrollableResultset,
      @Value("${spring.jpa.properties.hibernate.jdbc.use_get_generated_keys:}") 
      final Boolean useGetGeneratedKeys,      
      @Value("${spring.jpa.properties.hibernate.proc.param_null_passing:true}") 
      final Boolean nullPassing,
      @Value("${spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults:}") 
      final Boolean useJdbcMetadataDefaults,
      @Value("${spring.jpa.properties.show_sql:false}") final boolean showSql,
      @Value("${spring.jpa.properties.format_sql:false}") final boolean formatSql,
      @Value("${spring.jpa.properties.generate_ddl:false}") final boolean generateDdl,
      @Value("${spring.jpa.properties.use_sql_comments:false}") final boolean useSqlComments,      
      final DataSource dataSource) {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();
   
    if (this.getClass().isAnnotationPresent(PackageScan.class)) {
      final PackageScan packageScan = this.getClass().getAnnotation(PackageScan.class);
      final String[] value = packageScan.value();
      logger.info("Utilizando pacotes do package scan: '{}'", String.join(",", value));
      entityManagerFactoryBean.setPackagesToScan(value);  
    } else if (this.getClass().isAnnotationPresent(EnableJpaRepositories.class)) {
      final EnableJpaRepositories enableJpaRepositories = this.getClass()
          .getAnnotation(EnableJpaRepositories.class);
      final String[] value = enableJpaRepositories.value();
      logger.info("Utilizando pacotes dos repositorios JPA: '{}'", String.join(",", value));
      entityManagerFactoryBean.setPackagesToScan(value);
    } else {
      final String packageName = this.getClass().getPackageName();
      logger.info("Utilizando pacote padrão: '{}'", packageName);
      entityManagerFactoryBean.setPackagesToScan(packageName);
    }
        
    entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);
    entityManagerFactoryBean.setDataSource(dataSource);

    final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setShowSql(showSql);
    adapter.setGenerateDdl(generateDdl);

    final Properties propertyMap = new Properties();
    propertyMap.put("hibernate.show_sql", showSql);
    propertyMap.put("hibernate.format_sql", formatSql);
    propertyMap.put("hibernate.use_sql_comments", useSqlComments);

    if (!Strings.isNullOrEmpty(dialect)) {
      propertyMap.put("hibernate.dialect", dialect);
    }
    if (useScrollableResultset != null) {
      propertyMap.put("hibernate.jdbc.use_scrollable_resultset", useScrollableResultset);
    }
    if (useGetGeneratedKeys != null) {
      propertyMap.put("hibernate.jdbc.use_get_generated_keys", useGetGeneratedKeys);
    }    
    if (nullPassing != null) {
      propertyMap.put("hibernate.proc.param_null_passing", nullPassing);
    }
    if (useJdbcMetadataDefaults != null) {
      propertyMap.put("hibernate.temp.use_jdbc_metadata_defaults", useJdbcMetadataDefaults);
    }
    
    entityManagerFactoryBean.setJpaVendorAdapter(adapter);
    entityManagerFactoryBean.setJpaProperties(propertyMap);
    
    return entityManagerFactoryBean;
  }

  @Bean
  @ConditionalOnMissingBean(EntityManager.class)
  public EntityManager entityManager(final EntityManagerFactory entityManagerFactory) {
    return entityManagerFactory.createEntityManager();
  }

  @Bean
  @ConditionalOnMissingBean(PlatformTransactionManager.class)
  public PlatformTransactionManager transactionManager(
      final EntityManagerFactory entityManagerFactory) throws SQLException {
    final JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory);
    return txManager;
  }

  @Bean
  @ConditionalOnMissingBean(PersistenceExceptionTranslationPostProcessor.class)
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  @Bean
  @ConditionalOnMissingBean(PersistenceAnnotationBeanPostProcessor.class)
  public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
    return new PersistenceAnnotationBeanPostProcessor();
  }

  
}
