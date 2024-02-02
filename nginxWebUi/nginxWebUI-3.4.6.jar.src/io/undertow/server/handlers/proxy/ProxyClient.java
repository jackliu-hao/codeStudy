/*    */ package io.undertow.server.handlers.proxy;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.TimeUnit;
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
/*    */ public interface ProxyClient
/*    */ {
/*    */   ProxyTarget findTarget(HttpServerExchange paramHttpServerExchange);
/*    */   
/*    */   void getConnection(ProxyTarget paramProxyTarget, HttpServerExchange paramHttpServerExchange, ProxyCallback<ProxyConnection> paramProxyCallback, long paramLong, TimeUnit paramTimeUnit);
/*    */   
/*    */   default List<ProxyTarget> getAllTargets() {
/* 79 */     return new ArrayList<>();
/*    */   }
/*    */   
/*    */   public static interface HostProxyTarget extends ProxyTarget {
/*    */     void setHost(LoadBalancingProxyClient.Host param1Host);
/*    */   }
/*    */   
/*    */   public static interface MaxRetriesProxyTarget extends ProxyTarget {
/*    */     int getMaxRetries();
/*    */   }
/*    */   
/*    */   public static interface ProxyTarget {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ProxyClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */