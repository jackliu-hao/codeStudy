/*    */ package com.github.jaiimageio.impl.plugins.tiff;
/*    */ 
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
/*    */ public class TIFFPackBitsUtil
/*    */ {
/*    */   private static final boolean debug = false;
/* 53 */   byte[] dstData = new byte[8192];
/* 54 */   int dstIndex = 0;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void ensureCapacity(int bytesToAdd) {
/* 60 */     if (this.dstIndex + bytesToAdd > this.dstData.length) {
/* 61 */       byte[] newDstData = new byte[Math.max((int)(this.dstData.length * 1.2F), this.dstIndex + bytesToAdd)];
/*    */       
/* 63 */       System.arraycopy(this.dstData, 0, newDstData, 0, this.dstData.length);
/* 64 */       this.dstData = newDstData;
/*    */     } 
/*    */   }
/*    */   
/*    */   public byte[] decode(byte[] srcData) throws IOException {
/* 69 */     int inIndex = 0;
/* 70 */     while (inIndex < srcData.length) {
/* 71 */       byte b = srcData[inIndex++];
/*    */       
/* 73 */       if (b >= 0 && b <= Byte.MAX_VALUE) {
/*    */ 
/*    */         
/* 76 */         ensureCapacity(b + 1);
/* 77 */         for (int i = 0; i < b + 1; i++)
/* 78 */           this.dstData[this.dstIndex++] = srcData[inIndex++];  continue;
/*    */       } 
/* 80 */       if (b <= -1 && b >= -127) {
/*    */         
/* 82 */         byte repeat = srcData[inIndex++];
/* 83 */         ensureCapacity(-b + 1);
/* 84 */         for (int i = 0; i < -b + 1; i++) {
/* 85 */           this.dstData[this.dstIndex++] = repeat;
/*    */         }
/*    */         continue;
/*    */       } 
/* 89 */       inIndex++;
/*    */     } 
/*    */ 
/*    */     
/* 93 */     byte[] newDstData = new byte[this.dstIndex];
/* 94 */     System.arraycopy(this.dstData, 0, newDstData, 0, this.dstIndex);
/* 95 */     return newDstData;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\jaiimageio\impl\plugins\tiff\TIFFPackBitsUtil.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */