/*    */ package cn.hutool.jwt.signers;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoneJWTSigner
/*    */   implements JWTSigner
/*    */ {
/*    */   public static final String ID_NONE = "none";
/* 15 */   public static NoneJWTSigner NONE = new NoneJWTSigner();
/*    */ 
/*    */   
/*    */   public String sign(String headerBase64, String payloadBase64) {
/* 19 */     return "";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean verify(String headerBase64, String payloadBase64, String signBase64) {
/* 24 */     return StrUtil.isEmpty(signBase64);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAlgorithm() {
/* 29 */     return "none";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\signers\NoneJWTSigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */