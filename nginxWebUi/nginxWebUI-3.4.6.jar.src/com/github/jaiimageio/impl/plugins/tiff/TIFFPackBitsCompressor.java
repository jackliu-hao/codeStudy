/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
/*     */ import java.io.IOException;
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
/*     */ public class TIFFPackBitsCompressor
/*     */   extends TIFFCompressor
/*     */ {
/*     */   public TIFFPackBitsCompressor() {
/*  58 */     super("PackBits", 32773, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int packBits(byte[] input, int inOffset, int inCount, byte[] output, int outOffset) {
/*  68 */     int inMax = inOffset + inCount - 1;
/*  69 */     int inMaxMinus1 = inMax - 1;
/*     */     
/*  71 */     while (inOffset <= inMax) {
/*  72 */       int run = 1;
/*  73 */       byte replicate = input[inOffset];
/*  74 */       while (run < 127 && inOffset < inMax && input[inOffset] == input[inOffset + 1]) {
/*     */         
/*  76 */         run++;
/*  77 */         inOffset++;
/*     */       } 
/*  79 */       if (run > 1) {
/*  80 */         inOffset++;
/*  81 */         output[outOffset++] = (byte)-(run - 1);
/*  82 */         output[outOffset++] = replicate;
/*     */       } 
/*     */       
/*  85 */       run = 0;
/*  86 */       int saveOffset = outOffset;
/*  87 */       while (run < 128 && ((inOffset < inMax && input[inOffset] != input[inOffset + 1]) || (inOffset < inMaxMinus1 && input[inOffset] != input[inOffset + 2]))) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  92 */         run++;
/*  93 */         output[++outOffset] = input[inOffset++];
/*     */       } 
/*  95 */       if (run > 0) {
/*  96 */         output[saveOffset] = (byte)(run - 1);
/*  97 */         outOffset++;
/*     */       } 
/*     */       
/* 100 */       if (inOffset == inMax) {
/* 101 */         if (run > 0 && run < 128) {
/* 102 */           output[saveOffset] = (byte)(output[saveOffset] + 1);
/* 103 */           output[outOffset++] = input[inOffset++]; continue;
/*     */         } 
/* 105 */         output[outOffset++] = 0;
/* 106 */         output[outOffset++] = input[inOffset++];
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 111 */     return outOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/* 118 */     int bitsPerPixel = 0;
/* 119 */     for (int i = 0; i < bitsPerSample.length; i++) {
/* 120 */       bitsPerPixel += bitsPerSample[i];
/*     */     }
/* 122 */     int bytesPerRow = (bitsPerPixel * width + 7) / 8;
/* 123 */     int bufSize = bytesPerRow + (bytesPerRow + 127) / 128;
/* 124 */     byte[] compData = new byte[bufSize];
/*     */     
/* 126 */     int bytesWritten = 0;
/*     */     
/* 128 */     for (int j = 0; j < height; j++) {
/* 129 */       int bytes = packBits(b, off, scanlineStride, compData, 0);
/* 130 */       off += scanlineStride;
/* 131 */       bytesWritten += bytes;
/* 132 */       this.stream.write(compData, 0, bytes);
/*     */     } 
/*     */     
/* 135 */     return bytesWritten;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFPackBitsCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */