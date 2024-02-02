/*    */ package cn.hutool.crypto.digest;
/*    */ 
/*    */ import cn.hutool.crypto.digest.mac.Mac;
/*    */ import cn.hutool.crypto.digest.mac.MacEngine;
/*    */ import cn.hutool.crypto.digest.mac.MacEngineFactory;
/*    */ import java.security.Key;
/*    */ import java.security.spec.AlgorithmParameterSpec;
/*    */ import javax.crypto.spec.SecretKeySpec;
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
/*    */ public class HMac
/*    */   extends Mac
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public HMac(HmacAlgorithm algorithm) {
/* 32 */     this(algorithm, (Key)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMac(HmacAlgorithm algorithm, byte[] key) {
/* 42 */     this(algorithm.getValue(), key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMac(HmacAlgorithm algorithm, Key key) {
/* 52 */     this(algorithm.getValue(), key);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMac(String algorithm, byte[] key) {
/* 63 */     this(algorithm, new SecretKeySpec(key, algorithm));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMac(String algorithm, Key key) {
/* 74 */     this(algorithm, key, null);
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
/*    */   public HMac(String algorithm, Key key, AlgorithmParameterSpec spec) {
/* 86 */     this(MacEngineFactory.createEngine(algorithm, key, spec));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HMac(MacEngine engine) {
/* 96 */     super(engine);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\HMac.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */