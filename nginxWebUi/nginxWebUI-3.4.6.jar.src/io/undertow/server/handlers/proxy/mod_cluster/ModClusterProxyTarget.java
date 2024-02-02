/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.proxy.ProxyClient;
/*     */ import java.util.Iterator;
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
/*     */ public interface ModClusterProxyTarget
/*     */   extends ProxyClient.ProxyTarget, ProxyClient.MaxRetriesProxyTarget
/*     */ {
/*     */   Context resolveContext(HttpServerExchange paramHttpServerExchange);
/*     */   
/*     */   public static class ExistingSessionTarget
/*     */     implements ModClusterProxyTarget
/*     */   {
/*     */     private final String session;
/*     */     private final Iterator<CharSequence> routes;
/*     */     private final VirtualHost.HostEntry entry;
/*     */     private final boolean forceStickySession;
/*     */     private final ModClusterContainer container;
/*     */     private boolean resolved;
/*     */     private Context resolvedContext;
/*     */     
/*     */     public ExistingSessionTarget(String session, Iterator<CharSequence> routes, VirtualHost.HostEntry entry, ModClusterContainer container, boolean forceStickySession) {
/*  52 */       this.session = session;
/*  53 */       this.routes = routes;
/*  54 */       this.entry = entry;
/*  55 */       this.container = container;
/*  56 */       this.forceStickySession = forceStickySession;
/*     */     }
/*     */ 
/*     */     
/*     */     public Context resolveContext(HttpServerExchange exchange) {
/*  61 */       resolveContextIfUnresolved();
/*     */       
/*  63 */       return this.resolvedContext;
/*     */     }
/*     */     
/*     */     void resolveContextIfUnresolved() {
/*  67 */       if (this.resolved)
/*     */         return; 
/*  69 */       this.resolved = true;
/*  70 */       boolean firstResolved = false;
/*  71 */       String firstRoute = null;
/*  72 */       String firstRouteDomain = null;
/*     */       
/*  74 */       while (this.routes.hasNext()) {
/*  75 */         String jvmRoute = ((CharSequence)this.routes.next()).toString();
/*     */         
/*  77 */         Context context = this.entry.getContextForNode(jvmRoute);
/*  78 */         if (context != null && context.checkAvailable(true)) {
/*  79 */           Node node = context.getNode();
/*  80 */           node.elected();
/*  81 */           this.resolvedContext = context;
/*     */           
/*     */           return;
/*     */         } 
/*  85 */         if (!firstResolved) {
/*  86 */           firstResolved = true;
/*  87 */           firstRoute = jvmRoute;
/*  88 */           firstRouteDomain = (context != null) ? context.getNode().getNodeConfig().getDomain() : null;
/*     */         } 
/*     */       } 
/*     */       
/*  92 */       this.resolvedContext = this.container.findFailoverNode(this.entry, firstRouteDomain, this.session, firstRoute, this.forceStickySession);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxRetries() {
/*  97 */       resolveContextIfUnresolved();
/*     */       
/*  99 */       if (this.resolvedContext == null) {
/* 100 */         return 0;
/*     */       }
/* 102 */       Balancer balancer = this.resolvedContext.getNode().getBalancer();
/* 103 */       if (balancer == null) {
/* 104 */         return 0;
/*     */       }
/* 106 */       return balancer.getMaxRetries();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class BasicTarget
/*     */     implements ModClusterProxyTarget {
/*     */     private final VirtualHost.HostEntry entry;
/*     */     private final ModClusterContainer container;
/*     */     private Context resolved;
/*     */     
/*     */     public BasicTarget(VirtualHost.HostEntry entry, ModClusterContainer container) {
/* 117 */       this.entry = entry;
/* 118 */       this.container = container;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxRetries() {
/* 123 */       if (this.resolved == null) {
/* 124 */         resolveNode();
/*     */       }
/* 126 */       if (this.resolved == null) {
/* 127 */         return 0;
/*     */       }
/* 129 */       Balancer balancer = this.resolved.getNode().getBalancer();
/* 130 */       if (balancer == null) {
/* 131 */         return 0;
/*     */       }
/* 133 */       return balancer.getMaxRetries();
/*     */     }
/*     */ 
/*     */     
/*     */     public Context resolveContext(HttpServerExchange exchange) {
/* 138 */       if (this.resolved == null) {
/* 139 */         resolveNode();
/*     */       }
/* 141 */       return this.resolved;
/*     */     }
/*     */     
/*     */     private void resolveNode() {
/* 145 */       this.resolved = this.container.findNewNode(this.entry);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\ModClusterProxyTarget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */