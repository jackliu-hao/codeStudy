/*     */ package io.undertow.server.handlers.encoding;
/*     */ 
/*     */ import io.undertow.conduits.GzipStreamSourceConduit;
/*     */ import io.undertow.conduits.InflatingStreamSourceConduit;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.Headers;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ public class RequestEncodingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*  50 */   private final Map<String, ConduitWrapper<StreamSourceConduit>> requestEncodings = (Map<String, ConduitWrapper<StreamSourceConduit>>)new CopyOnWriteMap();
/*     */   
/*     */   public RequestEncodingHandler(HttpHandler next) {
/*  53 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  58 */     ConduitWrapper<StreamSourceConduit> encodings = this.requestEncodings.get(exchange.getRequestHeaders().getFirst(Headers.CONTENT_ENCODING));
/*  59 */     if (encodings != null && exchange.isRequestChannelAvailable()) {
/*  60 */       exchange.addRequestWrapper(encodings);
/*     */ 
/*     */       
/*  63 */       exchange.getRequestHeaders().remove(Headers.CONTENT_ENCODING);
/*     */     } 
/*  65 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public RequestEncodingHandler addEncoding(String name, ConduitWrapper<StreamSourceConduit> wrapper) {
/*  69 */     this.requestEncodings.put(name, wrapper);
/*  70 */     return this;
/*     */   }
/*     */   
/*     */   public RequestEncodingHandler removeEncoding(String encoding) {
/*  74 */     this.requestEncodings.remove(encoding);
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHandler getNext() {
/*  80 */     return this.next;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  85 */     return "uncompress()";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  93 */       return "uncompress";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  98 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 103 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 108 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 113 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 116 */             return (new RequestEncodingHandler(handler))
/* 117 */               .addEncoding("gzip", GzipStreamSourceConduit.WRAPPER)
/* 118 */               .addEncoding("deflate", InflatingStreamSourceConduit.WRAPPER);
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\RequestEncodingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */