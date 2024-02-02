/*    */ package org.noear.solon;
/*    */ 
/*    */ import org.noear.solon.annotation.Note;
/*    */ import org.noear.solon.core.NvMap;
/*    */ import org.noear.solon.core.event.AppInitEndEvent;
/*    */ import org.noear.solon.core.event.AppLoadEndEvent;
/*    */ import org.noear.solon.core.event.BeanLoadEndEvent;
/*    */ import org.noear.solon.core.event.EventBus;
/*    */ import org.noear.solon.core.event.EventListener;
/*    */ import org.noear.solon.core.event.PluginLoadEndEvent;
/*    */ import org.noear.solon.ext.ConsumerEx;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SolonBuilder
/*    */ {
/*    */   public <T> SolonBuilder onEvent(Class<T> type, EventListener<T> handler) {
/* 20 */     EventBus.subscribe(type, handler);
/* 21 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SolonBuilder onError(EventListener<Throwable> handler) {
/* 28 */     return onEvent(Throwable.class, handler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("1.")
/*    */   public SolonBuilder onAppInitEnd(EventListener<AppInitEndEvent> handler) {
/* 36 */     return onEvent(AppInitEndEvent.class, handler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("2.")
/*    */   public SolonBuilder onPluginLoadEnd(EventListener<PluginLoadEndEvent> handler) {
/* 44 */     return onEvent(PluginLoadEndEvent.class, handler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("3.")
/*    */   public SolonBuilder onBeanLoadEnd(EventListener<BeanLoadEndEvent> handler) {
/* 52 */     return onEvent(BeanLoadEndEvent.class, handler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Note("4.")
/*    */   public SolonBuilder onAppLoadEnd(EventListener<AppLoadEndEvent> handler) {
/* 60 */     return onEvent(AppLoadEndEvent.class, handler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SolonApp start(Class<?> source, String[] args) {
/* 70 */     return Solon.start(source, args);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SolonApp start(Class<?> source, String[] args, ConsumerEx<SolonApp> initialize) {
/* 81 */     return Solon.start(source, args, initialize);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SolonApp start(Class<?> source, NvMap argx, ConsumerEx<SolonApp> initialize) {
/* 92 */     return Solon.start(source, argx, initialize);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\SolonBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */