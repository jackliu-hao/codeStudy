package io.undertow.server.handlers.proxy;

import io.undertow.server.HttpServerExchange;

public interface ProxyCallback<T> {
   void completed(HttpServerExchange var1, T var2);

   void failed(HttpServerExchange var1);

   void couldNotResolveBackend(HttpServerExchange var1);

   void queuedRequestFailed(HttpServerExchange var1);
}
