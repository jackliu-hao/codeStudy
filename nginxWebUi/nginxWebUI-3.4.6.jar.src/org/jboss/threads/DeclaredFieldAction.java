/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import java.security.PrivilegedAction;
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
/*    */ final class DeclaredFieldAction
/*    */   implements PrivilegedAction<Field>
/*    */ {
/*    */   private final Class<?> clazz;
/*    */   private final String fieldName;
/*    */   
/*    */   DeclaredFieldAction(Class<?> clazz, String fieldName) {
/* 31 */     this.clazz = clazz;
/* 32 */     this.fieldName = fieldName;
/*    */   }
/*    */   
/*    */   public Field run() {
/*    */     try {
/* 37 */       return this.clazz.getDeclaredField(this.fieldName);
/* 38 */     } catch (NoSuchFieldException e) {
/* 39 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\DeclaredFieldAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */