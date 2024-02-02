/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class FastByteArrayOutputStream
/*     */   extends OutputStream
/*     */ {
/*     */   private final FastByteBuffer buffer;
/*     */   
/*     */   public FastByteArrayOutputStream() {
/*  29 */     this(1024);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FastByteArrayOutputStream(int size) {
/*  38 */     this.buffer = new FastByteBuffer(size);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) {
/*  43 */     this.buffer.append(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) {
/*  48 */     this.buffer.append((byte)b);
/*     */   }
/*     */   
/*     */   public int size() {
/*  52 */     return this.buffer.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/*  64 */     this.buffer.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream out) throws IORuntimeException {
/*  73 */     int index = this.buffer.index();
/*  74 */     if (index < 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  80 */       for (int i = 0; i < index; i++) {
/*  81 */         byte[] buf = this.buffer.array(i);
/*  82 */         out.write(buf);
/*     */       } 
/*  84 */       out.write(this.buffer.array(index), 0, this.buffer.offset());
/*  85 */     } catch (IOException e) {
/*  86 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] toByteArray() {
/*  96 */     return this.buffer.toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 101 */     return toString(CharsetUtil.defaultCharset());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(String charsetName) {
/* 110 */     return toString(CharsetUtil.charset(charsetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(Charset charset) {
/* 119 */     return new String(toByteArray(), 
/* 120 */         (Charset)ObjectUtil.defaultIfNull(charset, CharsetUtil::defaultCharset));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\FastByteArrayOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */