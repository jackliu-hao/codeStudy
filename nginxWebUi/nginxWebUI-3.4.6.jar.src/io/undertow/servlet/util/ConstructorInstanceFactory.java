/*    */ package io.undertow.servlet.util;
/*    */ 
/*    */ import io.undertow.servlet.api.InstanceFactory;
/*    */ import io.undertow.servlet.api.InstanceHandle;
/*    */ import java.lang.reflect.Constructor;
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
/*    */ public class ConstructorInstanceFactory<T>
/*    */   implements InstanceFactory<T>
/*    */ {
/*    */   private final Constructor<T> constructor;
/*    */   
/*    */   public ConstructorInstanceFactory(Constructor<T> constructor) {
/* 35 */     constructor.setAccessible(true);
/* 36 */     this.constructor = constructor;
/*    */   }
/*    */ 
/*    */   
/*    */   public InstanceHandle<T> createInstance() throws InstantiationException {
/*    */     try {
/* 42 */       T instance = this.constructor.newInstance(new Object[0]);
/* 43 */       return new ImmediateInstanceHandle<>(instance);
/* 44 */     } catch (IllegalAccessException e) {
/* 45 */       InstantiationException ite = new InstantiationException();
/* 46 */       ite.initCause(e);
/* 47 */       throw ite;
/* 48 */     } catch (InvocationTargetException e) {
/* 49 */       InstantiationException ite = new InstantiationException();
/* 50 */       ite.initCause(e);
/* 51 */       throw ite;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\ConstructorInstanceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */