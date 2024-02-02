package io.undertow.server.handlers.cache;

import io.undertow.Handlers;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.encoding.AllowedContentEncodings;
import io.undertow.util.ConduitFactory;
import io.undertow.util.Headers;
import org.xnio.conduits.StreamSinkConduit;

public class CacheHandler implements HttpHandler {
   private final DirectBufferCache cache;
   private volatile HttpHandler next;

   public CacheHandler(DirectBufferCache cache, HttpHandler next) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.cache = cache;
      this.next = next;
   }

   public CacheHandler(DirectBufferCache cache) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.cache = cache;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      final ResponseCache responseCache = new ResponseCache(this.cache, exchange);
      exchange.putAttachment(ResponseCache.ATTACHMENT_KEY, responseCache);
      exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
         public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
            if (!responseCache.isResponseCachable()) {
               return (StreamSinkConduit)factory.create();
            } else {
               AllowedContentEncodings contentEncodings = (AllowedContentEncodings)exchange.getAttachment(AllowedContentEncodings.ATTACHMENT_KEY);
               if (contentEncodings != null && !contentEncodings.isIdentity()) {
                  return (StreamSinkConduit)factory.create();
               } else {
                  String lengthString = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
                  if (lengthString == null) {
                     return (StreamSinkConduit)factory.create();
                  } else {
                     int length = Integer.parseInt(lengthString);
                     CachedHttpRequest key = new CachedHttpRequest(exchange);
                     DirectBufferCache.CacheEntry entry = CacheHandler.this.cache.add(key, length);
                     if (entry != null && entry.buffers().length != 0 && entry.claimEnable()) {
                        if (!entry.reference()) {
                           entry.disable();
                           return (StreamSinkConduit)factory.create();
                        } else {
                           return new ResponseCachingStreamSinkConduit((StreamSinkConduit)factory.create(), entry, (long)length);
                        }
                     } else {
                        return (StreamSinkConduit)factory.create();
                     }
                  }
               }
            }
         }
      });
      this.next.handleRequest(exchange);
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public CacheHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }
}
