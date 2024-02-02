package io.undertow.server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.PathTemplate;
import io.undertow.util.PathTemplateMatcher;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PathTemplateHandler implements HttpHandler {
   private final boolean rewriteQueryParameters;
   private final HttpHandler next;
   /** @deprecated */
   @Deprecated
   public static final AttachmentKey<PathTemplateMatch> PATH_TEMPLATE_MATCH = AttachmentKey.create(PathTemplateMatch.class);
   private final PathTemplateMatcher<HttpHandler> pathTemplateMatcher;

   public PathTemplateHandler() {
      this(true);
   }

   public PathTemplateHandler(boolean rewriteQueryParameters) {
      this(ResponseCodeHandler.HANDLE_404, rewriteQueryParameters);
   }

   public PathTemplateHandler(HttpHandler next) {
      this(next, true);
   }

   public PathTemplateHandler(HttpHandler next, boolean rewriteQueryParameters) {
      this.pathTemplateMatcher = new PathTemplateMatcher();
      this.rewriteQueryParameters = rewriteQueryParameters;
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      PathTemplateMatcher.PathMatchResult<HttpHandler> match = this.pathTemplateMatcher.match(exchange.getRelativePath());
      if (match == null) {
         this.next.handleRequest(exchange);
      } else {
         exchange.putAttachment(PATH_TEMPLATE_MATCH, new PathTemplateMatch(match.getMatchedTemplate(), match.getParameters()));
         exchange.putAttachment(io.undertow.util.PathTemplateMatch.ATTACHMENT_KEY, new io.undertow.util.PathTemplateMatch(match.getMatchedTemplate(), match.getParameters()));
         if (this.rewriteQueryParameters) {
            Iterator var3 = match.getParameters().entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry<String, String> entry = (Map.Entry)var3.next();
               exchange.addQueryParam((String)entry.getKey(), (String)entry.getValue());
            }
         }

         ((HttpHandler)match.getValue()).handleRequest(exchange);
      }
   }

   public PathTemplateHandler add(String uriTemplate, HttpHandler handler) {
      this.pathTemplateMatcher.add((String)uriTemplate, handler);
      return this;
   }

   public PathTemplateHandler remove(String uriTemplate) {
      this.pathTemplateMatcher.remove(uriTemplate);
      return this;
   }

   public String toString() {
      Set<PathTemplate> paths = this.pathTemplateMatcher.getPathTemplates();
      return paths.size() == 1 ? "path-template( " + paths.toArray()[0] + " )" : "path-template( {" + (String)paths.stream().map((s) -> {
         return s.getTemplateString().toString();
      }).collect(Collectors.joining(", ")) + "} )";
   }

   /** @deprecated */
   @Deprecated
   public static final class PathTemplateMatch {
      private final String matchedTemplate;
      private final Map<String, String> parameters;

      public PathTemplateMatch(String matchedTemplate, Map<String, String> parameters) {
         this.matchedTemplate = matchedTemplate;
         this.parameters = parameters;
      }

      public String getMatchedTemplate() {
         return this.matchedTemplate;
      }

      public Map<String, String> getParameters() {
         return this.parameters;
      }
   }
}
