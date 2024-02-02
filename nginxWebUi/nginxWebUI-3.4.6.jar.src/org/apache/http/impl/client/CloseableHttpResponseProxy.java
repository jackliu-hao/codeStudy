/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.util.EntityUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ class CloseableHttpResponseProxy
/*     */   implements InvocationHandler
/*     */ {
/*     */   private static final Constructor<?> CONSTRUCTOR;
/*     */   private final HttpResponse original;
/*     */   
/*     */   static {
/*     */     try {
/*  52 */       CONSTRUCTOR = Proxy.getProxyClass(CloseableHttpResponseProxy.class.getClassLoader(), new Class[] { CloseableHttpResponse.class }).getConstructor(new Class[] { InvocationHandler.class });
/*     */     }
/*  54 */     catch (NoSuchMethodException ex) {
/*  55 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   CloseableHttpResponseProxy(HttpResponse original) {
/*  63 */     this.original = original;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*  67 */     HttpEntity entity = this.original.getEntity();
/*  68 */     EntityUtils.consume(entity);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  74 */     String mname = method.getName();
/*  75 */     if (mname.equals("close")) {
/*  76 */       close();
/*  77 */       return null;
/*     */     } 
/*     */     try {
/*  80 */       return method.invoke(this.original, args);
/*  81 */     } catch (InvocationTargetException ex) {
/*  82 */       Throwable cause = ex.getCause();
/*  83 */       if (cause != null) {
/*  84 */         throw cause;
/*     */       }
/*  86 */       throw ex;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static CloseableHttpResponse newProxy(HttpResponse original) {
/*     */     try {
/*  94 */       return (CloseableHttpResponse)CONSTRUCTOR.newInstance(new Object[] { new CloseableHttpResponseProxy(original) });
/*  95 */     } catch (InstantiationException ex) {
/*  96 */       throw new IllegalStateException(ex);
/*  97 */     } catch (InvocationTargetException ex) {
/*  98 */       throw new IllegalStateException(ex);
/*  99 */     } catch (IllegalAccessException ex) {
/* 100 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\CloseableHttpResponseProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */