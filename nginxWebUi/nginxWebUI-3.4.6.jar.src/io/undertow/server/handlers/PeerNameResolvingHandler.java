/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
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
/*     */ public class PeerNameResolvingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final ResolveType resolveType;
/*     */   
/*     */   public PeerNameResolvingHandler(HttpHandler next) {
/*  48 */     this.next = next;
/*  49 */     this.resolveType = ResolveType.FORWARD_AND_REVERSE;
/*     */   }
/*     */   
/*     */   public PeerNameResolvingHandler(HttpHandler next, ResolveType resolveType) {
/*  53 */     this.next = next;
/*  54 */     this.resolveType = resolveType;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(final HttpServerExchange exchange) throws Exception {
/*  59 */     final InetSocketAddress address = exchange.getSourceAddress();
/*  60 */     if (address != null) {
/*  61 */       if ((this.resolveType == ResolveType.FORWARD || this.resolveType == ResolveType.FORWARD_AND_REVERSE) && address
/*  62 */         .isUnresolved()) {
/*     */         try {
/*  64 */           if (System.getSecurityManager() == null) {
/*  65 */             InetSocketAddress resolvedAddress = new InetSocketAddress(InetAddress.getByName(address.getHostName()), address.getPort());
/*  66 */             exchange.setSourceAddress(resolvedAddress);
/*     */           } else {
/*  68 */             AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */                 {
/*     */                   public Object run() throws UnknownHostException {
/*  71 */                     InetSocketAddress resolvedAddress = new InetSocketAddress(InetAddress.getByName(address.getHostName()), address.getPort());
/*  72 */                     exchange.setSourceAddress(resolvedAddress);
/*  73 */                     return null;
/*     */                   }
/*     */                 });
/*     */           } 
/*  77 */         } catch (UnknownHostException e) {
/*  78 */           UndertowLogger.REQUEST_LOGGER.debugf(e, "Could not resolve hostname %s", address.getHostString());
/*     */         }
/*     */       
/*  81 */       } else if (this.resolveType == ResolveType.REVERSE || this.resolveType == ResolveType.FORWARD_AND_REVERSE) {
/*  82 */         if (System.getSecurityManager() == null) {
/*  83 */           address.getHostName();
/*     */         } else {
/*  85 */           AccessController.doPrivileged(new PrivilegedAction()
/*     */               {
/*     */                 public Object run() {
/*  88 */                   address.getHostName();
/*  89 */                   return null;
/*     */                 }
/*     */               });
/*     */         } 
/*     */         
/*  94 */         exchange.setSourceAddress(address);
/*     */       } 
/*     */     }
/*     */     
/*  98 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public enum ResolveType {
/* 102 */     FORWARD,
/* 103 */     REVERSE,
/* 104 */     FORWARD_AND_REVERSE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 110 */     return "resolve-peer-name()";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 117 */       return "resolve-peer-name";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 122 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 127 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 132 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 137 */       return new PeerNameResolvingHandler.Wrapper();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper implements HandlerWrapper {
/*     */     private Wrapper() {}
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 145 */       return new PeerNameResolvingHandler(handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\PeerNameResolvingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */