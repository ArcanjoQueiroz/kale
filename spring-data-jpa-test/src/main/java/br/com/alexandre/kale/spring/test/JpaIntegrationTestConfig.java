package br.com.alexandre.kale.spring.test;

import java.sql.SQLException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import com.zaxxer.hikari.HikariDataSource;

public abstract class JpaIntegrationTestConfig {

  private String[] packagesToScan;

  public JpaIntegrationTestConfig(final String packageToScan) {
    this.packagesToScan = new String[] {packageToScan};
  }

  public JpaIntegrationTestConfig(final String[] packagesToScan) {
    this.packagesToScan = packagesToScan;
  }
  
  @Bean
  @ConditionalOnMissingBean(IntegrationTestVendorAdapter.class)
  public IntegrationTestVendorAdapter integrationTestVendorAdapter() {
    return new HibernateIntegrationTestVendorAdapter();
  }

  @Bean
  @Primary
  public DataSource getDataSource(
      @Value("${spring.datasource.url}") final String url,
      @Value("${spring.datasource.driverClassName}") final String driverClassName,
      @Value("${spring.datasource.username}") final String username,
      @Value("${spring.datasource.password}") final String password) {
    return DataSourceBuilder.create()
        .driverClassName(driverClassName)
        .username(username)
        .password(password)
        .url(url)
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      @Value("${spring.jpa.persistence-unit-name:default}") final String persistenceUnitName,
      final IntegrationTestVendorAdapter integrationTestVendorAdapter,
      final DataSource dataSource) {

    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();
    if (packagesToScan != null && packagesToScan.length > 0) {
      entityManagerFactoryBean.setPackagesToScan(packagesToScan);
    }
    entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);
    entityManagerFactoryBean.setDataSource(dataSource);
    entityManagerFactoryBean.setJpaVendorAdapter(integrationTestVendorAdapter.getVendorAdapter());
    entityManagerFactoryBean.setJpaPropertyMap(integrationTestVendorAdapter.getProperties());
    
    entityManagerFactoryBean.afterPropertiesSet();
    entityManagerFactoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());

    return entityManagerFactoryBean;
  }

  @Bean
  public EntityManager entityManager(final EntityManagerFactory entityManagerFactory) {
    return entityManagerFactory.createEntityManager();
  }

  @Bean
  public PlatformTransactionManager transactionManager(
      final EntityManagerFactory entityManagerFactory) throws SQLException {
    final JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory);
    return txManager;
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }
}
