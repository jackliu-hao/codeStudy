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
/*    */ final class MapFieldSchemas
/*    */ {
/* 34 */   private static final MapFieldSchema FULL_SCHEMA = loadSchemaForFullRuntime();
/* 35 */   private static final MapFieldSchema LITE_SCHEMA = new MapFieldSchemaLite();
/*    */   
/*    */   static MapFieldSchema full() {
/* 38 */     return FULL_SCHEMA;
/*    */   }
/*    */   
/*    */   static MapFieldSchema lite() {
/* 42 */     return LITE_SCHEMA;
/*    */   }
/*    */   
/*    */   private static MapFieldSchema loadSchemaForFullRuntime() {
/*    */     try {
/* 47 */       Class<?> clazz = Class.forName("com.google.protobuf.MapFieldSchemaFull");
/* 48 */       return clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 49 */     } catch (Exception e) {
/* 50 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MapFieldSchemas.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */