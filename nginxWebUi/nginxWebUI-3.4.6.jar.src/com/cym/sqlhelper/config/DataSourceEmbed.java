/*    */ package com.cym.sqlhelper.config;
/*    */ 
/*    */ import com.cym.config.HomeConfig;
/*    */ import com.zaxxer.hikari.HikariConfig;
/*    */ import com.zaxxer.hikari.HikariDataSource;
/*    */ import javax.sql.DataSource;
/*    */ import org.noear.solon.annotation.Component;
/*    */ import org.noear.solon.annotation.Init;
/*    */ import org.noear.solon.annotation.Inject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Component
/*    */ public class DataSourceEmbed
/*    */ {
/*    */   @Inject
/*    */   HomeConfig homeConfig;
/*    */   @Inject("${spring.database.type}")
/*    */   String databaseType;
/*    */   @Inject("${spring.datasource.url}")
/*    */   String url;
/*    */   @Inject("${spring.datasource.username}")
/*    */   String username;
/*    */   @Inject("${spring.datasource.password}")
/*    */   String password;
/*    */   DataSource dataSource;
/*    */   
/*    */   @Init
/*    */   public void init() {
/* 31 */     if (this.databaseType.equalsIgnoreCase("sqlite") || this.databaseType.equalsIgnoreCase("h2")) {
/* 32 */       HikariConfig dbConfig = new HikariConfig();
/* 33 */       dbConfig.setJdbcUrl("jdbc:h2:" + this.homeConfig.home + "h2");
/* 34 */       dbConfig.setUsername("sa");
/* 35 */       dbConfig.setPassword("");
/* 36 */       dbConfig.setMaximumPoolSize(1);
/* 37 */       this.dataSource = (DataSource)new HikariDataSource(dbConfig);
/* 38 */     } else if (this.databaseType.equalsIgnoreCase("mysql")) {
/* 39 */       HikariConfig dbConfig = new HikariConfig();
/* 40 */       dbConfig.setJdbcUrl(this.url);
/* 41 */       dbConfig.setUsername(this.username);
/* 42 */       dbConfig.setPassword(this.password);
/* 43 */       dbConfig.setMaximumPoolSize(1);
/* 44 */       dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
/* 45 */       this.dataSource = (DataSource)new HikariDataSource(dbConfig);
/*    */     } 
/*    */   }
/*    */   
/*    */   public DataSource getDataSource() {
/* 50 */     return this.dataSource;
/*    */   }
/*    */   
/*    */   public void setDataSource(DataSource dataSource) {
/* 54 */     this.dataSource = dataSource;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelper\config\DataSourceEmbed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */