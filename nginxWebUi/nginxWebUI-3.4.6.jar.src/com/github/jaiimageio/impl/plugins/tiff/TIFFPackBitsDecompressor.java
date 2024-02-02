/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
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
/*     */ 
/*     */ 
/*     */ public class TIFFPackBitsDecompressor
/*     */   extends TIFFDecompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   
/*     */   public int decode(byte[] srcData, int srcOffset, byte[] dstData, int dstOffset) throws IOException {
/*  62 */     int srcIndex = srcOffset;
/*  63 */     int dstIndex = dstOffset;
/*     */     
/*  65 */     int dstArraySize = dstData.length;
/*  66 */     int srcArraySize = srcData.length;
/*     */     try {
/*  68 */       while (dstIndex < dstArraySize && srcIndex < srcArraySize) {
/*  69 */         byte b = srcData[srcIndex++];
/*     */         
/*  71 */         if (b >= 0 && b <= Byte.MAX_VALUE) {
/*     */ 
/*     */           
/*  74 */           for (int i = 0; i < b + 1; i++)
/*  75 */             dstData[dstIndex++] = srcData[srcIndex++];  continue;
/*     */         } 
/*  77 */         if (b <= -1 && b >= -127) {
/*     */           
/*  79 */           byte repeat = srcData[srcIndex++];
/*  80 */           for (int i = 0; i < -b + 1; i++) {
/*  81 */             dstData[dstIndex++] = repeat;
/*     */           }
/*     */           continue;
/*     */         } 
/*  85 */         srcIndex++;
/*     */       }
/*     */     
/*  88 */     } catch (ArrayIndexOutOfBoundsException e) {
/*  89 */       if (this.reader instanceof TIFFImageReader) {
/*  90 */         ((TIFFImageReader)this.reader)
/*  91 */           .forwardWarningMessage("ArrayIndexOutOfBoundsException ignored in TIFFPackBitsDecompressor.decode()");
/*     */       }
/*     */     } 
/*     */     
/*  95 */     return dstIndex - dstOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/*     */     byte[] buf;
/*     */     int bufOffset;
/* 102 */     this.stream.seek(this.offset);
/*     */     
/* 104 */     byte[] srcData = new byte[this.byteCount];
/* 105 */     this.stream.readFully(srcData);
/*     */     
/* 107 */     int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
/*     */ 
/*     */     
/* 110 */     if (bytesPerRow == scanlineStride) {
/* 111 */       buf = b;
/* 112 */       bufOffset = dstOffset;
/*     */     } else {
/* 114 */       buf = new byte[bytesPerRow * this.srcHeight];
/* 115 */       bufOffset = 0;
/*     */     } 
/*     */     
/* 118 */     decode(srcData, 0, buf, bufOffset);
/*     */     
/* 120 */     if (bytesPerRow != scanlineStride) {
/*     */ 
/*     */ 
/*     */       
/* 124 */       int off = 0;
/* 125 */       for (int y = 0; y < this.srcHeight; y++) {
/* 126 */         System.arraycopy(buf, off, b, dstOffset, bytesPerRow);
/* 127 */         off += bytesPerRow;
/* 128 */         dstOffset += scanlineStride;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFPackBitsDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */