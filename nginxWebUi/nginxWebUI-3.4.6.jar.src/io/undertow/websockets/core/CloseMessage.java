/*    */ package io.undertow.websockets.core;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.charset.StandardCharsets;
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
/*    */ public class CloseMessage
/*    */ {
/*    */   private final int code;
/*    */   private final String reason;
/*    */   public static final int NORMAL_CLOSURE = 1000;
/*    */   public static final int GOING_AWAY = 1001;
/*    */   public static final int WRONG_CODE = 1002;
/*    */   public static final int PROTOCOL_ERROR = 1003;
/*    */   public static final int MSG_CONTAINS_INVALID_DATA = 1007;
/*    */   public static final int MSG_VIOLATES_POLICY = 1008;
/*    */   public static final int MSG_TOO_BIG = 1009;
/*    */   public static final int MISSING_EXTENSIONS = 1010;
/*    */   public static final int UNEXPECTED_ERROR = 1011;
/*    */   
/*    */   public CloseMessage(ByteBuffer buffer) {
/* 48 */     if (buffer.remaining() >= 2) {
/* 49 */       this.code = (buffer.get() & 0xFF) << 8 | buffer.get() & 0xFF;
/* 50 */       this.reason = (new UTF8Output(new ByteBuffer[] { buffer })).extract();
/*    */     } else {
/* 52 */       this.code = 1000;
/* 53 */       this.reason = "";
/*    */     } 
/*    */   }
/*    */   
/*    */   public CloseMessage(int code, String reason) {
/* 58 */     this.code = code;
/* 59 */     this.reason = (reason == null) ? "" : reason;
/*    */   }
/*    */   
/*    */   public CloseMessage(ByteBuffer[] buffers) {
/* 63 */     this(WebSockets.mergeBuffers(buffers));
/*    */   }
/*    */   
/*    */   public String getReason() {
/* 67 */     return this.reason;
/*    */   }
/*    */   
/*    */   public int getCode() {
/* 71 */     return this.code;
/*    */   }
/*    */   
/*    */   public ByteBuffer toByteBuffer() {
/* 75 */     byte[] data = this.reason.getBytes(StandardCharsets.UTF_8);
/* 76 */     ByteBuffer buffer = ByteBuffer.allocate(data.length + 2);
/* 77 */     buffer.putShort((short)this.code);
/* 78 */     buffer.put(data);
/* 79 */     buffer.flip();
/* 80 */     return buffer;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isValid(int code) {
/* 87 */     if ((code >= 0 && code <= 999) || (code >= 1004 && code <= 1006) || (code >= 1012 && code <= 2999))
/*    */     {
/* 89 */       return false;
/*    */     }
/* 91 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\CloseMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */