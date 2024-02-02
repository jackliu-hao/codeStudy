/*    */ package com.google.zxing;
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
/*    */ public abstract class ReaderException
/*    */   extends Exception
/*    */ {
/* 30 */   protected static final boolean isStackTrace = (System.getProperty("surefire.test.class.path") != null);
/* 31 */   protected static final StackTraceElement[] NO_TRACE = new StackTraceElement[0];
/*    */ 
/*    */   
/*    */   ReaderException() {}
/*    */ 
/*    */   
/*    */   ReaderException(Throwable cause) {
/* 38 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public final synchronized Throwable fillInStackTrace() {
/* 44 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\ReaderException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */