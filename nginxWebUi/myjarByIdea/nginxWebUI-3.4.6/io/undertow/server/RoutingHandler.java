package io.undertow.server;

import io.undertow.predicate.Predicate;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.PathTemplate;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.PathTemplateMatcher;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoutingHandler implements HttpHandler {
   private final Map<HttpString, PathTemplateMatcher<RoutingMatch>> matches = new CopyOnWriteMap();
   private final PathTemplateMatcher<RoutingMatch> allMethodsMatcher = new PathTemplateMatcher();
   private volatile HttpHandler fallbackHandler;
   private volatile HttpHandler invalidMethodHandler;
   private final boolean rewriteQueryParameters;

   public RoutingHandler(boolean rewriteQueryParameters) {
      this.fallbackHandler = ResponseCodeHandler.HANDLE_404;
      this.invalidMethodHandler = ResponseCodeHandler.HANDLE_405;
      this.rewriteQueryParameters = rewriteQueryParameters;
   }

   public RoutingHandler() {
      this.fallbackHandler = ResponseCodeHandler.HANDLE_404;
      this.invalidMethodHandler = ResponseCodeHandler.HANDLE_405;
      this.rewriteQueryParameters = true;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      PathTemplateMatcher<RoutingMatch> matcher = (PathTemplateMatcher)this.matches.get(exchange.getRequestMethod());
      if (matcher == null) {
         this.handleNoMatch(exchange);
      } else {
         PathTemplateMatcher.PathMatchResult<RoutingMatch> match = matcher.match(exchange.getRelativePath());
         if (match == null) {
            this.handleNoMatch(exchange);
         } else {
            exchange.putAttachment(PathTemplateMatch.ATTACHMENT_KEY, match);
            Iterator var4;
            if (this.rewriteQueryParameters) {
               var4 = match.getParameters().entrySet().iterator();

               while(var4.hasNext()) {
                  Map.Entry<String, String> entry = (Map.Entry)var4.next();
                  exchange.addQueryParam((String)entry.getKey(), (String)entry.getValue());
               }
            }

            var4 = ((RoutingMatch)match.getValue()).predicatedHandlers.iterator();

            HandlerHolder handler;
            do {
               if (!var4.hasNext()) {
                  if (((RoutingMatch)match.getValue()).defaultHandler != null) {
                     ((RoutingMatch)match.getValue()).defaultHandler.handleRequest(exchange);
                  } else {
                     this.fallbackHandler.handleRequest(exchange);
                  }

                  return;
               }

               handler = (HandlerHolder)var4.next();
            } while(!handler.predicate.resolve(exchange));

            handler.handler.handleRequest(exchange);
         }
      }
   }

   private void handleNoMatch(HttpServerExchange exchange) throws Exception {
      if (this.invalidMethodHandler != null && this.allMethodsMatcher.match(exchange.getRelativePath()) != null) {
         this.invalidMethodHandler.handleRequest(exchange);
      } else {
         this.fallbackHandler.handleRequest(exchange);
      }
   }

   public synchronized RoutingHandler add(String method, String template, HttpHandler handler) {
      return this.add(new HttpString(method), template, handler);
   }

   public synchronized RoutingHandler add(HttpString method, String template, HttpHandler handler) {
      PathTemplateMatcher<RoutingMatch> matcher = (PathTemplateMatcher)this.matches.get(method);
      if (matcher == null) {
         this.matches.put(method, matcher = new PathTemplateMatcher());
      }

      RoutingMatch res = (RoutingMatch)matcher.get(template);
      if (res == null) {
         matcher.add((String)template, res = new RoutingMatch());
      }

      if (this.allMethodsMatcher.match(template) == null) {
         this.allMethodsMatcher.add((String)template, res);
      }

      res.defaultHandler = handler;
      return this;
   }

   public synchronized RoutingHandler get(String template, HttpHandler handler) {
      return this.add(Methods.GET, template, handler);
   }

   public synchronized RoutingHandler post(String template, HttpHandler handler) {
      return this.add(Methods.POST, template, handler);
   }

   public synchronized RoutingHandler put(String template, HttpHandler handler) {
      return this.add(Methods.PUT, template, handler);
   }

   public synchronized RoutingHandler delete(String template, HttpHandler handler) {
      return this.add(Methods.DELETE, template, handler);
   }

   public synchronized RoutingHandler add(String method, String template, Predicate predicate, HttpHandler handler) {
      return this.add(new HttpString(method), template, predicate, handler);
   }

   public synchronized RoutingHandler add(HttpString method, String template, Predicate predicate, HttpHandler handler) {
      PathTemplateMatcher<RoutingMatch> matcher = (PathTemplateMatcher)this.matches.get(method);
      if (matcher == null) {
         this.matches.put(method, matcher = new PathTemplateMatcher());
      }

      RoutingMatch res = (RoutingMatch)matcher.get(template);
      if (res == null) {
         matcher.add((String)template, res = new RoutingMatch());
      }

      if (this.allMethodsMatcher.match(template) == null) {
         this.allMethodsMatcher.add((String)template, res);
      }

      res.predicatedHandlers.add(new HandlerHolder(predicate, handler));
      return this;
   }

   public synchronized RoutingHandler get(String template, Predicate predicate, HttpHandler handler) {
      return this.add(Methods.GET, template, predicate, handler);
   }

   public synchronized RoutingHandler post(String template, Predicate predicate, HttpHandler handler) {
      return this.add(Methods.POST, template, predicate, handler);
   }

   public synchronized RoutingHandler put(String template, Predicate predicate, HttpHandler handler) {
      return this.add(Methods.PUT, template, predicate, handler);
   }

   public synchronized RoutingHandler delete(String template, Predicate predicate, HttpHandler handler) {
      return this.add(Methods.DELETE, template, predicate, handler);
   }

   public synchronized RoutingHandler addAll(RoutingHandler routingHandler) {
      Iterator var2 = routingHandler.getMatches().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<HttpString, PathTemplateMatcher<RoutingMatch>> entry = (Map.Entry)var2.next();
         HttpString method = (HttpString)entry.getKey();
         PathTemplateMatcher<RoutingMatch> matcher = (PathTemplateMatcher)this.matches.get(method);
         if (matcher == null) {
            this.matches.put(method, matcher = new PathTemplateMatcher());
         }

         matcher.addAll((PathTemplateMatcher)entry.getValue());
         Iterator var6 = ((PathTemplateMatcher)entry.getValue()).getPathTemplates().iterator();

         while(var6.hasNext()) {
            PathTemplate template = (PathTemplate)var6.next();
            if (this.allMethodsMatcher.match(template.getTemplateString()) == null) {
               this.allMethodsMatcher.add((PathTemplate)template, new RoutingMatch());
            }
         }
      }

      return this;
   }

   public RoutingHandler remove(HttpString method, String path) {
      PathTemplateMatcher<RoutingMatch> handler = (PathTemplateMatcher)this.matches.get(method);
      if (handler != null) {
         handler.remove(path);
      }

      return this;
   }

   public RoutingHandler remove(String path) {
      this.allMethodsMatcher.remove(path);
      return this;
   }

   Map<HttpString, PathTemplateMatcher<RoutingMatch>> getMatches() {
      return this.matches;
   }

   public HttpHandler getFallbackHandler() {
      return this.fallbackHandler;
   }

   public RoutingHandler setFallbackHandler(HttpHandler fallbackHandler) {
      this.fallbackHandler = fallbackHandler;
      return this;
   }

   public HttpHandler getInvalidMethodHandler() {
      return this.invalidMethodHandler;
   }

   public RoutingHandler setInvalidMethodHandler(HttpHandler invalidMethodHandler) {
      this.invalidMethodHandler = invalidMethodHandler;
      return this;
   }

   private static class HandlerHolder {
      final Predicate predicate;
      final HttpHandler handler;

      private HandlerHolder(Predicate predicate, HttpHandler handler) {
         this.predicate = predicate;
         this.handler = handler;
      }

      // $FF: synthetic method
      HandlerHolder(Predicate x0, HttpHandler x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class RoutingMatch {
      final List<HandlerHolder> predicatedHandlers;
      volatile HttpHandler defaultHandler;

      private RoutingMatch() {
         this.predicatedHandlers = new CopyOnWriteArrayList();
      }

      // $FF: synthetic method
      RoutingMatch(Object x0) {
         this();
      }
   }
}
