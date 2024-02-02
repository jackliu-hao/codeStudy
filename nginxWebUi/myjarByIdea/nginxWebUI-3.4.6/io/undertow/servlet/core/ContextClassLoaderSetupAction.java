package io.undertow.servlet.core;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.ThreadSetupHandler;

public class ContextClassLoaderSetupAction implements ThreadSetupHandler {
   private final ClassLoader classLoader;

   public ContextClassLoaderSetupAction(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public <T, C> ThreadSetupHandler.Action<T, C> create(final ThreadSetupHandler.Action<T, C> action) {
      return new ThreadSetupHandler.Action<T, C>() {
         public T call(HttpServerExchange exchange, C context) throws Exception {
            ClassLoader old = SecurityActions.getContextClassLoader();
            SecurityActions.setContextClassLoader(ContextClassLoaderSetupAction.this.classLoader);

            Object var4;
            try {
               var4 = action.call(exchange, context);
            } finally {
               SecurityActions.setContextClassLoader(old);
            }

            return var4;
         }
      };
   }
}
