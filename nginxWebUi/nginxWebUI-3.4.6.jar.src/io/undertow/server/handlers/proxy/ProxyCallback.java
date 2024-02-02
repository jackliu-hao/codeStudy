package io.undertow.server.handlers.proxy;

import io.undertow.server.HttpServerExchange;

public interface ProxyCallback<T> {
  void completed(HttpServerExchange paramHttpServerExchange, T paramT);
  
  void failed(HttpServerExchange paramHttpServerExchange);
  
  void couldNotResolveBackend(HttpServerExchange paramHttpServerExchange);
  
  void queuedRequestFailed(HttpServerExchange paramHttpServerExchange);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ProxyCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */