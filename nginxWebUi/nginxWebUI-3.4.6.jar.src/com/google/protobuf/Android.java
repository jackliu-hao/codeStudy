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
/*    */ final class Android
/*    */ {
/* 35 */   private static final Class<?> MEMORY_CLASS = getClassForName("libcore.io.Memory");
/*    */   
/* 37 */   private static final boolean IS_ROBOLECTRIC = (getClassForName("org.robolectric.Robolectric") != null);
/*    */ 
/*    */   
/*    */   static boolean isOnAndroidDevice() {
/* 41 */     return (MEMORY_CLASS != null && !IS_ROBOLECTRIC);
/*    */   }
/*    */ 
/*    */   
/*    */   static Class<?> getMemoryClass() {
/* 46 */     return MEMORY_CLASS;
/*    */   }
/*    */ 
/*    */   
/*    */   private static <T> Class<T> getClassForName(String name) {
/*    */     try {
/* 52 */       return (Class)Class.forName(name);
/* 53 */     } catch (Throwable e) {
/* 54 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Android.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */