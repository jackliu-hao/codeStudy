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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFNullDecompressor
/*     */   extends TIFFDecompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private boolean isReadActiveOnly = false;
/*     */   private int originalSrcMinX;
/*     */   private int originalSrcMinY;
/*     */   private int originalSrcWidth;
/*     */   private int originalSrcHeight;
/*     */   
/*     */   public void beginDecoding() {
/*  84 */     int bitsPerPixel = 0;
/*  85 */     for (int i = 0; i < this.bitsPerSample.length; i++) {
/*  86 */       bitsPerPixel += this.bitsPerSample[i];
/*     */     }
/*     */ 
/*     */     
/*  90 */     if ((this.activeSrcMinX != this.srcMinX || this.activeSrcMinY != this.srcMinY || this.activeSrcWidth != this.srcWidth || this.activeSrcHeight != this.srcHeight) && (this.activeSrcMinX - this.srcMinX) * bitsPerPixel % 8 == 0) {
/*     */ 
/*     */ 
/*     */       
/*  94 */       this.isReadActiveOnly = true;
/*     */ 
/*     */       
/*  97 */       this.originalSrcMinX = this.srcMinX;
/*  98 */       this.originalSrcMinY = this.srcMinY;
/*  99 */       this.originalSrcWidth = this.srcWidth;
/* 100 */       this.originalSrcHeight = this.srcHeight;
/*     */ 
/*     */       
/* 103 */       this.srcMinX = this.activeSrcMinX;
/* 104 */       this.srcMinY = this.activeSrcMinY;
/* 105 */       this.srcWidth = this.activeSrcWidth;
/* 106 */       this.srcHeight = this.activeSrcHeight;
/*     */     } else {
/*     */       
/* 109 */       this.isReadActiveOnly = false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 140 */     super.beginDecoding();
/*     */   }
/*     */   
/*     */   public void decode() throws IOException {
/* 144 */     super.decode();
/*     */ 
/*     */     
/* 147 */     if (this.isReadActiveOnly) {
/*     */       
/* 149 */       this.srcMinX = this.originalSrcMinX;
/* 150 */       this.srcMinY = this.originalSrcMinY;
/* 151 */       this.srcWidth = this.originalSrcWidth;
/* 152 */       this.srcHeight = this.originalSrcHeight;
/*     */ 
/*     */       
/* 155 */       this.isReadActiveOnly = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/* 163 */     if (this.isReadActiveOnly) {
/*     */ 
/*     */       
/* 166 */       int activeBytesPerRow = (this.activeSrcWidth * bitsPerPixel + 7) / 8;
/* 167 */       int totalBytesPerRow = (this.originalSrcWidth * bitsPerPixel + 7) / 8;
/* 168 */       int bytesToSkipPerRow = totalBytesPerRow - activeBytesPerRow;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 184 */       this.stream.seek(this.offset + ((this.activeSrcMinY - this.originalSrcMinY) * totalBytesPerRow) + ((this.activeSrcMinX - this.originalSrcMinX) * bitsPerPixel / 8));
/*     */ 
/*     */ 
/*     */       
/* 188 */       int lastRow = this.activeSrcHeight - 1;
/* 189 */       for (int y = 0; y < this.activeSrcHeight; y++) {
/* 190 */         this.stream.read(b, dstOffset, activeBytesPerRow);
/* 191 */         dstOffset += scanlineStride;
/*     */ 
/*     */         
/* 194 */         if (y != lastRow) {
/* 195 */           this.stream.skipBytes(bytesToSkipPerRow);
/*     */         }
/*     */       } 
/*     */     } else {
/*     */       
/* 200 */       this.stream.seek(this.offset);
/* 201 */       int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
/* 202 */       if (bytesPerRow == scanlineStride) {
/* 203 */         this.stream.read(b, dstOffset, bytesPerRow * this.srcHeight);
/*     */       } else {
/* 205 */         for (int y = 0; y < this.srcHeight; y++) {
/* 206 */           this.stream.read(b, dstOffset, bytesPerRow);
/* 207 */           dstOffset += scanlineStride;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFNullDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */