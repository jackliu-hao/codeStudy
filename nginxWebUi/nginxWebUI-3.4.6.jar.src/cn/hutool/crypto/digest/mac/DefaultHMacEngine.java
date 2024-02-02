/*     */ package cn.hutool.crypto.digest.mac;
/*     */ 
/*     */ import cn.hutool.crypto.CryptoException;
/*     */ import cn.hutool.crypto.SecureUtil;
/*     */ import java.security.Key;
/*     */ import java.security.spec.AlgorithmParameterSpec;
/*     */ import javax.crypto.Mac;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultHMacEngine
/*     */   implements MacEngine
/*     */ {
/*     */   private Mac mac;
/*     */   
/*     */   public DefaultHMacEngine(String algorithm, byte[] key) {
/*  33 */     this(algorithm, (null == key) ? null : new SecretKeySpec(key, algorithm));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHMacEngine(String algorithm, Key key) {
/*  44 */     this(algorithm, key, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHMacEngine(String algorithm, Key key, AlgorithmParameterSpec spec) {
/*  56 */     init(algorithm, key, spec);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHMacEngine init(String algorithm, byte[] key) {
/*  68 */     return init(algorithm, (null == key) ? null : new SecretKeySpec(key, algorithm));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHMacEngine init(String algorithm, Key key) {
/*  80 */     return init(algorithm, key, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultHMacEngine init(String algorithm, Key key, AlgorithmParameterSpec spec) {
/*     */     try {
/*  95 */       this.mac = SecureUtil.createMac(algorithm);
/*  96 */       if (null == key) {
/*  97 */         key = SecureUtil.generateKey(algorithm);
/*     */       }
/*  99 */       if (null != spec) {
/* 100 */         this.mac.init(key, spec);
/*     */       } else {
/* 102 */         this.mac.init(key);
/*     */       } 
/* 104 */     } catch (Exception e) {
/* 105 */       throw new CryptoException(e);
/*     */     } 
/* 107 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Mac getMac() {
/* 116 */     return this.mac;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(byte[] in) {
/* 121 */     this.mac.update(in);
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(byte[] in, int inOff, int len) {
/* 126 */     this.mac.update(in, inOff, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] doFinal() {
/* 131 */     return this.mac.doFinal();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 136 */     this.mac.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMacLength() {
/* 141 */     return this.mac.getMacLength();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlgorithm() {
/* 146 */     return this.mac.getAlgorithm();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\mac\DefaultHMacEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */