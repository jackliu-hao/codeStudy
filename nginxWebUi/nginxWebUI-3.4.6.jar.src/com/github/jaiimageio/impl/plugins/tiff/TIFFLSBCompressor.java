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
/*    */ public class TIFFLSBCompressor
/*    */   extends TIFFCompressor
/*    */ {
/*    */   public TIFFLSBCompressor() {
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
/* 69 */     int bytesPerRow = (bitsPerPixel * width + 7) / 8;
/* 70 */     byte[] compData = new byte[bytesPerRow];
/* 71 */     byte[] flipTable = TIFFFaxDecompressor.flipTable;
/* 72 */     for (int row = 0; row < height; row++) {
/* 73 */       System.arraycopy(b, off, compData, 0, bytesPerRow);
/* 74 */       for (int j = 0; j < bytesPerRow; j++) {
/* 75 */         compData[j] = flipTable[compData[j] & 0xFF];
/*    */       }
/* 77 */       this.stream.write(compData, 0, bytesPerRow);
/* 78 */       off += scanlineStride;
/*    */     } 
/* 80 */     return height * bytesPerRow;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFLSBCompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */