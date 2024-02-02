/*     */ package org.h2;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
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
/*     */ public class Driver
/*     */   implements Driver, JdbcDriverBackwardsCompat
/*     */ {
/*  33 */   private static final Driver INSTANCE = new Driver();
/*     */   private static final String DEFAULT_URL = "jdbc:default:connection";
/*  35 */   private static final ThreadLocal<Connection> DEFAULT_CONNECTION = new ThreadLocal<>();
/*     */   
/*     */   private static boolean registered;
/*     */ 
/*     */   
/*     */   static {
/*  41 */     load();
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
/*     */   public Connection connect(String paramString, Properties paramProperties) throws SQLException {
/*  56 */     if (paramString == null)
/*  57 */       throw DbException.getJdbcSQLException(90046, null, new String[] { "jdbc:h2:{ {.|mem:}[name] | [file:]fileName | {tcp|ssl}:[//]server[:port][,server2[:port]]/name }[;key=value...]", null }); 
/*  58 */     if (paramString.startsWith("jdbc:h2:"))
/*  59 */       return (Connection)new JdbcConnection(paramString, paramProperties, null, null, false); 
/*  60 */     if (paramString.equals("jdbc:default:connection")) {
/*  61 */       return DEFAULT_CONNECTION.get();
/*     */     }
/*  63 */     return null;
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
/*     */   public boolean acceptsURL(String paramString) throws SQLException {
/*  77 */     if (paramString == null)
/*  78 */       throw DbException.getJdbcSQLException(90046, null, new String[] { "jdbc:h2:{ {.|mem:}[name] | [file:]fileName | {tcp|ssl}:[//]server[:port][,server2[:port]]/name }[;key=value...]", null }); 
/*  79 */     if (paramString.startsWith("jdbc:h2:"))
/*  80 */       return true; 
/*  81 */     if (paramString.equals("jdbc:default:connection")) {
/*  82 */       return (DEFAULT_CONNECTION.get() != null);
/*     */     }
/*  84 */     return false;
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
/*     */   public int getMajorVersion() {
/*  96 */     return 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinorVersion() {
/* 107 */     return 1;
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
/*     */   public DriverPropertyInfo[] getPropertyInfo(String paramString, Properties paramProperties) {
/* 120 */     return new DriverPropertyInfo[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean jdbcCompliant() {
/* 131 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() {
/* 139 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized Driver load() {
/*     */     try {
/* 148 */       if (!registered) {
/* 149 */         registered = true;
/* 150 */         DriverManager.registerDriver(INSTANCE);
/*     */       } 
/* 152 */     } catch (SQLException sQLException) {
/* 153 */       DbException.traceThrowable(sQLException);
/*     */     } 
/* 155 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void unload() {
/*     */     try {
/* 163 */       if (registered) {
/* 164 */         registered = false;
/* 165 */         DriverManager.deregisterDriver(INSTANCE);
/*     */       } 
/* 167 */     } catch (SQLException sQLException) {
/* 168 */       DbException.traceThrowable(sQLException);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setDefaultConnection(Connection paramConnection) {
/* 179 */     if (paramConnection == null) {
/* 180 */       DEFAULT_CONNECTION.remove();
/*     */     } else {
/* 182 */       DEFAULT_CONNECTION.set(paramConnection);
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
/*     */ 
/*     */   
/*     */   public static void setThreadContextClassLoader(Thread paramThread) {
/*     */     try {
/* 198 */       paramThread.setContextClassLoader(Driver.class.getClassLoader());
/* 199 */     } catch (Throwable throwable) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\Driver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */