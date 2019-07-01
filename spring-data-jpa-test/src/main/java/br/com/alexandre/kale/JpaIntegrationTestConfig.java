package br.com.alexandre.kale;

import java.sql.SQLException;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import com.zaxxer.hikari.HikariDataSource;

public abstract class JpaIntegrationTestConfig {

    private String[] packagesToScan;
    
    public JpaIntegrationTestConfig(final String... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Bean
    @Primary
    public DataSource getDataSource(@Value("${spring.datasource.url}") final String url,
            @Value("${spring.datasource.driverClassName}") final String driverClassName,
            @Value("${spring.datasource.username}") final String username,
            @Value("${spring.datasource.password}") final String password) {     
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .username(username)
                .password(password)
                .url(url)
                .type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Value("${spring.jpa.persistence-unit-name:default}") final String persistenceUnitName,
            @Value("${spring.jpa.properties.proc.param_null_passing:true}") final boolean nullPassing,
            @Value("${spring.jpa.properties.show_sql:true}") final boolean showSql,
            @Value("${spring.jpa.properties.format_sql:true}") final boolean formatSql,
            @Value("${spring.jpa.properties.generate_ddl:false}") final boolean generateDdl,
            @Value("${spring.jpa.properties.use_sql_comments:true}") final boolean useSqlComments, 
            final DataSource dataSource) {
     
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        if (packagesToScan != null && packagesToScan.length > 0) {
            entityManagerFactoryBean.setPackagesToScan(packagesToScan);
        }
        entityManagerFactoryBean.setPersistenceUnitName(persistenceUnitName);
        entityManagerFactoryBean.setDataSource(dataSource);

        final JpaVendorAdapter adapter = createHibernateJpaVendorAdapter(nullPassing, showSql, formatSql, generateDdl, useSqlComments);     
        entityManagerFactoryBean.setJpaVendorAdapter(adapter);

        return entityManagerFactoryBean;
    }

    private JpaVendorAdapter createHibernateJpaVendorAdapter(final boolean nullPassing, final boolean showSql, 
        final boolean formatSql, final boolean generateDdl, final boolean useSqlComments) {
      final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
      adapter.setShowSql(showSql);
      adapter.setGenerateDdl(generateDdl);

      final Map<String, Object> propertyMap = adapter.getJpaPropertyMap();
      propertyMap.put("hibernate.format_sql", formatSql);
      propertyMap.put("hibernate.use_sql_comments", useSqlComments);
      propertyMap.put("hibernate.proc.param_null_passing", nullPassing);
      
      return adapter;
    }

    @Bean
    public EntityManager entityManager(final EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) throws SQLException {
        final JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}

