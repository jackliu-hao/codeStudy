/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class UriHttpRequestHandlerMapper
/*     */   implements HttpRequestHandlerMapper
/*     */ {
/*     */   private final UriPatternMatcher<HttpRequestHandler> matcher;
/*     */   
/*     */   protected UriHttpRequestHandlerMapper(UriPatternMatcher<HttpRequestHandler> matcher) {
/*  59 */     this.matcher = (UriPatternMatcher<HttpRequestHandler>)Args.notNull(matcher, "Pattern matcher");
/*     */   }
/*     */   
/*     */   public UriHttpRequestHandlerMapper() {
/*  63 */     this(new UriPatternMatcher<HttpRequestHandler>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void register(String pattern, HttpRequestHandler handler) {
/*  74 */     Args.notNull(pattern, "Pattern");
/*  75 */     Args.notNull(handler, "Handler");
/*  76 */     this.matcher.register(pattern, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregister(String pattern) {
/*  85 */     this.matcher.unregister(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getRequestPath(HttpRequest request) {
/*  92 */     String uriPath = request.getRequestLine().getUri();
/*  93 */     int index = uriPath.indexOf('?');
/*  94 */     if (index != -1) {
/*  95 */       uriPath = uriPath.substring(0, index);
/*     */     } else {
/*  97 */       index = uriPath.indexOf('#');
/*  98 */       if (index != -1) {
/*  99 */         uriPath = uriPath.substring(0, index);
/*     */       }
/*     */     } 
/* 102 */     return uriPath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequestHandler lookup(HttpRequest request) {
/* 113 */     Args.notNull(request, "HTTP request");
/* 114 */     return this.matcher.lookup(getRequestPath(request));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\UriHttpRequestHandlerMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */