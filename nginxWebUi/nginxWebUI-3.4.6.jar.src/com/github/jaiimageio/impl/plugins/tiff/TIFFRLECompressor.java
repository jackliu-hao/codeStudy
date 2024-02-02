/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import javax.imageio.IIOException;
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
/*     */ public class TIFFRLECompressor
/*     */   extends TIFFFaxCompressor
/*     */ {
/*     */   public TIFFRLECompressor() {
/*  59 */     super("CCITT RLE", 2, true);
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
/*     */   public int encodeRLE(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData) {
/*  82 */     initBitBuf();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  88 */     int outIndex = encode1D(data, rowOffset, colOffset, rowLength, compData, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     while (this.ndex > 0) {
/*  94 */       compData[outIndex++] = (byte)(this.bits >>> 24);
/*  95 */       this.bits <<= 8;
/*  96 */       this.ndex -= 8;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 102 */     if (this.inverseFill) {
/* 103 */       byte[] flipTable = TIFFFaxDecompressor.flipTable;
/* 104 */       for (int i = 0; i < outIndex; i++) {
/* 105 */         compData[i] = flipTable[compData[i] & 0xFF];
/*     */       }
/*     */     } 
/*     */     
/* 109 */     return outIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/* 116 */     if (bitsPerSample.length != 1 || bitsPerSample[0] != 1) {
/* 117 */       throw new IIOException("Bits per sample must be 1 for RLE compression!");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 123 */     int maxBits = 9 * (width + 1) / 2 + 2;
/* 124 */     byte[] compData = new byte[(maxBits + 7) / 8];
/*     */     
/* 126 */     int bytes = 0;
/* 127 */     int rowOffset = off;
/*     */     
/* 129 */     for (int i = 0; i < height; i++) {
/* 130 */       int rowBytes = encodeRLE(b, rowOffset, 0, width, compData);
/* 131 */       this.stream.write(compData, 0, rowBytes);
/*     */       
/* 133 */       rowOffset += scanlineStride;
/* 134 */       bytes += rowBytes;
/*     */     } 
/*     */     
/* 137 */     return bytes;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFRLECompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */