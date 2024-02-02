/*     */ package com.mysql.cj.jdbc.exceptions;
/*     */ 
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.jdbc.JdbcConnection;
/*     */ import com.mysql.cj.protocol.PacketReceivedTimeHolder;
/*     */ import com.mysql.cj.protocol.PacketSentTimeHolder;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.sql.SQLDataException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.sql.SQLIntegrityConstraintViolationException;
/*     */ import java.sql.SQLNonTransientConnectionException;
/*     */ import java.sql.SQLSyntaxErrorException;
/*     */ import java.sql.SQLTransientConnectionException;
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
/*     */ public class SQLError
/*     */ {
/*     */   public static SQLException createSQLException(String message, String sqlState, ExceptionInterceptor interceptor) {
/*  63 */     return createSQLException(message, sqlState, 0, interceptor);
/*     */   }
/*     */   
/*     */   public static SQLException createSQLException(String message, ExceptionInterceptor interceptor) {
/*  67 */     SQLException sqlEx = new SQLException(message);
/*     */     
/*  69 */     return runThroughExceptionInterceptor(interceptor, sqlEx);
/*     */   }
/*     */   
/*     */   public static SQLException createSQLException(String message, String sqlState, Throwable cause, ExceptionInterceptor interceptor) {
/*  73 */     SQLException sqlEx = createSQLException(message, sqlState, null);
/*     */     
/*  75 */     if (sqlEx.getCause() == null && 
/*  76 */       cause != null) {
/*     */       try {
/*  78 */         sqlEx.initCause(cause);
/*  79 */       } catch (Throwable throwable) {}
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  85 */     return runThroughExceptionInterceptor(interceptor, sqlEx);
/*     */   }
/*     */   
/*     */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, ExceptionInterceptor interceptor) {
/*  89 */     return createSQLException(message, sqlState, vendorErrorCode, false, interceptor);
/*     */   }
/*     */   
/*     */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, Throwable cause, ExceptionInterceptor interceptor) {
/*  93 */     return createSQLException(message, sqlState, vendorErrorCode, false, cause, interceptor);
/*     */   }
/*     */   
/*     */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, ExceptionInterceptor interceptor) {
/*  97 */     return createSQLException(message, sqlState, vendorErrorCode, isTransient, null, interceptor);
/*     */   }
/*     */ 
/*     */   
/*     */   public static SQLException createSQLException(String message, String sqlState, int vendorErrorCode, boolean isTransient, Throwable cause, ExceptionInterceptor interceptor) {
/*     */     try {
/* 103 */       SQLException sqlEx = null;
/*     */       
/* 105 */       if (sqlState != null) {
/* 106 */         if (sqlState.startsWith("08")) {
/* 107 */           if (isTransient) {
/* 108 */             sqlEx = new SQLTransientConnectionException(message, sqlState, vendorErrorCode);
/*     */           } else {
/* 110 */             sqlEx = new SQLNonTransientConnectionException(message, sqlState, vendorErrorCode);
/*     */           }
/*     */         
/* 113 */         } else if (sqlState.startsWith("22")) {
/* 114 */           sqlEx = new SQLDataException(message, sqlState, vendorErrorCode);
/*     */         }
/* 116 */         else if (sqlState.startsWith("23")) {
/* 117 */           sqlEx = new SQLIntegrityConstraintViolationException(message, sqlState, vendorErrorCode);
/*     */         }
/* 119 */         else if (sqlState.startsWith("42")) {
/* 120 */           sqlEx = new SQLSyntaxErrorException(message, sqlState, vendorErrorCode);
/*     */         }
/* 122 */         else if (sqlState.startsWith("40")) {
/* 123 */           sqlEx = new MySQLTransactionRollbackException(message, sqlState, vendorErrorCode);
/*     */         }
/* 125 */         else if (sqlState.startsWith("70100")) {
/* 126 */           sqlEx = new MySQLQueryInterruptedException(message, sqlState, vendorErrorCode);
/*     */         } else {
/*     */           
/* 129 */           sqlEx = new SQLException(message, sqlState, vendorErrorCode);
/*     */         } 
/*     */       } else {
/* 132 */         sqlEx = new SQLException(message, sqlState, vendorErrorCode);
/*     */       } 
/*     */       
/* 135 */       if (cause != null) {
/*     */         try {
/* 137 */           sqlEx.initCause(cause);
/* 138 */         } catch (Throwable throwable) {}
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 143 */       return runThroughExceptionInterceptor(interceptor, sqlEx);
/*     */     }
/* 145 */     catch (Exception sqlEx) {
/*     */       
/* 147 */       SQLException unexpectedEx = new SQLException("Unable to create correct SQLException class instance, error class/codes may be incorrect. Reason: " + Util.stackTraceToString(sqlEx), "S1000");
/*     */ 
/*     */       
/* 150 */       return runThroughExceptionInterceptor(interceptor, unexpectedEx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SQLException createCommunicationsException(JdbcConnection conn, PacketSentTimeHolder packetSentTimeHolder, PacketReceivedTimeHolder packetReceivedTimeHolder, Exception underlyingException, ExceptionInterceptor interceptor) {
/* 158 */     SQLException exToReturn = new CommunicationsException(conn, packetSentTimeHolder, packetReceivedTimeHolder, underlyingException);
/*     */     
/* 160 */     if (underlyingException != null) {
/*     */       try {
/* 162 */         exToReturn.initCause(underlyingException);
/* 163 */       } catch (Throwable throwable) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 168 */     return runThroughExceptionInterceptor(interceptor, exToReturn);
/*     */   }
/*     */   
/*     */   public static SQLException createCommunicationsException(String message, Throwable underlyingException, ExceptionInterceptor interceptor) {
/* 172 */     SQLException exToReturn = null;
/*     */     
/* 174 */     exToReturn = new CommunicationsException(message, underlyingException);
/*     */     
/* 176 */     if (underlyingException != null) {
/*     */       try {
/* 178 */         exToReturn.initCause(underlyingException);
/* 179 */       } catch (Throwable throwable) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 184 */     return runThroughExceptionInterceptor(interceptor, exToReturn);
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
/*     */   private static SQLException runThroughExceptionInterceptor(ExceptionInterceptor exInterceptor, SQLException sqlEx) {
/* 197 */     if (exInterceptor != null) {
/* 198 */       SQLException interceptedEx = (SQLException)exInterceptor.interceptException(sqlEx);
/*     */       
/* 200 */       if (interceptedEx != null) {
/* 201 */         return interceptedEx;
/*     */       }
/*     */     } 
/* 204 */     return sqlEx;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SQLException createBatchUpdateException(SQLException underlyingEx, long[] updateCounts, ExceptionInterceptor interceptor) throws SQLException {
/* 224 */     SQLException newEx = (SQLException)Util.getInstance("java.sql.BatchUpdateException", new Class[] { String.class, String.class, int.class, long[].class, Throwable.class }, new Object[] { underlyingEx
/*     */           
/* 226 */           .getMessage(), underlyingEx.getSQLState(), Integer.valueOf(underlyingEx.getErrorCode()), updateCounts, underlyingEx }, interceptor);
/* 227 */     return runThroughExceptionInterceptor(interceptor, newEx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SQLException createSQLFeatureNotSupportedException() {
/* 236 */     return new SQLFeatureNotSupportedException();
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
/*     */   
/*     */   public static SQLException createSQLFeatureNotSupportedException(String message, String sqlState, ExceptionInterceptor interceptor) throws SQLException {
/* 253 */     SQLException newEx = new SQLFeatureNotSupportedException(message, sqlState);
/* 254 */     return runThroughExceptionInterceptor(interceptor, newEx);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\SQLError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */