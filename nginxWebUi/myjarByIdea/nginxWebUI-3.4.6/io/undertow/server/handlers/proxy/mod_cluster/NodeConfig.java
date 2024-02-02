package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import java.net.URI;
import java.net.URISyntaxException;

public class NodeConfig {
   private final String jvmRoute;
   private final URI connectionURI;
   private final String balancer;
   private final String domain;
   private boolean flushPackets;
   private final int flushwait;
   private final int ping;
   private final long ttl;
   private final int timeout;
   private final int maxConnections;
   private final int cacheConnections;
   private final int requestQueueSize;
   private final boolean queueNewRequests;
   private final int waitWorker;

   NodeConfig(NodeBuilder b, URI connectionURI) {
      this.connectionURI = connectionURI;
      this.balancer = b.balancer;
      this.domain = b.domain;
      this.jvmRoute = b.jvmRoute;
      this.flushPackets = b.flushPackets;
      this.flushwait = b.flushwait;
      this.ping = b.ping;
      this.ttl = b.ttl;
      this.timeout = b.timeout;
      this.maxConnections = b.maxConnections;
      this.cacheConnections = b.cacheConnections;
      this.requestQueueSize = b.requestQueueSize;
      this.queueNewRequests = b.queueNewRequests;
      this.waitWorker = b.waitWorker;
      UndertowLogger.ROOT_LOGGER.nodeConfigCreated(this.connectionURI, this.balancer, this.domain, this.jvmRoute, this.flushPackets, this.flushwait, this.ping, this.ttl, this.timeout, this.maxConnections, this.cacheConnections, this.requestQueueSize, this.queueNewRequests);
   }

   public URI getConnectionURI() {
      return this.connectionURI;
   }

   public String getDomain() {
      return this.domain;
   }

   public int getFlushwait() {
      return this.flushwait;
   }

   public int getPing() {
      return this.ping;
   }

   public int getSmax() {
      return this.cacheConnections;
   }

   public long getTtl() {
      return this.ttl;
   }

   public int getTimeout() {
      return this.timeout;
   }

   public String getBalancer() {
      return this.balancer;
   }

   public boolean isFlushPackets() {
      return this.flushPackets;
   }

   public void setFlushPackets(boolean flushPackets) {
      this.flushPackets = flushPackets;
   }

   public String getJvmRoute() {
      return this.jvmRoute;
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

   public static NodeBuilder builder(ModCluster modCluster) {
      return new NodeBuilder(modCluster);
   }

   public static class NodeBuilder {
      private String jvmRoute;
      private String balancer = "mycluster";
      private String domain = null;
      private String type = "http";
      private String hostname;
      private int port;
      private boolean flushPackets = false;
      private int flushwait = 10;
      private int ping = 10000;
      private int maxConnections;
      private int cacheConnections;
      private int requestQueueSize;
      private boolean queueNewRequests;
      private long ttl;
      private int timeout = 0;
      private int waitWorker = -1;

      NodeBuilder(ModCluster modCluster) {
         this.maxConnections = modCluster.getMaxConnections();
         this.cacheConnections = modCluster.getCacheConnections();
         this.requestQueueSize = modCluster.getRequestQueueSize();
         this.queueNewRequests = modCluster.isQueueNewRequests();
         this.ttl = modCluster.getTtl();
      }

      public NodeBuilder setHostname(String hostname) {
         this.hostname = hostname;
         return this;
      }

      public NodeBuilder setPort(int port) {
         this.port = port;
         return this;
      }

      public NodeBuilder setType(String type) {
         this.type = type;
         return this;
      }

      public NodeBuilder setBalancer(String balancer) {
         this.balancer = balancer;
         return this;
      }

      public NodeBuilder setDomain(String domain) {
         this.domain = domain;
         return this;
      }

      public NodeBuilder setJvmRoute(String jvmRoute) {
         this.jvmRoute = jvmRoute;
         return this;
      }

      public NodeBuilder setFlushPackets(boolean flushPackets) {
         this.flushPackets = flushPackets;
         return this;
      }

      public NodeBuilder setFlushwait(int flushwait) {
         this.flushwait = flushwait;
         return this;
      }

      public NodeBuilder setPing(int ping) {
         this.ping = ping;
         return this;
      }

      public NodeBuilder setSmax(int smax) {
         this.cacheConnections = smax;
         return this;
      }

      public NodeBuilder setMaxConnections(int maxConnections) {
         this.maxConnections = maxConnections;
         return this;
      }

      public NodeBuilder setCacheConnections(int cacheConnections) {
         this.cacheConnections = cacheConnections;
         return this;
      }

      public NodeBuilder setRequestQueueSize(int requestQueueSize) {
         this.requestQueueSize = requestQueueSize;
         return this;
      }

      public NodeBuilder setQueueNewRequests(boolean queueNewRequests) {
         this.queueNewRequests = queueNewRequests;
         return this;
      }

      public NodeBuilder setTtl(long ttl) {
         this.ttl = ttl;
         return this;
      }

      public NodeBuilder setTimeout(int timeout) {
         this.timeout = timeout;
         return this;
      }

      public NodeConfig build() throws URISyntaxException {
         URI uri = new URI(this.type, (String)null, this.hostname, this.port, "/", "", "");
         return new NodeConfig(this, uri);
      }

      public void setWaitWorker(int waitWorker) {
         this.waitWorker = waitWorker;
      }

      public int getWaitWorker() {
         return this.waitWorker;
      }
   }
}
