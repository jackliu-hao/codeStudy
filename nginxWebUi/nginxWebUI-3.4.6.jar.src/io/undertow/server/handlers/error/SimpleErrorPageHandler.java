/*     */ package io.undertow.server.handlers.error;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.DefaultResponseListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.StatusCodes;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class SimpleErrorPageHandler
/*     */   implements HttpHandler
/*     */ {
/*  42 */   private volatile HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   private volatile Set<Integer> responseCodes = null;
/*     */ 
/*     */ 
/*     */   
/*     */   private final DefaultResponseListener responseListener;
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleErrorPageHandler(HttpHandler next) {
/*  56 */     this.responseListener = new DefaultResponseListener()
/*     */       {
/*     */         public boolean handleDefaultResponse(HttpServerExchange exchange) {
/*  59 */           if (!exchange.isResponseChannelAvailable()) {
/*  60 */             return false;
/*     */           }
/*  62 */           Set<Integer> codes = SimpleErrorPageHandler.this.responseCodes;
/*  63 */           if ((codes == null) ? (exchange.getStatusCode() >= 400) : codes.contains(Integer.valueOf(exchange.getStatusCode()))) {
/*  64 */             String errorPage = "<html><head><title>Error</title></head><body>" + exchange.getStatusCode() + " - " + StatusCodes.getReason(exchange.getStatusCode()) + "</body></html>";
/*  65 */             exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + errorPage.length());
/*  66 */             exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
/*  67 */             Sender sender = exchange.getResponseSender();
/*  68 */             sender.send(errorPage);
/*  69 */             return true;
/*     */           } 
/*  71 */           return false; } }; this.next = next; } public SimpleErrorPageHandler() { this.responseListener = new DefaultResponseListener() { public boolean handleDefaultResponse(HttpServerExchange exchange) { if (!exchange.isResponseChannelAvailable()) return false;  Set<Integer> codes = SimpleErrorPageHandler.this.responseCodes; if ((codes == null) ? (exchange.getStatusCode() >= 400) : codes.contains(Integer.valueOf(exchange.getStatusCode()))) { String errorPage = "<html><head><title>Error</title></head><body>" + exchange.getStatusCode() + " - " + StatusCodes.getReason(exchange.getStatusCode()) + "</body></html>"; exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, "" + errorPage.length()); exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html"); Sender sender = exchange.getResponseSender(); sender.send(errorPage); return true; }  return false; }
/*     */          }
/*     */       ; }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  77 */     exchange.addDefaultResponseListener(this.responseListener);
/*  78 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public HttpHandler getNext() {
/*  82 */     return this.next;
/*     */   }
/*     */   
/*     */   public SimpleErrorPageHandler setNext(HttpHandler next) {
/*  86 */     Handlers.handlerNotNull(next);
/*  87 */     this.next = next;
/*  88 */     return this;
/*     */   }
/*     */   
/*     */   public Set<Integer> getResponseCodes() {
/*  92 */     return Collections.unmodifiableSet(this.responseCodes);
/*     */   }
/*     */   
/*     */   public SimpleErrorPageHandler setResponseCodes(Set<Integer> responseCodes) {
/*  96 */     this.responseCodes = new HashSet<>(responseCodes);
/*  97 */     return this;
/*     */   }
/*     */   
/*     */   public SimpleErrorPageHandler setResponseCodes(Integer... responseCodes) {
/* 101 */     this.responseCodes = new HashSet<>(Arrays.asList(responseCodes));
/* 102 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\error\SimpleErrorPageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */