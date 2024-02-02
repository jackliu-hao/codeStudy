/*     */ package io.undertow.websockets.core.protocol.version07;
/*     */ 
/*     */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*     */ import io.undertow.websockets.core.WebSocketMessages;
/*     */ import io.undertow.websockets.core.function.ChannelFunction;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UTF8Checker
/*     */   implements ChannelFunction
/*     */ {
/*     */   private static final int UTF8_ACCEPT = 0;
/*     */   private static final int UTF8_REJECT = 12;
/*  41 */   private static final byte[] TYPES = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private static final byte[] STATES = new byte[] { 0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   private int state = 0;
/*     */   
/*     */   private void checkUTF8(int b) throws UnsupportedEncodingException {
/*  62 */     byte type = TYPES[b & 0xFF];
/*     */     
/*  64 */     this.state = STATES[this.state + type];
/*  65 */     if (this.state == 12) {
/*  66 */       throw WebSocketMessages.MESSAGES.invalidTextFrameEncoding();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkUTF8(ByteBuffer buf, int position, int length) throws UnsupportedEncodingException {
/*  79 */     int limit = position + length;
/*  80 */     for (int i = position; i < limit; i++) {
/*  81 */       checkUTF8(buf.get(i));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void newFrame(FrameHeaderData headerData) {}
/*     */ 
/*     */   
/*     */   public void afterRead(ByteBuffer buf, int position, int length) throws IOException {
/*  91 */     checkUTF8(buf, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeWrite(ByteBuffer buf, int position, int length) throws UnsupportedEncodingException {
/*  96 */     checkUTF8(buf, position, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void complete() throws UnsupportedEncodingException {
/* 101 */     if (this.state != 0)
/* 102 */       throw WebSocketMessages.MESSAGES.invalidTextFrameEncoding(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\UTF8Checker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */