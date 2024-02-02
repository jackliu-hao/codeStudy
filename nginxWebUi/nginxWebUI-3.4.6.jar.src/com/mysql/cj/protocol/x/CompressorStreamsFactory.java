/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ public class CompressorStreamsFactory
/*     */ {
/*     */   private CompressionAlgorithm compressionAlgorithm;
/*  44 */   private InputStream compressorInputStreamInstance = null;
/*  45 */   private ContinuousInputStream underlyingInputStream = null;
/*     */   
/*  47 */   private OutputStream compressorOutputStreamInstance = null;
/*  48 */   private ReusableOutputStream underlyingOutputStream = null;
/*     */   
/*     */   public CompressorStreamsFactory(CompressionAlgorithm algorithm) {
/*  51 */     this.compressionAlgorithm = algorithm;
/*     */   }
/*     */   
/*     */   public CompressionMode getCompressionMode() {
/*  55 */     return this.compressionAlgorithm.getCompressionMode();
/*     */   }
/*     */   
/*     */   public boolean areCompressedStreamsContinuous() {
/*  59 */     return (getCompressionMode() == CompressionMode.STREAM);
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
/*     */   public InputStream getInputStreamInstance(InputStream in) {
/*  75 */     InputStream underlyingIn = in;
/*     */     
/*  77 */     if (areCompressedStreamsContinuous()) {
/*  78 */       if (this.compressorInputStreamInstance != null) {
/*  79 */         this.underlyingInputStream.addInputStream(underlyingIn);
/*  80 */         return this.compressorInputStreamInstance;
/*     */       } 
/*  82 */       this.underlyingInputStream = new ContinuousInputStream(underlyingIn);
/*  83 */       underlyingIn = this.underlyingInputStream;
/*     */     } 
/*     */     
/*  86 */     InputStream compressionIn = (InputStream)Util.getInstance(this.compressionAlgorithm.getInputStreamClass().getName(), new Class[] { InputStream.class }, new Object[] { underlyingIn }, null, 
/*  87 */         Messages.getString("Protocol.Compression.IoFactory.0", new Object[] {
/*  88 */             this.compressionAlgorithm.getInputStreamClass().getName(), this.compressionAlgorithm
/*     */           }));
/*  90 */     if (areCompressedStreamsContinuous()) {
/*  91 */       this.compressorInputStreamInstance = compressionIn;
/*     */     }
/*  93 */     return compressionIn;
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
/*     */   public OutputStream getOutputStreamInstance(OutputStream out) {
/* 109 */     OutputStream underlyingOut = out;
/*     */     
/* 111 */     if (areCompressedStreamsContinuous()) {
/* 112 */       if (this.compressorOutputStreamInstance != null) {
/* 113 */         this.underlyingOutputStream.setOutputStream(underlyingOut);
/* 114 */         return this.compressorOutputStreamInstance;
/*     */       } 
/* 116 */       this.underlyingOutputStream = new ReusableOutputStream(underlyingOut);
/* 117 */       underlyingOut = this.underlyingOutputStream;
/*     */     } 
/*     */     
/* 120 */     OutputStream compressionOut = (OutputStream)Util.getInstance(this.compressionAlgorithm.getOutputStreamClass().getName(), new Class[] { OutputStream.class }, new Object[] { underlyingOut }, null, 
/* 121 */         Messages.getString("Protocol.Compression.IoFactory.1", new Object[] {
/* 122 */             this.compressionAlgorithm.getOutputStreamClass().getName(), this.compressionAlgorithm
/*     */           }));
/* 124 */     if (areCompressedStreamsContinuous()) {
/* 125 */       compressionOut = new ContinuousOutputStream(compressionOut);
/* 126 */       this.compressorOutputStreamInstance = compressionOut;
/*     */     } 
/* 128 */     return compressionOut;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\CompressorStreamsFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */