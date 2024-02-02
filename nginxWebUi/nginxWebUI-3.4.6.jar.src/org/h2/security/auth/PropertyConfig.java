/*    */ package org.h2.security.auth;
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
/*    */ public class PropertyConfig
/*    */ {
/*    */   private String name;
/*    */   private String value;
/*    */   
/*    */   public PropertyConfig() {}
/*    */   
/*    */   public PropertyConfig(String paramString1, String paramString2) {
/* 21 */     this.name = paramString1;
/* 22 */     this.value = paramString2;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 26 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String paramString) {
/* 30 */     this.name = paramString;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 34 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(String paramString) {
/* 38 */     this.value = paramString;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\security\auth\PropertyConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */