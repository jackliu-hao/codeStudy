/*    */ package org.xnio.channels;
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
/*    */ 
/*    */ public class UnsupportedOptionException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   private static final long serialVersionUID = 250195510855241708L;
/*    */   
/*    */   public UnsupportedOptionException() {}
/*    */   
/*    */   public UnsupportedOptionException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnsupportedOptionException(String message, Throwable cause) {
/* 51 */     super(message, cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnsupportedOptionException(Throwable cause) {
/* 60 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\UnsupportedOptionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */