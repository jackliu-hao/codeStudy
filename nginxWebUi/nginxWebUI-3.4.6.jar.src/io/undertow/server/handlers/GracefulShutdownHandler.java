/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import java.util.function.LongUnaryOperator;
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
/*     */ public class GracefulShutdownHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private static final long SHUTDOWN_MASK = -9223372036854775808L;
/*     */   private static final long ACTIVE_COUNT_MASK = 9223372036854775807L;
/*     */   private static final LongUnaryOperator incrementActive;
/*     */   private static final LongUnaryOperator incrementActiveAndShutdown;
/*     */   private static final LongUnaryOperator decrementActive;
/*     */   
/*     */   static {
/*  48 */     incrementActive = (current -> {
/*     */         long incrementedActiveCount = activeCount(current) + 1L;
/*     */         
/*     */         return incrementedActiveCount | current & Long.MIN_VALUE;
/*     */       });
/*     */     
/*  54 */     incrementActiveAndShutdown = incrementActive.andThen(current -> current | Long.MIN_VALUE);
/*     */     
/*  56 */     decrementActive = (current -> {
/*     */         long decrementedActiveCount = activeCount(current) - 1L;
/*     */         return decrementedActiveCount | current & Long.MIN_VALUE;
/*     */       });
/*     */   }
/*  61 */   private final GracefulShutdownListener listener = new GracefulShutdownListener();
/*  62 */   private final List<ShutdownListener> shutdownListeners = new ArrayList<>();
/*     */   
/*  64 */   private final Object lock = new Object();
/*     */   
/*  66 */   private volatile long state = 0L;
/*     */   
/*  68 */   private static final AtomicLongFieldUpdater<GracefulShutdownHandler> stateUpdater = AtomicLongFieldUpdater.newUpdater(GracefulShutdownHandler.class, "state");
/*     */   
/*     */   private final HttpHandler next;
/*     */   
/*     */   public GracefulShutdownHandler(HttpHandler next) {
/*  73 */     this.next = next;
/*     */   }
/*     */   
/*     */   private static boolean isShutdown(long state) {
/*  77 */     return ((state & Long.MIN_VALUE) != 0L);
/*     */   }
/*     */   
/*     */   private static long activeCount(long state) {
/*  81 */     return state & Long.MAX_VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  86 */     long snapshot = stateUpdater.updateAndGet(this, incrementActive);
/*  87 */     if (isShutdown(snapshot)) {
/*  88 */       decrementRequests();
/*  89 */       exchange.setStatusCode(503);
/*  90 */       exchange.endExchange();
/*     */       return;
/*     */     } 
/*  93 */     exchange.addExchangeCompleteListener(this.listener);
/*  94 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 100 */     stateUpdater.updateAndGet(this, incrementActiveAndShutdown);
/* 101 */     decrementRequests();
/*     */   }
/*     */   
/*     */   public void start() {
/* 105 */     synchronized (this.lock) {
/* 106 */       stateUpdater.updateAndGet(this, current -> current & Long.MAX_VALUE);
/* 107 */       for (ShutdownListener listener : this.shutdownListeners) {
/* 108 */         listener.shutdown(false);
/*     */       }
/* 110 */       this.shutdownListeners.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void shutdownComplete() {
/* 115 */     synchronized (this.lock) {
/* 116 */       this.lock.notifyAll();
/* 117 */       for (ShutdownListener listener : this.shutdownListeners) {
/* 118 */         listener.shutdown(true);
/*     */       }
/* 120 */       this.shutdownListeners.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void awaitShutdown() throws InterruptedException {
/* 128 */     synchronized (this.lock) {
/* 129 */       if (!isShutdown(stateUpdater.get(this))) {
/* 130 */         throw UndertowMessages.MESSAGES.handlerNotShutdown();
/*     */       }
/* 132 */       while (activeCount(stateUpdater.get(this)) > 0L) {
/* 133 */         this.lock.wait();
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
/*     */   public boolean awaitShutdown(long millis) throws InterruptedException {
/* 145 */     synchronized (this.lock) {
/* 146 */       if (!isShutdown(stateUpdater.get(this))) {
/* 147 */         throw UndertowMessages.MESSAGES.handlerNotShutdown();
/*     */       }
/* 149 */       long end = System.currentTimeMillis() + millis;
/* 150 */       while (activeCount(stateUpdater.get(this)) != 0L) {
/* 151 */         long left = end - System.currentTimeMillis();
/* 152 */         if (left <= 0L) {
/* 153 */           return false;
/*     */         }
/* 155 */         this.lock.wait(left);
/*     */       } 
/* 157 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addShutdownListener(ShutdownListener shutdownListener) {
/* 168 */     synchronized (this.lock) {
/* 169 */       if (!isShutdown(stateUpdater.get(this))) {
/* 170 */         throw UndertowMessages.MESSAGES.handlerNotShutdown();
/*     */       }
/* 172 */       long count = activeCount(stateUpdater.get(this));
/* 173 */       if (count == 0L) {
/* 174 */         shutdownListener.shutdown(true);
/*     */       } else {
/* 176 */         this.shutdownListeners.add(shutdownListener);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void decrementRequests() {
/* 182 */     long snapshot = stateUpdater.updateAndGet(this, decrementActive);
/*     */     
/* 184 */     if (snapshot == Long.MIN_VALUE)
/* 185 */       shutdownComplete(); 
/*     */   }
/*     */   
/*     */   public static interface ShutdownListener {
/*     */     void shutdown(boolean param1Boolean); }
/*     */   
/*     */   private final class GracefulShutdownListener implements ExchangeCompletionListener {
/*     */     public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*     */       try {
/* 194 */         GracefulShutdownHandler.this.decrementRequests();
/*     */       } finally {
/* 196 */         nextListener.proceed();
/*     */       } 
/*     */     }
/*     */     
/*     */     private GracefulShutdownListener() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\GracefulShutdownHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */