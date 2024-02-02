/*    */ package cn.hutool.jwt;
/*    */ 
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
/*    */ public class JWTHeader
/*    */   extends Claims
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 18 */   public static String ALGORITHM = "alg";
/*    */ 
/*    */ 
/*    */   
/* 22 */   public static String TYPE = "typ";
/*    */ 
/*    */ 
/*    */   
/* 26 */   public static String CONTENT_TYPE = "cty";
/*    */ 
/*    */ 
/*    */   
/* 30 */   public static String KEY_ID = "kid";
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JWTHeader() {
/* 36 */     setClaim(TYPE, "JWT");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JWTHeader setKeyId(String keyId) {
/* 46 */     setClaim(KEY_ID, keyId);
/* 47 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JWTHeader addHeaders(Map<String, ?> headerClaims) {
/* 57 */     putAll(headerClaims);
/* 58 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\JWTHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */