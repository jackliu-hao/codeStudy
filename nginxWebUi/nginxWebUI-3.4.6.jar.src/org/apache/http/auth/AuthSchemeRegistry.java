/*     */ package org.apache.http.auth;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.config.Lookup;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public final class AuthSchemeRegistry
/*     */   implements Lookup<AuthSchemeProvider>
/*     */ {
/*  60 */   private final ConcurrentHashMap<String, AuthSchemeFactory> registeredSchemes = new ConcurrentHashMap<String, AuthSchemeFactory>();
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
/*     */   public void register(String name, AuthSchemeFactory factory) {
/*  81 */     Args.notNull(name, "Name");
/*  82 */     Args.notNull(factory, "Authentication scheme factory");
/*  83 */     this.registeredSchemes.put(name.toLowerCase(Locale.ENGLISH), factory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unregister(String name) {
/*  93 */     Args.notNull(name, "Name");
/*  94 */     this.registeredSchemes.remove(name.toLowerCase(Locale.ENGLISH));
/*     */   }
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
/*     */   public AuthScheme getAuthScheme(String name, HttpParams params) throws IllegalStateException {
/* 111 */     Args.notNull(name, "Name");
/* 112 */     AuthSchemeFactory factory = this.registeredSchemes.get(name.toLowerCase(Locale.ENGLISH));
/* 113 */     if (factory != null) {
/* 114 */       return factory.newInstance(params);
/*     */     }
/* 116 */     throw new IllegalStateException("Unsupported authentication scheme: " + name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getSchemeNames() {
/* 126 */     return new ArrayList<String>(this.registeredSchemes.keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setItems(Map<String, AuthSchemeFactory> map) {
/* 136 */     if (map == null) {
/*     */       return;
/*     */     }
/* 139 */     this.registeredSchemes.clear();
/* 140 */     this.registeredSchemes.putAll(map);
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthSchemeProvider lookup(final String name) {
/* 145 */     return new AuthSchemeProvider()
/*     */       {
/*     */         public AuthScheme create(HttpContext context)
/*     */         {
/* 149 */           HttpRequest request = (HttpRequest)context.getAttribute("http.request");
/*     */           
/* 151 */           return AuthSchemeRegistry.this.getAuthScheme(name, request.getParams());
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\AuthSchemeRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */