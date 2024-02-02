/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.PathTemplateMatch;
/*     */ import io.undertow.util.URLUtils;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.function.Supplier;
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
/*     */ 
/*     */ public class URLDecodingHandler
/*     */   implements HttpHandler
/*     */ {
/*  49 */   private static final ThreadLocal<StringBuilder> DECODING_BUFFER_CACHE = ThreadLocal.withInitial(StringBuilder::new);
/*  50 */   private static final AttachmentKey<Object> ALREADY_DECODED = AttachmentKey.create(Object.class);
/*     */   
/*     */   private final HttpHandler next;
/*     */   private final String charset;
/*     */   
/*     */   public URLDecodingHandler(HttpHandler next, String charset) {
/*  56 */     this.next = next;
/*  57 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  62 */     if (shouldDecode(exchange)) {
/*  63 */       StringBuilder sb = getStringBuilderForDecoding(exchange);
/*  64 */       decodePath(exchange, this.charset, sb);
/*  65 */       decodeQueryString(exchange, this.charset, sb);
/*  66 */       decodePathTemplateMatch(exchange, this.charset, sb);
/*     */     } 
/*  68 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean shouldDecode(HttpServerExchange exchange) {
/*  74 */     return (!exchange.getConnection().getUndertowOptions().get(UndertowOptions.DECODE_URL, true) && exchange
/*  75 */       .putAttachment(ALREADY_DECODED, Boolean.TRUE) == null);
/*     */   }
/*     */   
/*     */   private static void decodePath(HttpServerExchange exchange, String charset, StringBuilder sb) {
/*  79 */     boolean decodeSlash = exchange.getConnection().getUndertowOptions().get(UndertowOptions.ALLOW_ENCODED_SLASH, false);
/*  80 */     exchange.setRequestPath(URLUtils.decode(exchange.getRequestPath(), charset, decodeSlash, false, sb));
/*  81 */     exchange.setRelativePath(URLUtils.decode(exchange.getRelativePath(), charset, decodeSlash, false, sb));
/*  82 */     exchange.setResolvedPath(URLUtils.decode(exchange.getResolvedPath(), charset, decodeSlash, false, sb));
/*     */   }
/*     */   
/*     */   private static void decodeQueryString(HttpServerExchange exchange, String charset, StringBuilder sb) {
/*  86 */     if (!exchange.getQueryString().isEmpty()) {
/*  87 */       TreeMap<String, Deque<String>> newParams = new TreeMap<>();
/*  88 */       for (Map.Entry<String, Deque<String>> param : (Iterable<Map.Entry<String, Deque<String>>>)exchange.getQueryParameters().entrySet()) {
/*  89 */         Deque<String> newValues = new ArrayDeque<>(((Deque)param.getValue()).size());
/*  90 */         for (String val : param.getValue()) {
/*  91 */           newValues.add(URLUtils.decode(val, charset, true, true, sb));
/*     */         }
/*  93 */         newParams.put(URLUtils.decode(param.getKey(), charset, true, true, sb), newValues);
/*     */       } 
/*  95 */       exchange.getQueryParameters().clear();
/*  96 */       exchange.getQueryParameters().putAll(newParams);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void decodePathTemplateMatch(HttpServerExchange exchange, String charset, StringBuilder sb) {
/* 101 */     PathTemplateMatch pathTemplateMatch = (PathTemplateMatch)exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
/* 102 */     if (pathTemplateMatch != null) {
/* 103 */       Map<String, String> parameters = pathTemplateMatch.getParameters();
/* 104 */       if (parameters != null) {
/* 105 */         for (Map.Entry<String, String> entry : parameters.entrySet()) {
/* 106 */           entry.setValue(URLUtils.decode(entry.getValue(), charset, true, false, sb));
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static StringBuilder getStringBuilderForDecoding(HttpServerExchange exchange) {
/* 113 */     if (exchange.isInIoThread())
/*     */     {
/*     */ 
/*     */       
/* 117 */       return DECODING_BUFFER_CACHE.get();
/*     */     }
/* 119 */     return new StringBuilder();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 124 */     return "url-decoding( " + this.charset + " )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 131 */       return "url-decoding";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 136 */       return Collections.singletonMap("charset", String.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 141 */       return Collections.singleton("charset");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 146 */       return "charset";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 151 */       return new URLDecodingHandler.Wrapper(config.get("charset").toString());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final String charset;
/*     */     
/*     */     private Wrapper(String charset) {
/* 161 */       this.charset = charset;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 166 */       return new URLDecodingHandler(handler, this.charset);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\URLDecodingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */