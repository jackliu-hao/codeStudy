/*     */ package cn.hutool.db.ds;
/*     */ 
/*     */ import cn.hutool.core.clone.CloneRuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sql.DataSource;
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
/*     */ public class DataSourceWrapper
/*     */   implements DataSource, Closeable, Cloneable
/*     */ {
/*     */   private final DataSource ds;
/*     */   private final String driver;
/*     */   
/*     */   public static DataSourceWrapper wrap(DataSource ds, String driver) {
/*  37 */     return new DataSourceWrapper(ds, driver);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataSourceWrapper(DataSource ds, String driver) {
/*  47 */     this.ds = ds;
/*  48 */     this.driver = driver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDriver() {
/*  57 */     return this.driver;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataSource getRaw() {
/*  66 */     return this.ds;
/*     */   }
/*     */ 
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/*  71 */     return this.ds.getLogWriter();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLogWriter(PrintWriter out) throws SQLException {
/*  76 */     this.ds.setLogWriter(out);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLoginTimeout(int seconds) throws SQLException {
/*  81 */     this.ds.setLoginTimeout(seconds);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/*  86 */     return this.ds.getLoginTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
/*  91 */     return this.ds.getParentLogger();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/*  96 */     return this.ds.unwrap(iface);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 101 */     return this.ds.isWrapperFor(iface);
/*     */   }
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 106 */     return this.ds.getConnection();
/*     */   }
/*     */ 
/*     */   
/*     */   public Connection getConnection(String username, String password) throws SQLException {
/* 111 */     return this.ds.getConnection(username, password);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 116 */     if (this.ds instanceof AutoCloseable) {
/* 117 */       IoUtil.close((AutoCloseable)this.ds);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public DataSourceWrapper clone() {
/*     */     try {
/* 124 */       return (DataSourceWrapper)super.clone();
/* 125 */     } catch (CloneNotSupportedException e) {
/* 126 */       throw new CloneRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\DataSourceWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */