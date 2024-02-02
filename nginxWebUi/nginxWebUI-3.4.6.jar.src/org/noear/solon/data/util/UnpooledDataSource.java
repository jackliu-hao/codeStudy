/*     */ package org.noear.solon.data.util;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.Objects;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sql.DataSource;
/*     */ import org.noear.solon.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UnpooledDataSource
/*     */   implements DataSource
/*     */ {
/*     */   private PrintWriter logWriter;
/*     */   private String url;
/*     */   private String username;
/*     */   private String password;
/*     */   private String driverClassName;
/*     */   
/*     */   public UnpooledDataSource(Properties props) {
/*  29 */     this.url = props.getProperty("url");
/*  30 */     if (Utils.isEmpty(this.url)) {
/*  31 */       this.url = props.getProperty("jdbcUrl");
/*     */     }
/*     */     
/*  34 */     if (Utils.isEmpty(this.url)) {
/*  35 */       throw new IllegalArgumentException("Invalid ds url parameter");
/*     */     }
/*     */     
/*  38 */     this.logWriter = new PrintWriter(System.out);
/*     */     
/*  40 */     this.username = props.getProperty("username");
/*  41 */     this.password = props.getProperty("password");
/*     */     
/*  43 */     setDriverClassName(this.driverClassName);
/*     */   }
/*     */   
/*     */   public UnpooledDataSource(String url, String username, String password, String driverClassName) {
/*  47 */     if (Utils.isEmpty(url)) {
/*  48 */       throw new IllegalArgumentException("Invalid ds url parameter");
/*     */     }
/*     */     
/*  51 */     this.logWriter = new PrintWriter(System.out);
/*     */     
/*  53 */     this.url = url;
/*  54 */     this.username = username;
/*  55 */     this.password = password;
/*     */     
/*  57 */     setDriverClassName(driverClassName);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUrl(String url) {
/*  62 */     this.url = url;
/*     */   }
/*     */   
/*     */   public void setUsername(String username) {
/*  66 */     this.username = username;
/*     */   }
/*     */   
/*     */   public void setPassword(String password) {
/*  70 */     this.password = password;
/*     */   }
/*     */   
/*     */   public void setDriverClassName(String driverClassName) {
/*  74 */     if (driverClassName == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/*  79 */       this.driverClassName = driverClassName;
/*  80 */       Class.forName(driverClassName);
/*  81 */     } catch (ClassNotFoundException e) {
/*  82 */       throw new IllegalArgumentException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/*  88 */     if (this.username == null) {
/*  89 */       return DriverManager.getConnection(this.url);
/*     */     }
/*  91 */     return DriverManager.getConnection(this.url, this.username, this.password);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection(String username, String password) throws SQLException {
/*  97 */     return DriverManager.getConnection(this.url, username, password);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 107 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 112 */     return this.logWriter;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLogWriter(PrintWriter out) throws SQLException {
/* 117 */     this.logWriter = out;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLoginTimeout(int seconds) throws SQLException {
/* 122 */     DriverManager.setLoginTimeout(seconds);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 127 */     return DriverManager.getLoginTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
/* 132 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 137 */     if (this == o) return true; 
/* 138 */     if (o == null || getClass() != o.getClass()) return false; 
/* 139 */     UnpooledDataSource that = (UnpooledDataSource)o;
/* 140 */     return (Objects.equals(this.url, that.url) && 
/* 141 */       Objects.equals(this.username, that.username) && 
/* 142 */       Objects.equals(this.password, that.password));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 147 */     return Objects.hash(new Object[] { this.url, this.username, this.password });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\dat\\util\UnpooledDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */