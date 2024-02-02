package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.Connectors;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.SameThreadExecutor;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class RequestLimit {
   private volatile int requests;
   private volatile int max;
   private static final AtomicIntegerFieldUpdater<RequestLimit> requestsUpdater = AtomicIntegerFieldUpdater.newUpdater(RequestLimit.class, "requests");
   private volatile HttpHandler failureHandler;
   private final Queue<SuspendedRequest> queue;
   private final ExchangeCompletionListener COMPLETION_LISTENER;

   public RequestLimit(int maximumConcurrentRequests) {
      this(maximumConcurrentRequests, -1);
   }

   public RequestLimit(int maximumConcurrentRequests, int queueSize) {
      this.failureHandler = new ResponseCodeHandler(503);
      this.COMPLETION_LISTENER = new ExchangeCompletionListener() {
         public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
            SuspendedRequest task = null;
            boolean found = false;

            while((task = (SuspendedRequest)RequestLimit.this.queue.poll()) != null) {
               try {
                  task.exchange.addExchangeCompleteListener(RequestLimit.this.COMPLETION_LISTENER);
                  task.exchange.dispatch(task.next);
                  found = true;
                  break;
               } catch (Throwable var6) {
                  UndertowLogger.ROOT_LOGGER.error("Suspended request was skipped", var6);
               }
            }

            if (!found) {
               RequestLimit.this.decrementRequests();
            }

            nextListener.proceed();
         }
      };
      if (maximumConcurrentRequests < 1) {
         throw new IllegalArgumentException("Maximum concurrent requests must be at least 1");
      } else {
         this.max = maximumConcurrentRequests;
         this.queue = new LinkedBlockingQueue(queueSize <= 0 ? Integer.MAX_VALUE : queueSize);
      }
   }

   public void handleRequest(final HttpServerExchange exchange, final HttpHandler next) throws Exception {
      int oldVal;
      int newVal;
      do {
         oldVal = this.requests;
         if (oldVal >= this.max) {
            exchange.dispatch(SameThreadExecutor.INSTANCE, new Runnable() {
               public void run() {
                  synchronized(RequestLimit.this) {
                     int oldVal;
                     int newVal;
                     do {
                        oldVal = RequestLimit.this.requests;
                        if (oldVal >= RequestLimit.this.max) {
                           if (!RequestLimit.this.queue.offer(new SuspendedRequest(exchange, next))) {
                              Connectors.executeRootHandler(RequestLimit.this.failureHandler, exchange);
                           }

                           return;
                        }

                        newVal = oldVal + 1;
                     } while(!RequestLimit.requestsUpdater.compareAndSet(RequestLimit.this, oldVal, newVal));

                     exchange.addExchangeCompleteListener(RequestLimit.this.COMPLETION_LISTENER);
                     exchange.dispatch(next);
                  }
               }
            });
            return;
         }

         newVal = oldVal + 1;
      } while(!requestsUpdater.compareAndSet(this, oldVal, newVal));

      exchange.addExchangeCompleteListener(this.COMPLETION_LISTENER);
      next.handleRequest(exchange);
   }

   public int getMaximumConcurrentRequests() {
      return this.max;
   }

   public int setMaximumConcurrentRequests(int newMax) {
      if (newMax < 1) {
         throw new IllegalArgumentException("Maximum concurrent requests must be at least 1");
      } else {
         int oldMax = this.max;
         this.max = newMax;
         if (newMax > oldMax) {
            synchronized(this) {
               while(true) {
                  if (this.queue.isEmpty()) {
                     break;
                  }

                  int oldVal;
                  int newVal;
                  do {
                     oldVal = this.requests;
                     if (oldVal >= this.max) {
                        return oldMax;
                     }

                     newVal = oldVal + 1;
                  } while(!requestsUpdater.compareAndSet(this, oldVal, newVal));

                  SuspendedRequest res = (SuspendedRequest)this.queue.poll();
                  res.exchange.dispatch(res.next);
               }
            }
         }

         return oldMax;
      }
   }

   private void decrementRequests() {
      requestsUpdater.decrementAndGet(this);
   }

   public HttpHandler getFailureHandler() {
      return this.failureHandler;
   }

   public void setFailureHandler(HttpHandler failureHandler) {
      this.failureHandler = failureHandler;
   }

   private static final class SuspendedRequest {
      final HttpServerExchange exchange;
      final HttpHandler next;

      private SuspendedRequest(HttpServerExchange exchange, HttpHandler next) {
         this.exchange = exchange;
         this.next = next;
      }

      // $FF: synthetic method
      SuspendedRequest(HttpServerExchange x0, HttpHandler x1, Object x2) {
         this(x0, x1);
      }
   }
}
