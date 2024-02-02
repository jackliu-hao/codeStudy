/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
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
/*     */ public class NodeConfig
/*     */ {
/*     */   private final String jvmRoute;
/*     */   private final URI connectionURI;
/*     */   private final String balancer;
/*     */   private final String domain;
/*     */   private boolean flushPackets;
/*     */   private final int flushwait;
/*     */   private final int ping;
/*     */   private final long ttl;
/*     */   private final int timeout;
/*     */   private final int maxConnections;
/*     */   private final int cacheConnections;
/*     */   private final int requestQueueSize;
/*     */   private final boolean queueNewRequests;
/*     */   private final int waitWorker;
/*     */   
/*     */   NodeConfig(NodeBuilder b, URI connectionURI) {
/*  88 */     this.connectionURI = connectionURI;
/*  89 */     this.balancer = b.balancer;
/*  90 */     this.domain = b.domain;
/*  91 */     this.jvmRoute = b.jvmRoute;
/*  92 */     this.flushPackets = b.flushPackets;
/*  93 */     this.flushwait = b.flushwait;
/*  94 */     this.ping = b.ping;
/*  95 */     this.ttl = b.ttl;
/*  96 */     this.timeout = b.timeout;
/*  97 */     this.maxConnections = b.maxConnections;
/*  98 */     this.cacheConnections = b.cacheConnections;
/*  99 */     this.requestQueueSize = b.requestQueueSize;
/* 100 */     this.queueNewRequests = b.queueNewRequests;
/* 101 */     this.waitWorker = b.waitWorker;
/* 102 */     UndertowLogger.ROOT_LOGGER.nodeConfigCreated(this.connectionURI, this.balancer, this.domain, this.jvmRoute, this.flushPackets, this.flushwait, this.ping, this.ttl, this.timeout, this.maxConnections, this.cacheConnections, this.requestQueueSize, this.queueNewRequests);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getConnectionURI() {
/* 111 */     return this.connectionURI;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDomain() {
/* 120 */     return this.domain;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFlushwait() {
/* 129 */     return this.flushwait;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPing() {
/* 138 */     return this.ping;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSmax() {
/* 147 */     return this.cacheConnections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getTtl() {
/* 156 */     return this.ttl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTimeout() {
/* 165 */     return this.timeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getBalancer() {
/* 174 */     return this.balancer;
/*     */   }
/*     */   
/*     */   public boolean isFlushPackets() {
/* 178 */     return this.flushPackets;
/*     */   }
/*     */   
/*     */   public void setFlushPackets(boolean flushPackets) {
/* 182 */     this.flushPackets = flushPackets;
/*     */   }
/*     */   
/*     */   public String getJvmRoute() {
/* 186 */     return this.jvmRoute;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxConnections() {
/* 195 */     return this.maxConnections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCacheConnections() {
/* 204 */     return this.cacheConnections;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRequestQueueSize() {
/* 213 */     return this.requestQueueSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isQueueNewRequests() {
/* 222 */     return this.queueNewRequests;
/*     */   }
/*     */   
/*     */   public static NodeBuilder builder(ModCluster modCluster) {
/* 226 */     return new NodeBuilder(modCluster);
/*     */   }
/*     */   
/*     */   public static class NodeBuilder
/*     */   {
/*     */     private String jvmRoute;
/* 232 */     private String balancer = "mycluster";
/* 233 */     private String domain = null;
/*     */     
/* 235 */     private String type = "http";
/*     */     
/*     */     private String hostname;
/*     */     private int port;
/*     */     private boolean flushPackets = false;
/* 240 */     private int flushwait = 10;
/* 241 */     private int ping = 10000;
/*     */     
/*     */     private int maxConnections;
/*     */     
/*     */     private int cacheConnections;
/*     */     private int requestQueueSize;
/*     */     private boolean queueNewRequests;
/*     */     private long ttl;
/* 249 */     private int timeout = 0;
/* 250 */     private int waitWorker = -1;
/*     */     
/*     */     NodeBuilder(ModCluster modCluster) {
/* 253 */       this.maxConnections = modCluster.getMaxConnections();
/* 254 */       this.cacheConnections = modCluster.getCacheConnections();
/* 255 */       this.requestQueueSize = modCluster.getRequestQueueSize();
/* 256 */       this.queueNewRequests = modCluster.isQueueNewRequests();
/* 257 */       this.ttl = modCluster.getTtl();
/*     */     }
/*     */     
/*     */     public NodeBuilder setHostname(String hostname) {
/* 261 */       this.hostname = hostname;
/* 262 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setPort(int port) {
/* 266 */       this.port = port;
/* 267 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setType(String type) {
/* 271 */       this.type = type;
/* 272 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setBalancer(String balancer) {
/* 276 */       this.balancer = balancer;
/* 277 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setDomain(String domain) {
/* 281 */       this.domain = domain;
/* 282 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setJvmRoute(String jvmRoute) {
/* 286 */       this.jvmRoute = jvmRoute;
/* 287 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setFlushPackets(boolean flushPackets) {
/* 291 */       this.flushPackets = flushPackets;
/* 292 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setFlushwait(int flushwait) {
/* 296 */       this.flushwait = flushwait;
/* 297 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setPing(int ping) {
/* 301 */       this.ping = ping;
/* 302 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setSmax(int smax) {
/* 306 */       this.cacheConnections = smax;
/* 307 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setMaxConnections(int maxConnections) {
/* 311 */       this.maxConnections = maxConnections;
/* 312 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setCacheConnections(int cacheConnections) {
/* 316 */       this.cacheConnections = cacheConnections;
/* 317 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setRequestQueueSize(int requestQueueSize) {
/* 321 */       this.requestQueueSize = requestQueueSize;
/* 322 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setQueueNewRequests(boolean queueNewRequests) {
/* 326 */       this.queueNewRequests = queueNewRequests;
/* 327 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setTtl(long ttl) {
/* 331 */       this.ttl = ttl;
/* 332 */       return this;
/*     */     }
/*     */     
/*     */     public NodeBuilder setTimeout(int timeout) {
/* 336 */       this.timeout = timeout;
/* 337 */       return this;
/*     */     }
/*     */     
/*     */     public NodeConfig build() throws URISyntaxException {
/* 341 */       URI uri = new URI(this.type, null, this.hostname, this.port, "/", "", "");
/* 342 */       return new NodeConfig(this, uri);
/*     */     }
/*     */     
/*     */     public void setWaitWorker(int waitWorker) {
/* 346 */       this.waitWorker = waitWorker;
/*     */     }
/*     */     
/*     */     public int getWaitWorker() {
/* 350 */       return this.waitWorker;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\NodeConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */