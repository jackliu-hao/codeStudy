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
/*    */ public final class NotFoundException
/*    */   extends ReaderException
/*    */ {
/*    */   private static final NotFoundException INSTANCE;
/*    */   
/*    */   static {
/* 29 */     (INSTANCE = new NotFoundException()).setStackTrace(NO_TRACE);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static NotFoundException getNotFoundInstance() {
/* 37 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\NotFoundException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */