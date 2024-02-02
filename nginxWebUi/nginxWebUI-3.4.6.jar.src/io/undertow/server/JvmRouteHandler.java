/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class JvmRouteHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final String sessionCookieName;
/*     */   private final String jvmRoute;
/*  43 */   private final JvmRouteWrapper wrapper = new JvmRouteWrapper();
/*     */ 
/*     */   
/*     */   public JvmRouteHandler(HttpHandler next, String sessionCookieName, String jvmRoute) {
/*  47 */     this.next = next;
/*  48 */     this.sessionCookieName = sessionCookieName;
/*  49 */     this.jvmRoute = jvmRoute;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  54 */     for (Cookie cookie : exchange.requestCookies()) {
/*  55 */       if (this.sessionCookieName.equals(cookie.getName())) {
/*  56 */         int part = cookie.getValue().indexOf('.');
/*  57 */         if (part != -1) {
/*  58 */           cookie.setValue(cookie.getValue().substring(0, part));
/*     */         }
/*     */       } 
/*     */     } 
/*  62 */     exchange.addResponseWrapper(this.wrapper);
/*  63 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private class JvmRouteWrapper implements ConduitWrapper<StreamSinkConduit> {
/*     */     private JvmRouteWrapper() {}
/*     */     
/*     */     public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/*  70 */       for (Cookie cookie : exchange.responseCookies()) {
/*  71 */         if (JvmRouteHandler.this.sessionCookieName.equals(cookie.getName())) {
/*  72 */           StringBuilder sb = new StringBuilder(cookie.getValue());
/*  73 */           sb.append('.');
/*  74 */           sb.append(JvmRouteHandler.this.jvmRoute);
/*  75 */           cookie.setValue(sb.toString());
/*     */         } 
/*     */       } 
/*  78 */       return (StreamSinkConduit)factory.create();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  86 */       return "jvm-route";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  91 */       Map<String, Class<?>> params = new HashMap<>();
/*  92 */       params.put("value", String.class);
/*  93 */       params.put("session-cookie-name", String.class);
/*     */       
/*  95 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 100 */       return Collections.singleton("value");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 105 */       return "value";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 110 */       String sessionCookieName = (String)config.get("session-cookie-name");
/*     */       
/* 112 */       return new JvmRouteHandler.Wrapper((String)config.get("value"), (sessionCookieName == null) ? "JSESSIONID" : sessionCookieName);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final String value;
/*     */     private final String sessionCookieName;
/*     */     
/*     */     private Wrapper(String value, String sessionCookieName) {
/* 123 */       this.value = value;
/* 124 */       this.sessionCookieName = sessionCookieName;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 129 */       return new JvmRouteHandler(handler, this.sessionCookieName, this.value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\JvmRouteHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */