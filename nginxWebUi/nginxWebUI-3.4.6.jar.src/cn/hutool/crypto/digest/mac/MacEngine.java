/*    */ package cn.hutool.crypto.digest.mac;
/*    */ 
/*    */ import cn.hutool.crypto.CryptoException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ public interface MacEngine
/*    */ {
/*    */   default void update(byte[] in) {
/* 23 */     update(in, 0, in.length);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   byte[] doFinal();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void reset();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default byte[] digest(InputStream data, int bufferLength) {
/*    */     byte[] result;
/* 57 */     if (bufferLength < 1) {
/* 58 */       bufferLength = 8192;
/*    */     }
/*    */     
/* 61 */     byte[] buffer = new byte[bufferLength];
/*    */ 
/*    */     
/*    */     try {
/* 65 */       int read = data.read(buffer, 0, bufferLength);
/*    */       
/* 67 */       while (read > -1) {
/* 68 */         update(buffer, 0, read);
/* 69 */         read = data.read(buffer, 0, bufferLength);
/*    */       } 
/* 71 */       result = doFinal();
/* 72 */     } catch (IOException e) {
/* 73 */       throw new CryptoException(e);
/*    */     } finally {
/* 75 */       reset();
/*    */     } 
/* 77 */     return result;
/*    */   }
/*    */   
/*    */   int getMacLength();
/*    */   
/*    */   String getAlgorithm();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\MacEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */