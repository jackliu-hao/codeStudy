/*     */ package org.h2.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ public class CountingReaderInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final Reader reader;
/*  28 */   private final CharBuffer charBuffer = CharBuffer.allocate(4096);
/*     */   
/*  30 */   private final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder()
/*  31 */     .onMalformedInput(CodingErrorAction.REPLACE)
/*  32 */     .onUnmappableCharacter(CodingErrorAction.REPLACE);
/*     */   
/*  34 */   private ByteBuffer byteBuffer = ByteBuffer.allocate(0);
/*     */   private long length;
/*     */   private long remaining;
/*     */   
/*     */   public CountingReaderInputStream(Reader paramReader, long paramLong) {
/*  39 */     this.reader = paramReader;
/*  40 */     this.remaining = paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
/*  45 */     if (!fetch()) {
/*  46 */       return -1;
/*     */     }
/*  48 */     paramInt2 = Math.min(paramInt2, this.byteBuffer.remaining());
/*  49 */     this.byteBuffer.get(paramArrayOfbyte, paramInt1, paramInt2);
/*  50 */     return paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  55 */     if (!fetch()) {
/*  56 */       return -1;
/*     */     }
/*  58 */     return this.byteBuffer.get() & 0xFF;
/*     */   }
/*     */   
/*     */   private boolean fetch() throws IOException {
/*  62 */     if (this.byteBuffer != null && this.byteBuffer.remaining() == 0) {
/*  63 */       fillBuffer();
/*     */     }
/*  65 */     return (this.byteBuffer != null);
/*     */   }
/*     */   
/*     */   private void fillBuffer() throws IOException {
/*  69 */     int i = (int)Math.min((this.charBuffer.capacity() - this.charBuffer.position()), this.remaining);
/*     */     
/*  71 */     if (i > 0) {
/*  72 */       i = this.reader.read(this.charBuffer.array(), this.charBuffer.position(), i);
/*     */     }
/*  74 */     if (i > 0) {
/*  75 */       this.remaining -= i;
/*     */     } else {
/*  77 */       i = 0;
/*  78 */       this.remaining = 0L;
/*     */     } 
/*  80 */     this.length += i;
/*  81 */     this.charBuffer.limit(this.charBuffer.position() + i);
/*  82 */     this.charBuffer.rewind();
/*  83 */     this.byteBuffer = ByteBuffer.allocate(4096);
/*  84 */     boolean bool = (this.remaining == 0L) ? true : false;
/*  85 */     this.encoder.encode(this.charBuffer, this.byteBuffer, bool);
/*  86 */     if (bool && this.byteBuffer.position() == 0) {
/*     */       
/*  88 */       this.byteBuffer = null;
/*     */       return;
/*     */     } 
/*  91 */     this.byteBuffer.flip();
/*  92 */     this.charBuffer.compact();
/*  93 */     this.charBuffer.flip();
/*  94 */     this.charBuffer.position(this.charBuffer.limit());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getLength() {
/* 104 */     return this.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 109 */     this.reader.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\CountingReaderInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */