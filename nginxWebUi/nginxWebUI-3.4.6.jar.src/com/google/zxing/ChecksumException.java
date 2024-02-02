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
/*    */ public final class ChecksumException
/*    */   extends ReaderException
/*    */ {
/*    */   private static final ChecksumException INSTANCE;
/*    */   
/*    */   static {
/* 29 */     (INSTANCE = new ChecksumException()).setStackTrace(NO_TRACE);
/*    */   }
/*    */ 
/*    */   
/*    */   private ChecksumException() {}
/*    */ 
/*    */   
/*    */   private ChecksumException(Throwable cause) {
/* 37 */     super(cause);
/*    */   }
/*    */   
/*    */   public static ChecksumException getChecksumInstance() {
/* 41 */     return isStackTrace ? new ChecksumException() : INSTANCE;
/*    */   }
/*    */   
/*    */   public static ChecksumException getChecksumInstance(Throwable cause) {
/* 45 */     return isStackTrace ? new ChecksumException(cause) : INSTANCE;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\ChecksumException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */