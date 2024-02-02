/*    */ package com.github.jaiimageio.impl.plugins.tiff;
/*    */ 
/*    */ import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TIFFNullCompressor
/*    */   extends TIFFCompressor
/*    */ {
/*    */   public TIFFNullCompressor() {
/* 58 */     super("", 1, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
/* 65 */     int bitsPerPixel = 0;
/* 66 */     for (int i = 0; i < bitsPerSample.length; i++) {
/* 67 */       bitsPerPixel += bitsPerSample[i];
/*    */     }
/*    */     
/* 70 */     int bytesPerRow = (bitsPerPixel * width + 7) / 8;
/* 71 */     int numBytes = height * bytesPerRow;
/*    */     
/* 73 */     if (bytesPerRow == scanlineStride) {
/* 74 */       this.stream.write(b, off, numBytes);
/*    */     } else {
/* 76 */       for (int row = 0; row < height; row++) {
/* 77 */         this.stream.write(b, off, bytesPerRow);
/* 78 */         off += scanlineStride;
/*    */       } 
/*    */     } 
/*    */     
/* 82 */     return numBytes;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFNullCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */