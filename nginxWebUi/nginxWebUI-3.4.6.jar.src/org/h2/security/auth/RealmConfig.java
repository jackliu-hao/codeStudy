/*    */ package org.h2.security.auth;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class RealmConfig
/*    */   implements HasConfigProperties
/*    */ {
/*    */   private String name;
/*    */   private String validatorClass;
/*    */   private List<PropertyConfig> properties;
/*    */   
/*    */   public String getName() {
/* 26 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setName(String paramString) {
/* 35 */     this.name = paramString;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValidatorClass() {
/* 44 */     return this.validatorClass;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setValidatorClass(String paramString) {
/* 53 */     this.validatorClass = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<PropertyConfig> getProperties() {
/* 58 */     if (this.properties == null) {
/* 59 */       this.properties = new ArrayList<>();
/*    */     }
/* 61 */     return this.properties;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\RealmConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */