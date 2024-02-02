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
/*    */ final class ExtensionRegistryFactory
/*    */ {
/*    */   static final String FULL_REGISTRY_CLASS_NAME = "com.google.protobuf.ExtensionRegistry";
/* 47 */   static final Class<?> EXTENSION_REGISTRY_CLASS = reflectExtensionRegistry();
/*    */ 
/*    */   
/*    */   static Class<?> reflectExtensionRegistry() {
/*    */     try {
/* 52 */       return Class.forName("com.google.protobuf.ExtensionRegistry");
/* 53 */     } catch (ClassNotFoundException e) {
/*    */ 
/*    */       
/* 56 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static ExtensionRegistryLite create() {
/* 62 */     ExtensionRegistryLite result = invokeSubclassFactory("newInstance");
/*    */     
/* 64 */     return (result != null) ? result : new ExtensionRegistryLite();
/*    */   }
/*    */ 
/*    */   
/*    */   public static ExtensionRegistryLite createEmpty() {
/* 69 */     ExtensionRegistryLite result = invokeSubclassFactory("getEmptyRegistry");
/*    */     
/* 71 */     return (result != null) ? result : ExtensionRegistryLite.EMPTY_REGISTRY_LITE;
/*    */   }
/*    */ 
/*    */   
/*    */   static boolean isFullRegistry(ExtensionRegistryLite registry) {
/* 76 */     return (EXTENSION_REGISTRY_CLASS != null && EXTENSION_REGISTRY_CLASS
/* 77 */       .isAssignableFrom(registry.getClass()));
/*    */   }
/*    */ 
/*    */   
/*    */   private static final ExtensionRegistryLite invokeSubclassFactory(String methodName) {
/* 82 */     if (EXTENSION_REGISTRY_CLASS == null) {
/* 83 */       return null;
/*    */     }
/*    */     
/*    */     try {
/* 87 */       return (ExtensionRegistryLite)EXTENSION_REGISTRY_CLASS
/* 88 */         .getDeclaredMethod(methodName, new Class[0]).invoke(null, new Object[0]);
/* 89 */     } catch (Exception e) {
/* 90 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ExtensionRegistryFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */