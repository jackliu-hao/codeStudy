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
/*     */ 
/*     */ public class LocalNameResolvingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final ResolveType resolveType;
/*     */   
/*     */   public LocalNameResolvingHandler(HttpHandler next) {
/*  49 */     this.next = next;
/*  50 */     this.resolveType = ResolveType.FORWARD_AND_REVERSE;
/*     */   }
/*     */   
/*     */   public LocalNameResolvingHandler(HttpHandler next, ResolveType resolveType) {
/*  54 */     this.next = next;
/*  55 */     this.resolveType = resolveType;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(final HttpServerExchange exchange) throws Exception {
/*  60 */     final InetSocketAddress address = exchange.getDestinationAddress();
/*  61 */     if (address != null) {
/*  62 */       if ((this.resolveType == ResolveType.FORWARD || this.resolveType == ResolveType.FORWARD_AND_REVERSE) && address
/*  63 */         .isUnresolved()) {
/*     */         try {
/*  65 */           if (System.getSecurityManager() == null) {
/*  66 */             InetSocketAddress resolvedAddress = new InetSocketAddress(InetAddress.getByName(address.getHostName()), address.getPort());
/*  67 */             exchange.setDestinationAddress(resolvedAddress);
/*     */           } else {
/*  69 */             AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */                 {
/*     */                   public Object run() throws UnknownHostException {
/*  72 */                     InetSocketAddress resolvedAddress = new InetSocketAddress(InetAddress.getByName(address.getHostName()), address.getPort());
/*  73 */                     exchange.setDestinationAddress(resolvedAddress);
/*  74 */                     return null;
/*     */                   }
/*     */                 });
/*     */           } 
/*  78 */         } catch (UnknownHostException e) {
/*  79 */           UndertowLogger.REQUEST_LOGGER.debugf(e, "Could not resolve hostname %s", address.getHostString());
/*     */         }
/*     */       
/*  82 */       } else if (this.resolveType == ResolveType.REVERSE || this.resolveType == ResolveType.FORWARD_AND_REVERSE) {
/*  83 */         if (System.getSecurityManager() == null) {
/*  84 */           address.getHostName();
/*     */         } else {
/*  86 */           AccessController.doPrivileged(new PrivilegedAction()
/*     */               {
/*     */                 public Object run() {
/*  89 */                   address.getHostName();
/*  90 */                   return null;
/*     */                 }
/*     */               });
/*     */         } 
/*     */         
/*  95 */         exchange.setDestinationAddress(address);
/*     */       } 
/*     */     }
/*     */     
/*  99 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public enum ResolveType {
/* 103 */     FORWARD,
/* 104 */     REVERSE,
/* 105 */     FORWARD_AND_REVERSE;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 113 */       return "resolve-local-name";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 118 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 123 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 128 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 133 */       return new LocalNameResolvingHandler.Wrapper();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper implements HandlerWrapper {
/*     */     private Wrapper() {}
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 141 */       return new LocalNameResolvingHandler(handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\LocalNameResolvingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */