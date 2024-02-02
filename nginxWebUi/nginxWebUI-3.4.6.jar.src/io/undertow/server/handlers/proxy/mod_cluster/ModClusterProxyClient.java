/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.handlers.proxy.ExclusivityChecker;
/*     */ import io.undertow.server.handlers.proxy.ProxyCallback;
/*     */ import io.undertow.server.handlers.proxy.ProxyClient;
/*     */ import io.undertow.server.handlers.proxy.ProxyConnection;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import java.io.Closeable;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.IoUtils;
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
/*     */ class ModClusterProxyClient
/*     */   implements ProxyClient
/*     */ {
/*  42 */   private final AttachmentKey<ExclusiveConnectionHolder> exclusiveConnectionKey = AttachmentKey.create(ExclusiveConnectionHolder.class);
/*     */   
/*     */   private final ExclusivityChecker exclusivityChecker;
/*     */   private final ModClusterContainer container;
/*     */   
/*     */   protected ModClusterProxyClient(ExclusivityChecker exclusivityChecker, ModClusterContainer container) {
/*  48 */     this.exclusivityChecker = exclusivityChecker;
/*  49 */     this.container = container;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProxyClient.ProxyTarget findTarget(HttpServerExchange exchange) {
/*  54 */     return this.container.findTarget(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public void getConnection(ProxyClient.ProxyTarget target, HttpServerExchange exchange, final ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
/*  59 */     final ExclusiveConnectionHolder holder = (ExclusiveConnectionHolder)exchange.getConnection().getAttachment(this.exclusiveConnectionKey);
/*  60 */     if (holder != null && holder.connection.getConnection().isOpen()) {
/*     */ 
/*     */       
/*  63 */       callback.completed(exchange, holder.connection);
/*     */       return;
/*     */     } 
/*  66 */     if (!(target instanceof ModClusterProxyTarget)) {
/*  67 */       callback.couldNotResolveBackend(exchange);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  72 */     ModClusterProxyTarget proxyTarget = (ModClusterProxyTarget)target;
/*  73 */     Context context = proxyTarget.resolveContext(exchange);
/*  74 */     if (context == null) {
/*  75 */       callback.couldNotResolveBackend(exchange);
/*     */     }
/*  77 */     else if (holder != null || (this.exclusivityChecker != null && this.exclusivityChecker.isExclusivityRequired(exchange))) {
/*     */ 
/*     */ 
/*     */       
/*  81 */       ProxyCallback<ProxyConnection> wrappedCallback = new ProxyCallback<ProxyConnection>()
/*     */         {
/*     */           public void completed(HttpServerExchange exchange, ProxyConnection result)
/*     */           {
/*  85 */             if (holder != null) {
/*  86 */               holder.connection = result;
/*     */             } else {
/*  88 */               final ModClusterProxyClient.ExclusiveConnectionHolder newHolder = new ModClusterProxyClient.ExclusiveConnectionHolder();
/*  89 */               newHolder.connection = result;
/*  90 */               ServerConnection connection = exchange.getConnection();
/*  91 */               connection.putAttachment(ModClusterProxyClient.this.exclusiveConnectionKey, newHolder);
/*  92 */               connection.addCloseListener(new ServerConnection.CloseListener()
/*     */                   {
/*     */                     public void closed(ServerConnection connection)
/*     */                     {
/*  96 */                       ClientConnection clientConnection = newHolder.connection.getConnection();
/*  97 */                       if (clientConnection.isOpen()) {
/*  98 */                         IoUtils.safeClose((Closeable)clientConnection);
/*     */                       }
/*     */                     }
/*     */                   });
/*     */             } 
/* 103 */             callback.completed(exchange, result);
/*     */           }
/*     */ 
/*     */           
/*     */           public void queuedRequestFailed(HttpServerExchange exchange) {
/* 108 */             callback.queuedRequestFailed(exchange);
/*     */           }
/*     */ 
/*     */           
/*     */           public void failed(HttpServerExchange exchange) {
/* 113 */             callback.failed(exchange);
/*     */           }
/*     */ 
/*     */           
/*     */           public void couldNotResolveBackend(HttpServerExchange exchange) {
/* 118 */             callback.couldNotResolveBackend(exchange);
/*     */           }
/*     */         };
/*     */       
/* 122 */       context.handleRequest(proxyTarget, exchange, wrappedCallback, timeout, timeUnit, true);
/*     */     } else {
/* 124 */       context.handleRequest(proxyTarget, exchange, callback, timeout, timeUnit, false);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class ExclusiveConnectionHolder {
/*     */     private ProxyConnection connection;
/*     */     
/*     */     private ExclusiveConnectionHolder() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\ModClusterProxyClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */