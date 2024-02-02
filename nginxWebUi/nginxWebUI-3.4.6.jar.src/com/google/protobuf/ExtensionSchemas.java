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
/*    */ final class ExtensionSchemas
/*    */ {
/* 34 */   private static final ExtensionSchema<?> LITE_SCHEMA = new ExtensionSchemaLite();
/* 35 */   private static final ExtensionSchema<?> FULL_SCHEMA = loadSchemaForFullRuntime();
/*    */   
/*    */   private static ExtensionSchema<?> loadSchemaForFullRuntime() {
/*    */     try {
/* 39 */       Class<?> clazz = Class.forName("com.google.protobuf.ExtensionSchemaFull");
/* 40 */       return clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 41 */     } catch (Exception e) {
/* 42 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   static ExtensionSchema<?> lite() {
/* 47 */     return LITE_SCHEMA;
/*    */   }
/*    */   
/*    */   static ExtensionSchema<?> full() {
/* 51 */     if (FULL_SCHEMA == null) {
/* 52 */       throw new IllegalStateException("Protobuf runtime is not correctly loaded.");
/*    */     }
/* 54 */     return FULL_SCHEMA;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ExtensionSchemas.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */