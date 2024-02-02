package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.NetworkUtils;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ForwardedHandler implements HttpHandler {
   public static final String BY = "by";
   public static final String FOR = "for";
   public static final String HOST = "host";
   public static final String PROTO = "proto";
   private static final String UNKNOWN = "unknown";
   private static final boolean DEFAULT_CHANGE_LOCAL_ADDR_PORT = Boolean.getBoolean("io.undertow.forwarded.change-local-addr-port");
   private static final String CHANGE_LOCAL_ADDR_PORT = "change-local-addr-port";
   private final boolean isChangeLocalAddrPort;
   private final HttpHandler next;
   private static final Map<String, Token> TOKENS;

   public ForwardedHandler(HttpHandler next) {
      this(next, DEFAULT_CHANGE_LOCAL_ADDR_PORT);
   }

   public ForwardedHandler(HttpHandler next, boolean isChangeLocalAddrPort) {
      this.next = next;
      this.isChangeLocalAddrPort = isChangeLocalAddrPort;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      HeaderValues forwarded = exchange.getRequestHeaders().get(Headers.FORWARDED);
      if (forwarded != null) {
         Map<Token, String> values = new HashMap();
         Iterator var4 = forwarded.iterator();

         String proto;
         while(var4.hasNext()) {
            proto = (String)var4.next();
            parseHeader(proto, values);
         }

         String host = (String)values.get(ForwardedHandler.Token.HOST);
         proto = (String)values.get(ForwardedHandler.Token.PROTO);
         String by = (String)values.get(ForwardedHandler.Token.BY);
         String forVal = (String)values.get(ForwardedHandler.Token.FOR);
         InetSocketAddress sourceAddress;
         if (host != null) {
            exchange.getRequestHeaders().put(Headers.HOST, host);
            if (this.isChangeLocalAddrPort) {
               exchange.setDestinationAddress(InetSocketAddress.createUnresolved(exchange.getHostName(), exchange.getHostPort()));
            }
         } else if (by != null) {
            sourceAddress = parseAddress(by);
            if (sourceAddress != null && this.isChangeLocalAddrPort) {
               exchange.setDestinationAddress(sourceAddress);
            }
         }

         if (proto != null) {
            exchange.setRequestScheme(proto);
         }

         if (forVal != null) {
            sourceAddress = parseAddress(forVal);
            if (sourceAddress != null) {
               exchange.setSourceAddress(sourceAddress);
            }
         }
      }

      this.next.handleRequest(exchange);
   }

   static InetSocketAddress parseAddress(String address) {
      try {
         if (address.equals("unknown")) {
            return null;
         } else if (address.startsWith("_")) {
            return null;
         } else {
            int pos;
            if (address.startsWith("[")) {
               pos = address.indexOf("]");
               String ipPart = address.substring(1, pos);
               int pos = address.indexOf(58, pos);
               return pos == -1 ? new InetSocketAddress(NetworkUtils.parseIpv6Address(ipPart), 0) : new InetSocketAddress(NetworkUtils.parseIpv6Address(ipPart), parsePort(address.substring(pos + 1)));
            } else {
               pos = address.indexOf(58);
               return pos == -1 ? new InetSocketAddress(NetworkUtils.parseIpv4Address(address), 0) : new InetSocketAddress(NetworkUtils.parseIpv4Address(address.substring(0, pos)), parsePort(address.substring(pos + 1)));
            }
         }
      } catch (Exception var4) {
         UndertowLogger.REQUEST_IO_LOGGER.debug("Failed to parse address", var4);
         return null;
      }
   }

   private static int parsePort(String substring) {
      return substring.startsWith("_") ? 0 : Integer.parseInt(substring);
   }

   static void parseHeader(String header, Map<Token, String> response) {
      if (response.size() != ForwardedHandler.Token.values().length) {
         char[] headerChars = header.toCharArray();
         SearchingFor searchingFor = ForwardedHandler.SearchingFor.START_OF_NAME;
         int nameStart = 0;
         Token currentToken = null;
         int valueStart = 0;
         int escapeCount = 0;
         boolean containsEscapes = false;

         for(int i = 0; i < headerChars.length; ++i) {
            String value;
            switch (searchingFor) {
               case START_OF_NAME:
                  if (headerChars[i] != ';' && !Character.isWhitespace(headerChars[i])) {
                     nameStart = i;
                     searchingFor = ForwardedHandler.SearchingFor.EQUALS_SIGN;
                  }
                  break;
               case EQUALS_SIGN:
                  if (headerChars[i] == '=') {
                     value = String.valueOf(headerChars, nameStart, i - nameStart);
                     currentToken = (Token)TOKENS.get(value.toLowerCase(Locale.ENGLISH));
                     searchingFor = ForwardedHandler.SearchingFor.START_OF_VALUE;
                  }
                  break;
               case START_OF_VALUE:
                  if (!Character.isWhitespace(headerChars[i])) {
                     if (headerChars[i] == '"') {
                        valueStart = i + 1;
                        searchingFor = ForwardedHandler.SearchingFor.LAST_QUOTE;
                     } else {
                        valueStart = i;
                        searchingFor = ForwardedHandler.SearchingFor.END_OF_VALUE;
                     }
                  }
                  break;
               case LAST_QUOTE:
                  if (headerChars[i] == '\\') {
                     ++escapeCount;
                     containsEscapes = true;
                  } else {
                     if (headerChars[i] == '"' && escapeCount % 2 == 0) {
                        value = String.valueOf(headerChars, valueStart, i - valueStart);
                        if (containsEscapes) {
                           StringBuilder sb = new StringBuilder();
                           boolean lastEscape = false;

                           for(int j = 0; j < value.length(); ++j) {
                              char c = value.charAt(j);
                              if (c == '\\' && !lastEscape) {
                                 lastEscape = true;
                              } else {
                                 lastEscape = false;
                                 sb.append(c);
                              }
                           }

                           value = sb.toString();
                           containsEscapes = false;
                        }

                        if (currentToken != null && !response.containsKey(currentToken)) {
                           response.put(currentToken, value);
                        }

                        searchingFor = ForwardedHandler.SearchingFor.START_OF_NAME;
                        escapeCount = 0;
                        continue;
                     }

                     escapeCount = 0;
                  }
                  break;
               case END_OF_VALUE:
                  if (headerChars[i] == ';' || headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) {
                     value = String.valueOf(headerChars, valueStart, i - valueStart);
                     if (currentToken != null && !response.containsKey(currentToken)) {
                        response.put(currentToken, value);
                     }

                     searchingFor = ForwardedHandler.SearchingFor.START_OF_NAME;
                  }
            }
         }

         if (searchingFor == ForwardedHandler.SearchingFor.END_OF_VALUE) {
            String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
            if (currentToken != null && !response.containsKey(currentToken)) {
               response.put(currentToken, value);
            }
         } else if (searchingFor != ForwardedHandler.SearchingFor.START_OF_NAME) {
            throw UndertowMessages.MESSAGES.invalidHeader();
         }

      }
   }

   public String toString() {
      return "forwarded()";
   }

   static {
      Map<String, Token> map = new HashMap();
      Token[] var1 = ForwardedHandler.Token.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Token token = var1[var3];
         map.put(token.name().toLowerCase(), token);
      }

      TOKENS = Collections.unmodifiableMap(map);
   }

   private static class Wrapper implements HandlerWrapper {
      private final boolean isChangeLocalAddrPort;

      private Wrapper(boolean isChangeLocalAddrPort) {
         this.isChangeLocalAddrPort = isChangeLocalAddrPort;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new ForwardedHandler(handler, this.isChangeLocalAddrPort);
      }

      // $FF: synthetic method
      Wrapper(boolean x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "forwarded";
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
         return new Wrapper(isChangeLocalAddrPort == null ? ForwardedHandler.DEFAULT_CHANGE_LOCAL_ADDR_PORT : isChangeLocalAddrPort);
      }
   }

   private static enum SearchingFor {
      START_OF_NAME,
      EQUALS_SIGN,
      START_OF_VALUE,
      LAST_QUOTE,
      END_OF_VALUE;
   }

   static enum Token {
      BY,
      FOR,
      HOST,
      PROTO;
   }
}
