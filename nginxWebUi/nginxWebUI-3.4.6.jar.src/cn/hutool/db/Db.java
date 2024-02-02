/*     */ package cn.hutool.db;
/*     */ 
/*     */ import cn.hutool.core.lang.func.VoidFunc1;
/*     */ import cn.hutool.db.dialect.Dialect;
/*     */ import cn.hutool.db.dialect.DialectFactory;
/*     */ import cn.hutool.db.ds.DSFactory;
/*     */ import cn.hutool.db.sql.Wrapper;
/*     */ import cn.hutool.db.transaction.TransactionLevel;
/*     */ import cn.hutool.log.StaticLog;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
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
/*     */ public class Db
/*     */   extends AbstractDb
/*     */ {
/*     */   private static final long serialVersionUID = -3378415769645309514L;
/*     */   
/*     */   public static Db use() {
/*  32 */     return use(DSFactory.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Db use(String group) {
/*  43 */     return use(DSFactory.get(group));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Db use(DataSource ds) {
/*  54 */     return (ds == null) ? null : new Db(ds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Db use(DataSource ds, Dialect dialect) {
/*  65 */     return new Db(ds, dialect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Db use(DataSource ds, String driverClassName) {
/*  76 */     return new Db(ds, DialectFactory.newDialect(driverClassName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Db(DataSource ds) {
/*  86 */     this(ds, DialectFactory.getDialect(ds));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Db(DataSource ds, String driverClassName) {
/*  96 */     this(ds, DialectFactory.newDialect(driverClassName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Db(DataSource ds, Dialect dialect) {
/* 106 */     super(ds, dialect);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Db setWrapper(Character wrapperChar) {
/* 113 */     return (Db)super.setWrapper(wrapperChar);
/*     */   }
/*     */ 
/*     */   
/*     */   public Db setWrapper(Wrapper wrapper) {
/* 118 */     return (Db)super.setWrapper(wrapper);
/*     */   }
/*     */ 
/*     */   
/*     */   public Db disableWrapper() {
/* 123 */     return (Db)super.disableWrapper();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 129 */     return ThreadLocalConnection.INSTANCE.get(this.ds);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeConnection(Connection conn) {
/*     */     try {
/* 135 */       if (conn != null && false == conn.getAutoCommit()) {
/*     */         return;
/*     */       }
/*     */     }
/* 139 */     catch (SQLException sQLException) {}
/*     */ 
/*     */ 
/*     */     
/* 143 */     ThreadLocalConnection.INSTANCE.close(this.ds);
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
/*     */   public Db tx(VoidFunc1<Db> func) throws SQLException {
/* 155 */     return tx((TransactionLevel)null, func);
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
/*     */   public Db tx(TransactionLevel transactionLevel, VoidFunc1<Db> func) throws SQLException {
/* 168 */     Connection conn = getConnection();
/*     */ 
/*     */     
/* 171 */     checkTransactionSupported(conn);
/*     */ 
/*     */     
/* 174 */     if (null != transactionLevel) {
/* 175 */       int level = transactionLevel.getLevel();
/* 176 */       if (conn.getTransactionIsolation() < level)
/*     */       {
/*     */         
/* 179 */         conn.setTransactionIsolation(level);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 184 */     boolean autoCommit = conn.getAutoCommit();
/* 185 */     if (autoCommit) {
/* 186 */       conn.setAutoCommit(false);
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 191 */       func.call(this);
/*     */       
/* 193 */       conn.commit();
/* 194 */     } catch (Throwable e) {
/* 195 */       quietRollback(conn);
/* 196 */       throw (e instanceof SQLException) ? (SQLException)e : new SQLException(e);
/*     */     } finally {
/*     */       
/* 199 */       quietSetAutoCommit(conn, Boolean.valueOf(autoCommit));
/*     */       
/* 201 */       closeConnection(conn);
/*     */     } 
/*     */     
/* 204 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void quietRollback(Connection conn) {
/* 214 */     if (null != conn) {
/*     */       try {
/* 216 */         conn.rollback();
/* 217 */       } catch (Exception e) {
/* 218 */         StaticLog.error(e);
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
/*     */   private void quietSetAutoCommit(Connection conn, Boolean autoCommit) {
/* 230 */     if (null != conn && null != autoCommit)
/*     */       try {
/* 232 */         conn.setAutoCommit(autoCommit.booleanValue());
/* 233 */       } catch (Exception e) {
/* 234 */         StaticLog.error(e);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\Db.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */