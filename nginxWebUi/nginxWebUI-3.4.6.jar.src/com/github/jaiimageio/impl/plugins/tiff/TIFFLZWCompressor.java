/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.impl.common.LZWCompressor;
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
/*     */ import java.io.IOException;
/*     */ import javax.imageio.stream.ImageOutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TIFFLZWCompressor
/*     */   extends TIFFCompressor
/*     */ {
/*     */   int predictor;
/*     */   
/*     */   public TIFFLZWCompressor(int predictorValue) {
/*  63 */     super("LZW", 5, true);
/*  64 */     this.predictor = predictorValue;
/*     */   }
/*     */   
/*     */   public void setStream(ImageOutputStream stream) {
/*  68 */     super.setStream(stream);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/*  76 */     LZWCompressor lzwCompressor = new LZWCompressor(this.stream, 8, true);
/*     */     
/*  78 */     int samplesPerPixel = bitsPerSample.length;
/*  79 */     int bitsPerPixel = 0;
/*  80 */     for (int i = 0; i < samplesPerPixel; i++) {
/*  81 */       bitsPerPixel += bitsPerSample[i];
/*     */     }
/*  83 */     int bytesPerRow = (bitsPerPixel * width + 7) / 8;
/*     */     
/*  85 */     long initialStreamPosition = this.stream.getStreamPosition();
/*     */     
/*  87 */     boolean usePredictor = (this.predictor == 2);
/*     */ 
/*     */     
/*  90 */     if (bytesPerRow == scanlineStride && !usePredictor) {
/*  91 */       lzwCompressor.compress(b, off, bytesPerRow * height);
/*     */     } else {
/*  93 */       byte[] rowBuf = usePredictor ? new byte[bytesPerRow] : null;
/*  94 */       for (int j = 0; j < height; j++) {
/*  95 */         if (usePredictor) {
/*     */ 
/*     */           
/*  98 */           System.arraycopy(b, off, rowBuf, 0, bytesPerRow);
/*  99 */           for (int k = bytesPerRow - 1; k >= samplesPerPixel; k--) {
/* 100 */             rowBuf[k] = (byte)(rowBuf[k] - rowBuf[k - samplesPerPixel]);
/*     */           }
/* 102 */           lzwCompressor.compress(rowBuf, 0, bytesPerRow);
/*     */         } else {
/* 104 */           lzwCompressor.compress(b, off, bytesPerRow);
/*     */         } 
/* 106 */         off += scanlineStride;
/*     */       } 
/*     */     } 
/*     */     
/* 110 */     lzwCompressor.flush();
/*     */ 
/*     */     
/* 113 */     int bytesWritten = (int)(this.stream.getStreamPosition() - initialStreamPosition);
/*     */     
/* 115 */     return bytesWritten;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFLZWCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */