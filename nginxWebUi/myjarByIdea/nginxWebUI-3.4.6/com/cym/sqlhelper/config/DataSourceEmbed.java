package com.cym.sqlhelper.config;

import com.cym.config.HomeConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

@Component
public class DataSourceEmbed {
   @Inject
   HomeConfig homeConfig;
   @Inject("${spring.database.type}")
   String databaseType;
   @Inject("${spring.datasource.url}")
   String url;
   @Inject("${spring.datasource.username}")
   String username;
   @Inject("${spring.datasource.password}")
   String password;
   DataSource dataSource;

   @Init
   public void init() {
      HikariConfig dbConfig;
      if (!this.databaseType.equalsIgnoreCase("sqlite") && !this.databaseType.equalsIgnoreCase("h2")) {
         if (this.databaseType.equalsIgnoreCase("mysql")) {
            dbConfig = new HikariConfig();
            dbConfig.setJdbcUrl(this.url);
            dbConfig.setUsername(this.username);
            dbConfig.setPassword(this.password);
            dbConfig.setMaximumPoolSize(1);
            dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            this.dataSource = new HikariDataSource(dbConfig);
         }
      } else {
         dbConfig = new HikariConfig();
         dbConfig.setJdbcUrl("jdbc:h2:" + this.homeConfig.home + "h2");
         dbConfig.setUsername("sa");
         dbConfig.setPassword("");
         dbConfig.setMaximumPoolSize(1);
         this.dataSource = new HikariDataSource(dbConfig);
      }

   }

   public DataSource getDataSource() {
      return this.dataSource;
   }

   public void setDataSource(DataSource dataSource) {
      this.dataSource = dataSource;
   }
}
