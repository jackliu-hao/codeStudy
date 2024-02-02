/*    */ package cn.hutool.jwt.signers;
/*    */ 
/*    */ import cn.hutool.core.map.BiMap;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.crypto.asymmetric.SignAlgorithm;
/*    */ import cn.hutool.crypto.digest.HmacAlgorithm;
/*    */ import java.util.HashMap;
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
/*    */ public class AlgorithmUtil
/*    */ {
/* 21 */   private static final BiMap<String, String> map = new BiMap(new HashMap<>()); static {
/* 22 */     map.put("HS256", HmacAlgorithm.HmacSHA256.getValue());
/* 23 */     map.put("HS384", HmacAlgorithm.HmacSHA384.getValue());
/* 24 */     map.put("HS512", HmacAlgorithm.HmacSHA512.getValue());
/*    */     
/* 26 */     map.put("RS256", SignAlgorithm.SHA256withRSA.getValue());
/* 27 */     map.put("RS384", SignAlgorithm.SHA384withRSA.getValue());
/* 28 */     map.put("RS512", SignAlgorithm.SHA512withRSA.getValue());
/*    */     
/* 30 */     map.put("ES256", SignAlgorithm.SHA256withECDSA.getValue());
/* 31 */     map.put("ES384", SignAlgorithm.SHA384withECDSA.getValue());
/* 32 */     map.put("ES512", SignAlgorithm.SHA512withECDSA.getValue());
/*    */     
/* 34 */     map.put("PS256", SignAlgorithm.SHA256withRSA_PSS.getValue());
/* 35 */     map.put("PS384", SignAlgorithm.SHA384withRSA_PSS.getValue());
/* 36 */     map.put("PS512", SignAlgorithm.SHA512withRSA_PSS.getValue());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getAlgorithm(String idOrAlgorithm) {
/* 45 */     return (String)ObjectUtil.defaultIfNull(getAlgorithmById(idOrAlgorithm), idOrAlgorithm);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getId(String idOrAlgorithm) {
/* 54 */     return (String)ObjectUtil.defaultIfNull(getIdByAlgorithm(idOrAlgorithm), idOrAlgorithm);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String getAlgorithmById(String id) {
/* 64 */     return (String)map.get(id.toUpperCase());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String getIdByAlgorithm(String algorithm) {
/* 74 */     return (String)map.getKey(algorithm);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\signers\AlgorithmUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */