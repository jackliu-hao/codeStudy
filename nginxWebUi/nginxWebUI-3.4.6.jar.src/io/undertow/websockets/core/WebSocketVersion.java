/*    */ package io.undertow.websockets.core;
/*    */ 
/*    */ import io.undertow.util.AttachmentKey;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum WebSocketVersion
/*    */ {
/* 40 */   UNKNOWN,
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 46 */   V00,
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 52 */   V07,
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 58 */   V08,
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 65 */   V13;
/*    */   
/*    */   public static final AttachmentKey<WebSocketVersion> ATTACHMENT_KEY;
/*    */ 
/*    */   
/*    */   public String toHttpHeaderValue() {
/* 71 */     if (this == V00) {
/* 72 */       return "0";
/*    */     }
/* 74 */     if (this == V07) {
/* 75 */       return "7";
/*    */     }
/* 77 */     if (this == V08) {
/* 78 */       return "8";
/*    */     }
/* 80 */     if (this == V13) {
/* 81 */       return "13";
/*    */     }
/*    */     
/* 84 */     throw new IllegalStateException("Unknown WebSocket version: " + this);
/*    */   }
/*    */   
/*    */   static {
/* 88 */     ATTACHMENT_KEY = AttachmentKey.create(WebSocketVersion.class);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */