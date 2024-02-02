package io.undertow.server.handlers.proxy;

public interface ConnectionPoolManager extends ProxyConnectionPoolConfig, ConnectionPoolErrorHandler {
   int getProblemServerRetry();
}
