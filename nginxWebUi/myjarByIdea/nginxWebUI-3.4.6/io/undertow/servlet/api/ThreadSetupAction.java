package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;

/** @deprecated */
@Deprecated
public interface ThreadSetupAction {
   Handle setup(HttpServerExchange var1);

   public interface Handle {
      void tearDown();
   }
}
