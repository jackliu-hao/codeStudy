/*    */ package io.undertow.servlet.util;
/*    */ 
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
/*    */ public class ImmediateInstanceHandle<T>
/*    */   implements InstanceHandle<T>
/*    */ {
/*    */   private final T instance;
/*    */   
/*    */   public ImmediateInstanceHandle(T instance) {
/* 31 */     this.instance = instance;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getInstance() {
/* 36 */     return this.instance;
/*    */   }
/*    */   
/*    */   public void release() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\ImmediateInstanceHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */