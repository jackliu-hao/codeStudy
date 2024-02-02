/*    */ package io.undertow.server.handlers.proxy;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProxyHandlerBuilder
/*    */   implements HandlerBuilder
/*    */ {
/*    */   public String name() {
/* 23 */     return "reverse-proxy";
/*    */   }
/*    */ 
/*    */   
/*    */   public Map<String, Class<?>> parameters() {
/* 28 */     Map<String, Class<?>> params = new HashMap<>();
/* 29 */     params.put("hosts", String[].class);
/* 30 */     params.put("rewrite-host-header", Boolean.class);
/* 31 */     return params;
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<String> requiredParameters() {
/* 36 */     return Collections.singleton("hosts");
/*    */   }
/*    */ 
/*    */   
/*    */   public String defaultParameter() {
/* 41 */     return "hosts";
/*    */   }
/*    */ 
/*    */   
/*    */   public HandlerWrapper build(Map<String, Object> config) {
/* 46 */     String[] hosts = (String[])config.get("hosts");
/* 47 */     List<URI> uris = new ArrayList<>();
/* 48 */     for (String host : hosts) {
/*    */       try {
/* 50 */         uris.add(new URI(host));
/* 51 */       } catch (URISyntaxException e) {
/* 52 */         throw new RuntimeException(e);
/*    */       } 
/*    */     } 
/* 55 */     Boolean rewriteHostHeader = (Boolean)config.get("rewrite-host-header");
/* 56 */     return new Wrapper(uris, rewriteHostHeader);
/*    */   }
/*    */   
/*    */   private static class Wrapper
/*    */     implements HandlerWrapper {
/*    */     private final List<URI> uris;
/*    */     private final boolean rewriteHostHeader;
/*    */     
/*    */     private Wrapper(List<URI> uris, Boolean rewriteHostHeader) {
/* 65 */       this.uris = uris;
/* 66 */       this.rewriteHostHeader = (rewriteHostHeader != null && rewriteHostHeader.booleanValue());
/*    */     }
/*    */ 
/*    */     
/*    */     public HttpHandler wrap(HttpHandler handler) {
/* 71 */       LoadBalancingProxyClient loadBalancingProxyClient = new LoadBalancingProxyClient();
/* 72 */       for (URI url : this.uris) {
/* 73 */         loadBalancingProxyClient.addHost(url);
/*    */       }
/*    */       
/* 76 */       return new ProxyHandler(loadBalancingProxyClient, -1, handler, this.rewriteHostHeader, false);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ProxyHandlerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */