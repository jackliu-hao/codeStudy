/*     */ package com.mysql.cj.jdbc;
/*     */ 
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.util.Util;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
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
/*     */ abstract class WrapperBase
/*     */ {
/*     */   protected MysqlPooledConnection pooledConnection;
/*     */   
/*     */   protected void checkAndFireConnectionError(SQLException sqlEx) throws SQLException {
/*  58 */     if (this.pooledConnection != null && 
/*  59 */       "08S01".equals(sqlEx.getSQLState())) {
/*  60 */       this.pooledConnection.callConnectionEventListeners(1, sqlEx);
/*     */     }
/*     */ 
/*     */     
/*  64 */     throw sqlEx;
/*     */   }
/*     */   
/*  67 */   protected Map<Class<?>, Object> unwrappedInterfaces = null;
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   protected WrapperBase(MysqlPooledConnection pooledConnection) {
/*  71 */     this.pooledConnection = pooledConnection;
/*  72 */     this.exceptionInterceptor = this.pooledConnection.getExceptionInterceptor();
/*     */   }
/*     */   
/*     */   protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler {
/*  76 */     Object invokeOn = null;
/*     */     
/*     */     public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
/*  79 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  83 */       if ("equals".equals(method.getName()))
/*     */       {
/*  85 */         return Boolean.valueOf(args[0].equals(this));
/*     */       }
/*     */       
/*  88 */       Object result = null;
/*     */       
/*     */       try {
/*  91 */         result = method.invoke(this.invokeOn, args);
/*     */         
/*  93 */         if (result != null) {
/*  94 */           result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */         }
/*  96 */       } catch (InvocationTargetException e) {
/*  97 */         if (e.getTargetException() instanceof SQLException) {
/*  98 */           WrapperBase.this.checkAndFireConnectionError((SQLException)e.getTargetException());
/*     */         } else {
/* 100 */           throw e;
/*     */         } 
/*     */       } 
/*     */       
/* 104 */       return result;
/*     */     }
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
/*     */     private Object proxyIfInterfaceIsJdbc(Object toProxy, Class<?> clazz) {
/* 119 */       Class<?>[] interfaces = clazz.getInterfaces();
/*     */       
/* 121 */       Class<?>[] arrayOfClass1 = interfaces; int i = arrayOfClass1.length; byte b = 0; if (b < i) { Class<?> iclass = arrayOfClass1[b];
/* 122 */         String packageName = Util.getPackageName(iclass);
/*     */         
/* 124 */         if ("java.sql".equals(packageName) || "javax.sql".equals(packageName)) {
/* 125 */           return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(toProxy));
/*     */         }
/*     */         
/* 128 */         return proxyIfInterfaceIsJdbc(toProxy, iclass); }
/*     */ 
/*     */       
/* 131 */       return toProxy;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\WrapperBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */