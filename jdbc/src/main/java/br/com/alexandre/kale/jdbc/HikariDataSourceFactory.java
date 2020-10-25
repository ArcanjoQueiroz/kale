package br.com.alexandre.kale.jdbc;

import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDataSourceFactory {

  private static final Integer DEFAULT_MIN_IDLE = 5;
  private static final Integer DEFAULT_MAX_POOL_SIZE = 10;
  private static final Integer DEFAULT_PREP_STMT_CACHE_SIZE = 250;
  private static final Integer DEFAULT_PREP_STMT_CACHE_SQL_LIMIT = 2048;
  
  private final Logger logger = LoggerFactory.getLogger(HikariDataSourceFactory.class);
  
  public DataSource createDataSource(final DataSourceProperties dataSourceProperties) {
    checkArgument(!Strings.isNullOrEmpty(dataSourceProperties.getDriverClassName()));
    checkArgument(!Strings.isNullOrEmpty(dataSourceProperties.getUrl()));
    checkArgument(!Strings.isNullOrEmpty(dataSourceProperties.getUsername()));
    checkArgument(!Strings.isNullOrEmpty(dataSourceProperties.getPassword()));
    
    final Properties properties = new Properties();
    properties.put("driverClassName", dataSourceProperties.getDriverClassName());
    properties.put("jdbcUrl", dataSourceProperties.getUrl());
    properties.put("username", dataSourceProperties.getUsername());
    properties.put("password", dataSourceProperties.getPassword());    
    properties.put("maximumPoolSize", 
        dataSourceProperties.getMaxPoolSize() == null 
          ? DEFAULT_MAX_POOL_SIZE 
          : dataSourceProperties.getMaxPoolSize());
    properties.put("minimumIdle", 
        dataSourceProperties.getMinIdle() == null 
          ? DEFAULT_MIN_IDLE 
          : dataSourceProperties.getMinIdle());    
    properties.put("dataSource.cachePrepStmts", "true");
    properties.put("dataSource.prepStmtCacheSize", 
        dataSourceProperties.getPrepStmtCacheSize() == null 
          ? DEFAULT_PREP_STMT_CACHE_SIZE 
          : dataSourceProperties.getPrepStmtCacheSize());
    properties.put("dataSource.prepStmtCacheSqlLimit",
        dataSourceProperties.getPrepStmtCacheSqlLimit() == null 
          ? DEFAULT_PREP_STMT_CACHE_SQL_LIMIT 
          : dataSourceProperties.getPrepStmtCacheSqlLimit());
    if (!Strings.isNullOrEmpty(dataSourceProperties.getSessionProgramName())) {
      properties.put("dataSource.v$session.program", dataSourceProperties.getSessionProgramName());
    }
    
    final HikariConfig config = new HikariConfig(properties);
    
    logger.info("Hikari Max pool size: '{}', Min Idle: '{}'",
        config.getMaximumPoolSize(), config.getMinimumIdle());
    
    return new HikariDataSource(config);
  }

}