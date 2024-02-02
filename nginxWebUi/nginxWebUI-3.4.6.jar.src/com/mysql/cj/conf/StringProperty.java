/*    */ package com.mysql.cj.conf;
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
/*    */ public class StringProperty
/*    */   extends AbstractRuntimeProperty<String>
/*    */ {
/*    */   private static final long serialVersionUID = -4141084145739428803L;
/*    */   
/*    */   protected StringProperty(PropertyDefinition<String> propertyDefinition) {
/* 37 */     super(propertyDefinition);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getStringValue() {
/* 42 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\StringProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */