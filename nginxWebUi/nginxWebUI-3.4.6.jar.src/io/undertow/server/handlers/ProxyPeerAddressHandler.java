/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.NetworkUtils;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProxyPeerAddressHandler
/*     */   implements HttpHandler
/*     */ {
/*  46 */   private static final Pattern IP4_EXACT = Pattern.compile("(?:\\d{1,3}\\.){3}\\d{1,3}");
/*     */   
/*  48 */   private static final Pattern IP6_EXACT = Pattern.compile("^(?:([0-9a-fA-F]{1,4}:){7,7}(?:[0-9a-fA-F]){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,7}(?:(:))|(?:([0-9a-fA-F]{1,4}:)){1,6}(?:(:[0-9a-fA-F]){1,4})|(?:([0-9a-fA-F]{1,4}:)){1,5}(?:(:[0-9a-fA-F]{1,4})){1,2}|(?:([0-9a-fA-F]{1,4}:)){1,4}(?:(:[0-9a-fA-F]{1,4})){1,3}|(?:([0-9a-fA-F]{1,4}:)){1,3}(?:(:[0-9a-fA-F]{1,4})){1,4}|(?:([0-9a-fA-F]{1,4}:)){1,2}(?:(:[0-9a-fA-F]{1,4})){1,5}|(?:([0-9a-fA-F]{1,4}:))(?:(:[0-9a-fA-F]{1,4})){1,6}|(?:(:))(?:((:[0-9a-fA-F]{1,4}){1,7}|(?:(:)))))$");
/*     */   
/*     */   private final HttpHandler next;
/*     */   
/*  52 */   private static final boolean DEFAULT_CHANGE_LOCAL_ADDR_PORT = Boolean.getBoolean("io.undertow.forwarded.change-local-addr-port");
/*     */   
/*     */   private static final String CHANGE_LOCAL_ADDR_PORT = "change-local-addr-port";
/*     */   
/*     */   private final boolean isChangeLocalAddrPort;
/*     */   
/*     */   public ProxyPeerAddressHandler(HttpHandler next) {
/*  59 */     this(next, DEFAULT_CHANGE_LOCAL_ADDR_PORT);
/*     */   }
/*     */   
/*     */   public ProxyPeerAddressHandler(HttpHandler next, boolean isChangeLocalAddrPort) {
/*  63 */     this.next = next;
/*  64 */     this.isChangeLocalAddrPort = isChangeLocalAddrPort;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  69 */     String forwardedFor = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_FOR);
/*  70 */     if (forwardedFor != null) {
/*  71 */       String remoteClient = mostRecent(forwardedFor);
/*     */       
/*  73 */       if (IP4_EXACT.matcher(remoteClient).matches()) {
/*  74 */         exchange.setSourceAddress(new InetSocketAddress(NetworkUtils.parseIpv4Address(remoteClient), 0));
/*  75 */       } else if (IP6_EXACT.matcher(remoteClient).matches()) {
/*  76 */         exchange.setSourceAddress(new InetSocketAddress(NetworkUtils.parseIpv6Address(remoteClient), 0));
/*     */       } else {
/*  78 */         exchange.setSourceAddress(InetSocketAddress.createUnresolved(remoteClient, 0));
/*     */       } 
/*     */     } 
/*  81 */     String forwardedProto = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PROTO);
/*  82 */     if (forwardedProto != null) {
/*  83 */       exchange.setRequestScheme(mostRecent(forwardedProto));
/*     */     }
/*  85 */     String forwardedHost = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_HOST);
/*  86 */     String forwardedPort = exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PORT);
/*  87 */     if (forwardedHost != null) {
/*  88 */       String value = mostRecent(forwardedHost);
/*  89 */       if (value.startsWith("[")) {
/*  90 */         int end = value.lastIndexOf("]");
/*  91 */         if (end == -1) {
/*  92 */           end = 0;
/*     */         }
/*  94 */         int index = value.indexOf(":", end);
/*  95 */         if (index != -1) {
/*  96 */           forwardedPort = value.substring(index + 1);
/*  97 */           value = value.substring(0, index);
/*     */         } 
/*     */       } else {
/* 100 */         int index = value.lastIndexOf(":");
/* 101 */         if (index != -1) {
/* 102 */           forwardedPort = value.substring(index + 1);
/* 103 */           value = value.substring(0, index);
/*     */         } 
/*     */       } 
/* 106 */       int port = 0;
/* 107 */       String hostHeader = NetworkUtils.formatPossibleIpv6Address(value);
/* 108 */       if (forwardedPort != null) {
/*     */         try {
/* 110 */           port = Integer.parseInt(mostRecent(forwardedPort));
/* 111 */           if (port > 0) {
/* 112 */             String scheme = exchange.getRequestScheme();
/*     */             
/* 114 */             if (!standardPort(port, scheme)) {
/* 115 */               hostHeader = hostHeader + ":" + port;
/*     */             }
/*     */           } else {
/* 118 */             UndertowLogger.REQUEST_LOGGER.debugf("Ignoring negative port: %s", forwardedPort);
/*     */           } 
/* 120 */         } catch (NumberFormatException ignore) {
/* 121 */           UndertowLogger.REQUEST_LOGGER.debugf("Cannot parse port: %s", forwardedPort);
/*     */         } 
/*     */       }
/* 124 */       exchange.getRequestHeaders().put(Headers.HOST, hostHeader);
/* 125 */       if (this.isChangeLocalAddrPort) {
/* 126 */         exchange.setDestinationAddress(InetSocketAddress.createUnresolved(value, port));
/*     */       }
/*     */     } 
/* 129 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private String mostRecent(String header) {
/* 133 */     int index = header.indexOf(',');
/* 134 */     if (index == -1) {
/* 135 */       return header;
/*     */     }
/* 137 */     return header.substring(0, index);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean standardPort(int port, String scheme) {
/* 142 */     return ((port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme)));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 147 */     return "proxy-peer-address()";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 154 */       return "proxy-peer-address";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 159 */       Map<String, Class<?>> params = new HashMap<>();
/* 160 */       params.put("change-local-addr-port", boolean.class);
/* 161 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 166 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 171 */       return "change-local-addr-port";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 176 */       Boolean isChangeLocalAddrPort = (Boolean)config.get("change-local-addr-port");
/* 177 */       return new ProxyPeerAddressHandler.Wrapper((isChangeLocalAddrPort == null) ? ProxyPeerAddressHandler.DEFAULT_CHANGE_LOCAL_ADDR_PORT : isChangeLocalAddrPort.booleanValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper {
/*     */     private final boolean isChangeLocalAddrPort;
/*     */     
/*     */     private Wrapper(boolean isChangeLocalAddrPort) {
/* 186 */       this.isChangeLocalAddrPort = isChangeLocalAddrPort;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 191 */       return new ProxyPeerAddressHandler(handler, this.isChangeLocalAddrPort);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ProxyPeerAddressHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */