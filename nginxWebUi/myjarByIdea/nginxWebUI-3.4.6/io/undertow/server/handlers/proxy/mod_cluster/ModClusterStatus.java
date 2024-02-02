package io.undertow.server.handlers.proxy.mod_cluster;

import java.net.URI;
import java.util.List;

public interface ModClusterStatus {
   List<LoadBalancer> getLoadBalancers();

   LoadBalancer getLoadBalancer(String var1);

   public interface Context {
      String getName();

      boolean isEnabled();

      boolean isStopped();

      int getRequests();

      void enable();

      void disable();

      void stop();
   }

   public interface Node {
      String getName();

      URI getUri();

      List<Context> getContexts();

      Context getContext(String var1);

      int getLoad();

      NodeStatus getStatus();

      int getOpenConnections();

      long getTransferred();

      long getRead();

      int getElected();

      int getCacheConnections();

      String getJvmRoute();

      String getDomain();

      int getFlushWait();

      int getMaxConnections();

      int getPing();

      int getRequestQueueSize();

      int getTimeout();

      long getTtl();

      boolean isFlushPackets();

      boolean isQueueNewRequests();

      List<String> getAliases();

      void resetStatistics();
   }

   public interface LoadBalancer {
      String getName();

      List<Node> getNodes();

      Node getNode(String var1);

      boolean isStickySession();

      String getStickySessionCookie();

      String getStickySessionPath();

      boolean isStickySessionRemove();

      boolean isStickySessionForce();

      int getWaitWorker();

      int getMaxRetries();

      /** @deprecated */
      @Deprecated
      int getMaxAttempts();
   }
}
