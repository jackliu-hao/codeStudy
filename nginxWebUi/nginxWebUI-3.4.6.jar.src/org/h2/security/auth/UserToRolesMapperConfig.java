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
/*    */ public class UserToRolesMapperConfig
/*    */   implements HasConfigProperties
/*    */ {
/*    */   private String className;
/*    */   private List<PropertyConfig> properties;
/*    */   
/*    */   public String getClassName() {
/* 25 */     return this.className;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setClassName(String paramString) {
/* 32 */     this.className = paramString;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<PropertyConfig> getProperties() {
/* 40 */     if (this.properties == null) {
/* 41 */       this.properties = new ArrayList<>();
/*    */     }
/* 43 */     return this.properties;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\UserToRolesMapperConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */