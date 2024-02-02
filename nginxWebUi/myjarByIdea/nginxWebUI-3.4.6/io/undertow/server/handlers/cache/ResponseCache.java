package io.undertow.server.handlers.cache;

import io.undertow.UndertowLogger;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.ETagUtils;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ResponseCache {
   public static final AttachmentKey<ResponseCache> ATTACHMENT_KEY = AttachmentKey.create(ResponseCache.class);
   private final DirectBufferCache cache;
   private final HttpServerExchange exchange;
   private boolean responseCachable;

   public ResponseCache(DirectBufferCache cache, HttpServerExchange exchange) {
      this.cache = cache;
      this.exchange = exchange;
   }

   public boolean tryServeResponse() {
      return this.tryServeResponse(true);
   }

   public boolean tryServeResponse(boolean markCacheable) {
      CachedHttpRequest key = new CachedHttpRequest(this.exchange);
      DirectBufferCache.CacheEntry entry = this.cache.get(key);
      if (!this.exchange.getRequestMethod().equals(Methods.GET) && !this.exchange.getRequestMethod().equals(Methods.HEAD)) {
         return false;
      } else if (entry == null) {
         this.responseCachable = markCacheable;
         return false;
      } else if (entry.enabled() && entry.reference()) {
         CachedHttpRequest existingKey = (CachedHttpRequest)entry.key();
         ETag etag = existingKey.getEtag();
         if (!ETagUtils.handleIfMatch(this.exchange, etag, false)) {
            return false;
         } else if (!ETagUtils.handleIfNoneMatch(this.exchange, etag, true)) {
            this.exchange.setStatusCode(304);
            this.exchange.endExchange();
            return true;
         } else if (!DateUtils.handleIfUnmodifiedSince(this.exchange, existingKey.getLastModified())) {
            return false;
         } else if (!DateUtils.handleIfModifiedSince(this.exchange, existingKey.getLastModified())) {
            this.exchange.setStatusCode(304);
            this.exchange.endExchange();
            return true;
         } else {
            if (existingKey.getContentType() != null) {
               this.exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, existingKey.getContentType());
            }

            if (existingKey.getContentEncoding() != null && !Headers.IDENTITY.equals(HttpString.tryFromString(existingKey.getContentEncoding()))) {
               this.exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, existingKey.getContentEncoding());
            }

            if (existingKey.getLastModified() != null) {
               this.exchange.getResponseHeaders().put(Headers.LAST_MODIFIED, DateUtils.toDateString(existingKey.getLastModified()));
            }

            if (existingKey.getContentLocation() != null) {
               this.exchange.getResponseHeaders().put(Headers.CONTENT_LOCATION, existingKey.getContentLocation());
            }

            if (existingKey.getLanguage() != null) {
               this.exchange.getResponseHeaders().put(Headers.CONTENT_LANGUAGE, existingKey.getLanguage());
            }

            if (etag != null) {
               this.exchange.getResponseHeaders().put(Headers.CONTENT_LANGUAGE, etag.toString());
            }

            this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, Long.toString((long)entry.size()));
            if (this.exchange.getRequestMethod().equals(Methods.HEAD)) {
               this.exchange.endExchange();
               return true;
            } else {
               boolean ok = false;

               ByteBuffer[] buffers;
               try {
                  LimitedBufferSlicePool.PooledByteBuffer[] pooled = entry.buffers();
                  buffers = new ByteBuffer[pooled.length];
                  int i = 0;

                  while(true) {
                     if (i >= buffers.length) {
                        ok = true;
                        break;
                     }

                     buffers[i] = pooled[i].getBuffer().duplicate();
                     ++i;
                  }
               } finally {
                  if (!ok) {
                     entry.dereference();
                  }

               }

               this.exchange.getResponseSender().send((ByteBuffer[])buffers, (IoCallback)(new DereferenceCallback(entry)));
               return true;
            }
         }
      } else {
         this.responseCachable = markCacheable;
         return false;
      }
   }

   boolean isResponseCachable() {
      return this.responseCachable;
   }

   private static class DereferenceCallback implements IoCallback {
      private final DirectBufferCache.CacheEntry entry;

      DereferenceCallback(DirectBufferCache.CacheEntry entry) {
         this.entry = entry;
      }

      public void onComplete(HttpServerExchange exchange, Sender sender) {
         this.entry.dereference();
         exchange.endExchange();
      }

      public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
         this.entry.dereference();
         exchange.endExchange();
      }
   }
}
