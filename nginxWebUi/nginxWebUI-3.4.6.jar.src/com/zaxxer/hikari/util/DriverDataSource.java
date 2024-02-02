/*     */ package com.zaxxer.hikari.util;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sql.DataSource;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DriverDataSource
/*     */   implements DataSource
/*     */ {
/*  35 */   private static final Logger LOGGER = LoggerFactory.getLogger(DriverDataSource.class);
/*     */   
/*     */   private static final String PASSWORD = "password";
/*     */   
/*     */   private static final String USER = "user";
/*     */   private final String jdbcUrl;
/*     */   private final Properties driverProperties;
/*     */   private Driver driver;
/*     */   
/*     */   public DriverDataSource(String jdbcUrl, String driverClassName, Properties properties, String username, String password) {
/*  45 */     this.jdbcUrl = jdbcUrl;
/*  46 */     this.driverProperties = new Properties();
/*     */     
/*  48 */     for (Map.Entry<Object, Object> entry : properties.entrySet()) {
/*  49 */       this.driverProperties.setProperty(entry.getKey().toString(), entry.getValue().toString());
/*     */     }
/*     */     
/*  52 */     if (username != null) {
/*  53 */       this.driverProperties.put("user", this.driverProperties.getProperty("user", username));
/*     */     }
/*  55 */     if (password != null) {
/*  56 */       this.driverProperties.put("password", this.driverProperties.getProperty("password", password));
/*     */     }
/*     */     
/*  59 */     if (driverClassName != null) {
/*  60 */       Enumeration<Driver> drivers = DriverManager.getDrivers();
/*  61 */       while (drivers.hasMoreElements()) {
/*  62 */         Driver d = drivers.nextElement();
/*  63 */         if (d.getClass().getName().equals(driverClassName)) {
/*  64 */           this.driver = d;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*  69 */       if (this.driver == null) {
/*  70 */         LOGGER.warn("Registered driver with driverClassName={} was not found, trying direct instantiation.", driverClassName);
/*  71 */         Class<?> driverClass = null;
/*  72 */         ClassLoader threadContextClassLoader = Thread.currentThread().getContextClassLoader();
/*     */         try {
/*  74 */           if (threadContextClassLoader != null) {
/*     */             try {
/*  76 */               driverClass = threadContextClassLoader.loadClass(driverClassName);
/*  77 */               LOGGER.debug("Driver class {} found in Thread context class loader {}", driverClassName, threadContextClassLoader);
/*     */             }
/*  79 */             catch (ClassNotFoundException e) {
/*  80 */               LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", new Object[] { driverClassName, threadContextClassLoader, 
/*  81 */                     getClass().getClassLoader() });
/*     */             } 
/*     */           }
/*     */           
/*  85 */           if (driverClass == null) {
/*  86 */             driverClass = getClass().getClassLoader().loadClass(driverClassName);
/*  87 */             LOGGER.debug("Driver class {} found in the HikariConfig class classloader {}", driverClassName, getClass().getClassLoader());
/*     */           } 
/*  89 */         } catch (ClassNotFoundException e) {
/*  90 */           LOGGER.debug("Failed to load driver class {} from HikariConfig class classloader {}", driverClassName, getClass().getClassLoader());
/*     */         } 
/*     */         
/*  93 */         if (driverClass != null) {
/*     */           try {
/*  95 */             this.driver = driverClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*  96 */           } catch (Exception e) {
/*  97 */             LOGGER.warn("Failed to create instance of driver class {}, trying jdbcUrl resolution", driverClassName, e);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 103 */     String sanitizedUrl = jdbcUrl.replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");
/*     */     try {
/* 105 */       if (this.driver == null) {
/* 106 */         this.driver = DriverManager.getDriver(jdbcUrl);
/* 107 */         LOGGER.debug("Loaded driver with class name {} for jdbcUrl={}", this.driver.getClass().getName(), sanitizedUrl);
/*     */       }
/* 109 */       else if (!this.driver.acceptsURL(jdbcUrl)) {
/* 110 */         throw new RuntimeException("Driver " + driverClassName + " claims to not accept jdbcUrl, " + sanitizedUrl);
/*     */       }
/*     */     
/* 113 */     } catch (SQLException e) {
/* 114 */       throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + sanitizedUrl, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 121 */     return this.driver.connect(this.jdbcUrl, this.driverProperties);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection(String username, String password) throws SQLException {
/* 127 */     Properties cloned = (Properties)this.driverProperties.clone();
/* 128 */     if (username != null) {
/* 129 */       cloned.put("user", username);
/* 130 */       if (cloned.containsKey("username")) {
/* 131 */         cloned.put("username", username);
/*     */       }
/*     */     } 
/* 134 */     if (password != null) {
/* 135 */       cloned.put("password", password);
/*     */     }
/*     */     
/* 138 */     return this.driver.connect(this.jdbcUrl, cloned);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 144 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLogWriter(PrintWriter logWriter) throws SQLException {
/* 150 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLoginTimeout(int seconds) throws SQLException {
/* 156 */     DriverManager.setLoginTimeout(seconds);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 162 */     return DriverManager.getLoginTimeout();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
/* 168 */     return this.driver.getParentLogger();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/* 174 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 180 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\DriverDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */