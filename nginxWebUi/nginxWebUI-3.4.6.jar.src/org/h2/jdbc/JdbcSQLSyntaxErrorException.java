/*    */ package org.h2.jdbc;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.sql.SQLSyntaxErrorException;
/*    */ import org.h2.message.DbException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class JdbcSQLSyntaxErrorException
/*    */   extends SQLSyntaxErrorException
/*    */   implements JdbcException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final String originalMessage;
/*    */   private final String stackTrace;
/*    */   private String message;
/*    */   private String sql;
/*    */   
/*    */   public JdbcSQLSyntaxErrorException(String paramString1, String paramString2, String paramString3, int paramInt, Throwable paramThrowable, String paramString4) {
/* 38 */     super(paramString1, paramString3, paramInt);
/* 39 */     this.originalMessage = paramString1;
/* 40 */     this.stackTrace = paramString4;
/*    */     
/* 42 */     setSQL(paramString2);
/* 43 */     initCause(paramThrowable);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 48 */     return this.message;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getOriginalMessage() {
/* 53 */     return this.originalMessage;
/*    */   }
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintWriter paramPrintWriter) {
/* 58 */     super.printStackTrace(paramPrintWriter);
/* 59 */     DbException.printNextExceptions(this, paramPrintWriter);
/*    */   }
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintStream paramPrintStream) {
/* 64 */     super.printStackTrace(paramPrintStream);
/* 65 */     DbException.printNextExceptions(this, paramPrintStream);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQL() {
/* 70 */     return this.sql;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setSQL(String paramString) {
/* 75 */     this.sql = paramString;
/* 76 */     this.message = DbException.buildMessageForException(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 81 */     if (this.stackTrace == null) {
/* 82 */       return super.toString();
/*    */     }
/* 84 */     return this.stackTrace;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcSQLSyntaxErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */