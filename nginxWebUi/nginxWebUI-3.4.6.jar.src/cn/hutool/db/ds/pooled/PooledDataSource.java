/*     */ package cn.hutool.db.ds.pooled;
/*     */ 
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.thread.ThreadUtil;
/*     */ import cn.hutool.db.DbRuntimeException;
/*     */ import cn.hutool.db.ds.simple.AbstractDataSource;
/*     */ import java.io.Closeable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
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
/*     */ public class PooledDataSource
/*     */   extends AbstractDataSource
/*     */ {
/*     */   private Queue<PooledConnection> freePool;
/*     */   private int activeCount;
/*     */   private final DbConfig config;
/*     */   
/*     */   public static synchronized PooledDataSource getDataSource(String group) {
/*  35 */     return new PooledDataSource(group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized PooledDataSource getDataSource() {
/*  44 */     return new PooledDataSource();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledDataSource() {
/*  52 */     this("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledDataSource(String group) {
/*  61 */     this(new DbSetting(), group);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledDataSource(DbSetting setting, String group) {
/*  71 */     this(setting.getDbConfig(group));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledDataSource(DbConfig config) {
/*  80 */     this.config = config;
/*  81 */     this.freePool = new LinkedList<>();
/*  82 */     int initialSize = config.getInitialSize();
/*     */     try {
/*  84 */       while (initialSize-- > 0) {
/*  85 */         this.freePool.offer(newConnection());
/*     */       }
/*  87 */     } catch (SQLException e) {
/*  88 */       throw new DbRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/*  98 */     return getConnection(this.config.getMaxWait());
/*     */   }
/*     */ 
/*     */   
/*     */   public Connection getConnection(String username, String password) throws SQLException {
/* 103 */     throw new SQLException("Pooled DataSource is not allow to get special Connection!");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized boolean free(PooledConnection conn) {
/* 113 */     this.activeCount--;
/* 114 */     return this.freePool.offer(conn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledConnection newConnection() throws SQLException {
/* 124 */     return new PooledConnection(this);
/*     */   }
/*     */   
/*     */   public DbConfig getConfig() {
/* 128 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PooledConnection getConnection(long wait) throws SQLException {
/*     */     try {
/* 140 */       return getConnectionDirect();
/* 141 */     } catch (Exception e) {
/* 142 */       ThreadUtil.sleep(wait);
/*     */       
/* 144 */       return getConnectionDirect();
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void close() {
/* 149 */     if (CollectionUtil.isNotEmpty(this.freePool)) {
/* 150 */       this.freePool.forEach(PooledConnection::release);
/* 151 */       this.freePool.clear();
/* 152 */       this.freePool = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() {
/* 158 */     IoUtil.close((Closeable)this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PooledConnection getConnectionDirect() throws SQLException {
/* 168 */     if (null == this.freePool) {
/* 169 */       throw new SQLException("PooledDataSource is closed!");
/*     */     }
/*     */     
/* 172 */     int maxActive = this.config.getMaxActive();
/* 173 */     if (maxActive <= 0 || maxActive < this.activeCount)
/*     */     {
/* 175 */       throw new SQLException("In used Connection is more than Max Active.");
/*     */     }
/*     */     
/* 178 */     PooledConnection conn = this.freePool.poll();
/* 179 */     if (null == conn || conn.open().isClosed()) {
/* 180 */       conn = newConnection();
/*     */     }
/* 182 */     this.activeCount++;
/* 183 */     return conn;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\ds\pooled\PooledDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */