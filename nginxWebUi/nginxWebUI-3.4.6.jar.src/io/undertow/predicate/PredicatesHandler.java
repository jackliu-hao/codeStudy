/*     */ package io.undertow.predicate;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.server.handlers.builder.PredicatedHandler;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
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
/*     */ public class PredicatesHandler
/*     */   implements HttpHandler
/*     */ {
/*  46 */   public static final AttachmentKey<Boolean> DONE = AttachmentKey.create(Boolean.class);
/*  47 */   public static final AttachmentKey<Boolean> RESTART = AttachmentKey.create(Boolean.class);
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static final boolean traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
/*     */ 
/*     */   
/*  54 */   private volatile Holder[] handlers = new Holder[0];
/*     */   
/*     */   private volatile HttpHandler next;
/*     */   
/*     */   private final boolean outerHandler;
/*  59 */   private final AttachmentKey<Integer> CURRENT_POSITION = AttachmentKey.create(Integer.class);
/*     */   
/*     */   public PredicatesHandler(HttpHandler next) {
/*  62 */     this.next = next;
/*  63 */     this.outerHandler = true;
/*     */   }
/*     */   public PredicatesHandler(HttpHandler next, boolean outerHandler) {
/*  66 */     this.next = next;
/*  67 */     this.outerHandler = outerHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  72 */     return "PredicatesHandler with " + this.handlers.length + " predicates";
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  77 */     int length = this.handlers.length;
/*  78 */     Integer current = (Integer)exchange.getAttachment(this.CURRENT_POSITION);
/*     */     while (true) {
/*     */       int pos;
/*  81 */       if (current == null) {
/*  82 */         if (this.outerHandler) {
/*  83 */           exchange.removeAttachment(RESTART);
/*  84 */           exchange.removeAttachment(DONE);
/*  85 */           if (exchange.getAttachment(Predicate.PREDICATE_CONTEXT) == null) {
/*  86 */             exchange.putAttachment(Predicate.PREDICATE_CONTEXT, new TreeMap<>());
/*     */           }
/*     */         } 
/*  89 */         pos = 0;
/*     */       } else {
/*     */         
/*  92 */         if (exchange.getAttachment(DONE) != null) {
/*  93 */           if (traceEnabled && this.outerHandler) {
/*  94 */             UndertowLogger.PREDICATE_LOGGER.tracef("Predicate chain marked done. Next handler is [%s] for %s.", this.next.toString(), exchange);
/*     */           }
/*  96 */           exchange.removeAttachment(this.CURRENT_POSITION);
/*  97 */           this.next.handleRequest(exchange);
/*     */           return;
/*     */         } 
/* 100 */         pos = current.intValue();
/*     */       } 
/* 102 */       for (; pos < length; pos++) {
/* 103 */         Holder handler = this.handlers[pos];
/* 104 */         if (handler.predicate.resolve(exchange)) {
/* 105 */           if (traceEnabled) {
/* 106 */             if (handler.predicate.toString().equals("true")) {
/* 107 */               UndertowLogger.PREDICATE_LOGGER.tracef("Executing handler [%s] for %s.", handler.handler.toString(), exchange);
/*     */             } else {
/* 109 */               UndertowLogger.PREDICATE_LOGGER.tracef("Predicate [%s] resolved to true. Next handler is [%s] for %s.", handler.predicate.toString(), handler.handler.toString(), exchange);
/*     */             } 
/*     */           }
/* 112 */           exchange.putAttachment(this.CURRENT_POSITION, Integer.valueOf(pos + 1));
/* 113 */           handler.handler.handleRequest(exchange);
/* 114 */           if (shouldRestart(exchange, current)) {
/* 115 */             if (traceEnabled) {
/* 116 */               UndertowLogger.PREDICATE_LOGGER.tracef("Restarting predicate resolution for %s.", exchange);
/*     */             }
/*     */             break;
/*     */           } 
/*     */           return;
/*     */         } 
/* 122 */         if (handler.elseBranch != null) {
/* 123 */           if (traceEnabled) {
/* 124 */             UndertowLogger.PREDICATE_LOGGER.tracef("Predicate [%s] resolved to false. Else branch is [%s] for %s.", handler.predicate.toString(), handler.elseBranch.toString(), exchange);
/*     */           }
/* 126 */           exchange.putAttachment(this.CURRENT_POSITION, Integer.valueOf(pos + 1));
/* 127 */           handler.elseBranch.handleRequest(exchange);
/* 128 */           if (shouldRestart(exchange, current)) {
/* 129 */             if (traceEnabled) {
/* 130 */               UndertowLogger.PREDICATE_LOGGER.tracef("Restarting predicate resolution for %s.", exchange);
/*     */             }
/*     */             break;
/*     */           } 
/*     */           return;
/*     */         } 
/* 136 */         if (traceEnabled) {
/* 137 */           UndertowLogger.PREDICATE_LOGGER.tracef("Predicate [%s] resolved to false for %s.", handler.predicate.toString(), exchange);
/*     */         }
/*     */       } 
/* 140 */       if (!shouldRestart(exchange, current)) {
/* 141 */         this.next.handleRequest(exchange);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   } private boolean shouldRestart(HttpServerExchange exchange, Integer current) {
/* 146 */     return (exchange.getAttachment(RESTART) != null && this.outerHandler && current == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PredicatesHandler addPredicatedHandler(Predicate predicate, HandlerWrapper handlerWrapper, HandlerWrapper elseBranch) {
/* 157 */     Holder[] old = this.handlers;
/* 158 */     Holder[] handlers = new Holder[old.length + 1];
/* 159 */     System.arraycopy(old, 0, handlers, 0, old.length);
/* 160 */     HttpHandler elseHandler = (elseBranch != null) ? elseBranch.wrap(this) : null;
/* 161 */     handlers[old.length] = new Holder(predicate, handlerWrapper.wrap(this), elseHandler);
/* 162 */     this.handlers = handlers;
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PredicatesHandler addPredicatedHandler(Predicate predicate, HandlerWrapper handlerWrapper) {
/* 174 */     addPredicatedHandler(predicate, handlerWrapper, null);
/* 175 */     return this;
/*     */   }
/*     */   
/*     */   public PredicatesHandler addPredicatedHandler(PredicatedHandler handler) {
/* 179 */     return addPredicatedHandler(handler.getPredicate(), handler.getHandler(), handler.getElseHandler());
/*     */   }
/*     */   
/*     */   public void setNext(HttpHandler next) {
/* 183 */     this.next = next;
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/* 187 */     return this.next;
/*     */   }
/*     */   
/*     */   private static final class Holder {
/*     */     final Predicate predicate;
/*     */     final HttpHandler handler;
/*     */     final HttpHandler elseBranch;
/*     */     
/*     */     private Holder(Predicate predicate, HttpHandler handler, HttpHandler elseBranch) {
/* 196 */       this.predicate = predicate;
/* 197 */       this.handler = handler;
/* 198 */       this.elseBranch = elseBranch;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class DoneHandlerBuilder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 206 */       return "done";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 211 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 216 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 221 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 226 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(final HttpHandler handler) {
/* 229 */             return new HttpHandler()
/*     */               {
/*     */                 public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 232 */                   exchange.putAttachment(PredicatesHandler.DONE, Boolean.valueOf(true));
/* 233 */                   handler.handleRequest(exchange);
/*     */                 }
/*     */                 
/*     */                 public String toString() {
/* 237 */                   return "done";
/*     */                 }
/*     */               };
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class RestartHandlerBuilder
/*     */     implements HandlerBuilder {
/* 247 */     private static final AttachmentKey<Integer> RESTART_COUNT = AttachmentKey.create(Integer.class);
/*     */     
/* 249 */     private static final int MAX_RESTARTS = Integer.getInteger("io.undertow.max_restarts", 1000).intValue();
/*     */ 
/*     */     
/*     */     public String name() {
/* 253 */       return "restart";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 258 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 263 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 268 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 273 */       return new HandlerWrapper()
/*     */         {
/*     */           public HttpHandler wrap(HttpHandler handler) {
/* 276 */             return new HttpHandler()
/*     */               {
/*     */                 public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 279 */                   Integer restarts = (Integer)exchange.getAttachment(PredicatesHandler.RestartHandlerBuilder.RESTART_COUNT);
/* 280 */                   if (restarts == null) {
/* 281 */                     restarts = Integer.valueOf(1);
/*     */                   } else {
/* 283 */                     Integer integer1 = restarts, integer2 = restarts = Integer.valueOf(restarts.intValue() + 1);
/*     */                   } 
/* 285 */                   exchange.putAttachment(PredicatesHandler.RestartHandlerBuilder.RESTART_COUNT, restarts);
/* 286 */                   if (restarts.intValue() > PredicatesHandler.RestartHandlerBuilder.MAX_RESTARTS) {
/* 287 */                     throw UndertowLogger.ROOT_LOGGER.maxRestartsExceeded(PredicatesHandler.RestartHandlerBuilder.MAX_RESTARTS);
/*     */                   }
/* 289 */                   exchange.putAttachment(PredicatesHandler.RESTART, Boolean.valueOf(true));
/*     */                 }
/*     */                 
/*     */                 public String toString() {
/* 293 */                   return "restart";
/*     */                 }
/*     */               };
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final List<PredicatedHandler> handlers;
/*     */     private final boolean outerHandler;
/*     */     
/*     */     public Wrapper(List<PredicatedHandler> handlers, boolean outerHandler) {
/* 308 */       this.handlers = handlers;
/* 309 */       this.outerHandler = outerHandler;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 314 */       PredicatesHandler h = new PredicatesHandler(handler, this.outerHandler);
/* 315 */       for (PredicatedHandler pred : this.handlers) {
/* 316 */         h.addPredicatedHandler(pred.getPredicate(), pred.getHandler());
/*     */       }
/* 318 */       return h;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\PredicatesHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */