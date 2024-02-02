/*    */ package cn.hutool.db.ds.pooled;
/*    */ 
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.db.DbUtil;
/*    */ import cn.hutool.setting.dialect.Props;
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PooledConnection
/*    */   extends ConnectionWraper
/*    */ {
/*    */   private final PooledDataSource ds;
/*    */   private boolean isClosed;
/*    */   
/*    */   public PooledConnection(PooledDataSource ds) throws SQLException {
/* 29 */     this.ds = ds;
/* 30 */     DbConfig config = ds.getConfig();
/*    */     
/* 32 */     Props info = new Props();
/* 33 */     String user = config.getUser();
/* 34 */     if (user != null) {
/* 35 */       info.setProperty("user", user);
/*    */     }
/* 37 */     String password = config.getPass();
/* 38 */     if (password != null) {
/* 39 */       info.setProperty("password", password);
/*    */     }
/*    */ 
/*    */     
/* 43 */     Properties connProps = config.getConnProps();
/* 44 */     if (MapUtil.isNotEmpty(connProps)) {
/* 45 */       info.putAll(connProps);
/*    */     }
/*    */     
/* 48 */     this.raw = DriverManager.getConnection(config.getUrl(), (Properties)info);
/*    */   }
/*    */   
/*    */   public PooledConnection(PooledDataSource ds, Connection conn) {
/* 52 */     this.ds = ds;
/* 53 */     this.raw = conn;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {
/* 61 */     this.ds.free(this);
/* 62 */     this.isClosed = true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isClosed() throws SQLException {
/* 72 */     return (this.isClosed || this.raw.isClosed());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PooledConnection open() {
/* 80 */     this.isClosed = false;
/* 81 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected PooledConnection release() {
/* 89 */     DbUtil.close(new Object[] { this.raw });
/* 90 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\pooled\PooledConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */