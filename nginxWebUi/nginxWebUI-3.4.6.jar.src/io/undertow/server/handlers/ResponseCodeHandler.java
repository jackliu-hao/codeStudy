/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ public final class ResponseCodeHandler
/*    */   implements HttpHandler
/*    */ {
/* 35 */   private static final boolean debugEnabled = UndertowLogger.PREDICATE_LOGGER.isDebugEnabled();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 42 */   public static final ResponseCodeHandler HANDLE_200 = new ResponseCodeHandler(200);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 47 */   public static final ResponseCodeHandler HANDLE_403 = new ResponseCodeHandler(403);
/*    */ 
/*    */ 
/*    */   
/* 51 */   public static final ResponseCodeHandler HANDLE_404 = new ResponseCodeHandler(404);
/*    */ 
/*    */ 
/*    */   
/* 55 */   public static final ResponseCodeHandler HANDLE_405 = new ResponseCodeHandler(405);
/*    */ 
/*    */ 
/*    */   
/* 59 */   public static final ResponseCodeHandler HANDLE_406 = new ResponseCodeHandler(406);
/*    */ 
/*    */ 
/*    */   
/* 63 */   public static final ResponseCodeHandler HANDLE_500 = new ResponseCodeHandler(500);
/*    */ 
/*    */ 
/*    */   
/*    */   private final int responseCode;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ResponseCodeHandler(int responseCode) {
/* 73 */     this.responseCode = responseCode;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 78 */     exchange.setStatusCode(this.responseCode);
/* 79 */     if (debugEnabled) {
/* 80 */       UndertowLogger.PREDICATE_LOGGER.debugf("Response code set to [%s] for %s.", this.responseCode, exchange);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 86 */     return "response-code( " + this.responseCode + " )";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ResponseCodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */