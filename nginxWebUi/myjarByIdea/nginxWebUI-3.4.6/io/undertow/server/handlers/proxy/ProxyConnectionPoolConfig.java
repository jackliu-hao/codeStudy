package io.undertow.server.handlers.proxy;

public interface ProxyConnectionPoolConfig {
   int getMaxConnections();

   int getMaxCachedConnections();

   int getSMaxConnections();

   long getTtl();

   int getMaxQueueSize();
}
