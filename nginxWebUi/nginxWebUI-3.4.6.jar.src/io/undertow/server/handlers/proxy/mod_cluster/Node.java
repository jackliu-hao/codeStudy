/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.proxy.ConnectionPoolManager;
/*     */ import io.undertow.server.handlers.proxy.ProxyConnectionPool;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.XnioIoThread;
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
/*     */ class Node
/*     */ {
/*     */   private final int id;
/*     */   private final String jvmRoute;
/*     */   private final ConnectionPoolManager connectionPoolManager;
/*     */   private final NodeConfig nodeConfig;
/*     */   private final Balancer balancerConfig;
/*     */   private final ProxyConnectionPool connectionPool;
/*  51 */   private final NodeLbStatus lbStatus = new NodeLbStatus();
/*     */   private final ModClusterContainer container;
/*  53 */   private final List<VHostMapping> vHosts = new CopyOnWriteArrayList<>();
/*  54 */   private final List<Context> contexts = new CopyOnWriteArrayList<>();
/*     */   
/*     */   private final XnioIoThread ioThread;
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*  59 */   private volatile int state = Integer.MIN_VALUE;
/*     */   
/*     */   private static final int ERROR = -2147483648;
/*     */   
/*     */   private static final int REMOVED = 1073741824;
/*     */   private static final int HOT_STANDBY = 536870912;
/*     */   private static final int ACTIVE_PING = 268435456;
/*     */   private static final int ERROR_MASK = 1023;
/*  67 */   private static final AtomicInteger idGen = new AtomicInteger();
/*  68 */   private static final AtomicIntegerFieldUpdater<Node> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(Node.class, "state");
/*     */   
/*     */   protected Node(NodeConfig nodeConfig, Balancer balancerConfig, XnioIoThread ioThread, ByteBufferPool bufferPool, ModClusterContainer container) {
/*  71 */     this.id = idGen.incrementAndGet();
/*  72 */     this.jvmRoute = nodeConfig.getJvmRoute();
/*  73 */     this.nodeConfig = nodeConfig;
/*  74 */     this.ioThread = ioThread;
/*  75 */     this.bufferPool = bufferPool;
/*  76 */     this.balancerConfig = balancerConfig;
/*  77 */     this.container = container;
/*  78 */     this.connectionPoolManager = new NodeConnectionPoolManager();
/*  79 */     this.connectionPool = new ProxyConnectionPool(this.connectionPoolManager, nodeConfig.getConnectionURI(), container.getXnioSsl(), container.getClient(), container.getClientOptions());
/*     */   }
/*     */   
/*     */   public int getId() {
/*  83 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getJvmRoute() {
/*  92 */     return this.jvmRoute;
/*     */   }
/*     */   
/*     */   public Balancer getBalancer() {
/*  96 */     return this.balancerConfig;
/*     */   }
/*     */   
/*     */   public NodeConfig getNodeConfig() {
/* 100 */     return this.nodeConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProxyConnectionPool getConnectionPool() {
/* 109 */     return this.connectionPool;
/*     */   }
/*     */   
/*     */   XnioIoThread getIoThread() {
/* 113 */     return this.ioThread;
/*     */   }
/*     */   
/*     */   public NodeStatus getStatus() {
/* 117 */     int status = this.state;
/* 118 */     if (Bits.anyAreSet(status, -2147483648))
/* 119 */       return NodeStatus.NODE_DOWN; 
/* 120 */     if (Bits.anyAreSet(status, 536870912)) {
/* 121 */       return NodeStatus.NODE_HOT_STANDBY;
/*     */     }
/* 123 */     return NodeStatus.NODE_UP;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getElected() {
/* 128 */     return this.lbStatus.getElected();
/*     */   }
/*     */   
/*     */   int getElectedDiff() {
/* 132 */     return this.lbStatus.getElectedDiff();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLoad() {
/* 141 */     int status = this.state;
/* 142 */     if (Bits.anyAreSet(status, -2147483648))
/* 143 */       return -1; 
/* 144 */     if (Bits.anyAreSet(status, 536870912)) {
/* 145 */       return 0;
/*     */     }
/* 147 */     return this.lbStatus.getLbFactor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLoadStatus() {
/* 157 */     return this.lbStatus.getLbStatus();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void elected() {
/* 164 */     this.lbStatus.elected();
/*     */   }
/*     */   
/*     */   List<VHostMapping> getVHosts() {
/* 168 */     return Collections.unmodifiableList(this.vHosts);
/*     */   }
/*     */   
/*     */   Collection<Context> getContexts() {
/* 172 */     return Collections.unmodifiableCollection(this.contexts);
/*     */   }
/*     */   
/*     */   void resetLbStatus() {
/* 176 */     if (Bits.allAreClear(this.state, -2147483648) && 
/* 177 */       this.lbStatus.update()) {
/*     */       return;
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
/*     */   protected void checkHealth(long threshold, NodeHealthChecker healthChecker) {
/* 190 */     int state = this.state;
/* 191 */     if (Bits.anyAreSet(state, 1342177280)) {
/*     */       return;
/*     */     }
/* 194 */     healthCheckPing(threshold, healthChecker);
/*     */   }
/*     */   
/*     */   void healthCheckPing(final long threshold, NodeHealthChecker healthChecker) {
/*     */     int oldState, newState;
/*     */     do {
/* 200 */       oldState = this.state;
/* 201 */       if ((oldState & 0x10000000) != 0) {
/*     */         return;
/*     */       }
/*     */       
/* 205 */       newState = oldState | 0x10000000;
/* 206 */     } while (!stateUpdater.compareAndSet(this, oldState, newState));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 211 */     NodePingUtil.internalPingNode(this, new NodePingUtil.PingCallback()
/*     */         {
/*     */           public void completed() {
/* 214 */             Node.this.clearActivePing();
/*     */           }
/*     */ 
/*     */           
/*     */           public void failed() {
/* 219 */             if (Node.this.healthCheckFailed() == threshold) {
/*     */               
/* 221 */               Node.this.ioThread.getWorker().execute(new Runnable()
/*     */                   {
/*     */                     public void run() {
/* 224 */                       Node.this.container.removeNode(Node.this, true);
/* 225 */                       Node.this.clearActivePing();
/*     */                     }
/*     */                   });
/*     */             } else {
/* 229 */               Node.this.clearActivePing();
/*     */             } 
/*     */           }
/* 232 */         }healthChecker, this.ioThread, this.bufferPool, this.container.getClient(), this.container.getXnioSsl(), OptionMap.EMPTY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void ping(HttpServerExchange exchange, NodePingUtil.PingCallback callback) {
/* 242 */     NodePingUtil.pingNode(this, exchange, callback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Context registerContext(String path, List<String> virtualHosts) {
/* 252 */     VHostMapping host = null;
/* 253 */     for (VHostMapping vhost : this.vHosts) {
/* 254 */       if (virtualHosts.equals(vhost.getAliases())) {
/* 255 */         host = vhost;
/*     */         break;
/*     */       } 
/*     */     } 
/* 259 */     if (host == null) {
/* 260 */       host = new VHostMapping(this, virtualHosts);
/* 261 */       this.vHosts.add(host);
/*     */     } 
/* 263 */     Context context = new Context(path, host, this);
/* 264 */     this.contexts.add(context);
/* 265 */     return context;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Context getContext(String path, List<String> aliases) {
/* 276 */     VHostMapping host = null;
/* 277 */     for (VHostMapping vhost : this.vHosts) {
/* 278 */       if (aliases.equals(vhost.getAliases())) {
/* 279 */         host = vhost;
/*     */         break;
/*     */       } 
/*     */     } 
/* 283 */     if (host == null) {
/* 284 */       return null;
/*     */     }
/* 286 */     for (Context context : this.contexts) {
/* 287 */       if (context.getPath().equals(path) && context.getVhost() == host) {
/* 288 */         return context;
/*     */       }
/*     */     } 
/* 291 */     return null;
/*     */   }
/*     */   
/*     */   Context removeContext(String path, List<String> aliases) {
/* 295 */     Context context = getContext(path, aliases);
/* 296 */     if (context != null) {
/* 297 */       context.stop();
/* 298 */       this.contexts.remove(context);
/* 299 */       return context;
/*     */     } 
/* 301 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void updateLoad(int i) {
/*     */     while (true) {
/* 307 */       int oldState = this.state;
/* 308 */       int newState = oldState & 0x5FFFFC00;
/* 309 */       if (stateUpdater.compareAndSet(this, oldState, newState)) {
/* 310 */         this.lbStatus.updateLoad(i);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void hotStandby() {
/*     */     while (true) {
/* 319 */       int oldState = this.state;
/* 320 */       int newState = oldState & 0x7FFFFC00 | 0x20000000;
/* 321 */       if (stateUpdater.compareAndSet(this, oldState, newState)) {
/* 322 */         this.lbStatus.updateLoad(0);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void markRemoved() {
/*     */     while (true) {
/* 331 */       int oldState = this.state;
/* 332 */       int newState = oldState | 0x40000000;
/* 333 */       if (stateUpdater.compareAndSet(this, oldState, newState)) {
/* 334 */         this.connectionPool.close();
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void markInError() {
/*     */     while (true) {
/* 343 */       int oldState = this.state;
/* 344 */       int newState = oldState | Integer.MIN_VALUE;
/* 345 */       if (stateUpdater.compareAndSet(this, oldState, newState)) {
/* 346 */         UndertowLogger.ROOT_LOGGER.nodeIsInError(this.jvmRoute);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private void clearActivePing() {
/*     */     int oldState;
/*     */     int newState;
/*     */     do {
/* 355 */       oldState = this.state;
/* 356 */       newState = oldState & 0xEFFFFFFF;
/* 357 */     } while (!stateUpdater.compareAndSet(this, oldState, newState));
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
/*     */   private int healthCheckFailed() {
/*     */     while (true) {
/* 371 */       int newState, oldState = this.state;
/* 372 */       if ((oldState & Integer.MIN_VALUE) != Integer.MIN_VALUE)
/* 373 */       { newState = oldState | Integer.MIN_VALUE;
/* 374 */         UndertowLogger.ROOT_LOGGER.nodeIsInError(this.jvmRoute); }
/* 375 */       else { if ((oldState & 0x3FF) == 1023) {
/* 376 */           return 1023;
/*     */         }
/* 378 */         newState = oldState + 1; }
/*     */       
/* 380 */       if (stateUpdater.compareAndSet(this, oldState, newState)) {
/* 381 */         return newState & 0x3FF;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void resetState() {
/* 387 */     this.state = Integer.MIN_VALUE;
/* 388 */     this.lbStatus.updateLoad(0);
/*     */   }
/*     */   
/*     */   protected boolean isInErrorState() {
/* 392 */     return ((this.state & Integer.MIN_VALUE) == Integer.MIN_VALUE);
/*     */   }
/*     */   
/*     */   boolean isHotStandby() {
/* 396 */     return Bits.anyAreSet(this.state, 536870912);
/*     */   }
/*     */   
/*     */   protected boolean checkAvailable(boolean existingSession) {
/* 400 */     if (Bits.allAreClear(this.state, -1073741824)) {
/*     */       
/* 402 */       ProxyConnectionPool.AvailabilityType availability = this.connectionPool.available();
/* 403 */       if (availability == ProxyConnectionPool.AvailabilityType.AVAILABLE)
/* 404 */         return true; 
/* 405 */       if (availability == ProxyConnectionPool.AvailabilityType.FULL) {
/* 406 */         if (existingSession)
/* 407 */           return true; 
/* 408 */         if (this.nodeConfig.isQueueNewRequests()) {
/* 409 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 413 */     return false;
/*     */   }
/*     */   
/*     */   private class NodeConnectionPoolManager implements ConnectionPoolManager {
/*     */     private NodeConnectionPoolManager() {}
/*     */     
/*     */     public boolean isAvailable() {
/* 420 */       return Bits.allAreClear(Node.this.state, -1073741824);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean handleError() {
/* 425 */       Node.this.markInError();
/* 426 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean clearError() {
/* 432 */       return isAvailable();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxConnections() {
/* 437 */       return Node.this.nodeConfig.getMaxConnections();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxCachedConnections() {
/* 442 */       return Node.this.nodeConfig.getMaxConnections();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getSMaxConnections() {
/* 447 */       return Node.this.nodeConfig.getSmax();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getTtl() {
/* 452 */       return Node.this.nodeConfig.getTtl();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxQueueSize() {
/* 457 */       return Node.this.nodeConfig.getRequestQueueSize();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getProblemServerRetry() {
/* 462 */       return -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 467 */   static final AtomicInteger vHostIdGen = new AtomicInteger();
/*     */   
/*     */   static class VHostMapping {
/*     */     private final int id;
/*     */     private final List<String> aliases;
/*     */     private final Node node;
/*     */     
/*     */     VHostMapping(Node node, List<String> aliases) {
/* 475 */       this.id = Node.vHostIdGen.incrementAndGet();
/* 476 */       this.aliases = aliases;
/* 477 */       this.node = node;
/*     */     }
/*     */     
/*     */     public int getId() {
/* 481 */       return this.id;
/*     */     }
/*     */     
/*     */     public List<String> getAliases() {
/* 485 */       return this.aliases;
/*     */     }
/*     */     
/*     */     Node getNode() {
/* 489 */       return this.node;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 495 */     return "Node{jvmRoute='" + this.jvmRoute + '\'' + ", contexts=" + this.contexts + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */