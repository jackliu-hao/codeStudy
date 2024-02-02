/*    */ package io.undertow.servlet.api;
/*    */ 
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
/*    */ public class AuthMethodConfig
/*    */   implements Cloneable
/*    */ {
/*    */   private final String name;
/*    */   private final Map<String, String> properties;
/*    */   
/*    */   public AuthMethodConfig(String name, Map<String, String> properties) {
/* 33 */     this.name = name;
/* 34 */     this.properties = new HashMap<>(properties);
/*    */   }
/*    */   
/*    */   public AuthMethodConfig(String name) {
/* 38 */     this.name = name;
/* 39 */     this.properties = new HashMap<>();
/*    */   }
/*    */   
/*    */   public String getName() {
/* 43 */     return this.name;
/*    */   }
/*    */   
/*    */   public Map<String, String> getProperties() {
/* 47 */     return this.properties;
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthMethodConfig clone() {
/* 52 */     return new AuthMethodConfig(this.name, this.properties);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\AuthMethodConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */