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
/*    */ public final class FormatException
/*    */   extends ReaderException
/*    */ {
/*    */   private static final FormatException INSTANCE;
/*    */   
/*    */   static {
/* 30 */     (INSTANCE = new FormatException()).setStackTrace(NO_TRACE);
/*    */   }
/*    */ 
/*    */   
/*    */   private FormatException() {}
/*    */   
/*    */   private FormatException(Throwable cause) {
/* 37 */     super(cause);
/*    */   }
/*    */   
/*    */   public static FormatException getFormatInstance() {
/* 41 */     return isStackTrace ? new FormatException() : INSTANCE;
/*    */   }
/*    */   
/*    */   public static FormatException getFormatInstance(Throwable cause) {
/* 45 */     return isStackTrace ? new FormatException(cause) : INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\FormatException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */