package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;

public interface ThreadSetupHandler {
   <T, C> Action<T, C> create(Action<T, C> var1);

   public interface Action<T, C> {
      T call(HttpServerExchange var1, C var2) throws Exception;
   }
}
