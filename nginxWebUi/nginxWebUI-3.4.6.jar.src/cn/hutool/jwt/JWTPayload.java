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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JWTPayload
/*    */   extends Claims
/*    */   implements RegisteredPayload<JWTPayload>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public JWTPayload addPayloads(Map<String, ?> payloadClaims) {
/* 30 */     putAll(payloadClaims);
/* 31 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public JWTPayload setPayload(String name, Object value) {
/* 36 */     setClaim(name, value);
/* 37 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\JWTPayload.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */