package io.undertow.server.handlers;

import io.undertow.UndertowMessages;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.LongUnaryOperator;

public class GracefulShutdownHandler implements HttpHandler {
   private static final long SHUTDOWN_MASK = Long.MIN_VALUE;
   private static final long ACTIVE_COUNT_MASK = Long.MAX_VALUE;
   private static final LongUnaryOperator incrementActive = (current) -> {
      long incrementedActiveCount = activeCount(current) + 1L;
      return incrementedActiveCount | current & Long.MIN_VALUE;
   };
   private static final LongUnaryOperator incrementActiveAndShutdown;
   private static final LongUnaryOperator decrementActive;
   private final GracefulShutdownListener listener = new GracefulShutdownListener();
   private final List<ShutdownListener> shutdownListeners = new ArrayList();
   private final Object lock = new Object();
   private volatile long state = 0L;
   private static final AtomicLongFieldUpdater<GracefulShutdownHandler> stateUpdater;
   private final HttpHandler next;

   public GracefulShutdownHandler(HttpHandler next) {
      this.next = next;
   }

   private static boolean isShutdown(long state) {
      return (state & Long.MIN_VALUE) != 0L;
   }

   private static long activeCount(long state) {
      return state & Long.MAX_VALUE;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      long snapshot = stateUpdater.updateAndGet(this, incrementActive);
      if (isShutdown(snapshot)) {
         this.decrementRequests();
         exchange.setStatusCode(503);
         exchange.endExchange();
      } else {
         exchange.addExchangeCompleteListener(this.listener);
         this.next.handleRequest(exchange);
      }
   }

   public void shutdown() {
      stateUpdater.updateAndGet(this, incrementActiveAndShutdown);
      this.decrementRequests();
   }

   public void start() {
      synchronized(this.lock) {
         stateUpdater.updateAndGet(this, (current) -> {
            return current & Long.MAX_VALUE;
         });
         Iterator var2 = this.shutdownListeners.iterator();

         while(var2.hasNext()) {
            ShutdownListener listener = (ShutdownListener)var2.next();
            listener.shutdown(false);
         }

         this.shutdownListeners.clear();
      }
   }

   private void shutdownComplete() {
      synchronized(this.lock) {
         this.lock.notifyAll();
         Iterator var2 = this.shutdownListeners.iterator();

         while(var2.hasNext()) {
            ShutdownListener listener = (ShutdownListener)var2.next();
            listener.shutdown(true);
         }

         this.shutdownListeners.clear();
      }
   }

   public void awaitShutdown() throws InterruptedException {
      synchronized(this.lock) {
         if (!isShutdown(stateUpdater.get(this))) {
            throw UndertowMessages.MESSAGES.handlerNotShutdown();
         } else {
            while(activeCount(stateUpdater.get(this)) > 0L) {
               this.lock.wait();
            }

         }
      }
   }

   public boolean awaitShutdown(long millis) throws InterruptedException {
      synchronized(this.lock) {
         if (!isShutdown(stateUpdater.get(this))) {
            throw UndertowMessages.MESSAGES.handlerNotShutdown();
         } else {
            long end = System.currentTimeMillis() + millis;

            while(activeCount(stateUpdater.get(this)) != 0L) {
               long left = end - System.currentTimeMillis();
               if (left <= 0L) {
                  return false;
               }

               this.lock.wait(left);
            }

            return true;
         }
      }
   }

   public void addShutdownListener(ShutdownListener shutdownListener) {
      synchronized(this.lock) {
         if (!isShutdown(stateUpdater.get(this))) {
            throw UndertowMessages.MESSAGES.handlerNotShutdown();
         } else {
            long count = activeCount(stateUpdater.get(this));
            if (count == 0L) {
               shutdownListener.shutdown(true);
            } else {
               this.shutdownListeners.add(shutdownListener);
            }

         }
      }
   }

   private void decrementRequests() {
      long snapshot = stateUpdater.updateAndGet(this, decrementActive);
      if (snapshot == Long.MIN_VALUE) {
         this.shutdownComplete();
      }

   }

   static {
      incrementActiveAndShutdown = incrementActive.andThen((current) -> {
         return current | Long.MIN_VALUE;
      });
      decrementActive = (current) -> {
         long decrementedActiveCount = activeCount(current) - 1L;
         return decrementedActiveCount | current & Long.MIN_VALUE;
      };
      stateUpdater = AtomicLongFieldUpdater.newUpdater(GracefulShutdownHandler.class, "state");
   }

   public interface ShutdownListener {
      void shutdown(boolean var1);
   }

   private final class GracefulShutdownListener implements ExchangeCompletionListener {
      private GracefulShutdownListener() {
      }

      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
         try {
            GracefulShutdownHandler.this.decrementRequests();
         } finally {
            nextListener.proceed();
         }

      }

      // $FF: synthetic method
      GracefulShutdownListener(Object x1) {
         this();
      }
   }
}
