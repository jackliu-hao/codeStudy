package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.Headers;
import io.undertow.util.NetworkUtils;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ProxyPeerAddressHandler implements HttpHandler {
   private static final Pattern IP4_EXACT = Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}");
   private static final Pattern IP6_EXACT = Pattern.compile("^(?:([0-9a-fA-F]{1,4}:){7,7}(?:[0-9a-fA-F]){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,7}(?:(:))|(?:([0-9a-fA-F]{1,4}:)){1,6}(?:(:[0-9a-fA-F]){1,4})|(?:([0-9a-fA-F]{1,4}:)){1,5}(?:(:[0-9a-fA-F]{1,4})){1,2}|(?:([0-9a-fA-F]{1,4}:)){1,4}(?:(:[0-9a-fA-F]{1,4})){1,3}|(?:([0-9a-fA-F]{1,4}:)){1,3}(?:(:[0-9a-fA-F]{1,4})){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,2}(?:(:[0-9a-fA-F]{1,4})){1,5}|(?:([0-9a-fA-F]{1,4}:))(?:(:[0-9a-fA-F]{1,4})){1,6}|(?:(:))(?:((:[0-9a-fA-F]{1,4}){1,7}|(?:(:)))))$");
   private final HttpHandler next;
   private static final boolean DEFAULT_CHANGE_LOCAL_ADDR_PORT = Boolean.getBoolean("io.undertow.forwarded.change-local-addr-port");
   private static final String CHANGE_LOCAL_ADDR_PORT = "change-local-addr-port";
   private final boolean isChangeLocalAddrPort;

   public ProxyPeerAddressHandler(HttpHandler next) {
      this(next, DEFAULT_CHANGE_LOCAL_ADDR_PORT);
   }

   public ProxyPeerAddressHandler(HttpHandler next, boolean isChangeLocalAddrPort) {
      this.next = next;
      this.isChangeLocalAddrPort = isChangeLocalAddrPort;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String forwardedFor = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_FOR);
      String remoteClient;
      if (forwardedFor != null) {
         remoteClient = this.mostRecent(forwardedFor);
         if (IP4_EXACT.matcher(remoteClient).matches()) {
            exchange.setSourceAddress(new InetSocketAddress(NetworkUtils.parseIpv4Address(remoteClient), 0));
         } else if (IP6_EXACT.matcher(remoteClient).matches()) {
            exchange.setSourceAddress(new InetSocketAddress(NetworkUtils.parseIpv6Address(remoteClient), 0));
         } else {
            exchange.setSourceAddress(InetSocketAddress.createUnresolved(remoteClient, 0));
         }
      }

      remoteClient = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PROTO);
      if (remoteClient != null) {
         exchange.setRequestScheme(this.mostRecent(remoteClient));
      }

      String forwardedHost = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_HOST);
      String forwardedPort = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PORT);
      if (forwardedHost != null) {
         String value = this.mostRecent(forwardedHost);
         int port;
         if (value.startsWith("[")) {
            port = value.lastIndexOf("]");
            if (port == -1) {
               port = 0;
            }

            int index = value.indexOf(":", port);
            if (index != -1) {
               forwardedPort = value.substring(index + 1);
               value = value.substring(0, index);
            }
         } else {
            port = value.lastIndexOf(":");
            if (port != -1) {
               forwardedPort = value.substring(port + 1);
               value = value.substring(0, port);
            }
         }

         port = 0;
         String hostHeader = NetworkUtils.formatPossibleIpv6Address(value);
         if (forwardedPort != null) {
            try {
               port = Integer.parseInt(this.mostRecent(forwardedPort));
               if (port > 0) {
                  String scheme = exchange.getRequestScheme();
                  if (!standardPort(port, scheme)) {
                     hostHeader = hostHeader + ":" + port;
                  }
               } else {
                  UndertowLogger.REQUEST_LOGGER.debugf("Ignoring negative port: %s", forwardedPort);
               }
            } catch (NumberFormatException var10) {
               UndertowLogger.REQUEST_LOGGER.debugf("Cannot parse port: %s", forwardedPort);
            }
         }

         exchange.getRequestHeaders().put(Headers.HOST, hostHeader);
         if (this.isChangeLocalAddrPort) {
            exchange.setDestinationAddress(InetSocketAddress.createUnresolved(value, port));
         }
      }

      this.next.handleRequest(exchange);
   }

   private String mostRecent(String header) {
      int index = header.indexOf(44);
      return index == -1 ? header : header.substring(0, index);
   }

   private static boolean standardPort(int port, String scheme) {
      return port == 80 && "http".equals(scheme) || port == 443 && "https".equals(scheme);
   }

   public String toString() {
      return "proxy-peer-address()";
   }

   private static class Wrapper implements HandlerWrapper {
      private final boolean isChangeLocalAddrPort;

      private Wrapper(boolean isChangeLocalAddrPort) {
         this.isChangeLocalAddrPort = isChangeLocalAddrPort;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new ProxyPeerAddressHandler(handler, this.isChangeLocalAddrPort);
      }

      // $FF: synthetic method
      Wrapper(boolean x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "proxy-peer-address";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("change-local-addr-port", Boolean.TYPE);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return "change-local-addr-port";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         Boolean isChangeLocalAddrPort = (Boolean)config.get("change-local-addr-port");
         return new Wrapper(isChangeLocalAddrPort == null ? ProxyPeerAddressHandler.DEFAULT_CHANGE_LOCAL_ADDR_PORT : isChangeLocalAddrPort);
      }
   }
}
