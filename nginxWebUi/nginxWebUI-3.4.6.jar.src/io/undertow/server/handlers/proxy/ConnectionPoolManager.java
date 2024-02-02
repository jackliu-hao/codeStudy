package io.undertow.server.handlers.proxy;

public interface ConnectionPoolManager extends ProxyConnectionPoolConfig, ConnectionPoolErrorHandler {
  int getProblemServerRetry();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ConnectionPoolManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */