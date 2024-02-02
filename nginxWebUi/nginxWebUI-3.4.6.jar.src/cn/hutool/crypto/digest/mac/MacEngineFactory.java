/*    */ package cn.hutool.crypto.digest.mac;
/*    */ 
/*    */ import cn.hutool.crypto.SmUtil;
/*    */ import cn.hutool.crypto.digest.HmacAlgorithm;
/*    */ import java.security.Key;
/*    */ import java.security.spec.AlgorithmParameterSpec;
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
/*    */ public class MacEngineFactory
/*    */ {
/*    */   public static MacEngine createEngine(String algorithm, Key key) {
/* 25 */     return createEngine(algorithm, key, null);
/*    */   }
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
/*    */   public static MacEngine createEngine(String algorithm, Key key, AlgorithmParameterSpec spec) {
/* 38 */     if (algorithm.equalsIgnoreCase(HmacAlgorithm.HmacSM3.getValue()))
/*    */     {
/* 40 */       return SmUtil.createHmacSm3Engine(key.getEncoded());
/*    */     }
/* 42 */     return new DefaultHMacEngine(algorithm, key, spec);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\MacEngineFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */