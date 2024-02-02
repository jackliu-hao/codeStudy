/*    */ package com.github.jaiimageio.impl.plugins.tiff;
/*    */ 
/*    */ import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
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
/*    */ public class TIFFLSBDecompressor
/*    */   extends TIFFDecompressor
/*    */ {
/* 56 */   private static byte[] flipTable = TIFFFaxDecompressor.flipTable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
/* 64 */     this.stream.seek(this.offset);
/*    */     
/* 66 */     int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
/* 67 */     if (bytesPerRow == scanlineStride) {
/* 68 */       int numBytes = bytesPerRow * this.srcHeight;
/* 69 */       this.stream.readFully(b, dstOffset, numBytes);
/* 70 */       int xMax = dstOffset + numBytes;
/* 71 */       for (int x = dstOffset; x < xMax; x++) {
/* 72 */         b[x] = flipTable[b[x] & 0xFF];
/*    */       }
/*    */     } else {
/* 75 */       for (int y = 0; y < this.srcHeight; y++) {
/* 76 */         this.stream.readFully(b, dstOffset, bytesPerRow);
/* 77 */         int xMax = dstOffset + bytesPerRow;
/* 78 */         for (int x = dstOffset; x < xMax; x++) {
/* 79 */           b[x] = flipTable[b[x] & 0xFF];
/*    */         }
/* 81 */         dstOffset += scanlineStride;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFLSBDecompressor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */