/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import io.undertow.servlet.UndertowServletMessages;
/*    */ import io.undertow.servlet.core.ApplicationListeners;
/*    */ import io.undertow.servlet.util.ConstructorInstanceFactory;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.util.EventListener;
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
/*    */ public class ListenerInfo
/*    */ {
/*    */   private final Class<? extends EventListener> listenerClass;
/*    */   private volatile InstanceFactory<? extends EventListener> instanceFactory;
/*    */   private final boolean programatic;
/*    */   
/*    */   public ListenerInfo(Class<? extends EventListener> listenerClass, InstanceFactory<? extends EventListener> instanceFactory) {
/* 39 */     this(listenerClass, instanceFactory, false);
/*    */   }
/*    */   
/*    */   public ListenerInfo(Class<? extends EventListener> listenerClass, InstanceFactory<? extends EventListener> instanceFactory, boolean programatic) {
/* 43 */     this.listenerClass = listenerClass;
/* 44 */     this.instanceFactory = instanceFactory;
/* 45 */     this.programatic = programatic;
/* 46 */     if (!ApplicationListeners.isListenerClass(listenerClass)) {
/* 47 */       throw UndertowServletMessages.MESSAGES.listenerMustImplementListenerClass(listenerClass);
/*    */     }
/*    */   }
/*    */   
/*    */   public ListenerInfo(Class<? extends EventListener> listenerClass) {
/* 52 */     this(listenerClass, false);
/*    */   }
/*    */   
/*    */   public ListenerInfo(Class<? extends EventListener> listenerClass, boolean programatic) {
/* 56 */     this.listenerClass = listenerClass;
/* 57 */     this.programatic = programatic;
/*    */     
/*    */     try {
/* 60 */       Constructor<EventListener> ctor = (Constructor)listenerClass.getDeclaredConstructor(new Class[0]);
/* 61 */       ctor.setAccessible(true);
/* 62 */       this.instanceFactory = (InstanceFactory<? extends EventListener>)new ConstructorInstanceFactory(ctor);
/* 63 */     } catch (NoSuchMethodException e) {
/* 64 */       throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("Listener", listenerClass);
/*    */     } 
/*    */   }
/*    */   
/*    */   public InstanceFactory<? extends EventListener> getInstanceFactory() {
/* 69 */     return this.instanceFactory;
/*    */   }
/*    */   
/*    */   public void setInstanceFactory(InstanceFactory<? extends EventListener> instanceFactory) {
/* 73 */     this.instanceFactory = instanceFactory;
/*    */   }
/*    */   
/*    */   public boolean isProgramatic() {
/* 77 */     return this.programatic;
/*    */   }
/*    */   
/*    */   public Class<? extends EventListener> getListenerClass() {
/* 81 */     return this.listenerClass;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 86 */     return "ListenerInfo{listenerClass=" + this.listenerClass + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ListenerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */