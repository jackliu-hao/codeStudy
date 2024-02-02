/*    */ package org.apache.http.client.utils;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
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
/*    */ public class CloneUtils
/*    */ {
/*    */   public static <T> T cloneObject(T obj) throws CloneNotSupportedException {
/* 43 */     if (obj == null) {
/* 44 */       return null;
/*    */     }
/* 46 */     if (obj instanceof Cloneable) {
/* 47 */       Method m; Class<?> clazz = obj.getClass();
/*    */       
/*    */       try {
/* 50 */         m = clazz.getMethod("clone", (Class[])null);
/* 51 */       } catch (NoSuchMethodException ex) {
/* 52 */         throw new NoSuchMethodError(ex.getMessage());
/*    */       } 
/*    */       
/*    */       try {
/* 56 */         T result = (T)m.invoke(obj, (Object[])null);
/* 57 */         return result;
/* 58 */       } catch (InvocationTargetException ex) {
/* 59 */         Throwable cause = ex.getCause();
/* 60 */         if (cause instanceof CloneNotSupportedException) {
/* 61 */           throw (CloneNotSupportedException)cause;
/*    */         }
/* 63 */         throw new Error("Unexpected exception", cause);
/* 64 */       } catch (IllegalAccessException ex) {
/* 65 */         throw new IllegalAccessError(ex.getMessage());
/*    */       } 
/*    */     } 
/* 68 */     throw new CloneNotSupportedException();
/*    */   }
/*    */   
/*    */   public static Object clone(Object obj) throws CloneNotSupportedException {
/* 72 */     return cloneObject(obj);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\CloneUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */