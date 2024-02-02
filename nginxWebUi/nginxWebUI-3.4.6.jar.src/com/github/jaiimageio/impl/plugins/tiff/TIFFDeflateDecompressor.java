/*     */ package com.github.jaiimageio.impl.plugins.tiff;
/*     */ 
/*     */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
/*     */ import java.io.IOException;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
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
/*     */ public class TIFFDeflateDecompressor
/*     */   extends TIFFDecompressor
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*  60 */   Inflater inflater = null;
/*     */   int predictor;
/*     */   
/*     */   public TIFFDeflateDecompressor(int predictor) throws IIOException {
/*  64 */     this.inflater = new Inflater();
/*     */     
/*  66 */     if (predictor != 1 && predictor != 2)
/*     */     {
/*     */       
/*  69 */       throw new IIOException("Illegal value for Predictor in TIFF file");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  77 */     this.predictor = predictor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/*     */     byte[] buf;
/*     */     int bufOffset;
/*  86 */     if (this.predictor == 2) {
/*     */       
/*  88 */       int len = this.bitsPerSample.length;
/*  89 */       for (int i = 0; i < len; i++) {
/*  90 */         if (this.bitsPerSample[i] != 8) {
/*  91 */           throw new IIOException(this.bitsPerSample[i] + "-bit samples " + "are not supported for Horizontal " + "differencing Predictor");
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     this.stream.seek(this.offset);
/*     */ 
/*     */     
/* 103 */     byte[] srcData = new byte[this.byteCount];
/* 104 */     this.stream.readFully(srcData);
/*     */     
/* 106 */     int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
/*     */ 
/*     */     
/* 109 */     if (bytesPerRow == scanlineStride) {
/* 110 */       buf = b;
/* 111 */       bufOffset = dstOffset;
/*     */     } else {
/* 113 */       buf = new byte[bytesPerRow * this.srcHeight];
/* 114 */       bufOffset = 0;
/*     */     } 
/*     */ 
/*     */     
/* 118 */     this.inflater.setInput(srcData);
/*     */ 
/*     */     
/*     */     try {
/* 122 */       this.inflater.inflate(buf, bufOffset, bytesPerRow * this.srcHeight);
/* 123 */     } catch (DataFormatException dfe) {
/* 124 */       throw new IIOException(I18N.getString("TIFFDeflateDecompressor0"), dfe);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 129 */     this.inflater.reset();
/*     */     
/* 131 */     if (this.predictor == 2)
/*     */     {
/*     */       
/* 134 */       for (int j = 0; j < this.srcHeight; j++) {
/* 135 */         int count = bufOffset + this.samplesPerPixel * (j * this.srcWidth + 1);
/* 136 */         for (int i = this.samplesPerPixel; i < this.srcWidth * this.samplesPerPixel; i++) {
/* 137 */           buf[count] = (byte)(buf[count] + buf[count - this.samplesPerPixel]);
/* 138 */           count++;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 143 */     if (bytesPerRow != scanlineStride) {
/*     */ 
/*     */ 
/*     */       
/* 147 */       int off = 0;
/* 148 */       for (int y = 0; y < this.srcHeight; y++) {
/* 149 */         System.arraycopy(buf, off, b, dstOffset, bytesPerRow);
/* 150 */         off += bytesPerRow;
/* 151 */         dstOffset += scanlineStride;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFDeflateDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */