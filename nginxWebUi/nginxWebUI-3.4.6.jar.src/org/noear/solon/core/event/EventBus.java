/*     */ package org.noear.solon.core.event;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class EventBus
/*     */ {
/*  16 */   private static Map<Object, HH> sThrow = new HashMap<>();
/*     */   
/*  18 */   private static Map<Object, HH> sOther = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void pushAsyn(Object event) {
/*  26 */     if (event != null) {
/*  27 */       Utils.pools.submit(() -> push0(event));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void push(Object event) {
/*  39 */     if (event != null) {
/*  40 */       push0(event);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void push0(Object event) {
/*  45 */     if (event instanceof Throwable) {
/*     */       
/*  47 */       if (Solon.app() == null || Solon.app().enableErrorAutoprint()) {
/*  48 */         ((Throwable)event).printStackTrace();
/*     */       }
/*     */ 
/*     */       
/*  52 */       push1(sThrow.values(), event, false);
/*     */     } else {
/*     */       
/*  55 */       push1(sOther.values(), event, true);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void push1(Collection<HH> hhs, Object event, boolean thrown) {
/*  60 */     for (HH h1 : hhs) {
/*  61 */       if (h1.t.isInstance(event)) {
/*     */         try {
/*  63 */           h1.l.onEvent(event);
/*  64 */         } catch (Throwable e) {
/*  65 */           if (thrown) {
/*  66 */             push(e);
/*     */             continue;
/*     */           } 
/*  69 */           e.printStackTrace();
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
/*  83 */     if (Throwable.class.isAssignableFrom(eventType)) {
/*  84 */       sThrow.putIfAbsent(listener, new HH(eventType, listener));
/*     */       
/*  86 */       if (Solon.app() != null) {
/*  87 */         Solon.app().enableErrorAutoprint(false);
/*     */       }
/*     */     } else {
/*  90 */       sOther.putIfAbsent(listener, new HH(eventType, listener));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized <T> void unsubscribe(EventListener<T> listener) {
/* 100 */     sThrow.remove(listener);
/* 101 */     sOther.remove(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   static class HH
/*     */   {
/*     */     protected Class<?> t;
/*     */     
/*     */     protected EventListener l;
/*     */     
/*     */     public HH(Class<?> type, EventListener listener) {
/* 112 */       this.t = type;
/* 113 */       this.l = listener;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\event\EventBus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */