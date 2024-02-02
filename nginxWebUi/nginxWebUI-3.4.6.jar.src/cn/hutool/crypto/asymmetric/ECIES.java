/*     */ package cn.hutool.crypto.asymmetric;
/*     */ 
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
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
/*     */ public class ECIES
/*     */   extends AsymmetricCrypto
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String ALGORITHM_ECIES = "ECIES";
/*     */   
/*     */   public ECIES() {
/*  27 */     super("ECIES");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ECIES(String eciesAlgorithm) {
/*  36 */     super(eciesAlgorithm);
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
/*     */   public ECIES(String privateKeyStr, String publicKeyStr) {
/*  48 */     super("ECIES", privateKeyStr, publicKeyStr);
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
/*     */   public ECIES(String eciesAlgorithm, String privateKeyStr, String publicKeyStr) {
/*  62 */     super(eciesAlgorithm, privateKeyStr, publicKeyStr);
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
/*     */   public ECIES(byte[] privateKey, byte[] publicKey) {
/*  74 */     super("ECIES", privateKey, publicKey);
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
/*     */   public ECIES(PrivateKey privateKey, PublicKey publicKey) {
/*  87 */     super("ECIES", privateKey, publicKey);
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
/*     */   public ECIES(String eciesAlgorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 101 */     super(eciesAlgorithm, privateKey, publicKey);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\ECIES.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */