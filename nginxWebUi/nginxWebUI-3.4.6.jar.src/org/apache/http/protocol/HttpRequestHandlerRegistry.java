/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class HttpRequestHandlerRegistry
/*     */   implements HttpRequestHandlerResolver
/*     */ {
/*  61 */   private final UriPatternMatcher<HttpRequestHandler> matcher = new UriPatternMatcher<HttpRequestHandler>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void register(String pattern, HttpRequestHandler handler) {
/*  72 */     Args.notNull(pattern, "URI request pattern");
/*  73 */     Args.notNull(handler, "Request handler");
/*  74 */     this.matcher.register(pattern, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregister(String pattern) {
/*  83 */     this.matcher.unregister(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHandlers(Map<String, HttpRequestHandler> map) {
/*  91 */     this.matcher.setObjects(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, HttpRequestHandler> getHandlers() {
/* 101 */     return this.matcher.getObjects();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpRequestHandler lookup(String requestURI) {
/* 106 */     return this.matcher.lookup(requestURI);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\HttpRequestHandlerRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */