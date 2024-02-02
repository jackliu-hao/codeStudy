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
/*    */ public class ZUC
/*    */   extends SymmetricCrypto
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public static byte[] generateKey(ZUCAlgorithm algorithm) {
/* 26 */     return KeyUtil.generateKey(algorithm.value).getEncoded();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ZUC(ZUCAlgorithm algorithm, byte[] key, byte[] iv) {
/* 37 */     super(algorithm.value, 
/* 38 */         KeyUtil.generateKey(algorithm.value, key), 
/* 39 */         generateIvParam(algorithm, iv));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public enum ZUCAlgorithm
/*    */   {
/* 48 */     ZUC_128("ZUC-128"),
/* 49 */     ZUC_256("ZUC-256");
/*    */ 
/*    */ 
/*    */     
/*    */     private final String value;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     ZUCAlgorithm(String value) {
/* 59 */       this.value = value;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public String getValue() {
/* 68 */       return this.value;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static IvParameterSpec generateIvParam(ZUCAlgorithm algorithm, byte[] iv) {
/* 80 */     if (null == iv) {
/* 81 */       switch (algorithm) {
/*    */         case ZUC_128:
/* 83 */           iv = RandomUtil.randomBytes(16);
/*    */           break;
/*    */         case ZUC_256:
/* 86 */           iv = RandomUtil.randomBytes(25);
/*    */           break;
/*    */       } 
/*    */     }
/* 90 */     return new IvParameterSpec(iv);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\ZUC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */