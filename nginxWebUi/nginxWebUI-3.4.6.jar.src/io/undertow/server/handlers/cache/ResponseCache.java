/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.ETagUtils;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class ResponseCache
/*     */ {
/*  64 */   public static final AttachmentKey<ResponseCache> ATTACHMENT_KEY = AttachmentKey.create(ResponseCache.class);
/*     */   
/*     */   private final DirectBufferCache cache;
/*     */   private final HttpServerExchange exchange;
/*     */   private boolean responseCachable;
/*     */   
/*     */   public ResponseCache(DirectBufferCache cache, HttpServerExchange exchange) {
/*  71 */     this.cache = cache;
/*  72 */     this.exchange = exchange;
/*     */   }
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
/*     */   public boolean tryServeResponse() {
/*  87 */     return tryServeResponse(true);
/*     */   }
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
/*     */   public boolean tryServeResponse(boolean markCacheable) {
/*     */     ByteBuffer[] buffers;
/* 103 */     CachedHttpRequest key = new CachedHttpRequest(this.exchange);
/* 104 */     DirectBufferCache.CacheEntry entry = this.cache.get(key);
/*     */ 
/*     */     
/* 107 */     if (!this.exchange.getRequestMethod().equals(Methods.GET) && 
/* 108 */       !this.exchange.getRequestMethod().equals(Methods.HEAD)) {
/* 109 */       return false;
/*     */     }
/*     */     
/* 112 */     if (entry == null) {
/* 113 */       this.responseCachable = markCacheable;
/* 114 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 118 */     if (!entry.enabled() || !entry.reference()) {
/* 119 */       this.responseCachable = markCacheable;
/* 120 */       return false;
/*     */     } 
/*     */     
/* 123 */     CachedHttpRequest existingKey = (CachedHttpRequest)entry.key();
/*     */ 
/*     */ 
/*     */     
/* 127 */     ETag etag = existingKey.getEtag();
/* 128 */     if (!ETagUtils.handleIfMatch(this.exchange, etag, false)) {
/* 129 */       return false;
/*     */     }
/*     */     
/* 132 */     if (!ETagUtils.handleIfNoneMatch(this.exchange, etag, true)) {
/* 133 */       this.exchange.setStatusCode(304);
/* 134 */       this.exchange.endExchange();
/* 135 */       return true;
/*     */     } 
/*     */     
/* 138 */     if (!DateUtils.handleIfUnmodifiedSince(this.exchange, existingKey.getLastModified())) {
/* 139 */       return false;
/*     */     }
/* 141 */     if (!DateUtils.handleIfModifiedSince(this.exchange, existingKey.getLastModified())) {
/* 142 */       this.exchange.setStatusCode(304);
/* 143 */       this.exchange.endExchange();
/* 144 */       return true;
/*     */     } 
/*     */ 
/*     */     
/* 148 */     if (existingKey.getContentType() != null) {
/* 149 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, existingKey.getContentType());
/*     */     }
/* 151 */     if (existingKey.getContentEncoding() != null && !Headers.IDENTITY.equals(HttpString.tryFromString(existingKey.getContentEncoding()))) {
/* 152 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, existingKey.getContentEncoding());
/*     */     }
/* 154 */     if (existingKey.getLastModified() != null) {
/* 155 */       this.exchange.getResponseHeaders().put(Headers.LAST_MODIFIED, DateUtils.toDateString(existingKey.getLastModified()));
/*     */     }
/* 157 */     if (existingKey.getContentLocation() != null) {
/* 158 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_LOCATION, existingKey.getContentLocation());
/*     */     }
/* 160 */     if (existingKey.getLanguage() != null) {
/* 161 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_LANGUAGE, existingKey.getLanguage());
/*     */     }
/* 163 */     if (etag != null) {
/* 164 */       this.exchange.getResponseHeaders().put(Headers.CONTENT_LANGUAGE, etag.toString());
/*     */     }
/*     */ 
/*     */     
/* 168 */     this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, Long.toString(entry.size()));
/* 169 */     if (this.exchange.getRequestMethod().equals(Methods.HEAD)) {
/* 170 */       this.exchange.endExchange();
/* 171 */       return true;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 177 */     boolean ok = false;
/*     */     try {
/* 179 */       LimitedBufferSlicePool.PooledByteBuffer[] pooled = entry.buffers();
/* 180 */       buffers = new ByteBuffer[pooled.length];
/* 181 */       for (int i = 0; i < buffers.length; i++)
/*     */       {
/* 183 */         buffers[i] = pooled[i].getBuffer().duplicate();
/*     */       }
/* 185 */       ok = true;
/*     */     } finally {
/* 187 */       if (!ok) {
/* 188 */         entry.dereference();
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 194 */     this.exchange.getResponseSender().send(buffers, new DereferenceCallback(entry));
/* 195 */     return true;
/*     */   }
/*     */   
/*     */   boolean isResponseCachable() {
/* 199 */     return this.responseCachable;
/*     */   }
/*     */   
/*     */   private static class DereferenceCallback implements IoCallback {
/*     */     private final DirectBufferCache.CacheEntry entry;
/*     */     
/*     */     DereferenceCallback(DirectBufferCache.CacheEntry entry) {
/* 206 */       this.entry = entry;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete(HttpServerExchange exchange, Sender sender) {
/* 211 */       this.entry.dereference();
/* 212 */       exchange.endExchange();
/*     */     }
/*     */ 
/*     */     
/*     */     public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 217 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/* 218 */       this.entry.dereference();
/* 219 */       exchange.endExchange();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\ResponseCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */