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
/*    */ public class StreamErrorException
/*    */   extends IOException
/*    */ {
/*    */   private final int errorId;
/*    */   
/*    */   public StreamErrorException(int errorId) {
/* 31 */     this.errorId = errorId;
/*    */   }
/*    */   
/*    */   public int getErrorId() {
/* 35 */     return this.errorId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\StreamErrorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */