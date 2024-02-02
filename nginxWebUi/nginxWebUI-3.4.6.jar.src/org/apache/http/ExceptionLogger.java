/*    */ package org.apache.http;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface ExceptionLogger
/*    */ {
/* 34 */   public static final ExceptionLogger NO_OP = new ExceptionLogger()
/*    */     {
/*    */       public void log(Exception ex) {}
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 43 */   public static final ExceptionLogger STD_ERR = new ExceptionLogger()
/*    */     {
/*    */       public void log(Exception ex)
/*    */       {
/* 47 */         ex.printStackTrace();
/*    */       }
/*    */     };
/*    */   
/*    */   void log(Exception paramException);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ExceptionLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */