/*    */ package io.undertow.protocols.http2;
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
/*    */ public class HpackException
/*    */   extends Exception
/*    */ {
/*    */   private final int closeCode;
/*    */   
/*    */   public HpackException() {
/* 31 */     this(null, 9);
/*    */   }
/*    */   
/*    */   public HpackException(String message, int closeCode) {
/* 35 */     super(message);
/* 36 */     this.closeCode = closeCode;
/*    */   }
/*    */   
/*    */   public HpackException(int closeCode) {
/* 40 */     this.closeCode = closeCode;
/*    */   }
/*    */   
/*    */   public int getCloseCode() {
/* 44 */     return this.closeCode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\HpackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */