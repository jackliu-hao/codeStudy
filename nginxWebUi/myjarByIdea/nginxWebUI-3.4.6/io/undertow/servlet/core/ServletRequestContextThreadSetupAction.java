package io.undertow.servlet.core;

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.handlers.ServletRequestContext;

class ServletRequestContextThreadSetupAction implements ThreadSetupHandler {
   static final ServletRequestContextThreadSetupAction INSTANCE = new ServletRequestContextThreadSetupAction();

   private ServletRequestContextThreadSetupAction() {
   }

   public <T, C> ThreadSetupHandler.Action<T, C> create(final ThreadSetupHandler.Action<T, C> action) {
      return new ThreadSetupHandler.Action<T, C>() {
         public T call(HttpServerExchange exchange, C context) throws Exception {
            if (exchange == null) {
               return action.call((HttpServerExchange)null, context);
            } else {
               ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
               ServletRequestContext old = ServletRequestContext.current();
               SecurityActions.setCurrentRequestContext(servletRequestContext);

               Object var5;
               try {
                  var5 = action.call(exchange, context);
               } finally {
                  ServletRequestContext.setCurrentRequestContext(old);
               }

               return var5;
            }
         }
      };
   }
}
