/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.xnio.Buffers;
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
/*     */ public final class UTF8Output
/*     */ {
/*     */   private static final int UTF8_ACCEPT = 0;
/*     */   private static final byte HIGH_BIT = -128;
/*  32 */   private static final byte[] TYPES = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 };
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
/*  43 */   private static final byte[] STATES = new byte[] { 0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   private byte state = 0;
/*     */   
/*     */   private int codep;
/*     */   
/*     */   private final StringBuilder stringBuilder;
/*     */   
/*     */   public UTF8Output(ByteBuffer... payload) {
/*  57 */     this.stringBuilder = new StringBuilder((int)Buffers.remaining((Buffer[])payload));
/*  58 */     write(payload);
/*     */   }
/*     */   
/*     */   public UTF8Output() {
/*  62 */     this.stringBuilder = new StringBuilder();
/*     */   }
/*     */   
/*     */   public void write(ByteBuffer... bytes) {
/*  66 */     for (ByteBuffer buf : bytes) {
/*  67 */       while (buf.hasRemaining()) {
/*  68 */         write(buf.get());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void write(byte b) {
/*  74 */     if (this.state == 0 && (b & Byte.MIN_VALUE) == 0) {
/*  75 */       this.stringBuilder.append((char)b);
/*     */       return;
/*     */     } 
/*  78 */     byte type = TYPES[b & 0xFF];
/*     */     
/*  80 */     this.codep = (this.state != 0) ? (b & 0x3F | this.codep << 6) : (255 >> type & b);
/*     */     
/*  82 */     this.state = STATES[this.state + type];
/*     */     
/*  84 */     if (this.state == 0) {
/*  85 */       for (char c : Character.toChars(this.codep)) {
/*  86 */         this.stringBuilder.append(c);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String extract() {
/*  95 */     String text = this.stringBuilder.toString();
/*  96 */     this.stringBuilder.setLength(0);
/*  97 */     return text;
/*     */   }
/*     */   
/*     */   public boolean hasData() {
/* 101 */     return (this.stringBuilder.length() != 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\UTF8Output.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */