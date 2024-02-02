/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.servlet.UndertowServletMessages;
/*    */ import io.undertow.servlet.api.InstanceHandle;
/*    */ import io.undertow.servlet.api.ListenerInfo;
/*    */ import java.util.EventListener;
/*    */ import javax.servlet.ServletException;
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
/*    */ public class ManagedListener
/*    */   implements Lifecycle
/*    */ {
/*    */   private final ListenerInfo listenerInfo;
/*    */   private final boolean programatic;
/*    */   private volatile boolean started = false;
/*    */   private volatile InstanceHandle<? extends EventListener> handle;
/*    */   
/*    */   public ManagedListener(ListenerInfo listenerInfo, boolean programatic) {
/* 41 */     this.listenerInfo = listenerInfo;
/* 42 */     this.programatic = programatic;
/*    */   }
/*    */   
/*    */   public synchronized void start() throws ServletException {
/* 46 */     if (!this.started) {
/*    */       try {
/* 48 */         this.handle = this.listenerInfo.getInstanceFactory().createInstance();
/* 49 */       } catch (Exception e) {
/* 50 */         throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.listenerInfo.getListenerClass().getName(), e);
/*    */       } 
/* 52 */       this.started = true;
/*    */     } 
/*    */   }
/*    */   
/*    */   public synchronized void stop() {
/* 57 */     this.started = false;
/* 58 */     if (this.handle != null) {
/* 59 */       this.handle.release();
/*    */     }
/*    */   }
/*    */   
/*    */   public ListenerInfo getListenerInfo() {
/* 64 */     return this.listenerInfo;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isStarted() {
/* 69 */     return this.started;
/*    */   }
/*    */   
/*    */   public EventListener instance() {
/* 73 */     if (!this.started) {
/* 74 */       throw UndertowServletMessages.MESSAGES.listenerIsNotStarted();
/*    */     }
/* 76 */     return (EventListener)this.handle.getInstance();
/*    */   }
/*    */   
/*    */   public boolean isProgramatic() {
/* 80 */     return this.programatic;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 85 */     return "ManagedListener{listenerInfo=" + this.listenerInfo + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ManagedListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */