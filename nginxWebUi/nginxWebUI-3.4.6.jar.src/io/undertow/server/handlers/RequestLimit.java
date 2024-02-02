/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.SameThreadExecutor;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RequestLimit
/*     */ {
/*     */   private volatile int requests;
/*     */   private volatile int max;
/*  52 */   private static final AtomicIntegerFieldUpdater<RequestLimit> requestsUpdater = AtomicIntegerFieldUpdater.newUpdater(RequestLimit.class, "requests");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private volatile HttpHandler failureHandler = new ResponseCodeHandler(503);
/*     */   
/*     */   private final Queue<SuspendedRequest> queue;
/*     */   
/*  62 */   private final ExchangeCompletionListener COMPLETION_LISTENER = new ExchangeCompletionListener()
/*     */     {
/*     */       public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener)
/*     */       {
/*  66 */         RequestLimit.SuspendedRequest task = null;
/*  67 */         boolean found = false;
/*  68 */         while ((task = RequestLimit.this.queue.poll()) != null) {
/*     */           try {
/*  70 */             task.exchange.addExchangeCompleteListener(RequestLimit.this.COMPLETION_LISTENER);
/*  71 */             task.exchange.dispatch(task.next);
/*  72 */             found = true;
/*     */             break;
/*  74 */           } catch (Throwable e) {
/*  75 */             UndertowLogger.ROOT_LOGGER.error("Suspended request was skipped", e);
/*     */           } 
/*     */         } 
/*     */         
/*  79 */         if (!found) {
/*  80 */           RequestLimit.this.decrementRequests();
/*     */         }
/*     */         
/*  83 */         nextListener.proceed();
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public RequestLimit(int maximumConcurrentRequests) {
/*  89 */     this(maximumConcurrentRequests, -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestLimit(int maximumConcurrentRequests, int queueSize) {
/*  99 */     if (maximumConcurrentRequests < 1) {
/* 100 */       throw new IllegalArgumentException("Maximum concurrent requests must be at least 1");
/*     */     }
/* 102 */     this.max = maximumConcurrentRequests;
/*     */     
/* 104 */     this.queue = new LinkedBlockingQueue<>((queueSize <= 0) ? Integer.MAX_VALUE : queueSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(final HttpServerExchange exchange, final HttpHandler next) throws Exception {
/*     */     while (true) {
/* 110 */       int oldVal = this.requests;
/* 111 */       if (oldVal >= this.max) {
/* 112 */         exchange.dispatch(SameThreadExecutor.INSTANCE, new Runnable()
/*     */             {
/*     */               
/*     */               public void run()
/*     */               {
/* 117 */                 synchronized (RequestLimit.this) {
/*     */                   
/*     */                   while (true) {
/* 120 */                     int oldVal = RequestLimit.this.requests;
/* 121 */                     if (oldVal >= RequestLimit.this.max) {
/* 122 */                       if (!RequestLimit.this.queue.offer(new RequestLimit.SuspendedRequest(exchange, next))) {
/* 123 */                         Connectors.executeRootHandler(RequestLimit.this.failureHandler, exchange);
/*     */                       }
/*     */                       return;
/*     */                     } 
/* 127 */                     int newVal = oldVal + 1;
/* 128 */                     if (RequestLimit.requestsUpdater.compareAndSet(RequestLimit.this, oldVal, newVal)) {
/* 129 */                       exchange.addExchangeCompleteListener(RequestLimit.this.COMPLETION_LISTENER);
/* 130 */                       exchange.dispatch(next); return;
/*     */                     } 
/*     */                   } 
/*     */                 }  } });
/*     */         return;
/*     */       } 
/* 136 */       int newVal = oldVal + 1;
/* 137 */       if (requestsUpdater.compareAndSet(this, oldVal, newVal)) {
/* 138 */         exchange.addExchangeCompleteListener(this.COMPLETION_LISTENER);
/* 139 */         next.handleRequest(exchange);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaximumConcurrentRequests() {
/* 148 */     return this.max;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int setMaximumConcurrentRequests(int newMax) {
/* 157 */     if (newMax < 1) {
/* 158 */       throw new IllegalArgumentException("Maximum concurrent requests must be at least 1");
/*     */     }
/* 160 */     int oldMax = this.max;
/* 161 */     this.max = newMax;
/* 162 */     if (newMax > oldMax)
/* 163 */       synchronized (this) {
/* 164 */         while (!this.queue.isEmpty()) {
/*     */           
/*     */           while (true) {
/* 167 */             int oldVal = this.requests;
/* 168 */             if (oldVal >= this.max) {
/* 169 */               return oldMax;
/*     */             }
/* 171 */             int newVal = oldVal + 1;
/* 172 */             if (requestsUpdater.compareAndSet(this, oldVal, newVal)) {
/* 173 */               SuspendedRequest res = this.queue.poll();
/* 174 */               res.exchange.dispatch(res.next);
/*     */             } 
/*     */           } 
/*     */         } 
/* 178 */       }   return oldMax;
/*     */   }
/*     */   
/*     */   private void decrementRequests() {
/* 182 */     requestsUpdater.decrementAndGet(this);
/*     */   }
/*     */   
/*     */   public HttpHandler getFailureHandler() {
/* 186 */     return this.failureHandler;
/*     */   }
/*     */   
/*     */   public void setFailureHandler(HttpHandler failureHandler) {
/* 190 */     this.failureHandler = failureHandler;
/*     */   }
/*     */   
/*     */   private static final class SuspendedRequest {
/*     */     final HttpServerExchange exchange;
/*     */     final HttpHandler next;
/*     */     
/*     */     private SuspendedRequest(HttpServerExchange exchange, HttpHandler next) {
/* 198 */       this.exchange = exchange;
/* 199 */       this.next = next;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\RequestLimit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */