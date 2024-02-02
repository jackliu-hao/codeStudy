/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.DateUtils;
/*    */ import io.undertow.util.Headers;
/*    */ import java.util.Date;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class DateHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   private volatile String cachedDateString;
/* 45 */   private volatile long nextUpdateTime = -1L;
/*    */ 
/*    */   
/*    */   public DateHandler(HttpHandler next) {
/* 49 */     this.next = next;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 55 */     long time = System.nanoTime();
/* 56 */     if (time < this.nextUpdateTime) {
/* 57 */       exchange.getResponseHeaders().put(Headers.DATE, this.cachedDateString);
/*    */     } else {
/* 59 */       long realTime = System.currentTimeMillis();
/* 60 */       String dateString = DateUtils.toDateString(new Date(realTime));
/* 61 */       this.cachedDateString = dateString;
/* 62 */       this.nextUpdateTime = time + 1000000000L;
/* 63 */       exchange.getResponseHeaders().put(Headers.DATE, dateString);
/*    */     } 
/* 65 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\DateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */