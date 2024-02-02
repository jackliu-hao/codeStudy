/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.conduits.RateLimitingStreamSinkConduit;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class ResponseRateLimitingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final long time;
/*     */   private final int bytes;
/*     */   private final HttpHandler next;
/*     */   
/*  48 */   private final ConduitWrapper<StreamSinkConduit> WRAPPER = new ConduitWrapper<StreamSinkConduit>()
/*     */     {
/*     */       public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/*  51 */         return (StreamSinkConduit)new RateLimitingStreamSinkConduit((StreamSinkConduit)factory.create(), ResponseRateLimitingHandler.this.bytes, ResponseRateLimitingHandler.this.time, TimeUnit.MILLISECONDS);
/*     */       }
/*     */     };
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
/*     */   public ResponseRateLimitingHandler(HttpHandler next, int bytes, long time, TimeUnit timeUnit) {
/*  65 */     this.time = timeUnit.toMillis(time);
/*  66 */     this.bytes = bytes;
/*  67 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  72 */     exchange.addResponseWrapper(this.WRAPPER);
/*  73 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  78 */     return "response-rate-limit( bytes=" + this.bytes + ", time=" + this.time + " )";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  86 */       return "response-rate-limit";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  91 */       Map<String, Class<?>> ret = new HashMap<>();
/*  92 */       ret.put("bytes", Integer.class);
/*  93 */       ret.put("time", Long.class);
/*  94 */       return ret;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  99 */       return new HashSet<>(Arrays.asList(new String[] { "bytes", "time" }));
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 104 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 109 */       return new ResponseRateLimitingHandler.Wrapper(((Integer)config.get("bytes")).intValue(), ((Long)config.get("time")).longValue(), TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final long time;
/*     */     private final int bytes;
/*     */     
/*     */     private Wrapper(int bytes, long time, TimeUnit timeUnit) {
/* 120 */       this.time = timeUnit.toMillis(time);
/* 121 */       this.bytes = bytes;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 126 */       return new ResponseRateLimitingHandler(handler, this.bytes, this.time, TimeUnit.MILLISECONDS);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ResponseRateLimitingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */