/*     */ package com.zaxxer.hikari.hibernate;
/*     */ 
/*     */ import com.zaxxer.hikari.HikariConfig;
/*     */ import com.zaxxer.hikari.HikariDataSource;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
/*     */ import javax.sql.DataSource;
/*     */ import org.hibernate.HibernateException;
/*     */ import org.hibernate.Version;
/*     */ import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
/*     */ import org.hibernate.service.UnknownUnwrapTypeException;
/*     */ import org.hibernate.service.spi.Configurable;
/*     */ import org.hibernate.service.spi.Stoppable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HikariConnectionProvider
/*     */   implements ConnectionProvider, Configurable, Stoppable
/*     */ {
/*     */   private static final long serialVersionUID = -9131625057941275711L;
/*  46 */   private static final Logger LOGGER = LoggerFactory.getLogger(HikariConnectionProvider.class);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   private HikariConfig hcfg = null;
/*  68 */   private HikariDataSource hds = null; public HikariConnectionProvider() {
/*  69 */     if (Version.getVersionString().substring(0, 5).compareTo("4.3.6") >= 1) {
/*  70 */       LOGGER.warn("com.zaxxer.hikari.hibernate.HikariConnectionProvider has been deprecated for versions of Hibernate 4.3.6 and newer.  Please switch to org.hibernate.hikaricp.internal.HikariCPConnectionProvider.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void configure(Map props) throws HibernateException {
/*     */     try {
/*  84 */       LOGGER.debug("Configuring HikariCP");
/*     */       
/*  86 */       this.hcfg = HikariConfigurationUtil.loadConfiguration(props);
/*  87 */       this.hds = new HikariDataSource(this.hcfg);
/*     */     
/*     */     }
/*  90 */     catch (Exception e) {
/*  91 */       throw new HibernateException(e);
/*     */     } 
/*     */     
/*  94 */     LOGGER.debug("HikariCP Configured");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 104 */     Connection conn = null;
/* 105 */     if (this.hds != null) {
/* 106 */       conn = this.hds.getConnection();
/*     */     }
/*     */     
/* 109 */     return conn;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeConnection(Connection conn) throws SQLException {
/* 115 */     conn.close();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean supportsAggressiveRelease() {
/* 121 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUnwrappableAs(Class<?> unwrapType) {
/* 128 */     return (ConnectionProvider.class.equals(unwrapType) || HikariConnectionProvider.class.isAssignableFrom(unwrapType));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T unwrap(Class<T> unwrapType) {
/* 135 */     if (ConnectionProvider.class.equals(unwrapType) || HikariConnectionProvider.class
/* 136 */       .isAssignableFrom(unwrapType)) {
/* 137 */       return (T)this;
/*     */     }
/* 139 */     if (DataSource.class.isAssignableFrom(unwrapType)) {
/* 140 */       return (T)this.hds;
/*     */     }
/*     */     
/* 143 */     throw new UnknownUnwrapTypeException(unwrapType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 154 */     this.hds.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\hibernate\HikariConnectionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */