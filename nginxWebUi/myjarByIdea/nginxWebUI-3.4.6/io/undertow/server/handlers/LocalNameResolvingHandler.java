package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class LocalNameResolvingHandler implements HttpHandler {
   private final HttpHandler next;
   private final ResolveType resolveType;

   public LocalNameResolvingHandler(HttpHandler next) {
      this.next = next;
      this.resolveType = LocalNameResolvingHandler.ResolveType.FORWARD_AND_REVERSE;
   }

   public LocalNameResolvingHandler(HttpHandler next, ResolveType resolveType) {
      this.next = next;
      this.resolveType = resolveType;
   }

   public void handleRequest(final HttpServerExchange exchange) throws Exception {
      final InetSocketAddress address = exchange.getDestinationAddress();
      if (address != null) {
         if ((this.resolveType == LocalNameResolvingHandler.ResolveType.FORWARD || this.resolveType == LocalNameResolvingHandler.ResolveType.FORWARD_AND_REVERSE) && address.isUnresolved()) {
            try {
               if (System.getSecurityManager() == null) {
                  InetSocketAddress resolvedAddress = new InetSocketAddress(InetAddress.getByName(address.getHostName()), address.getPort());
                  exchange.setDestinationAddress(resolvedAddress);
               } else {
                  AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                     public Object run() throws UnknownHostException {
                        InetSocketAddress resolvedAddress = new InetSocketAddress(InetAddress.getByName(address.getHostName()), address.getPort());
                        exchange.setDestinationAddress(resolvedAddress);
                        return null;
                     }
                  });
               }
            } catch (UnknownHostException var4) {
               UndertowLogger.REQUEST_LOGGER.debugf(var4, "Could not resolve hostname %s", address.getHostString());
            }
         } else if (this.resolveType == LocalNameResolvingHandler.ResolveType.REVERSE || this.resolveType == LocalNameResolvingHandler.ResolveType.FORWARD_AND_REVERSE) {
            if (System.getSecurityManager() == null) {
               address.getHostName();
            } else {
               AccessController.doPrivileged(new PrivilegedAction<Object>() {
                  public Object run() {
                     address.getHostName();
                     return null;
                  }
               });
            }

            exchange.setDestinationAddress(address);
         }
      }

      this.next.handleRequest(exchange);
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new LocalNameResolvingHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "resolve-local-name";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper();
      }
   }

   public static enum ResolveType {
      FORWARD,
      REVERSE,
      FORWARD_AND_REVERSE;
   }
}
