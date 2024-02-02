package io.undertow.server.handlers.proxy.mod_cluster;

import java.net.URI;
import java.util.List;

public interface ModClusterStatus {
  List<LoadBalancer> getLoadBalancers();
  
  LoadBalancer getLoadBalancer(String paramString);
  
  public static interface Context {
    String getName();
    
    boolean isEnabled();
    
    boolean isStopped();
    
    int getRequests();
    
    void enable();
    
    void disable();
    
    void stop();
  }
  
  public static interface Node {
    String getName();
    
    URI getUri();
    
    List<ModClusterStatus.Context> getContexts();
    
    ModClusterStatus.Context getContext(String param1String);
    
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
  
  public static interface LoadBalancer {
    String getName();
    
    List<ModClusterStatus.Node> getNodes();
    
    ModClusterStatus.Node getNode(String param1String);
    
    boolean isStickySession();
    
    String getStickySessionCookie();
    
    String getStickySessionPath();
    
    boolean isStickySessionRemove();
    
    boolean isStickySessionForce();
    
    int getWaitWorker();
    
    int getMaxRetries();
    
    @Deprecated
    int getMaxAttempts();
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\ModClusterStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */