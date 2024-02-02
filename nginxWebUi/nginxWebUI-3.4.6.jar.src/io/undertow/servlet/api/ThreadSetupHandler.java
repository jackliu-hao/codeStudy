package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;

public interface ThreadSetupHandler {
  <T, C> Action<T, C> create(Action<T, C> paramAction);
  
  public static interface Action<T, C> {
    T call(HttpServerExchange param1HttpServerExchange, C param1C) throws Exception;
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ThreadSetupHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */