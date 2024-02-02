/*    */ package cn.hutool.db.ds.simple;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.PrintWriter;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLFeatureNotSupportedException;
/*    */ import java.util.logging.Logger;
/*    */ import javax.sql.DataSource;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractDataSource
/*    */   implements DataSource, Cloneable, Closeable
/*    */ {
/*    */   public PrintWriter getLogWriter() {
/* 20 */     return DriverManager.getLogWriter();
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLogWriter(PrintWriter out) {
/* 25 */     DriverManager.setLogWriter(out);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLoginTimeout(int seconds) {
/* 30 */     DriverManager.setLoginTimeout(seconds);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getLoginTimeout() {
/* 35 */     return DriverManager.getLoginTimeout();
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> T unwrap(Class<T> iface) throws SQLException {
/* 40 */     throw new SQLException("Can't support unwrap method!");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 45 */     throw new SQLException("Can't support isWrapperFor method!");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
/* 54 */     throw new SQLFeatureNotSupportedException("DataSource can't support getParentLogger method!");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\simple\AbstractDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */