/*    */ package cn.hutool.crypto.digest.mac;
/*    */ 
/*    */ import org.bouncycastle.crypto.BlockCipher;
/*    */ import org.bouncycastle.crypto.CipherParameters;
/*    */ import org.bouncycastle.crypto.engines.SM4Engine;
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
/*    */ public class SM4MacEngine
/*    */   extends CBCBlockCipherMacEngine
/*    */ {
/*    */   private static final int MAC_SIZE = 128;
/*    */   
/*    */   public SM4MacEngine(CipherParameters params) {
/* 22 */     super((BlockCipher)new SM4Engine(), 128, params);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\SM4MacEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */