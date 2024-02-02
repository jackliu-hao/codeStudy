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
/*    */ public class EnumProperty<T extends Enum<T>>
/*    */   extends AbstractRuntimeProperty<T>
/*    */ {
/*    */   private static final long serialVersionUID = -60853080911910124L;
/*    */   
/*    */   protected EnumProperty(PropertyDefinition<T> propertyDefinition) {
/* 37 */     super(propertyDefinition);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\EnumProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */