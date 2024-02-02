package io.undertow.server.handlers.proxy;

public interface ProxyConnectionPoolConfig {
  int getMaxConnections();
  
  int getMaxCachedConnections();
  
  int getSMaxConnections();
  
  long getTtl();
  
  int getMaxQueueSize();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ProxyConnectionPoolConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */