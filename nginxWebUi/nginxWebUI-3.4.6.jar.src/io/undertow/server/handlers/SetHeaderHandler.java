/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ public class SetHeaderHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpString header;
/*     */   private final ExchangeAttribute value;
/*     */   private final HttpHandler next;
/*     */   
/*     */   public SetHeaderHandler(String header, String value) {
/*  47 */     if (value == null) {
/*  48 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("value");
/*     */     }
/*  50 */     if (header == null) {
/*  51 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("header");
/*     */     }
/*  53 */     this.next = ResponseCodeHandler.HANDLE_404;
/*  54 */     this.value = ExchangeAttributes.constant(value);
/*  55 */     this.header = new HttpString(header);
/*     */   }
/*     */   
/*     */   public SetHeaderHandler(HttpHandler next, String header, ExchangeAttribute value) {
/*  59 */     if (value == null) {
/*  60 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("value");
/*     */     }
/*  62 */     if (header == null) {
/*  63 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("header");
/*     */     }
/*  65 */     if (next == null) {
/*  66 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("next");
/*     */     }
/*  68 */     this.next = next;
/*  69 */     this.value = value;
/*  70 */     this.header = new HttpString(header);
/*     */   }
/*     */   
/*     */   public SetHeaderHandler(HttpHandler next, String header, String value) {
/*  74 */     if (value == null) {
/*  75 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("value");
/*     */     }
/*  77 */     if (header == null) {
/*  78 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("header");
/*     */     }
/*  80 */     if (next == null) {
/*  81 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("next");
/*     */     }
/*  83 */     this.next = next;
/*  84 */     this.value = ExchangeAttributes.constant(value);
/*  85 */     this.header = new HttpString(header);
/*     */   }
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  89 */     exchange.getResponseHeaders().put(this.header, this.value.readAttribute(exchange));
/*  90 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public ExchangeAttribute getValue() {
/*  94 */     return this.value;
/*     */   }
/*     */   
/*     */   public HttpString getHeader() {
/*  98 */     return this.header;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 103 */     return "set( header='" + this.header.toString() + "', value='" + this.value.toString() + "' )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder {
/*     */     public String name() {
/* 109 */       return "header";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 114 */       Map<String, Class<?>> parameters = new HashMap<>();
/* 115 */       parameters.put("header", String.class);
/* 116 */       parameters.put("value", ExchangeAttribute.class);
/*     */       
/* 118 */       return parameters;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 123 */       Set<String> req = new HashSet<>();
/* 124 */       req.add("value");
/* 125 */       req.add("header");
/* 126 */       return req;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 131 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 136 */       final ExchangeAttribute value = (ExchangeAttribute)config.get("value");
/* 137 */       final String header = (String)config.get("header");
/*     */       
/* 139 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 142 */             return new SetHeaderHandler(handler, header, value);
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\SetHeaderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */