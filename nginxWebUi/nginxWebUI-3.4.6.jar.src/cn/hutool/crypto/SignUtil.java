/*     */ package cn.hutool.crypto;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.crypto.asymmetric.Sign;
/*     */ import cn.hutool.crypto.asymmetric.SignAlgorithm;
/*     */ import cn.hutool.crypto.digest.DigestAlgorithm;
/*     */ import cn.hutool.crypto.digest.Digester;
/*     */ import cn.hutool.crypto.symmetric.SymmetricCrypto;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignUtil
/*     */ {
/*     */   public static Sign sign(SignAlgorithm algorithm) {
/*  36 */     return new Sign(algorithm);
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
/*     */   
/*     */   public static Sign sign(SignAlgorithm algorithm, String privateKeyBase64, String publicKeyBase64) {
/*  51 */     return new Sign(algorithm, privateKeyBase64, publicKeyBase64);
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
/*     */   
/*     */   public static Sign sign(SignAlgorithm algorithm, byte[] privateKey, byte[] publicKey) {
/*  66 */     return new Sign(algorithm, privateKey, publicKey);
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
/*     */   
/*     */   public static String signParams(SymmetricCrypto crypto, Map<?, ?> params, String... otherParams) {
/*  81 */     return signParams(crypto, params, "", "", true, otherParams);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String signParams(SymmetricCrypto crypto, Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
/*  99 */     return crypto.encryptHex(MapUtil.sortJoin(params, separator, keyValueSeparator, isIgnoreNull, otherParams));
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
/*     */   public static String signParamsMd5(Map<?, ?> params, String... otherParams) {
/* 113 */     return signParams(DigestAlgorithm.MD5, params, otherParams);
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
/*     */   public static String signParamsSha1(Map<?, ?> params, String... otherParams) {
/* 127 */     return signParams(DigestAlgorithm.SHA1, params, otherParams);
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
/*     */   public static String signParamsSha256(Map<?, ?> params, String... otherParams) {
/* 141 */     return signParams(DigestAlgorithm.SHA256, params, otherParams);
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
/*     */   
/*     */   public static String signParams(DigestAlgorithm digestAlgorithm, Map<?, ?> params, String... otherParams) {
/* 156 */     return signParams(digestAlgorithm, params, "", "", true, otherParams);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String signParams(DigestAlgorithm digestAlgorithm, Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
/* 174 */     return (new Digester(digestAlgorithm)).digestHex(MapUtil.sortJoin(params, separator, keyValueSeparator, isIgnoreNull, otherParams));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\SignUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */