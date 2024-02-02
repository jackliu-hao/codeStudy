/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.conduits.HeadStreamSinkConduit;
/*     */ import io.undertow.conduits.RangeStreamSinkConduit;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ResponseCommitListener;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.ByteRange;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteRangeHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final boolean sendAcceptRanges;
/*     */   
/*  59 */   private static final ResponseCommitListener ACCEPT_RANGE_LISTENER = new ResponseCommitListener()
/*     */     {
/*     */       public void beforeCommit(HttpServerExchange exchange) {
/*  62 */         if (!exchange.getResponseHeaders().contains(Headers.ACCEPT_RANGES)) {
/*  63 */           if (exchange.getResponseHeaders().contains(Headers.CONTENT_LENGTH)) {
/*  64 */             exchange.getResponseHeaders().put(Headers.ACCEPT_RANGES, "bytes");
/*     */           } else {
/*  66 */             exchange.getResponseHeaders().put(Headers.ACCEPT_RANGES, "none");
/*     */           } 
/*     */         }
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public ByteRangeHandler(HttpHandler next, boolean sendAcceptRanges) {
/*  74 */     this.next = next;
/*  75 */     this.sendAcceptRanges = sendAcceptRanges;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  82 */     if (!Methods.GET.equals(exchange.getRequestMethod()) && !Methods.HEAD.equals(exchange.getRequestMethod())) {
/*  83 */       this.next.handleRequest(exchange);
/*     */       return;
/*     */     } 
/*  86 */     if (this.sendAcceptRanges) {
/*  87 */       exchange.addResponseCommitListener(ACCEPT_RANGE_LISTENER);
/*     */     }
/*  89 */     final ByteRange range = ByteRange.parse(exchange.getRequestHeaders().getFirst(Headers.RANGE));
/*  90 */     if (range != null && range.getRanges() == 1) {
/*  91 */       exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>()
/*     */           {
/*     */             public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/*  94 */               if (exchange.getStatusCode() != 200) {
/*  95 */                 return (StreamSinkConduit)factory.create();
/*     */               }
/*  97 */               String length = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
/*  98 */               if (length == null) {
/*  99 */                 return (StreamSinkConduit)factory.create();
/*     */               }
/* 101 */               long responseLength = Long.parseLong(length);
/* 102 */               String lastModified = exchange.getResponseHeaders().getFirst(Headers.LAST_MODIFIED);
/* 103 */               ByteRange.RangeResponseResult rangeResponse = range.getResponseResult(responseLength, exchange.getRequestHeaders().getFirst(Headers.IF_RANGE), (lastModified == null) ? null : DateUtils.parseDate(lastModified), exchange.getResponseHeaders().getFirst(Headers.ETAG));
/* 104 */               if (rangeResponse != null) {
/* 105 */                 long start = rangeResponse.getStart();
/* 106 */                 long end = rangeResponse.getEnd();
/* 107 */                 exchange.setStatusCode(rangeResponse.getStatusCode());
/* 108 */                 exchange.getResponseHeaders().put(Headers.CONTENT_RANGE, rangeResponse.getContentRange());
/* 109 */                 exchange.setResponseContentLength(rangeResponse.getContentLength());
/* 110 */                 if (rangeResponse.getStatusCode() == 416) {
/* 111 */                   return (StreamSinkConduit)new HeadStreamSinkConduit((StreamSinkConduit)factory.create(), null, true);
/*     */                 }
/* 113 */                 return (StreamSinkConduit)new RangeStreamSinkConduit((StreamSinkConduit)factory.create(), start, end, responseLength);
/*     */               } 
/* 115 */               return (StreamSinkConduit)factory.create();
/*     */             }
/*     */           });
/*     */     }
/*     */     
/* 120 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 126 */     return "byte-range( " + this.sendAcceptRanges + " )";
/*     */   }
/*     */   
/*     */   public static class Wrapper
/*     */     implements HandlerWrapper {
/*     */     private final boolean sendAcceptRanges;
/*     */     
/*     */     public Wrapper(boolean sendAcceptRanges) {
/* 134 */       this.sendAcceptRanges = sendAcceptRanges;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 139 */       return new ByteRangeHandler(handler, this.sendAcceptRanges);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 147 */       return "byte-range";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 152 */       Map<String, Class<?>> params = new HashMap<>();
/* 153 */       params.put("send-accept-ranges", boolean.class);
/* 154 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 159 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 164 */       return "send-accept-ranges";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 169 */       Boolean send = (Boolean)config.get("send-accept-ranges");
/* 170 */       return new ByteRangeHandler.Wrapper((send != null && send.booleanValue()));
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ByteRangeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */