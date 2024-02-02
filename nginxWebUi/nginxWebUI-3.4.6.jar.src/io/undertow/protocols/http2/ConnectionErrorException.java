/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public class ConnectionErrorException
/*    */   extends IOException
/*    */ {
/*    */   private final int code;
/*    */   
/*    */   public ConnectionErrorException(int code) {
/* 32 */     this.code = code;
/*    */   }
/*    */ 
/*    */   
/*    */   public ConnectionErrorException(int code, String message) {
/* 37 */     super(message);
/* 38 */     this.code = code;
/*    */   }
/*    */   
/*    */   public ConnectionErrorException(int code, Throwable cause) {
/* 42 */     super(cause);
/* 43 */     this.code = code;
/*    */   }
/*    */   
/*    */   public int getCode() {
/* 47 */     return this.code;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\ConnectionErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */