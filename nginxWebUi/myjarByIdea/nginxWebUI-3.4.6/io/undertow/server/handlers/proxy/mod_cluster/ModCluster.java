package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.client.UndertowClient;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.proxy.RouteParsingStrategy;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.xnio.OptionMap;
import org.xnio.XnioWorker;
import org.xnio.ssl.XnioSsl;

public class ModCluster {
   private final long healthCheckInterval;
   private final long removeBrokenNodes;
   private final NodeHealthChecker healthChecker;
   private final int maxConnections;
   private final int cacheConnections;
   private final int requestQueueSize;
   private final boolean queueNewRequests;
   private final int maxRequestTime;
   private final long ttl;
   private final boolean useAlias;
   private final XnioWorker xnioWorker;
   private final ModClusterContainer container;
   private final int maxRetries;
   private final boolean deterministicFailover;
   private final RouteParsingStrategy routeParsingStrategy;
   private final String rankedAffinityDelimiter;
   private final boolean reuseXForwarded;
   private final String serverID = UUID.randomUUID().toString();

   ModCluster(Builder builder) {
      this.xnioWorker = builder.xnioWorker;
      this.maxConnections = builder.maxConnections;
      this.cacheConnections = builder.cacheConnections;
      this.requestQueueSize = builder.requestQueueSize;
      this.queueNewRequests = builder.queueNewRequests;
      this.healthCheckInterval = builder.healthCheckInterval;
      this.removeBrokenNodes = builder.removeBrokenNodes;
      this.deterministicFailover = builder.deterministicFailover;
      this.routeParsingStrategy = builder.routeParsingStrategy;
      this.rankedAffinityDelimiter = builder.rankedAffinityDelimiter;
      this.healthChecker = builder.healthChecker;
      this.maxRequestTime = builder.maxRequestTime;
      this.ttl = builder.ttl;
      this.useAlias = builder.useAlias;
      this.maxRetries = builder.maxRetries;
      this.reuseXForwarded = builder.reuseXForwarded;
      this.container = new ModClusterContainer(this, builder.xnioSsl, builder.client, builder.clientOptions);
   }

   protected String getServerID() {
      return this.serverID;
   }

   protected ModClusterContainer getContainer() {
      return this.container;
   }

   public ModClusterController getController() {
      return this.container;
   }

   public int getMaxConnections() {
      return this.maxConnections;
   }

   public int getCacheConnections() {
      return this.cacheConnections;
   }

   public int getRequestQueueSize() {
      return this.requestQueueSize;
   }

   public boolean isQueueNewRequests() {
      return this.queueNewRequests;
   }

   public long getHealthCheckInterval() {
      return this.healthCheckInterval;
   }

   public long getRemoveBrokenNodes() {
      return this.removeBrokenNodes;
   }

   public NodeHealthChecker getHealthChecker() {
      return this.healthChecker;
   }

   public long getTtl() {
      return this.ttl;
   }

   public boolean isUseAlias() {
      return this.useAlias;
   }

   public boolean isDeterministicFailover() {
      return this.deterministicFailover;
   }

   public RouteParsingStrategy routeParsingStrategy() {
      return this.routeParsingStrategy;
   }

   public String rankedAffinityDelimiter() {
      return this.rankedAffinityDelimiter;
   }

   /** @deprecated */
   @Deprecated
   public HttpHandler getProxyHandler() {
      return this.createProxyHandler();
   }

   public HttpHandler createProxyHandler() {
      return ProxyHandler.builder().setProxyClient(this.container.getProxyClient()).setMaxRequestTime(this.maxRequestTime).setMaxConnectionRetries(this.maxRetries).setReuseXForwarded(this.reuseXForwarded).build();
   }

   public HttpHandler createProxyHandler(HttpHandler next) {
      return ProxyHandler.builder().setProxyClient(this.container.getProxyClient()).setNext(next).setMaxRequestTime(this.maxRequestTime).setMaxConnectionRetries(this.maxRetries).setReuseXForwarded(this.reuseXForwarded).build();
   }

   public synchronized void start() {
   }

   public synchronized void advertise(MCMPConfig config) throws IOException {
      MCMPConfig.AdvertiseConfig advertiseConfig = config.getAdvertiseConfig();
      if (advertiseConfig == null) {
         throw new IllegalArgumentException("advertise not enabled");
      } else {
         MCMPAdvertiseTask.advertise(this.container, advertiseConfig, this.xnioWorker);
      }
   }

   public synchronized void stop() {
   }

   public static Builder builder(XnioWorker worker) {
      return builder(worker, UndertowClient.getInstance(), (XnioSsl)null);
   }

   public static Builder builder(XnioWorker worker, UndertowClient client) {
      return builder(worker, client, (XnioSsl)null);
   }

   public static Builder builder(XnioWorker worker, UndertowClient client, XnioSsl xnioSsl) {
      return new Builder(worker, client, xnioSsl);
   }

   public static class Builder {
      private final XnioSsl xnioSsl;
      private final UndertowClient client;
      private final XnioWorker xnioWorker;
      private int maxConnections;
      private int cacheConnections;
      private int requestQueueSize;
      private boolean queueNewRequests;
      private int maxRequestTime;
      private long ttl;
      private boolean useAlias;
      private NodeHealthChecker healthChecker;
      private long healthCheckInterval;
      private long removeBrokenNodes;
      private OptionMap clientOptions;
      private int maxRetries;
      private boolean deterministicFailover;
      private RouteParsingStrategy routeParsingStrategy;
      private String rankedAffinityDelimiter;
      private boolean reuseXForwarded;

      private Builder(XnioWorker xnioWorker, UndertowClient client, XnioSsl xnioSsl) {
         this.maxConnections = 16;
         this.cacheConnections = 1;
         this.requestQueueSize = 0;
         this.queueNewRequests = false;
         this.maxRequestTime = -1;
         this.ttl = TimeUnit.SECONDS.toMillis(60L);
         this.useAlias = false;
         this.healthChecker = NodeHealthChecker.NO_CHECK;
         this.healthCheckInterval = TimeUnit.SECONDS.toMillis(10L);
         this.removeBrokenNodes = TimeUnit.MINUTES.toMillis(1L);
         this.clientOptions = OptionMap.EMPTY;
         this.deterministicFailover = false;
         this.routeParsingStrategy = RouteParsingStrategy.SINGLE;
         this.rankedAffinityDelimiter = ".";
         this.xnioSsl = xnioSsl;
         this.client = client;
         this.xnioWorker = xnioWorker;
      }

      public ModCluster build() {
         return new ModCluster(this);
      }

      public Builder setMaxRequestTime(int maxRequestTime) {
         this.maxRequestTime = maxRequestTime;
         return this;
      }

      public Builder setHealthCheckInterval(long healthCheckInterval) {
         this.healthCheckInterval = healthCheckInterval;
         return this;
      }

      public Builder setRemoveBrokenNodes(long removeBrokenNodes) {
         this.removeBrokenNodes = removeBrokenNodes;
         return this;
      }

      public Builder setMaxConnections(int maxConnections) {
         this.maxConnections = maxConnections;
         return this;
      }

      public Builder setCacheConnections(int cacheConnections) {
         this.cacheConnections = cacheConnections;
         return this;
      }

      public Builder setRequestQueueSize(int requestQueueSize) {
         this.requestQueueSize = requestQueueSize;
         return this;
      }

      public Builder setQueueNewRequests(boolean queueNewRequests) {
         this.queueNewRequests = queueNewRequests;
         return this;
      }

      public Builder setHealthChecker(NodeHealthChecker healthChecker) {
         this.healthChecker = healthChecker;
         return this;
      }

      public Builder setUseAlias(boolean useAlias) {
         this.useAlias = useAlias;
         return this;
      }

      public Builder setMaxRetries(int maxRetries) {
         this.maxRetries = maxRetries;
         return this;
      }

      public Builder setDeterministicFailover(boolean deterministicFailover) {
         this.deterministicFailover = deterministicFailover;
         return this;
      }

      public Builder setRouteParsingStrategy(RouteParsingStrategy routeParsingStrategy) {
         this.routeParsingStrategy = routeParsingStrategy;
         return this;
      }

      public Builder setRankedAffinityDelimiter(String rankedAffinityDelimiter) {
         this.rankedAffinityDelimiter = rankedAffinityDelimiter;
         return this;
      }

      public Builder setTtl(long ttl) {
         this.ttl = ttl;
         return this;
      }

      public Builder setClientOptions(OptionMap clientOptions) {
         this.clientOptions = clientOptions;
         return this;
      }

      public Builder setReuseXForwarded(boolean reuseXForwarded) {
         this.reuseXForwarded = reuseXForwarded;
         return this;
      }

      // $FF: synthetic method
      Builder(XnioWorker x0, UndertowClient x1, XnioSsl x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
