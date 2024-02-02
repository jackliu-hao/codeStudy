/*    */ package com.google.protobuf;
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
/*    */ final class NewInstanceSchemas
/*    */ {
/* 34 */   private static final NewInstanceSchema FULL_SCHEMA = loadSchemaForFullRuntime();
/* 35 */   private static final NewInstanceSchema LITE_SCHEMA = new NewInstanceSchemaLite();
/*    */   
/*    */   static NewInstanceSchema full() {
/* 38 */     return FULL_SCHEMA;
/*    */   }
/*    */   
/*    */   static NewInstanceSchema lite() {
/* 42 */     return LITE_SCHEMA;
/*    */   }
/*    */   
/*    */   private static NewInstanceSchema loadSchemaForFullRuntime() {
/*    */     try {
/* 47 */       Class<?> clazz = Class.forName("com.google.protobuf.NewInstanceSchemaFull");
/* 48 */       return clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 49 */     } catch (Exception e) {
/* 50 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\NewInstanceSchemas.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */