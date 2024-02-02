/*    */ package cn.hutool.jwt;
/*    */ 
/*    */ import cn.hutool.jwt.signers.JWTSigner;
/*    */ import java.util.Map;
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
/*    */ public class JWTUtil
/*    */ {
/*    */   public static String createToken(Map<String, Object> payload, byte[] key) {
/* 20 */     return createToken((Map<String, Object>)null, payload, key);
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
/*    */   public static String createToken(Map<String, Object> headers, Map<String, Object> payload, byte[] key) {
/* 32 */     return JWT.create()
/* 33 */       .addHeaders(headers)
/* 34 */       .addPayloads(payload)
/* 35 */       .setKey(key)
/* 36 */       .sign();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String createToken(Map<String, Object> payload, JWTSigner signer) {
/* 47 */     return createToken((Map<String, Object>)null, payload, signer);
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
/*    */   public static String createToken(Map<String, Object> headers, Map<String, Object> payload, JWTSigner signer) {
/* 59 */     return JWT.create()
/* 60 */       .addHeaders(headers)
/* 61 */       .addPayloads(payload)
/* 62 */       .setSigner(signer)
/* 63 */       .sign();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static JWT parseToken(String token) {
/* 73 */     return JWT.of(token);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean verify(String token, byte[] key) {
/* 84 */     return JWT.of(token).setKey(key).verify();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean verify(String token, JWTSigner signer) {
/* 95 */     return JWT.of(token).verify(signer);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\JWTUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */