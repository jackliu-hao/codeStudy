package io.undertow.server.handlers;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.Headers;
import java.util.Locale;
import java.util.Map;

public class NameVirtualHostHandler implements HttpHandler {
   private volatile HttpHandler defaultHandler;
   private final Map<String, HttpHandler> hosts;

   public NameVirtualHostHandler() {
      this.defaultHandler = ResponseCodeHandler.HANDLE_404;
      this.hosts = new CopyOnWriteMap();
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String hostHeader = exchange.getRequestHeaders().getFirst(Headers.HOST);
      if (hostHeader != null) {
         String host;
         if (hostHeader.contains(":")) {
            host = hostHeader.substring(0, hostHeader.lastIndexOf(":"));
         } else {
            host = hostHeader;
         }

         HttpHandler handler = (HttpHandler)this.hosts.get(host);
         if (handler != null) {
            handler.handleRequest(exchange);
            return;
         }

         handler = (HttpHandler)this.hosts.get(host.toLowerCase(Locale.ENGLISH));
         if (handler != null) {
            handler.handleRequest(exchange);
            return;
         }
      }

      this.defaultHandler.handleRequest(exchange);
   }

   public HttpHandler getDefaultHandler() {
      return this.defaultHandler;
   }

   public Map<String, HttpHandler> getHosts() {
      return this.hosts;
   }

   public NameVirtualHostHandler setDefaultHandler(HttpHandler defaultHandler) {
      Handlers.handlerNotNull(defaultHandler);
      this.defaultHandler = defaultHandler;
      return this;
   }

   public synchronized NameVirtualHostHandler addHost(String host, HttpHandler handler) {
      Handlers.handlerNotNull(handler);
      this.hosts.put(host.toLowerCase(Locale.ENGLISH), handler);
      return this;
   }

   public synchronized NameVirtualHostHandler removeHost(String host) {
      this.hosts.remove(host.toLowerCase(Locale.ENGLISH));
      return this;
   }
}
