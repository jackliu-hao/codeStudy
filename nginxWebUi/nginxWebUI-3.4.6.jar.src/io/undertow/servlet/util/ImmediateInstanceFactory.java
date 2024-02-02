/*    */ package io.undertow.servlet.util;
/*    */ 
/*    */ import io.undertow.servlet.api.InstanceFactory;
/*    */ import io.undertow.servlet.api.InstanceHandle;
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
/*    */ public class ImmediateInstanceFactory<T>
/*    */   implements InstanceFactory<T>
/*    */ {
/*    */   private final T instance;
/*    */   
/*    */   public ImmediateInstanceFactory(T instance) {
/* 32 */     this.instance = instance;
/*    */   }
/*    */ 
/*    */   
/*    */   public InstanceHandle<T> createInstance() throws InstantiationException {
/* 37 */     return new ImmediateInstanceHandle<>(this.instance);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\ImmediateInstanceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */