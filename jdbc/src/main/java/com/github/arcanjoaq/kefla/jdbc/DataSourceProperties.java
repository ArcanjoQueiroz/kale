package com.github.arcanjoaq.kefla.jdbc;

import java.io.Serializable;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.ToString
public class DataSourceProperties implements Serializable {

  private static final long serialVersionUID = -1707031982506063442L;
  
  private String url; 
  private String username; 
  private String password; 
  private String driverClassName; 
  private Integer maxPoolSize;
  private Integer minIdle;
  private Integer prepStmtCacheSize;
  private Integer prepStmtCacheSqlLimit;
  private String sessionProgramName;
   
}