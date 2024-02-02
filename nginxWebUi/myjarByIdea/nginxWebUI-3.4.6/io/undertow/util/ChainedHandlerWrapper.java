package io.undertow.util;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import java.util.Iterator;
import java.util.List;

public class ChainedHandlerWrapper implements HandlerWrapper {
   private final List<HandlerWrapper> handlers;

   public ChainedHandlerWrapper(List<HandlerWrapper> handlers) {
      this.handlers = handlers;
   }

   public HttpHandler wrap(HttpHandler handler) {
      HttpHandler cur = handler;

      HandlerWrapper h;
      for(Iterator var3 = this.handlers.iterator(); var3.hasNext(); cur = h.wrap(cur)) {
         h = (HandlerWrapper)var3.next();
      }

      return cur;
   }
}
