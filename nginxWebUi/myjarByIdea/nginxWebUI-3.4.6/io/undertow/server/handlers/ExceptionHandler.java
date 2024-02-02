package io.undertow.server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExceptionHandler implements HttpHandler {
   public static final AttachmentKey<Throwable> THROWABLE = AttachmentKey.create(Throwable.class);
   private final HttpHandler handler;
   private final List<ExceptionHandlerHolder<?>> exceptionHandlers = new CopyOnWriteArrayList();

   public ExceptionHandler(HttpHandler handler) {
      this.handler = handler;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      try {
         this.handler.handleRequest(exchange);
      } catch (Throwable var5) {
         Throwable throwable = var5;
         Iterator var3 = this.exceptionHandlers.iterator();

         ExceptionHandlerHolder holder;
         do {
            if (!var3.hasNext()) {
               throw throwable;
            }

            holder = (ExceptionHandlerHolder)var3.next();
         } while(!holder.getClazz().isInstance(throwable));

         exchange.putAttachment(THROWABLE, throwable);
         holder.getHandler().handleRequest(exchange);
      }
   }

   public <T extends Throwable> ExceptionHandler addExceptionHandler(Class<T> clazz, HttpHandler handler) {
      this.exceptionHandlers.add(new ExceptionHandlerHolder(clazz, handler));
      return this;
   }

   private static class ExceptionHandlerHolder<T extends Throwable> {
      private final Class<T> clazz;
      private final HttpHandler handler;

      ExceptionHandlerHolder(Class<T> clazz, HttpHandler handler) {
         this.clazz = clazz;
         this.handler = handler;
      }

      public Class<T> getClazz() {
         return this.clazz;
      }

      public HttpHandler getHandler() {
         return this.handler;
      }
   }
}
