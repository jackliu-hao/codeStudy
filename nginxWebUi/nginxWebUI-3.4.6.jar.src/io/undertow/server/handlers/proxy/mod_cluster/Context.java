/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.proxy.ProxyCallback;
/*     */ import io.undertow.server.handlers.proxy.ProxyConnection;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
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
/*     */ class Context
/*     */ {
/*     */   private final int id;
/*     */   private final Node node;
/*     */   private final String path;
/*     */   private final Node.VHostMapping vhost;
/*     */   private static final int STOPPED = -2147483648;
/*     */   private static final int DISABLED = 1073741824;
/*  40 */   private static final AtomicInteger idGen = new AtomicInteger();
/*     */   private static final int REQUEST_MASK = 1073741823;
/*     */   
/*     */   enum Status {
/*  44 */     ENABLED,
/*  45 */     DISABLED,
/*  46 */     STOPPED;
/*     */   }
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
/*  60 */   private volatile int state = Integer.MIN_VALUE;
/*  61 */   private static final AtomicIntegerFieldUpdater<Context> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(Context.class, "state");
/*     */   
/*     */   Context(String path, Node.VHostMapping vHost, Node node) {
/*  64 */     this.id = idGen.incrementAndGet();
/*  65 */     this.path = path;
/*  66 */     this.node = node;
/*  67 */     this.vhost = vHost;
/*     */   }
/*     */   
/*     */   public int getId() {
/*  71 */     return this.id;
/*     */   }
/*     */   
/*     */   public String getJVMRoute() {
/*  75 */     return this.node.getJvmRoute();
/*     */   }
/*     */   
/*     */   public String getPath() {
/*  79 */     return this.path;
/*     */   }
/*     */   
/*     */   public List<String> getVirtualHosts() {
/*  83 */     return this.vhost.getAliases();
/*     */   }
/*     */   
/*     */   public int getActiveRequests() {
/*  87 */     return this.state & 0x3FFFFFFF;
/*     */   }
/*     */   
/*     */   public Status getStatus() {
/*  91 */     int state = this.state;
/*  92 */     if ((state & Integer.MIN_VALUE) == Integer.MIN_VALUE)
/*  93 */       return Status.STOPPED; 
/*  94 */     if ((state & 0x40000000) == 1073741824) {
/*  95 */       return Status.DISABLED;
/*     */     }
/*  97 */     return Status.ENABLED;
/*     */   }
/*     */   
/*     */   public boolean isEnabled() {
/* 101 */     return Bits.allAreClear(this.state, -1073741824);
/*     */   }
/*     */   
/*     */   public boolean isStopped() {
/* 105 */     return Bits.allAreSet(this.state, -2147483648);
/*     */   }
/*     */   
/*     */   public boolean isDisabled() {
/* 109 */     return Bits.allAreSet(this.state, 1073741824);
/*     */   }
/*     */   
/*     */   Node getNode() {
/* 113 */     return this.node;
/*     */   }
/*     */   
/*     */   Node.VHostMapping getVhost() {
/* 117 */     return this.vhost;
/*     */   }
/*     */   
/*     */   boolean checkAvailable(boolean existingSession) {
/* 121 */     if (this.node.checkAvailable(existingSession)) {
/* 122 */       return existingSession ? (!isStopped()) : isEnabled();
/*     */     }
/* 124 */     return false;
/*     */   }
/*     */   void enable() {
/*     */     int oldState;
/*     */     int newState;
/*     */     do {
/* 130 */       oldState = this.state;
/* 131 */       newState = oldState & 0x3FFFFFFF;
/* 132 */     } while (!stateUpdater.compareAndSet(this, oldState, newState));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void disable() {
/*     */     int oldState;
/*     */     int newState;
/*     */     do {
/* 141 */       oldState = this.state;
/* 142 */       newState = oldState | 0x40000000;
/* 143 */     } while (!stateUpdater.compareAndSet(this, oldState, newState));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void stop() {
/*     */     int oldState;
/*     */     int newState;
/*     */     do {
/* 152 */       oldState = this.state;
/* 153 */       newState = oldState | Integer.MIN_VALUE;
/* 154 */     } while (!stateUpdater.compareAndSet(this, oldState, newState));
/*     */   }
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
/*     */   void handleRequest(ModClusterProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit, boolean exclusive) {
/* 171 */     if (addRequest()) {
/* 172 */       exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 175 */               Context.this.requestDone();
/* 176 */               nextListener.proceed();
/*     */             }
/*     */           });
/* 179 */       this.node.getConnectionPool().connect(target, exchange, callback, timeout, timeUnit, exclusive);
/*     */     } else {
/* 181 */       callback.failed(exchange);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   boolean addRequest() {
/*     */     while (true) {
/* 188 */       int oldState = this.state;
/* 189 */       if ((oldState & Integer.MIN_VALUE) != 0) {
/* 190 */         return false;
/*     */       }
/* 192 */       int newState = oldState + 1;
/* 193 */       if ((newState & 0x3FFFFFFF) == 1073741823) {
/* 194 */         return false;
/*     */       }
/* 196 */       if (stateUpdater.compareAndSet(this, oldState, newState))
/* 197 */         return true; 
/*     */     } 
/*     */   }
/*     */   
/*     */   void requestDone() {
/*     */     int oldState;
/*     */     int newState;
/*     */     do {
/* 205 */       oldState = this.state;
/* 206 */       if ((oldState & 0x3FFFFFFF) == 0) {
/*     */         return;
/*     */       }
/* 209 */       newState = oldState - 1;
/* 210 */     } while (!stateUpdater.compareAndSet(this, oldState, newState));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 218 */     return "Context{, path='" + this.path + '\'' + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */