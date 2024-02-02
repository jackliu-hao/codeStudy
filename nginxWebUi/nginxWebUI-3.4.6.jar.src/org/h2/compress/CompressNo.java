/*    */ package org.h2.compress;
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
/*    */ public class CompressNo
/*    */   implements Compressor
/*    */ {
/*    */   public int getAlgorithm() {
/* 17 */     return 0;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void setOptions(String paramString) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public int compress(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
/* 27 */     System.arraycopy(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt3, paramInt2);
/* 28 */     return paramInt3 + paramInt2;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void expand(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3, int paramInt4) {
/* 34 */     System.arraycopy(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt3, paramInt4);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\compress\CompressNo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */