/*    */ package com.sun.jna;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
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
/*    */ abstract class Klass
/*    */ {
/*    */   public static <T> T newInstance(Class<T> klass) {
/*    */     try {
/* 48 */       return klass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/* 49 */     } catch (IllegalAccessException e) {
/* 50 */       String msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e;
/*    */       
/* 52 */       throw new IllegalArgumentException(msg, e);
/* 53 */     } catch (IllegalArgumentException e) {
/* 54 */       String msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e;
/*    */       
/* 56 */       throw new IllegalArgumentException(msg, e);
/* 57 */     } catch (InstantiationException e) {
/* 58 */       String msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e;
/*    */       
/* 60 */       throw new IllegalArgumentException(msg, e);
/* 61 */     } catch (NoSuchMethodException e) {
/* 62 */       String msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e;
/*    */       
/* 64 */       throw new IllegalArgumentException(msg, e);
/* 65 */     } catch (SecurityException e) {
/* 66 */       String msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e;
/*    */       
/* 68 */       throw new IllegalArgumentException(msg, e);
/* 69 */     } catch (InvocationTargetException e) {
/* 70 */       if (e.getCause() instanceof RuntimeException) {
/* 71 */         throw (RuntimeException)e.getCause();
/*    */       }
/* 73 */       String msg = "Can't create an instance of " + klass + ", requires a public no-arg constructor: " + e;
/*    */       
/* 75 */       throw new IllegalArgumentException(msg, e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Klass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */