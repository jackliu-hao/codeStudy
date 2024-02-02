/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.PathTemplate;
/*     */ import io.undertow.util.PathTemplateMatch;
/*     */ import io.undertow.util.PathTemplateMatcher;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
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
/*     */ public class RoutingHandler
/*     */   implements HttpHandler
/*     */ {
/*  43 */   private final Map<HttpString, PathTemplateMatcher<RoutingMatch>> matches = (Map<HttpString, PathTemplateMatcher<RoutingMatch>>)new CopyOnWriteMap();
/*     */ 
/*     */   
/*  46 */   private final PathTemplateMatcher<RoutingMatch> allMethodsMatcher = new PathTemplateMatcher();
/*     */ 
/*     */   
/*  49 */   private volatile HttpHandler fallbackHandler = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   private volatile HttpHandler invalidMethodHandler = (HttpHandler)ResponseCodeHandler.HANDLE_405;
/*     */   
/*     */   private final boolean rewriteQueryParameters;
/*     */ 
/*     */   
/*     */   public RoutingHandler(boolean rewriteQueryParameters) {
/*  60 */     this.rewriteQueryParameters = rewriteQueryParameters;
/*     */   }
/*     */   
/*     */   public RoutingHandler() {
/*  64 */     this.rewriteQueryParameters = true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  70 */     PathTemplateMatcher<RoutingMatch> matcher = this.matches.get(exchange.getRequestMethod());
/*  71 */     if (matcher == null) {
/*  72 */       handleNoMatch(exchange);
/*     */       return;
/*     */     } 
/*  75 */     PathTemplateMatcher.PathMatchResult<RoutingMatch> match = matcher.match(exchange.getRelativePath());
/*  76 */     if (match == null) {
/*  77 */       handleNoMatch(exchange);
/*     */       return;
/*     */     } 
/*  80 */     exchange.putAttachment(PathTemplateMatch.ATTACHMENT_KEY, match);
/*  81 */     if (this.rewriteQueryParameters) {
/*  82 */       for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)match.getParameters().entrySet()) {
/*  83 */         exchange.addQueryParam(entry.getKey(), entry.getValue());
/*     */       }
/*     */     }
/*  86 */     for (HandlerHolder handler : ((RoutingMatch)match.getValue()).predicatedHandlers) {
/*  87 */       if (handler.predicate.resolve(exchange)) {
/*  88 */         handler.handler.handleRequest(exchange);
/*     */         return;
/*     */       } 
/*     */     } 
/*  92 */     if (((RoutingMatch)match.getValue()).defaultHandler != null) {
/*  93 */       ((RoutingMatch)match.getValue()).defaultHandler.handleRequest(exchange);
/*     */     } else {
/*  95 */       this.fallbackHandler.handleRequest(exchange);
/*     */     } 
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
/*     */   private void handleNoMatch(HttpServerExchange exchange) throws Exception {
/* 108 */     if (this.invalidMethodHandler != null && this.allMethodsMatcher.match(exchange.getRelativePath()) != null) {
/* 109 */       this.invalidMethodHandler.handleRequest(exchange);
/*     */       return;
/*     */     } 
/* 112 */     this.fallbackHandler.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler add(String method, String template, HttpHandler handler) {
/* 116 */     return add(new HttpString(method), template, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler add(HttpString method, String template, HttpHandler handler) {
/* 120 */     PathTemplateMatcher<RoutingMatch> matcher = this.matches.get(method);
/* 121 */     if (matcher == null) {
/* 122 */       this.matches.put(method, matcher = new PathTemplateMatcher());
/*     */     }
/* 124 */     RoutingMatch res = (RoutingMatch)matcher.get(template);
/* 125 */     if (res == null) {
/* 126 */       matcher.add(template, res = new RoutingMatch());
/*     */     }
/* 128 */     if (this.allMethodsMatcher.match(template) == null) {
/* 129 */       this.allMethodsMatcher.add(template, res);
/*     */     }
/* 131 */     res.defaultHandler = handler;
/* 132 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler get(String template, HttpHandler handler) {
/* 136 */     return add(Methods.GET, template, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler post(String template, HttpHandler handler) {
/* 140 */     return add(Methods.POST, template, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler put(String template, HttpHandler handler) {
/* 144 */     return add(Methods.PUT, template, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler delete(String template, HttpHandler handler) {
/* 148 */     return add(Methods.DELETE, template, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler add(String method, String template, Predicate predicate, HttpHandler handler) {
/* 152 */     return add(new HttpString(method), template, predicate, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler add(HttpString method, String template, Predicate predicate, HttpHandler handler) {
/* 156 */     PathTemplateMatcher<RoutingMatch> matcher = this.matches.get(method);
/* 157 */     if (matcher == null) {
/* 158 */       this.matches.put(method, matcher = new PathTemplateMatcher());
/*     */     }
/* 160 */     RoutingMatch res = (RoutingMatch)matcher.get(template);
/* 161 */     if (res == null) {
/* 162 */       matcher.add(template, res = new RoutingMatch());
/*     */     }
/* 164 */     if (this.allMethodsMatcher.match(template) == null) {
/* 165 */       this.allMethodsMatcher.add(template, res);
/*     */     }
/* 167 */     res.predicatedHandlers.add(new HandlerHolder(predicate, handler));
/* 168 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler get(String template, Predicate predicate, HttpHandler handler) {
/* 172 */     return add(Methods.GET, template, predicate, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler post(String template, Predicate predicate, HttpHandler handler) {
/* 176 */     return add(Methods.POST, template, predicate, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler put(String template, Predicate predicate, HttpHandler handler) {
/* 180 */     return add(Methods.PUT, template, predicate, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler delete(String template, Predicate predicate, HttpHandler handler) {
/* 184 */     return add(Methods.DELETE, template, predicate, handler);
/*     */   }
/*     */   
/*     */   public synchronized RoutingHandler addAll(RoutingHandler routingHandler) {
/* 188 */     for (Map.Entry<HttpString, PathTemplateMatcher<RoutingMatch>> entry : routingHandler.getMatches().entrySet()) {
/* 189 */       HttpString method = entry.getKey();
/* 190 */       PathTemplateMatcher<RoutingMatch> matcher = this.matches.get(method);
/* 191 */       if (matcher == null) {
/* 192 */         this.matches.put(method, matcher = new PathTemplateMatcher());
/*     */       }
/* 194 */       matcher.addAll(entry.getValue());
/*     */ 
/*     */       
/* 197 */       for (PathTemplate template : ((PathTemplateMatcher)entry.getValue()).getPathTemplates()) {
/* 198 */         if (this.allMethodsMatcher.match(template.getTemplateString()) == null) {
/* 199 */           this.allMethodsMatcher.add(template, new RoutingMatch());
/*     */         }
/*     */       } 
/*     */     } 
/* 203 */     return this;
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
/*     */   public RoutingHandler remove(HttpString method, String path) {
/* 215 */     PathTemplateMatcher<RoutingMatch> handler = this.matches.get(method);
/* 216 */     if (handler != null) {
/* 217 */       handler.remove(path);
/*     */     }
/* 219 */     return this;
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
/*     */   public RoutingHandler remove(String path) {
/* 231 */     this.allMethodsMatcher.remove(path);
/* 232 */     return this;
/*     */   }
/*     */   
/*     */   Map<HttpString, PathTemplateMatcher<RoutingMatch>> getMatches() {
/* 236 */     return this.matches;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler getFallbackHandler() {
/* 243 */     return this.fallbackHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RoutingHandler setFallbackHandler(HttpHandler fallbackHandler) {
/* 252 */     this.fallbackHandler = fallbackHandler;
/* 253 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler getInvalidMethodHandler() {
/* 260 */     return this.invalidMethodHandler;
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
/*     */   public RoutingHandler setInvalidMethodHandler(HttpHandler invalidMethodHandler) {
/* 274 */     this.invalidMethodHandler = invalidMethodHandler;
/* 275 */     return this;
/*     */   }
/*     */   
/*     */   private static class RoutingMatch
/*     */   {
/* 280 */     final List<RoutingHandler.HandlerHolder> predicatedHandlers = new CopyOnWriteArrayList<>();
/*     */     
/*     */     private RoutingMatch() {}
/*     */     
/*     */     volatile HttpHandler defaultHandler; }
/*     */   
/*     */   private static class HandlerHolder { final Predicate predicate;
/*     */     final HttpHandler handler;
/*     */     
/*     */     private HandlerHolder(Predicate predicate, HttpHandler handler) {
/* 290 */       this.predicate = predicate;
/* 291 */       this.handler = handler;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\RoutingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */