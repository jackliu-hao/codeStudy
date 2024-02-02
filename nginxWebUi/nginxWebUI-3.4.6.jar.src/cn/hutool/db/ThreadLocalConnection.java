/*     */ package cn.hutool.db;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum ThreadLocalConnection
/*     */ {
/*  18 */   INSTANCE;
/*     */   ThreadLocalConnection() {
/*  20 */     this.threadLocal = new ThreadLocal<>();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final ThreadLocal<GroupedConnection> threadLocal;
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection get(DataSource ds) throws SQLException {
/*  30 */     GroupedConnection groupedConnection = this.threadLocal.get();
/*  31 */     if (null == groupedConnection) {
/*  32 */       groupedConnection = new GroupedConnection();
/*  33 */       this.threadLocal.set(groupedConnection);
/*     */     } 
/*  35 */     return groupedConnection.get(ds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(DataSource ds) {
/*  45 */     GroupedConnection groupedConnection = this.threadLocal.get();
/*  46 */     if (null != groupedConnection) {
/*  47 */       groupedConnection.close(ds);
/*  48 */       if (groupedConnection.isEmpty())
/*     */       {
/*  50 */         this.threadLocal.remove();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class GroupedConnection
/*     */   {
/*  63 */     private final Map<DataSource, Connection> connMap = new HashMap<>(1, 1.0F);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Connection get(DataSource ds) throws SQLException {
/*  73 */       Connection conn = this.connMap.get(ds);
/*  74 */       if (null == conn || conn.isClosed()) {
/*  75 */         conn = ds.getConnection();
/*  76 */         this.connMap.put(ds, conn);
/*     */       } 
/*  78 */       return conn;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public GroupedConnection close(DataSource ds) {
/*  89 */       Connection conn = this.connMap.get(ds);
/*  90 */       if (null != conn) {
/*     */         try {
/*  92 */           if (false == conn.getAutoCommit())
/*     */           {
/*  94 */             return this;
/*     */           }
/*  96 */         } catch (SQLException sQLException) {}
/*     */ 
/*     */         
/*  99 */         this.connMap.remove(ds);
/* 100 */         DbUtil.close(new Object[] { conn });
/*     */       } 
/* 102 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 112 */       return this.connMap.isEmpty();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ThreadLocalConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */