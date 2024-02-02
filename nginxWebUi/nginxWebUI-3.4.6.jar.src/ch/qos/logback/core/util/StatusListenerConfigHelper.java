/*    */ package ch.qos.logback.core.util;
/*    */ 
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.spi.ContextAware;
/*    */ import ch.qos.logback.core.spi.LifeCycle;
/*    */ import ch.qos.logback.core.status.OnConsoleStatusListener;
/*    */ import ch.qos.logback.core.status.StatusListener;
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
/*    */ public class StatusListenerConfigHelper
/*    */ {
/*    */   public static void installIfAsked(Context context) {
/* 26 */     String slClass = OptionHelper.getSystemProperty("logback.statusListenerClass");
/* 27 */     if (!OptionHelper.isEmpty(slClass)) {
/* 28 */       addStatusListener(context, slClass);
/*    */     }
/*    */   }
/*    */   
/*    */   private static void addStatusListener(Context context, String listenerClassName) {
/* 33 */     StatusListener listener = null;
/* 34 */     if ("SYSOUT".equalsIgnoreCase(listenerClassName)) {
/* 35 */       OnConsoleStatusListener onConsoleStatusListener = new OnConsoleStatusListener();
/*    */     } else {
/* 37 */       listener = createListenerPerClassName(context, listenerClassName);
/*    */     } 
/* 39 */     initAndAddListener(context, listener);
/*    */   }
/*    */   
/*    */   private static void initAndAddListener(Context context, StatusListener listener) {
/* 43 */     if (listener != null) {
/* 44 */       if (listener instanceof ContextAware) {
/* 45 */         ((ContextAware)listener).setContext(context);
/*    */       }
/* 47 */       boolean effectivelyAdded = context.getStatusManager().add(listener);
/* 48 */       if (effectivelyAdded && listener instanceof LifeCycle) {
/* 49 */         ((LifeCycle)listener).start();
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   private static StatusListener createListenerPerClassName(Context context, String listenerClass) {
/*    */     try {
/* 56 */       return (StatusListener)OptionHelper.instantiateByClassName(listenerClass, StatusListener.class, context);
/* 57 */     } catch (Exception e) {
/*    */       
/* 59 */       e.printStackTrace();
/* 60 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void addOnConsoleListenerInstance(Context context, OnConsoleStatusListener onConsoleStatusListener) {
/* 72 */     onConsoleStatusListener.setContext(context);
/* 73 */     boolean effectivelyAdded = context.getStatusManager().add((StatusListener)onConsoleStatusListener);
/* 74 */     if (effectivelyAdded)
/* 75 */       onConsoleStatusListener.start(); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\StatusListenerConfigHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */