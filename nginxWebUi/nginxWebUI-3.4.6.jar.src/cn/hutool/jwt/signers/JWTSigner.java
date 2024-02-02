/*    */ package cn.hutool.jwt.signers;
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
/*    */ public interface JWTSigner
/*    */ {
/*    */   String sign(String paramString1, String paramString2);
/*    */   
/*    */   boolean verify(String paramString1, String paramString2, String paramString3);
/*    */   
/*    */   String getAlgorithm();
/*    */   
/*    */   default String getAlgorithmId() {
/* 43 */     return AlgorithmUtil.getId(getAlgorithm());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\signers\JWTSigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */