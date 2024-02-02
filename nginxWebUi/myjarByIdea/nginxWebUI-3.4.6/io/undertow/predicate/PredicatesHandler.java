package io.undertow.predicate;

import io.undertow.UndertowLogger;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.server.handlers.builder.PredicatedHandler;
import io.undertow.util.AttachmentKey;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class PredicatesHandler implements HttpHandler {
   public static final AttachmentKey<Boolean> DONE = AttachmentKey.create(Boolean.class);
   public static final AttachmentKey<Boolean> RESTART = AttachmentKey.create(Boolean.class);
   private static final boolean traceEnabled;
   private volatile Holder[] handlers = new Holder[0];
   private volatile HttpHandler next;
   private final boolean outerHandler;
   private final AttachmentKey<Integer> CURRENT_POSITION = AttachmentKey.create(Integer.class);

   public PredicatesHandler(HttpHandler next) {
      this.next = next;
      this.outerHandler = true;
   }

   public PredicatesHandler(HttpHandler next, boolean outerHandler) {
      this.next = next;
      this.outerHandler = outerHandler;
   }

   public String toString() {
      return "PredicatesHandler with " + this.handlers.length + " predicates";
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      int length = this.handlers.length;
      Integer current = (Integer)exchange.getAttachment(this.CURRENT_POSITION);

      do {
         int pos;
         if (current == null) {
            if (this.outerHandler) {
               exchange.removeAttachment(RESTART);
               exchange.removeAttachment(DONE);
               if (exchange.getAttachment(Predicate.PREDICATE_CONTEXT) == null) {
                  exchange.putAttachment(Predicate.PREDICATE_CONTEXT, new TreeMap());
               }
            }

            pos = 0;
         } else {
            if (exchange.getAttachment(DONE) != null) {
               if (traceEnabled && this.outerHandler) {
                  UndertowLogger.PREDICATE_LOGGER.tracef("Predicate chain marked done. Next handler is [%s] for %s.", this.next.toString(), exchange);
               }

               exchange.removeAttachment(this.CURRENT_POSITION);
               this.next.handleRequest(exchange);
               return;
            }

            pos = current;
         }

         for(; pos < length; ++pos) {
            Holder handler = this.handlers[pos];
            if (handler.predicate.resolve(exchange)) {
               if (traceEnabled) {
                  if (handler.predicate.toString().equals("true")) {
                     UndertowLogger.PREDICATE_LOGGER.tracef("Executing handler [%s] for %s.", handler.handler.toString(), exchange);
                  } else {
                     UndertowLogger.PREDICATE_LOGGER.tracef("Predicate [%s] resolved to true. Next handler is [%s] for %s.", handler.predicate.toString(), handler.handler.toString(), exchange);
                  }
               }

               exchange.putAttachment(this.CURRENT_POSITION, pos + 1);
               handler.handler.handleRequest(exchange);
               if (!this.shouldRestart(exchange, current)) {
                  return;
               }

               if (traceEnabled) {
                  UndertowLogger.PREDICATE_LOGGER.tracef("Restarting predicate resolution for %s.", exchange);
               }
               break;
            }

            if (handler.elseBranch != null) {
               if (traceEnabled) {
                  UndertowLogger.PREDICATE_LOGGER.tracef("Predicate [%s] resolved to false. Else branch is [%s] for %s.", handler.predicate.toString(), handler.elseBranch.toString(), exchange);
               }

               exchange.putAttachment(this.CURRENT_POSITION, pos + 1);
               handler.elseBranch.handleRequest(exchange);
               if (!this.shouldRestart(exchange, current)) {
                  return;
               }

               if (traceEnabled) {
                  UndertowLogger.PREDICATE_LOGGER.tracef("Restarting predicate resolution for %s.", exchange);
               }
               break;
            }

            if (traceEnabled) {
               UndertowLogger.PREDICATE_LOGGER.tracef("Predicate [%s] resolved to false for %s.", handler.predicate.toString(), exchange);
            }
         }
      } while(this.shouldRestart(exchange, current));

      this.next.handleRequest(exchange);
   }

   private boolean shouldRestart(HttpServerExchange exchange, Integer current) {
      return exchange.getAttachment(RESTART) != null && this.outerHandler && current == null;
   }

   public PredicatesHandler addPredicatedHandler(Predicate predicate, HandlerWrapper handlerWrapper, HandlerWrapper elseBranch) {
      Holder[] old = this.handlers;
      Holder[] handlers = new Holder[old.length + 1];
      System.arraycopy(old, 0, handlers, 0, old.length);
      HttpHandler elseHandler = elseBranch != null ? elseBranch.wrap(this) : null;
      handlers[old.length] = new Holder(predicate, handlerWrapper.wrap(this), elseHandler);
      this.handlers = handlers;
      return this;
   }

   public PredicatesHandler addPredicatedHandler(Predicate predicate, HandlerWrapper handlerWrapper) {
      this.addPredicatedHandler(predicate, handlerWrapper, (HandlerWrapper)null);
      return this;
   }

   public PredicatesHandler addPredicatedHandler(PredicatedHandler handler) {
      return this.addPredicatedHandler(handler.getPredicate(), handler.getHandler(), handler.getElseHandler());
   }

   public void setNext(HttpHandler next) {
      this.next = next;
   }

   public HttpHandler getNext() {
      return this.next;
   }

   static {
      traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
   }

   public static class Wrapper implements HandlerWrapper {
      private final List<PredicatedHandler> handlers;
      private final boolean outerHandler;

      public Wrapper(List<PredicatedHandler> handlers, boolean outerHandler) {
         this.handlers = handlers;
         this.outerHandler = outerHandler;
      }

      public HttpHandler wrap(HttpHandler handler) {
         PredicatesHandler h = new PredicatesHandler(handler, this.outerHandler);
         Iterator var3 = this.handlers.iterator();

         while(var3.hasNext()) {
            PredicatedHandler pred = (PredicatedHandler)var3.next();
            h.addPredicatedHandler(pred.getPredicate(), pred.getHandler());
         }

         return h;
      }
   }

   public static final class RestartHandlerBuilder implements HandlerBuilder {
      private static final AttachmentKey<Integer> RESTART_COUNT = AttachmentKey.create(Integer.class);
      private static final int MAX_RESTARTS = Integer.getInteger("io.undertow.max_restarts", 1000);

      public String name() {
         return "restart";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new HandlerWrapper() {
            public HttpHandler wrap(HttpHandler handler) {
               return new HttpHandler() {
                  public void handleRequest(HttpServerExchange exchange) throws Exception {
                     Integer restarts = (Integer)exchange.getAttachment(PredicatesHandler.RestartHandlerBuilder.RESTART_COUNT);
                     if (restarts == null) {
                        restarts = 1;
                     } else {
                        restarts = restarts + 1;
                     }

                     exchange.putAttachment(PredicatesHandler.RestartHandlerBuilder.RESTART_COUNT, restarts);
                     if (restarts > PredicatesHandler.RestartHandlerBuilder.MAX_RESTARTS) {
                        throw UndertowLogger.ROOT_LOGGER.maxRestartsExceeded(PredicatesHandler.RestartHandlerBuilder.MAX_RESTARTS);
                     } else {
                        exchange.putAttachment(PredicatesHandler.RESTART, true);
                     }
                  }

                  public String toString() {
                     return "restart";
                  }
               };
            }
         };
      }
   }

   public static final class DoneHandlerBuilder implements HandlerBuilder {
      public String name() {
         return "done";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new HandlerWrapper() {
            public HttpHandler wrap(final HttpHandler handler) {
               return new HttpHandler() {
                  public void handleRequest(HttpServerExchange exchange) throws Exception {
                     exchange.putAttachment(PredicatesHandler.DONE, true);
                     handler.handleRequest(exchange);
                  }

                  public String toString() {
                     return "done";
                  }
               };
            }
         };
      }
   }

   private static final class Holder {
      final Predicate predicate;
      final HttpHandler handler;
      final HttpHandler elseBranch;

      private Holder(Predicate predicate, HttpHandler handler, HttpHandler elseBranch) {
         this.predicate = predicate;
         this.handler = handler;
         this.elseBranch = elseBranch;
      }

      // $FF: synthetic method
      Holder(Predicate x0, HttpHandler x1, HttpHandler x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
