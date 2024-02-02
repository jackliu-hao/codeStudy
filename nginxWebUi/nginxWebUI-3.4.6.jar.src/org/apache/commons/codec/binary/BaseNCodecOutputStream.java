/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Objects;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BaseNCodecOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*     */   private final boolean doEncode;
/*     */   private final BaseNCodec baseNCodec;
/*  46 */   private final byte[] singleByte = new byte[1];
/*     */   
/*  48 */   private final BaseNCodec.Context context = new BaseNCodec.Context();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BaseNCodecOutputStream(OutputStream output, BaseNCodec basedCodec, boolean doEncode) {
/*  58 */     super(output);
/*  59 */     this.baseNCodec = basedCodec;
/*  60 */     this.doEncode = doEncode;
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
/*     */   
/*     */   public void write(int i) throws IOException {
/*  73 */     this.singleByte[0] = (byte)i;
/*  74 */     write(this.singleByte, 0, 1);
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
/*     */   public void write(byte[] array, int offset, int len) throws IOException {
/*  97 */     Objects.requireNonNull(array, "array");
/*  98 */     if (offset < 0 || len < 0)
/*  99 */       throw new IndexOutOfBoundsException(); 
/* 100 */     if (offset > array.length || offset + len > array.length)
/* 101 */       throw new IndexOutOfBoundsException(); 
/* 102 */     if (len > 0) {
/* 103 */       if (this.doEncode) {
/* 104 */         this.baseNCodec.encode(array, offset, len, this.context);
/*     */       } else {
/* 106 */         this.baseNCodec.decode(array, offset, len, this.context);
/*     */       } 
/* 108 */       flush(false);
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
/*     */   
/*     */   private void flush(boolean propagate) throws IOException {
/* 122 */     int avail = this.baseNCodec.available(this.context);
/* 123 */     if (avail > 0) {
/* 124 */       byte[] buf = new byte[avail];
/* 125 */       int c = this.baseNCodec.readResults(buf, 0, avail, this.context);
/* 126 */       if (c > 0) {
/* 127 */         this.out.write(buf, 0, c);
/*     */       }
/*     */     } 
/* 130 */     if (propagate) {
/* 131 */       this.out.flush();
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
/*     */   public void flush() throws IOException {
/* 143 */     flush(true);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 160 */     eof();
/* 161 */     flush();
/* 162 */     this.out.close();
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
/*     */   public void eof() throws IOException {
/* 174 */     if (this.doEncode) {
/* 175 */       this.baseNCodec.encode(this.singleByte, 0, -1, this.context);
/*     */     } else {
/* 177 */       this.baseNCodec.decode(this.singleByte, 0, -1, this.context);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\BaseNCodecOutputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */