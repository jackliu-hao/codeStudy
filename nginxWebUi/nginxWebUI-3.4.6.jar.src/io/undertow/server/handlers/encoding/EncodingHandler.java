/*     */ package io.undertow.server.handlers.encoding;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EncodingHandler
/*     */   implements HttpHandler
/*     */ {
/*  48 */   private volatile HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*  49 */   private volatile HttpHandler noEncodingHandler = (HttpHandler)ResponseCodeHandler.HANDLE_406;
/*     */   
/*     */   private final ContentEncodingRepository contentEncodingRepository;
/*     */   
/*     */   public EncodingHandler(HttpHandler next, ContentEncodingRepository contentEncodingRepository) {
/*  54 */     this.next = next;
/*  55 */     this.contentEncodingRepository = contentEncodingRepository;
/*     */   }
/*     */   
/*     */   public EncodingHandler(ContentEncodingRepository contentEncodingRepository) {
/*  59 */     this.contentEncodingRepository = contentEncodingRepository;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  64 */     AllowedContentEncodings encodings = this.contentEncodingRepository.getContentEncodings(exchange);
/*  65 */     if (encodings == null || !exchange.isResponseChannelAvailable()) {
/*  66 */       this.next.handleRequest(exchange);
/*  67 */     } else if (encodings.isNoEncodingsAllowed()) {
/*  68 */       this.noEncodingHandler.handleRequest(exchange);
/*     */     } else {
/*  70 */       exchange.addResponseWrapper(encodings);
/*  71 */       exchange.putAttachment(AllowedContentEncodings.ATTACHMENT_KEY, encodings);
/*  72 */       this.next.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getNext() {
/*  78 */     return this.next;
/*     */   }
/*     */   
/*     */   public EncodingHandler setNext(HttpHandler next) {
/*  82 */     Handlers.handlerNotNull(next);
/*  83 */     this.next = next;
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getNoEncodingHandler() {
/*  89 */     return this.noEncodingHandler;
/*     */   }
/*     */   
/*     */   public EncodingHandler setNoEncodingHandler(HttpHandler noEncodingHandler) {
/*  93 */     Handlers.handlerNotNull(noEncodingHandler);
/*  94 */     this.noEncodingHandler = noEncodingHandler;
/*  95 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     return "compress()";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 107 */       return "compress";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 112 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 117 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 122 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 127 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 130 */             return new EncodingHandler(handler, (new ContentEncodingRepository())
/* 131 */                 .addEncodingHandler("gzip", new GzipEncodingProvider(), 100)
/* 132 */                 .addEncodingHandler("deflate", new DeflateEncodingProvider(), 10));
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\EncodingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */