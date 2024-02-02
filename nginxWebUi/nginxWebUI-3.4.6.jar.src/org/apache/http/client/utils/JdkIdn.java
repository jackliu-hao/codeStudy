/*    */ package org.apache.http.client.utils;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class JdkIdn
/*    */   implements Idn
/*    */ {
/*    */   private final Method toUnicode;
/*    */   
/*    */   public JdkIdn() throws ClassNotFoundException {
/* 52 */     Class<?> clazz = Class.forName("java.net.IDN");
/*    */     try {
/* 54 */       this.toUnicode = clazz.getMethod("toUnicode", new Class[] { String.class });
/* 55 */     } catch (SecurityException e) {
/*    */       
/* 57 */       throw new IllegalStateException(e.getMessage(), e);
/* 58 */     } catch (NoSuchMethodException e) {
/*    */       
/* 60 */       throw new IllegalStateException(e.getMessage(), e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toUnicode(String punycode) {
/*    */     try {
/* 67 */       return (String)this.toUnicode.invoke(null, new Object[] { punycode });
/* 68 */     } catch (IllegalAccessException e) {
/* 69 */       throw new IllegalStateException(e.getMessage(), e);
/* 70 */     } catch (InvocationTargetException e) {
/* 71 */       Throwable t = e.getCause();
/* 72 */       throw new RuntimeException(t.getMessage(), t);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\clien\\utils\JdkIdn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */