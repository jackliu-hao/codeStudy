/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.lang.func.VoidFunc1;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.db.dialect.Dialect;
/*     */ import cn.hutool.db.dialect.DialectFactory;
/*     */ import cn.hutool.db.ds.DSFactory;
/*     */ import cn.hutool.db.sql.Wrapper;
/*     */ import cn.hutool.log.Log;
/*     */ import cn.hutool.log.LogFactory;
/*     */ import java.io.Closeable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Savepoint;
/*     */ import javax.sql.DataSource;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Session
/*     */   extends AbstractDb
/*     */   implements Closeable
/*     */ {
/*     */   private static final long serialVersionUID = 3421251905539056945L;
/*  29 */   private static final Log log = LogFactory.get();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Session create() {
/*  38 */     return new Session(DSFactory.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Session create(String group) {
/*  49 */     return new Session(DSFactory.get(group));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Session create(DataSource ds) {
/*  59 */     return new Session(ds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session(DataSource ds) {
/*  69 */     this(ds, DialectFactory.getDialect(ds));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session(DataSource ds, String driverClassName) {
/*  79 */     this(ds, DialectFactory.newDialect(driverClassName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session(DataSource ds, Dialect dialect) {
/*  89 */     super(ds, dialect);
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
/*     */   public SqlConnRunner getRunner() {
/* 101 */     return this.runner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void beginTransaction() throws SQLException {
/* 112 */     Connection conn = getConnection();
/* 113 */     checkTransactionSupported(conn);
/* 114 */     conn.setAutoCommit(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void commit() throws SQLException {
/*     */     try {
/* 124 */       getConnection().commit();
/*     */     } finally {
/*     */       try {
/* 127 */         getConnection().setAutoCommit(true);
/* 128 */       } catch (SQLException e) {
/* 129 */         log.error(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback() throws SQLException {
/*     */     try {
/* 141 */       getConnection().rollback();
/*     */     } finally {
/*     */       try {
/* 144 */         getConnection().setAutoCommit(true);
/* 145 */       } catch (SQLException e) {
/* 146 */         log.error(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void quietRollback() {
/*     */     try {
/* 157 */       getConnection().rollback();
/* 158 */     } catch (Exception e) {
/* 159 */       log.error(e);
/*     */     } finally {
/*     */       try {
/* 162 */         getConnection().setAutoCommit(true);
/* 163 */       } catch (SQLException e) {
/* 164 */         log.error(e);
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
/*     */   public void rollback(Savepoint savepoint) throws SQLException {
/*     */     try {
/* 177 */       getConnection().rollback(savepoint);
/*     */     } finally {
/*     */       try {
/* 180 */         getConnection().setAutoCommit(true);
/* 181 */       } catch (SQLException e) {
/* 182 */         log.error(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void quietRollback(Savepoint savepoint) {
/*     */     try {
/* 194 */       getConnection().rollback(savepoint);
/* 195 */     } catch (Exception e) {
/* 196 */       log.error(e);
/*     */     } finally {
/*     */       try {
/* 199 */         getConnection().setAutoCommit(true);
/* 200 */       } catch (SQLException e) {
/* 201 */         log.error(e);
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
/*     */   public Savepoint setSavepoint() throws SQLException {
/* 213 */     return getConnection().setSavepoint();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Savepoint setSavepoint(String name) throws SQLException {
/* 224 */     return getConnection().setSavepoint(name);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTransactionIsolation(int level) throws SQLException {
/* 240 */     if (!getConnection().getMetaData().supportsTransactionIsolationLevel(level)) {
/* 241 */       throw new SQLException(StrUtil.format("Transaction isolation [{}] not support!", new Object[] { Integer.valueOf(level) }));
/*     */     }
/* 243 */     getConnection().setTransactionIsolation(level);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void tx(VoidFunc1<Session> func) throws SQLException {
/*     */     try {
/* 255 */       beginTransaction();
/* 256 */       func.call(this);
/* 257 */       commit();
/* 258 */     } catch (Throwable e) {
/* 259 */       quietRollback();
/* 260 */       throw (e instanceof SQLException) ? (SQLException)e : new SQLException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session setWrapper(Character wrapperChar) {
/* 269 */     return (Session)super.setWrapper(wrapperChar);
/*     */   }
/*     */ 
/*     */   
/*     */   public Session setWrapper(Wrapper wrapper) {
/* 274 */     return (Session)super.setWrapper(wrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public Session disableWrapper() {
/* 279 */     return (Session)super.disableWrapper();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 285 */     return ThreadLocalConnection.INSTANCE.get(this.ds);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeConnection(Connection conn) {
/*     */     try {
/* 291 */       if (conn != null && false == conn.getAutoCommit()) {
/*     */         return;
/*     */       }
/*     */     }
/* 295 */     catch (SQLException e) {
/* 296 */       log.error(e);
/*     */     } 
/*     */ 
/*     */     
/* 300 */     ThreadLocalConnection.INSTANCE.close(this.ds);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 305 */     closeConnection((Connection)null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */