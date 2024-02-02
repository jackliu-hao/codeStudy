package io.undertow.server.handlers;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

public class AttachmentHandler<T> implements HttpHandler {
   private final AttachmentKey<T> key;
   private volatile T instance;
   private volatile HttpHandler next;

   public AttachmentHandler(AttachmentKey<T> key, HttpHandler next, T instance) {
      this.next = next;
      this.key = key;
      this.instance = instance;
   }

   public AttachmentHandler(AttachmentKey<T> key, HttpHandler next) {
      this(key, next, (Object)null);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.putAttachment(this.key, this.instance);
      this.next.handleRequest(exchange);
   }

   public T getInstance() {
      return this.instance;
   }

   public void setInstance(T instance) {
      this.instance = instance;
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public void setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
   }
}
