/*    */ package io.undertow.server.handlers.proxy.mod_cluster;
/*    */ 
/*    */ import io.undertow.client.ClientResponse;
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
/*    */ 
/*    */ 
/*    */ public interface NodeHealthChecker
/*    */ {
/* 39 */   public static final NodeHealthChecker NO_CHECK = new NodeHealthChecker()
/*    */     {
/*    */       public boolean checkResponse(ClientResponse response) {
/* 42 */         return true;
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 49 */   public static final NodeHealthChecker OK = new NodeHealthChecker()
/*    */     {
/*    */       public boolean checkResponse(ClientResponse response) {
/* 52 */         int code = response.getResponseCode();
/* 53 */         return (code >= 200 && code < 400);
/*    */       }
/*    */     };
/*    */   
/*    */   boolean checkResponse(ClientResponse paramClientResponse);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\NodeHealthChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */