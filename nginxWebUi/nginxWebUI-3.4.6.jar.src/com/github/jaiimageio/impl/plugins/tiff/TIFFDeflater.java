/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
/*     */ import java.io.IOException;
/*     */ import java.util.zip.Deflater;
/*     */ import javax.imageio.ImageWriteParam;
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
/*     */ public class TIFFDeflater
/*     */   extends TIFFCompressor
/*     */ {
/*     */   Deflater deflater;
/*     */   int predictor;
/*     */   
/*     */   public TIFFDeflater(String compressionType, int compressionTagValue, ImageWriteParam param, int predictorValue) {
/*  67 */     super(compressionType, compressionTagValue, true);
/*     */     int deflateLevel;
/*  69 */     this.predictor = predictorValue;
/*     */ 
/*     */ 
/*     */     
/*  73 */     if (param != null && param
/*  74 */       .getCompressionMode() == 2) {
/*  75 */       float quality = param.getCompressionQuality();
/*  76 */       deflateLevel = (int)(1.0F + 8.0F * quality);
/*     */     } else {
/*  78 */       deflateLevel = -1;
/*     */     } 
/*     */     
/*  81 */     this.deflater = new Deflater(deflateLevel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/*  89 */     int inputSize = height * scanlineStride;
/*  90 */     int blocks = (inputSize + 32767) / 32768;
/*     */ 
/*     */ 
/*     */     
/*  94 */     byte[] compData = new byte[inputSize + 5 * blocks + 6];
/*     */     
/*  96 */     int numCompressedBytes = 0;
/*  97 */     if (this.predictor == 2) {
/*  98 */       int samplesPerPixel = bitsPerSample.length;
/*  99 */       int bitsPerPixel = 0;
/* 100 */       for (int i = 0; i < samplesPerPixel; i++) {
/* 101 */         bitsPerPixel += bitsPerSample[i];
/*     */       }
/* 103 */       int bytesPerRow = (bitsPerPixel * width + 7) / 8;
/* 104 */       byte[] rowBuf = new byte[bytesPerRow];
/*     */       
/* 106 */       int maxRow = height - 1;
/* 107 */       for (int j = 0; j < height; j++) {
/*     */ 
/*     */         
/* 110 */         System.arraycopy(b, off, rowBuf, 0, bytesPerRow);
/* 111 */         for (int k = bytesPerRow - 1; k >= samplesPerPixel; k--) {
/* 112 */           rowBuf[k] = (byte)(rowBuf[k] - rowBuf[k - samplesPerPixel]);
/*     */         }
/*     */         
/* 115 */         this.deflater.setInput(rowBuf);
/* 116 */         if (j == maxRow) {
/* 117 */           this.deflater.finish();
/*     */         }
/*     */         
/* 120 */         int numBytes = 0;
/* 121 */         while ((numBytes = this.deflater.deflate(compData, numCompressedBytes, compData.length - numCompressedBytes)) != 0)
/*     */         {
/*     */ 
/*     */           
/* 125 */           numCompressedBytes += numBytes;
/*     */         }
/*     */         
/* 128 */         off += scanlineStride;
/*     */       } 
/*     */     } else {
/* 131 */       this.deflater.setInput(b, off, height * scanlineStride);
/* 132 */       this.deflater.finish();
/*     */       
/* 134 */       numCompressedBytes = this.deflater.deflate(compData);
/*     */     } 
/*     */     
/* 137 */     this.deflater.reset();
/*     */     
/* 139 */     this.stream.write(compData, 0, numCompressedBytes);
/*     */     
/* 141 */     return numCompressedBytes;
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 145 */     if (this.deflater != null) {
/* 146 */       this.deflater.end();
/* 147 */       this.deflater = null;
/*     */     } 
/* 149 */     super.dispose();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFDeflater.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */