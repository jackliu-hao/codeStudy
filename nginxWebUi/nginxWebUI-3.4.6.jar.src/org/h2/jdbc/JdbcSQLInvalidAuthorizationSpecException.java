/*    */ package org.h2.jdbc;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.sql.SQLInvalidAuthorizationSpecException;
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
/*    */ 
/*    */ public final class JdbcSQLInvalidAuthorizationSpecException
/*    */   extends SQLInvalidAuthorizationSpecException
/*    */   implements JdbcException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final String originalMessage;
/*    */   private final String stackTrace;
/*    */   private String message;
/*    */   private String sql;
/*    */   
/*    */   public JdbcSQLInvalidAuthorizationSpecException(String paramString1, String paramString2, String paramString3, int paramInt, Throwable paramThrowable, String paramString4) {
/* 39 */     super(paramString1, paramString3, paramInt);
/* 40 */     this.originalMessage = paramString1;
/* 41 */     this.stackTrace = paramString4;
/*    */     
/* 43 */     setSQL(paramString2);
/* 44 */     initCause(paramThrowable);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 49 */     return this.message;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getOriginalMessage() {
/* 54 */     return this.originalMessage;
/*    */   }
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintWriter paramPrintWriter) {
/* 59 */     super.printStackTrace(paramPrintWriter);
/* 60 */     DbException.printNextExceptions(this, paramPrintWriter);
/*    */   }
/*    */ 
/*    */   
/*    */   public void printStackTrace(PrintStream paramPrintStream) {
/* 65 */     super.printStackTrace(paramPrintStream);
/* 66 */     DbException.printNextExceptions(this, paramPrintStream);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getSQL() {
/* 71 */     return this.sql;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setSQL(String paramString) {
/* 76 */     this.sql = paramString;
/* 77 */     this.message = DbException.buildMessageForException(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 82 */     if (this.stackTrace == null) {
/* 83 */       return super.toString();
/*    */     }
/* 85 */     return this.stackTrace;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcSQLInvalidAuthorizationSpecException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */