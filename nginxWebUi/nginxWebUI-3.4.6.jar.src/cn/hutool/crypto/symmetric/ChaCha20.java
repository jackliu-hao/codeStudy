/*    */ package cn.hutool.crypto.symmetric;
/*    */ 
/*    */ import cn.hutool.core.util.RandomUtil;
/*    */ import cn.hutool.crypto.KeyUtil;
/*    */ import javax.crypto.spec.IvParameterSpec;
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
/*    */ public class ChaCha20
/*    */   extends SymmetricCrypto
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final String ALGORITHM_NAME = "ChaCha20";
/*    */   
/*    */   public ChaCha20(byte[] key, byte[] iv) {
/* 27 */     super("ChaCha20", 
/* 28 */         KeyUtil.generateKey("ChaCha20", key), 
/* 29 */         generateIvParam(iv));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static IvParameterSpec generateIvParam(byte[] iv) {
/* 39 */     if (null == iv) {
/* 40 */       iv = RandomUtil.randomBytes(12);
/*    */     }
/* 42 */     return new IvParameterSpec(iv);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\ChaCha20.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */