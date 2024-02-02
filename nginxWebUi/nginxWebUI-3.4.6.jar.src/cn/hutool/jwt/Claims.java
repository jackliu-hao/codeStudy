/*    */ package cn.hutool.jwt;
/*    */ 
/*    */ import cn.hutool.core.codec.Base64;
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.json.JSONConfig;
/*    */ import cn.hutool.json.JSONObject;
/*    */ import cn.hutool.json.JSONUtil;
/*    */ import java.io.Serializable;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Claims
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 24 */   private final JSONConfig CONFIG = JSONConfig.create().setDateFormat("#sss");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private JSONObject claimJSON;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void setClaim(String name, Object value) {
/* 35 */     init();
/* 36 */     Assert.notNull(name, "Name must be not null!", new Object[0]);
/* 37 */     if (value == null) {
/* 38 */       this.claimJSON.remove(name);
/*    */       return;
/*    */     } 
/* 41 */     this.claimJSON.set(name, value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void putAll(Map<String, ?> headerClaims) {
/* 49 */     if (MapUtil.isNotEmpty(headerClaims)) {
/* 50 */       for (Map.Entry<String, ?> entry : headerClaims.entrySet()) {
/* 51 */         setClaim(entry.getKey(), entry.getValue());
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getClaim(String name) {
/* 63 */     init();
/* 64 */     return this.claimJSON.getObj(name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JSONObject getClaimsJson() {
/* 73 */     init();
/* 74 */     return this.claimJSON;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void parse(String tokenPart, Charset charset) {
/* 84 */     this.claimJSON = JSONUtil.parseObj(Base64.decodeStr(tokenPart, charset), this.CONFIG);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 89 */     init();
/* 90 */     return this.claimJSON.toString();
/*    */   }
/*    */   
/*    */   private void init() {
/* 94 */     if (null == this.claimJSON)
/* 95 */       this.claimJSON = new JSONObject(this.CONFIG); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\jwt\Claims.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */