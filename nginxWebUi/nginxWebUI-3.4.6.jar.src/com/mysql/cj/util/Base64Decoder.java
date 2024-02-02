/*    */ package com.mysql.cj.util;
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
/*    */ public class Base64Decoder
/*    */ {
/* 43 */   private static byte[] decoderMap = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
/*    */ 
/*    */   
/*    */   public static class IntWrapper
/*    */   {
/*    */     public int value;
/*    */ 
/*    */     
/*    */     public IntWrapper(int value) {
/* 52 */       this.value = value;
/*    */     }
/*    */   }
/*    */   
/*    */   private static byte getNextValidByte(byte[] in, IntWrapper pos, int maxPos) {
/* 57 */     while (pos.value <= maxPos) {
/* 58 */       if (in[pos.value] >= 0 && decoderMap[in[pos.value]] >= 0) {
/* 59 */         return in[pos.value++];
/*    */       }
/* 61 */       pos.value++;
/*    */     } 
/*    */     
/* 64 */     return 61;
/*    */   }
/*    */   
/*    */   public static byte[] decode(byte[] in, int pos, int length) {
/* 68 */     IntWrapper offset = new IntWrapper(pos);
/* 69 */     byte[] sestet = new byte[4];
/*    */     
/* 71 */     int outLen = length * 3 / 4;
/* 72 */     byte[] octet = new byte[outLen];
/* 73 */     int octetId = 0;
/*    */     
/* 75 */     int maxPos = offset.value + length - 1;
/* 76 */     while (offset.value <= maxPos) {
/* 77 */       sestet[0] = decoderMap[getNextValidByte(in, offset, maxPos)];
/* 78 */       sestet[1] = decoderMap[getNextValidByte(in, offset, maxPos)];
/* 79 */       sestet[2] = decoderMap[getNextValidByte(in, offset, maxPos)];
/* 80 */       sestet[3] = decoderMap[getNextValidByte(in, offset, maxPos)];
/*    */       
/* 82 */       if (sestet[1] != -2) {
/* 83 */         octet[octetId++] = (byte)(sestet[0] << 2 | sestet[1] >>> 4);
/*    */       }
/* 85 */       if (sestet[2] != -2) {
/* 86 */         octet[octetId++] = (byte)((sestet[1] & 0xF) << 4 | sestet[2] >>> 2);
/*    */       }
/* 88 */       if (sestet[3] != -2) {
/* 89 */         octet[octetId++] = (byte)((sestet[2] & 0x3) << 6 | sestet[3]);
/*    */       }
/*    */     } 
/*    */     
/* 93 */     byte[] out = new byte[octetId];
/* 94 */     System.arraycopy(octet, 0, out, 0, octetId);
/* 95 */     return out;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\Base64Decoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */