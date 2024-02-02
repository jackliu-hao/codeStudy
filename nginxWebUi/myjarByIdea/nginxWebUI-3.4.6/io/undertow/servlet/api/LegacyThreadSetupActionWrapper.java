package io.undertow.servlet.api;

import io.undertow.server.HttpServerExchange;

class LegacyThreadSetupActionWrapper implements ThreadSetupHandler {
   private final ThreadSetupAction setupAction;

   LegacyThreadSetupActionWrapper(ThreadSetupAction setupAction) {
      this.setupAction = setupAction;
   }

   public <T, C> ThreadSetupHandler.Action<T, C> create(final ThreadSetupHandler.Action<T, C> action) {
      return new ThreadSetupHandler.Action<T, C>() {
         public T call(HttpServerExchange exchange, C context) throws Exception {
            ThreadSetupAction.Handle handle = LegacyThreadSetupActionWrapper.this.setupAction.setup(exchange);

            Object var4;
            try {
               var4 = action.call(exchange, context);
            } finally {
               if (handle != null) {
                  handle.tearDown();
               }

            }

            return var4;
         }
      };
   }
}
