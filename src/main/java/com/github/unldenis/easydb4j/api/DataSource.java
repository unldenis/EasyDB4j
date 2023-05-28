package com.github.unldenis.easydb4j.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class DataSource {

  private final HikariDataSource ds;

  public DataSource(String jdbcUrl, String username, String password) {
    var config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    this.ds = new HikariDataSource(config);
  }


  public Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

}
