/*    */ package com.sun.jna.platform.win32.COM.tlb.imp;
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
/*    */ public class TlbParameterNotFoundException
/*    */   extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public TlbParameterNotFoundException() {}
/*    */   
/*    */   public TlbParameterNotFoundException(String msg) {
/* 34 */     super(msg);
/*    */   }
/*    */   
/*    */   public TlbParameterNotFoundException(Throwable cause) {
/* 38 */     super(cause);
/*    */   }
/*    */   
/*    */   public TlbParameterNotFoundException(String msg, Throwable cause) {
/* 42 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\tlb\imp\TlbParameterNotFoundException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */