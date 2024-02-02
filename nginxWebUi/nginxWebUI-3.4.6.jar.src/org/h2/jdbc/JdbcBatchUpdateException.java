/*    */ package org.h2.jdbc;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.sql.BatchUpdateException;
/*    */ import java.sql.SQLException;
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
/*    */ 
/*    */ public final class JdbcBatchUpdateException
/*    */   extends BatchUpdateException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   JdbcBatchUpdateException(SQLException paramSQLException, int[] paramArrayOfint) {
/* 26 */     super(paramSQLException.getMessage(), paramSQLException.getSQLState(), paramSQLException.getErrorCode(), paramArrayOfint);
/* 27 */     setNextException(paramSQLException);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   JdbcBatchUpdateException(SQLException paramSQLException, long[] paramArrayOflong) {
/* 36 */     super(paramSQLException.getMessage(), paramSQLException.getSQLState(), paramSQLException.getErrorCode(), paramArrayOflong, (Throwable)null);
/* 37 */     setNextException(paramSQLException);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void printStackTrace() {
/* 49 */     printStackTrace(System.err);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintWriter paramPrintWriter) {
/* 57 */     if (paramPrintWriter != null) {
/* 58 */       super.printStackTrace(paramPrintWriter);
/* 59 */       if (getNextException() != null) {
/* 60 */         getNextException().printStackTrace(paramPrintWriter);
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintStream paramPrintStream) {
/* 70 */     if (paramPrintStream != null) {
/* 71 */       super.printStackTrace(paramPrintStream);
/* 72 */       if (getNextException() != null)
/* 73 */         getNextException().printStackTrace(paramPrintStream); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcBatchUpdateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */