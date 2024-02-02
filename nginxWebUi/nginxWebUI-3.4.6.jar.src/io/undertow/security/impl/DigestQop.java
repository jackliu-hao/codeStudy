/*    */ package io.undertow.security.impl;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum DigestQop
/*    */ {
/* 31 */   AUTH("auth", false), AUTH_INT("auth-int", true); private final boolean integrity;
/*    */   private final String token;
/*    */   private static final Map<String, DigestQop> BY_TOKEN;
/*    */   
/*    */   static {
/* 36 */     DigestQop[] qops = values();
/*    */     
/* 38 */     Map<String, DigestQop> byToken = new HashMap<>(qops.length);
/* 39 */     for (DigestQop current : qops) {
/* 40 */       byToken.put(current.token, current);
/*    */     }
/*    */     
/* 43 */     BY_TOKEN = Collections.unmodifiableMap(byToken);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   DigestQop(String token, boolean integrity) {
/* 50 */     this.token = token;
/* 51 */     this.integrity = integrity;
/*    */   }
/*    */   
/*    */   public String getToken() {
/* 55 */     return this.token;
/*    */   }
/*    */   
/*    */   public boolean isMessageIntegrity() {
/* 59 */     return this.integrity;
/*    */   }
/*    */   
/*    */   public static DigestQop forName(String name) {
/* 63 */     return BY_TOKEN.get(name);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\DigestQop.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */