/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.server.handlers.encoding.AllowedContentEncodings;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.Headers;
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
/*     */ public class CacheHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final DirectBufferCache cache;
/*  43 */   private volatile HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */   
/*     */   public CacheHandler(DirectBufferCache cache, HttpHandler next) {
/*  46 */     this.cache = cache;
/*  47 */     this.next = next;
/*     */   }
/*     */   
/*     */   public CacheHandler(DirectBufferCache cache) {
/*  51 */     this.cache = cache;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  56 */     final ResponseCache responseCache = new ResponseCache(this.cache, exchange);
/*  57 */     exchange.putAttachment(ResponseCache.ATTACHMENT_KEY, responseCache);
/*  58 */     exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>()
/*     */         {
/*     */           public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/*  61 */             if (!responseCache.isResponseCachable()) {
/*  62 */               return (StreamSinkConduit)factory.create();
/*     */             }
/*  64 */             AllowedContentEncodings contentEncodings = (AllowedContentEncodings)exchange.getAttachment(AllowedContentEncodings.ATTACHMENT_KEY);
/*  65 */             if (contentEncodings != null && 
/*  66 */               !contentEncodings.isIdentity())
/*     */             {
/*  68 */               return (StreamSinkConduit)factory.create();
/*     */             }
/*     */             
/*  71 */             String lengthString = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
/*  72 */             if (lengthString == null)
/*     */             {
/*  74 */               return (StreamSinkConduit)factory.create();
/*     */             }
/*  76 */             int length = Integer.parseInt(lengthString);
/*  77 */             CachedHttpRequest key = new CachedHttpRequest(exchange);
/*  78 */             DirectBufferCache.CacheEntry entry = CacheHandler.this.cache.add(key, length);
/*     */             
/*  80 */             if (entry == null || (entry.buffers()).length == 0 || !entry.claimEnable()) {
/*  81 */               return (StreamSinkConduit)factory.create();
/*     */             }
/*     */             
/*  84 */             if (!entry.reference()) {
/*  85 */               entry.disable();
/*  86 */               return (StreamSinkConduit)factory.create();
/*     */             } 
/*     */             
/*  89 */             return (StreamSinkConduit)new ResponseCachingStreamSinkConduit((StreamSinkConduit)factory.create(), entry, length);
/*     */           }
/*     */         });
/*  92 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/*  96 */     return this.next;
/*     */   }
/*     */   
/*     */   public CacheHandler setNext(HttpHandler next) {
/* 100 */     Handlers.handlerNotNull(next);
/* 101 */     this.next = next;
/* 102 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\CacheHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */