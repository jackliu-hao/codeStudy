/*    */ package cn.hutool.http.server.handler;
/*    */ 
/*    */ import cn.hutool.http.server.HttpServerRequest;
/*    */ import cn.hutool.http.server.HttpServerResponse;
/*    */ import cn.hutool.http.server.action.Action;
/*    */ import com.sun.net.httpserver.HttpExchange;
/*    */ import com.sun.net.httpserver.HttpHandler;
/*    */ import java.io.IOException;
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
/*    */ public class ActionHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final Action action;
/*    */   
/*    */   public ActionHandler(Action action) {
/* 27 */     this.action = action;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handle(HttpExchange httpExchange) throws IOException {
/* 32 */     this.action.doAction(new HttpServerRequest(httpExchange), new HttpServerResponse(httpExchange));
/*    */ 
/*    */ 
/*    */     
/* 36 */     httpExchange.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\server\handler\ActionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */