/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.NetworkUtils;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ForwardedHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   public static final String BY = "by";
/*     */   public static final String FOR = "for";
/*     */   public static final String HOST = "host";
/*     */   public static final String PROTO = "proto";
/*     */   private static final String UNKNOWN = "unknown";
/*  34 */   private static final boolean DEFAULT_CHANGE_LOCAL_ADDR_PORT = Boolean.getBoolean("io.undertow.forwarded.change-local-addr-port"); private static final String CHANGE_LOCAL_ADDR_PORT = "change-local-addr-port";
/*     */   private final boolean isChangeLocalAddrPort;
/*     */   private final HttpHandler next;
/*     */   private static final Map<String, Token> TOKENS;
/*     */   
/*     */   public ForwardedHandler(HttpHandler next) {
/*  40 */     this(next, DEFAULT_CHANGE_LOCAL_ADDR_PORT);
/*     */   }
/*     */   public ForwardedHandler(HttpHandler next, boolean isChangeLocalAddrPort) {
/*  43 */     this.next = next;
/*  44 */     this.isChangeLocalAddrPort = isChangeLocalAddrPort;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  50 */     HeaderValues forwarded = exchange.getRequestHeaders().get(Headers.FORWARDED);
/*  51 */     if (forwarded != null) {
/*  52 */       Map<Token, String> values = new HashMap<>();
/*  53 */       for (String val : forwarded) {
/*  54 */         parseHeader(val, values);
/*     */       }
/*  56 */       String host = values.get(Token.HOST);
/*  57 */       String proto = values.get(Token.PROTO);
/*  58 */       String by = values.get(Token.BY);
/*  59 */       String forVal = values.get(Token.FOR);
/*     */       
/*  61 */       if (host != null) {
/*  62 */         exchange.getRequestHeaders().put(Headers.HOST, host);
/*  63 */         if (this.isChangeLocalAddrPort) {
/*  64 */           exchange.setDestinationAddress(InetSocketAddress.createUnresolved(exchange.getHostName(), exchange.getHostPort()));
/*     */         }
/*  66 */       } else if (by != null) {
/*     */         
/*  68 */         InetSocketAddress destAddress = parseAddress(by);
/*  69 */         if (destAddress != null && this.isChangeLocalAddrPort) {
/*  70 */           exchange.setDestinationAddress(destAddress);
/*     */         }
/*     */       } 
/*  73 */       if (proto != null) {
/*  74 */         exchange.setRequestScheme(proto);
/*     */       }
/*  76 */       if (forVal != null) {
/*  77 */         InetSocketAddress sourceAddress = parseAddress(forVal);
/*  78 */         if (sourceAddress != null) {
/*  79 */           exchange.setSourceAddress(sourceAddress);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  85 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   static InetSocketAddress parseAddress(String address) {
/*     */     try {
/*  90 */       if (address.equals("unknown")) {
/*  91 */         return null;
/*     */       }
/*  93 */       if (address.startsWith("_"))
/*     */       {
/*     */         
/*  96 */         return null;
/*     */       }
/*  98 */       if (address.startsWith("[")) {
/*     */         
/* 100 */         int index = address.indexOf("]");
/* 101 */         String ipPart = address.substring(1, index);
/*     */         
/* 103 */         int i = address.indexOf(':', index);
/* 104 */         if (i == -1) {
/* 105 */           return new InetSocketAddress(NetworkUtils.parseIpv6Address(ipPart), 0);
/*     */         }
/* 107 */         return new InetSocketAddress(NetworkUtils.parseIpv6Address(ipPart), parsePort(address.substring(i + 1)));
/*     */       } 
/*     */       
/* 110 */       int pos = address.indexOf(':');
/* 111 */       if (pos == -1) {
/* 112 */         return new InetSocketAddress(NetworkUtils.parseIpv4Address(address), 0);
/*     */       }
/* 114 */       return new InetSocketAddress(NetworkUtils.parseIpv4Address(address.substring(0, pos)), parsePort(address.substring(pos + 1)));
/*     */     
/*     */     }
/* 117 */     catch (Exception e) {
/* 118 */       UndertowLogger.REQUEST_IO_LOGGER.debug("Failed to parse address", e);
/* 119 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int parsePort(String substring) {
/* 124 */     if (substring.startsWith("_")) {
/* 125 */       return 0;
/*     */     }
/* 127 */     return Integer.parseInt(substring);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void parseHeader(String header, Map<Token, String> response) {
/* 133 */     if (response.size() == (Token.values()).length) {
/*     */       return;
/*     */     }
/*     */     
/* 137 */     char[] headerChars = header.toCharArray();
/*     */     
/* 139 */     SearchingFor searchingFor = SearchingFor.START_OF_NAME;
/* 140 */     int nameStart = 0;
/* 141 */     Token currentToken = null;
/* 142 */     int valueStart = 0;
/*     */     
/* 144 */     int escapeCount = 0;
/* 145 */     boolean containsEscapes = false;
/*     */     
/* 147 */     for (int i = 0; i < headerChars.length; i++) {
/* 148 */       switch (searchingFor) {
/*     */         
/*     */         case START_OF_NAME:
/* 151 */           if (headerChars[i] != ';' && !Character.isWhitespace(headerChars[i])) {
/* 152 */             nameStart = i;
/* 153 */             searchingFor = SearchingFor.EQUALS_SIGN;
/*     */           } 
/*     */           break;
/*     */         case EQUALS_SIGN:
/* 157 */           if (headerChars[i] == '=') {
/* 158 */             String paramName = String.valueOf(headerChars, nameStart, i - nameStart);
/* 159 */             currentToken = TOKENS.get(paramName.toLowerCase(Locale.ENGLISH));
/*     */             
/* 161 */             searchingFor = SearchingFor.START_OF_VALUE;
/*     */           } 
/*     */           break;
/*     */         case START_OF_VALUE:
/* 165 */           if (!Character.isWhitespace(headerChars[i])) {
/* 166 */             if (headerChars[i] == '"') {
/* 167 */               valueStart = i + 1;
/* 168 */               searchingFor = SearchingFor.LAST_QUOTE; break;
/*     */             } 
/* 170 */             valueStart = i;
/* 171 */             searchingFor = SearchingFor.END_OF_VALUE;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case LAST_QUOTE:
/* 176 */           if (headerChars[i] == '\\') {
/* 177 */             escapeCount++;
/* 178 */             containsEscapes = true; break;
/* 179 */           }  if (headerChars[i] == '"' && escapeCount % 2 == 0) {
/* 180 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/* 181 */             if (containsEscapes) {
/* 182 */               StringBuilder sb = new StringBuilder();
/* 183 */               boolean lastEscape = false;
/* 184 */               for (int j = 0; j < value.length(); j++) {
/* 185 */                 char c = value.charAt(j);
/* 186 */                 if (c == '\\' && !lastEscape) {
/* 187 */                   lastEscape = true;
/*     */                 } else {
/* 189 */                   lastEscape = false;
/* 190 */                   sb.append(c);
/*     */                 } 
/*     */               } 
/* 193 */               value = sb.toString();
/* 194 */               containsEscapes = false;
/*     */             } 
/* 196 */             if (currentToken != null && !response.containsKey(currentToken)) {
/* 197 */               response.put(currentToken, value);
/*     */             }
/*     */             
/* 200 */             searchingFor = SearchingFor.START_OF_NAME;
/* 201 */             escapeCount = 0; break;
/*     */           } 
/* 203 */           escapeCount = 0;
/*     */           break;
/*     */         
/*     */         case END_OF_VALUE:
/* 207 */           if (headerChars[i] == ';' || headerChars[i] == ',' || Character.isWhitespace(headerChars[i])) {
/* 208 */             String value = String.valueOf(headerChars, valueStart, i - valueStart);
/* 209 */             if (currentToken != null && !response.containsKey(currentToken)) {
/* 210 */               response.put(currentToken, value);
/*     */             }
/*     */             
/* 213 */             searchingFor = SearchingFor.START_OF_NAME;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 219 */     if (searchingFor == SearchingFor.END_OF_VALUE) {
/*     */       
/* 221 */       String value = String.valueOf(headerChars, valueStart, headerChars.length - valueStart);
/* 222 */       if (currentToken != null && !response.containsKey(currentToken)) {
/* 223 */         response.put(currentToken, value);
/*     */       }
/* 225 */     } else if (searchingFor != SearchingFor.START_OF_NAME) {
/*     */       
/* 227 */       throw UndertowMessages.MESSAGES.invalidHeader();
/*     */     } 
/*     */   }
/*     */   
/*     */   enum Token
/*     */   {
/* 233 */     BY,
/* 234 */     FOR,
/* 235 */     HOST,
/* 236 */     PROTO;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 242 */     Map<String, Token> map = new HashMap<>();
/* 243 */     for (Token token : Token.values()) {
/* 244 */       map.put(token.name().toLowerCase(), token);
/*     */     }
/* 246 */     TOKENS = Collections.unmodifiableMap(map);
/*     */   }
/*     */   
/*     */   private enum SearchingFor {
/* 250 */     START_OF_NAME, EQUALS_SIGN, START_OF_VALUE, LAST_QUOTE, END_OF_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 256 */     return "forwarded()";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 264 */       return "forwarded";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 269 */       Map<String, Class<?>> params = new HashMap<>();
/* 270 */       params.put("change-local-addr-port", boolean.class);
/* 271 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 276 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 281 */       return "change-local-addr-port";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 286 */       Boolean isChangeLocalAddrPort = (Boolean)config.get("change-local-addr-port");
/* 287 */       return new ForwardedHandler.Wrapper((isChangeLocalAddrPort == null) ? ForwardedHandler.DEFAULT_CHANGE_LOCAL_ADDR_PORT : isChangeLocalAddrPort.booleanValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper implements HandlerWrapper {
/*     */     private final boolean isChangeLocalAddrPort;
/*     */     
/*     */     private Wrapper(boolean isChangeLocalAddrPort) {
/* 295 */       this.isChangeLocalAddrPort = isChangeLocalAddrPort;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 300 */       return new ForwardedHandler(handler, this.isChangeLocalAddrPort);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ForwardedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */