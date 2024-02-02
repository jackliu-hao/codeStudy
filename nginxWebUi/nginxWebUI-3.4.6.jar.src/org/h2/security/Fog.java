/*    */ package org.h2.security;
/*    */ 
/*    */ import org.h2.util.Bits;
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
/*    */ public class Fog
/*    */   implements BlockCipher
/*    */ {
/*    */   private int key;
/*    */   
/*    */   public void encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 21 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i += 16) {
/* 22 */       encryptBlock(paramArrayOfbyte, paramArrayOfbyte, i);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/* 28 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i += 16) {
/* 29 */       decryptBlock(paramArrayOfbyte, paramArrayOfbyte, i);
/*    */     }
/*    */   }
/*    */   
/*    */   private void encryptBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/* 34 */     int i = Bits.readInt(paramArrayOfbyte1, paramInt);
/* 35 */     int j = Bits.readInt(paramArrayOfbyte1, paramInt + 4);
/* 36 */     int k = Bits.readInt(paramArrayOfbyte1, paramInt + 8);
/* 37 */     int m = Bits.readInt(paramArrayOfbyte1, paramInt + 12);
/* 38 */     int n = this.key;
/* 39 */     i = Integer.rotateLeft(i ^ n, j);
/* 40 */     k = Integer.rotateLeft(k ^ n, j);
/* 41 */     j = Integer.rotateLeft(j ^ n, i);
/* 42 */     m = Integer.rotateLeft(m ^ n, i);
/* 43 */     Bits.writeInt(paramArrayOfbyte2, paramInt, i);
/* 44 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 4, j);
/* 45 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 8, k);
/* 46 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 12, m);
/*    */   }
/*    */   
/*    */   private void decryptBlock(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, int paramInt) {
/* 50 */     int i = Bits.readInt(paramArrayOfbyte1, paramInt);
/* 51 */     int j = Bits.readInt(paramArrayOfbyte1, paramInt + 4);
/* 52 */     int k = Bits.readInt(paramArrayOfbyte1, paramInt + 8);
/* 53 */     int m = Bits.readInt(paramArrayOfbyte1, paramInt + 12);
/* 54 */     int n = this.key;
/* 55 */     j = Integer.rotateRight(j, i) ^ n;
/* 56 */     m = Integer.rotateRight(m, i) ^ n;
/* 57 */     i = Integer.rotateRight(i, j) ^ n;
/* 58 */     k = Integer.rotateRight(k, j) ^ n;
/* 59 */     Bits.writeInt(paramArrayOfbyte2, paramInt, i);
/* 60 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 4, j);
/* 61 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 8, k);
/* 62 */     Bits.writeInt(paramArrayOfbyte2, paramInt + 12, m);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getKeyLength() {
/* 67 */     return 16;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setKey(byte[] paramArrayOfbyte) {
/* 72 */     this.key = (int)Bits.readLong(paramArrayOfbyte, 0);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\Fog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */