/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.client.UndertowClient;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.handlers.proxy.ProxyHandler;
/*     */ import io.undertow.server.handlers.proxy.RouteParsingStrategy;
/*     */ import java.io.IOException;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.ssl.XnioSsl;
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
/*     */ public class ModCluster
/*     */ {
/*     */   private final long healthCheckInterval;
/*     */   private final long removeBrokenNodes;
/*     */   private final NodeHealthChecker healthChecker;
/*     */   private final int maxConnections;
/*     */   private final int cacheConnections;
/*     */   private final int requestQueueSize;
/*     */   private final boolean queueNewRequests;
/*     */   private final int maxRequestTime;
/*     */   private final long ttl;
/*     */   private final boolean useAlias;
/*     */   private final XnioWorker xnioWorker;
/*     */   private final ModClusterContainer container;
/*     */   private final int maxRetries;
/*     */   private final boolean deterministicFailover;
/*     */   private final RouteParsingStrategy routeParsingStrategy;
/*     */   private final String rankedAffinityDelimiter;
/*     */   private final boolean reuseXForwarded;
/*  61 */   private final String serverID = UUID.randomUUID().toString();
/*     */   
/*     */   ModCluster(Builder builder) {
/*  64 */     this.xnioWorker = builder.xnioWorker;
/*  65 */     this.maxConnections = builder.maxConnections;
/*  66 */     this.cacheConnections = builder.cacheConnections;
/*  67 */     this.requestQueueSize = builder.requestQueueSize;
/*  68 */     this.queueNewRequests = builder.queueNewRequests;
/*  69 */     this.healthCheckInterval = builder.healthCheckInterval;
/*  70 */     this.removeBrokenNodes = builder.removeBrokenNodes;
/*  71 */     this.deterministicFailover = builder.deterministicFailover;
/*  72 */     this.routeParsingStrategy = builder.routeParsingStrategy;
/*  73 */     this.rankedAffinityDelimiter = builder.rankedAffinityDelimiter;
/*  74 */     this.healthChecker = builder.healthChecker;
/*  75 */     this.maxRequestTime = builder.maxRequestTime;
/*  76 */     this.ttl = builder.ttl;
/*  77 */     this.useAlias = builder.useAlias;
/*  78 */     this.maxRetries = builder.maxRetries;
/*  79 */     this.reuseXForwarded = builder.reuseXForwarded;
/*  80 */     this.container = new ModClusterContainer(this, builder.xnioSsl, builder.client, builder.clientOptions);
/*     */   }
/*     */   
/*     */   protected String getServerID() {
/*  84 */     return this.serverID;
/*     */   }
/*     */   
/*     */   protected ModClusterContainer getContainer() {
/*  88 */     return this.container;
/*     */   }
/*     */   
/*     */   public ModClusterController getController() {
/*  92 */     return this.container;
/*     */   }
/*     */   
/*     */   public int getMaxConnections() {
/*  96 */     return this.maxConnections;
/*     */   }
/*     */   
/*     */   public int getCacheConnections() {
/* 100 */     return this.cacheConnections;
/*     */   }
/*     */   
/*     */   public int getRequestQueueSize() {
/* 104 */     return this.requestQueueSize;
/*     */   }
/*     */   
/*     */   public boolean isQueueNewRequests() {
/* 108 */     return this.queueNewRequests;
/*     */   }
/*     */   
/*     */   public long getHealthCheckInterval() {
/* 112 */     return this.healthCheckInterval;
/*     */   }
/*     */   
/*     */   public long getRemoveBrokenNodes() {
/* 116 */     return this.removeBrokenNodes;
/*     */   }
/*     */   
/*     */   public NodeHealthChecker getHealthChecker() {
/* 120 */     return this.healthChecker;
/*     */   }
/*     */   
/*     */   public long getTtl() {
/* 124 */     return this.ttl;
/*     */   }
/*     */   
/*     */   public boolean isUseAlias() {
/* 128 */     return this.useAlias;
/*     */   }
/*     */   
/*     */   public boolean isDeterministicFailover() {
/* 132 */     return this.deterministicFailover;
/*     */   }
/*     */   
/*     */   public RouteParsingStrategy routeParsingStrategy() {
/* 136 */     return this.routeParsingStrategy;
/*     */   }
/*     */   
/*     */   public String rankedAffinityDelimiter() {
/* 140 */     return this.rankedAffinityDelimiter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpHandler getProxyHandler() {
/* 150 */     return createProxyHandler();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler createProxyHandler() {
/* 158 */     return (HttpHandler)ProxyHandler.builder()
/* 159 */       .setProxyClient(this.container.getProxyClient())
/* 160 */       .setMaxRequestTime(this.maxRequestTime)
/* 161 */       .setMaxConnectionRetries(this.maxRetries)
/* 162 */       .setReuseXForwarded(this.reuseXForwarded)
/* 163 */       .build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler createProxyHandler(HttpHandler next) {
/* 172 */     return (HttpHandler)ProxyHandler.builder()
/* 173 */       .setProxyClient(this.container.getProxyClient())
/* 174 */       .setNext(next)
/* 175 */       .setMaxRequestTime(this.maxRequestTime)
/* 176 */       .setMaxConnectionRetries(this.maxRetries)
/* 177 */       .setReuseXForwarded(this.reuseXForwarded)
/* 178 */       .build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void start() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void advertise(MCMPConfig config) throws IOException {
/* 194 */     MCMPConfig.AdvertiseConfig advertiseConfig = config.getAdvertiseConfig();
/* 195 */     if (advertiseConfig == null) {
/* 196 */       throw new IllegalArgumentException("advertise not enabled");
/*     */     }
/* 198 */     MCMPAdvertiseTask.advertise(this.container, advertiseConfig, this.xnioWorker);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void stop() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder(XnioWorker worker) {
/* 209 */     return builder(worker, UndertowClient.getInstance(), null);
/*     */   }
/*     */   
/*     */   public static Builder builder(XnioWorker worker, UndertowClient client) {
/* 213 */     return builder(worker, client, null);
/*     */   }
/*     */   
/*     */   public static Builder builder(XnioWorker worker, UndertowClient client, XnioSsl xnioSsl) {
/* 217 */     return new Builder(worker, client, xnioSsl);
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private final XnioSsl xnioSsl;
/*     */     
/*     */     private final UndertowClient client;
/*     */     private final XnioWorker xnioWorker;
/* 227 */     private int maxConnections = 16;
/* 228 */     private int cacheConnections = 1;
/* 229 */     private int requestQueueSize = 0;
/*     */     
/*     */     private boolean queueNewRequests = false;
/* 232 */     private int maxRequestTime = -1;
/* 233 */     private long ttl = TimeUnit.SECONDS.toMillis(60L);
/*     */     
/*     */     private boolean useAlias = false;
/* 236 */     private NodeHealthChecker healthChecker = NodeHealthChecker.NO_CHECK;
/* 237 */     private long healthCheckInterval = TimeUnit.SECONDS.toMillis(10L);
/* 238 */     private long removeBrokenNodes = TimeUnit.MINUTES.toMillis(1L);
/* 239 */     private OptionMap clientOptions = OptionMap.EMPTY;
/*     */     private int maxRetries;
/*     */     private boolean deterministicFailover = false;
/* 242 */     private RouteParsingStrategy routeParsingStrategy = RouteParsingStrategy.SINGLE;
/* 243 */     private String rankedAffinityDelimiter = ".";
/*     */     
/*     */     private boolean reuseXForwarded;
/*     */     
/*     */     private Builder(XnioWorker xnioWorker, UndertowClient client, XnioSsl xnioSsl) {
/* 248 */       this.xnioSsl = xnioSsl;
/* 249 */       this.client = client;
/* 250 */       this.xnioWorker = xnioWorker;
/*     */     }
/*     */     
/*     */     public ModCluster build() {
/* 254 */       return new ModCluster(this);
/*     */     }
/*     */     
/*     */     public Builder setMaxRequestTime(int maxRequestTime) {
/* 258 */       this.maxRequestTime = maxRequestTime;
/* 259 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setHealthCheckInterval(long healthCheckInterval) {
/* 263 */       this.healthCheckInterval = healthCheckInterval;
/* 264 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setRemoveBrokenNodes(long removeBrokenNodes) {
/* 268 */       this.removeBrokenNodes = removeBrokenNodes;
/* 269 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setMaxConnections(int maxConnections) {
/* 273 */       this.maxConnections = maxConnections;
/* 274 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCacheConnections(int cacheConnections) {
/* 278 */       this.cacheConnections = cacheConnections;
/* 279 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setRequestQueueSize(int requestQueueSize) {
/* 283 */       this.requestQueueSize = requestQueueSize;
/* 284 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setQueueNewRequests(boolean queueNewRequests) {
/* 288 */       this.queueNewRequests = queueNewRequests;
/* 289 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setHealthChecker(NodeHealthChecker healthChecker) {
/* 293 */       this.healthChecker = healthChecker;
/* 294 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setUseAlias(boolean useAlias) {
/* 298 */       this.useAlias = useAlias;
/* 299 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setMaxRetries(int maxRetries) {
/* 303 */       this.maxRetries = maxRetries;
/* 304 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setDeterministicFailover(boolean deterministicFailover) {
/* 308 */       this.deterministicFailover = deterministicFailover;
/* 309 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRouteParsingStrategy(RouteParsingStrategy routeParsingStrategy) {
/* 319 */       this.routeParsingStrategy = routeParsingStrategy;
/* 320 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Builder setRankedAffinityDelimiter(String rankedAffinityDelimiter) {
/* 332 */       this.rankedAffinityDelimiter = rankedAffinityDelimiter;
/* 333 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setTtl(long ttl) {
/* 337 */       this.ttl = ttl;
/* 338 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setClientOptions(OptionMap clientOptions) {
/* 342 */       this.clientOptions = clientOptions;
/* 343 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setReuseXForwarded(boolean reuseXForwarded) {
/* 347 */       this.reuseXForwarded = reuseXForwarded;
/* 348 */       return this;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\ModCluster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */