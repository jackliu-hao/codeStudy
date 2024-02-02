/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import javax.servlet.ServletContextEvent;
/*     */ import javax.servlet.ServletContextListener;
/*     */ import org.apache.commons.logging.LogFactory;
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
/*     */ public class ServletContextCleaner
/*     */   implements ServletContextListener
/*     */ {
/*  52 */   private static final Class[] RELEASE_SIGNATURE = new Class[] { ClassLoader.class };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void contextDestroyed(ServletContextEvent sce) {
/*  60 */     ClassLoader tccl = Thread.currentThread().getContextClassLoader();
/*     */     
/*  62 */     Object[] params = new Object[1];
/*  63 */     params[0] = tccl;
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
/*  95 */     ClassLoader loader = tccl;
/*  96 */     while (loader != null) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 101 */         Class logFactoryClass = loader.loadClass("org.apache.commons.logging.LogFactory");
/* 102 */         Method releaseMethod = logFactoryClass.getMethod("release", RELEASE_SIGNATURE);
/* 103 */         releaseMethod.invoke(null, params);
/* 104 */         loader = logFactoryClass.getClassLoader().getParent();
/* 105 */       } catch (ClassNotFoundException ex) {
/*     */ 
/*     */         
/* 108 */         loader = null;
/* 109 */       } catch (NoSuchMethodException ex) {
/*     */         
/* 111 */         System.err.println("LogFactory instance found which does not support release method!");
/* 112 */         loader = null;
/* 113 */       } catch (IllegalAccessException ex) {
/*     */         
/* 115 */         System.err.println("LogFactory instance found which is not accessable!");
/* 116 */         loader = null;
/* 117 */       } catch (InvocationTargetException ex) {
/*     */         
/* 119 */         System.err.println("LogFactory instance release method failed!");
/* 120 */         loader = null;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     LogFactory.release(tccl);
/*     */   }
/*     */   
/*     */   public void contextInitialized(ServletContextEvent sce) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\logging\impl\ServletContextCleaner.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       1.1.3
 */