/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.PathTemplate;
/*     */ import io.undertow.util.PathTemplateMatcher;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Collectors;
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
/*     */ 
/*     */ public class PathTemplateHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final boolean rewriteQueryParameters;
/*     */   private final HttpHandler next;
/*     */   @Deprecated
/*  46 */   public static final AttachmentKey<PathTemplateMatch> PATH_TEMPLATE_MATCH = AttachmentKey.create(PathTemplateMatch.class);
/*     */   
/*  48 */   private final PathTemplateMatcher<HttpHandler> pathTemplateMatcher = new PathTemplateMatcher();
/*     */   
/*     */   public PathTemplateHandler() {
/*  51 */     this(true);
/*     */   }
/*     */   
/*     */   public PathTemplateHandler(boolean rewriteQueryParameters) {
/*  55 */     this(ResponseCodeHandler.HANDLE_404, rewriteQueryParameters);
/*     */   }
/*     */   
/*     */   public PathTemplateHandler(HttpHandler next) {
/*  59 */     this(next, true);
/*     */   }
/*     */   
/*     */   public PathTemplateHandler(HttpHandler next, boolean rewriteQueryParameters) {
/*  63 */     this.rewriteQueryParameters = rewriteQueryParameters;
/*  64 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  69 */     PathTemplateMatcher.PathMatchResult<HttpHandler> match = this.pathTemplateMatcher.match(exchange.getRelativePath());
/*  70 */     if (match == null) {
/*  71 */       this.next.handleRequest(exchange);
/*     */       return;
/*     */     } 
/*  74 */     exchange.putAttachment(PATH_TEMPLATE_MATCH, new PathTemplateMatch(match.getMatchedTemplate(), match.getParameters()));
/*  75 */     exchange.putAttachment(io.undertow.util.PathTemplateMatch.ATTACHMENT_KEY, new io.undertow.util.PathTemplateMatch(match.getMatchedTemplate(), match.getParameters()));
/*  76 */     if (this.rewriteQueryParameters) {
/*  77 */       for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)match.getParameters().entrySet()) {
/*  78 */         exchange.addQueryParam(entry.getKey(), entry.getValue());
/*     */       }
/*     */     }
/*  81 */     ((HttpHandler)match.getValue()).handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public PathTemplateHandler add(String uriTemplate, HttpHandler handler) {
/*  85 */     this.pathTemplateMatcher.add(uriTemplate, handler);
/*  86 */     return this;
/*     */   }
/*     */   
/*     */   public PathTemplateHandler remove(String uriTemplate) {
/*  90 */     this.pathTemplateMatcher.remove(uriTemplate);
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  96 */     Set<PathTemplate> paths = this.pathTemplateMatcher.getPathTemplates();
/*  97 */     if (paths.size() == 1) {
/*  98 */       return "path-template( " + paths.toArray()[0] + " )";
/*     */     }
/* 100 */     return "path-template( {" + (String)paths.stream().map(s -> s.getTemplateString().toString()).collect(Collectors.joining(", ")) + "} )";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static final class PathTemplateMatch
/*     */   {
/*     */     private final String matchedTemplate;
/*     */     
/*     */     private final Map<String, String> parameters;
/*     */ 
/*     */     
/*     */     public PathTemplateMatch(String matchedTemplate, Map<String, String> parameters) {
/* 114 */       this.matchedTemplate = matchedTemplate;
/* 115 */       this.parameters = parameters;
/*     */     }
/*     */     
/*     */     public String getMatchedTemplate() {
/* 119 */       return this.matchedTemplate;
/*     */     }
/*     */     
/*     */     public Map<String, String> getParameters() {
/* 123 */       return this.parameters;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\PathTemplateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */