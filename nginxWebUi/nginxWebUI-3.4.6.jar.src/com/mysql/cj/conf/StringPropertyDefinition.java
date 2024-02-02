/*    */ package com.mysql.cj.conf;
/*    */ 
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
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
/*    */ public class StringPropertyDefinition
/*    */   extends AbstractPropertyDefinition<String>
/*    */ {
/*    */   private static final long serialVersionUID = 8228934389127796555L;
/*    */   
/*    */   public StringPropertyDefinition(String name, String alias, String defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/* 40 */     super(name, alias, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/*    */   }
/*    */ 
/*    */   
/*    */   public StringPropertyDefinition(PropertyKey key, String defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/* 45 */     super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/*    */   }
/*    */ 
/*    */   
/*    */   public String parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
/* 50 */     return value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RuntimeProperty<String> createRuntimeProperty() {
/* 60 */     return new StringProperty(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\StringPropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */