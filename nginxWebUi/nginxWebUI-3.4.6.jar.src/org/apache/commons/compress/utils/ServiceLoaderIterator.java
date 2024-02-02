/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
/*    */ import java.util.ServiceConfigurationError;
/*    */ import java.util.ServiceLoader;
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
/*    */ public class ServiceLoaderIterator<E>
/*    */   implements Iterator<E>
/*    */ {
/*    */   private E nextServiceLoader;
/*    */   private final Class<E> service;
/*    */   private final Iterator<E> serviceLoaderIterator;
/*    */   
/*    */   public ServiceLoaderIterator(Class<E> service) {
/* 42 */     this(service, ClassLoader.getSystemClassLoader());
/*    */   }
/*    */   
/*    */   public ServiceLoaderIterator(Class<E> service, ClassLoader classLoader) {
/* 46 */     this.service = service;
/* 47 */     this.serviceLoaderIterator = ServiceLoader.<E>load(service, classLoader).iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 52 */     while (this.nextServiceLoader == null) {
/*    */       try {
/* 54 */         if (!this.serviceLoaderIterator.hasNext()) {
/* 55 */           return false;
/*    */         }
/* 57 */         this.nextServiceLoader = this.serviceLoaderIterator.next();
/* 58 */       } catch (ServiceConfigurationError e) {
/* 59 */         if (e.getCause() instanceof SecurityException) {
/*    */           continue;
/*    */         }
/*    */ 
/*    */         
/* 64 */         throw e;
/*    */       } 
/*    */     } 
/* 67 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public E next() {
/* 72 */     if (!hasNext()) {
/* 73 */       throw new NoSuchElementException("No more elements for service " + this.service.getName());
/*    */     }
/* 75 */     E tempNext = this.nextServiceLoader;
/* 76 */     this.nextServiceLoader = null;
/* 77 */     return tempNext;
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 82 */     throw new UnsupportedOperationException("service=" + this.service.getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\ServiceLoaderIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */