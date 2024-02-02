/*     */ package com.mysql.cj.jdbc.exceptions;
/*     */ 
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.DataTruncationException;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import java.sql.SQLException;
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
/*     */ 
/*     */ 
/*     */ public class SQLExceptionsMapping
/*     */ {
/*     */   public static SQLException translateException(Throwable ex, ExceptionInterceptor interceptor) {
/*  57 */     if (ex instanceof SQLException) {
/*  58 */       return (SQLException)ex;
/*     */     }
/*  60 */     if (ex.getCause() != null && ex.getCause() instanceof SQLException) {
/*  61 */       return (SQLException)ex.getCause();
/*     */     }
/*  63 */     if (ex instanceof com.mysql.cj.exceptions.CJCommunicationsException) {
/*  64 */       return SQLError.createCommunicationsException(ex.getMessage(), ex, interceptor);
/*     */     }
/*  66 */     if (ex instanceof com.mysql.cj.exceptions.CJConnectionFeatureNotAvailableException) {
/*  67 */       return new ConnectionFeatureNotAvailableException(ex.getMessage(), ex);
/*     */     }
/*  69 */     if (ex instanceof com.mysql.cj.exceptions.SSLParamsException) {
/*  70 */       return SQLError.createSQLException(ex.getMessage(), "08000", 0, false, ex, interceptor);
/*     */     }
/*  72 */     if (ex instanceof com.mysql.cj.exceptions.ConnectionIsClosedException) {
/*  73 */       return SQLError.createSQLException(ex.getMessage(), "08003", ex, interceptor);
/*     */     }
/*  75 */     if (ex instanceof com.mysql.cj.exceptions.InvalidConnectionAttributeException) {
/*  76 */       return SQLError.createSQLException(ex.getMessage(), "01S00", ex, interceptor);
/*     */     }
/*  78 */     if (ex instanceof com.mysql.cj.exceptions.UnableToConnectException) {
/*  79 */       return SQLError.createSQLException(ex.getMessage(), "08001", ex, interceptor);
/*     */     }
/*  81 */     if (ex instanceof com.mysql.cj.exceptions.StatementIsClosedException) {
/*  82 */       return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor);
/*     */     }
/*  84 */     if (ex instanceof com.mysql.cj.exceptions.WrongArgumentException) {
/*  85 */       return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor);
/*     */     }
/*  87 */     if (ex instanceof StringIndexOutOfBoundsException) {
/*  88 */       return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor);
/*     */     }
/*  90 */     if (ex instanceof com.mysql.cj.exceptions.NumberOutOfRange)
/*     */     {
/*  92 */       return SQLError.createSQLException(ex.getMessage(), "22003", ex, interceptor);
/*     */     }
/*  94 */     if (ex instanceof com.mysql.cj.exceptions.DataConversionException)
/*     */     {
/*  96 */       return SQLError.createSQLException(ex.getMessage(), "22018", ex, interceptor);
/*     */     }
/*  98 */     if (ex instanceof com.mysql.cj.exceptions.DataReadException) {
/*  99 */       return SQLError.createSQLException(ex.getMessage(), "S1009", ex, interceptor);
/*     */     }
/* 101 */     if (ex instanceof DataTruncationException) {
/* 102 */       return new MysqlDataTruncation(((DataTruncationException)ex).getMessage(), ((DataTruncationException)ex).getIndex(), ((DataTruncationException)ex)
/* 103 */           .isParameter(), ((DataTruncationException)ex).isRead(), ((DataTruncationException)ex).getDataSize(), ((DataTruncationException)ex)
/* 104 */           .getTransferSize(), ((DataTruncationException)ex).getVendorCode());
/*     */     }
/* 106 */     if (ex instanceof com.mysql.cj.exceptions.CJPacketTooBigException) {
/* 107 */       return new PacketTooBigException(ex.getMessage());
/*     */     }
/* 109 */     if (ex instanceof com.mysql.cj.exceptions.OperationCancelledException) {
/* 110 */       return new MySQLStatementCancelledException(ex.getMessage());
/*     */     }
/* 112 */     if (ex instanceof com.mysql.cj.exceptions.CJTimeoutException) {
/* 113 */       return new MySQLTimeoutException(ex.getMessage());
/*     */     }
/* 115 */     if (ex instanceof com.mysql.cj.exceptions.CJOperationNotSupportedException) {
/* 116 */       return new OperationNotSupportedException(ex.getMessage());
/*     */     }
/* 118 */     if (ex instanceof UnsupportedOperationException) {
/* 119 */       return new OperationNotSupportedException(ex.getMessage());
/*     */     }
/* 121 */     if (ex instanceof CJException) {
/* 122 */       return SQLError.createSQLException(ex.getMessage(), ((CJException)ex).getSQLState(), ((CJException)ex).getVendorCode(), ((CJException)ex)
/* 123 */           .isTransient(), ex.getCause(), interceptor);
/*     */     }
/*     */     
/* 126 */     return SQLError.createSQLException(ex.getMessage(), "S1000", ex, interceptor);
/*     */   }
/*     */ 
/*     */   
/*     */   public static SQLException translateException(Throwable ex) {
/* 131 */     return translateException(ex, null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\exceptions\SQLExceptionsMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */