/*     */ package org.noear.solon.web.cors;
/*     */ 
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Filter;
/*     */ import org.noear.solon.core.handle.FilterChain;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CrossFilter
/*     */   implements Filter
/*     */ {
/*  14 */   protected int maxAge = 3600;
/*     */   
/*  16 */   protected String allowedOrigins = "*";
/*     */   
/*  18 */   protected String allowedMethods = "*";
/*  19 */   protected String allowedHeaders = "*";
/*     */   
/*     */   protected boolean allowCredentials = true;
/*     */   
/*     */   protected String exposedHeaders;
/*     */   
/*     */   public CrossFilter maxAge(int maxAge) {
/*  26 */     if (maxAge >= 0) {
/*  27 */       this.maxAge = maxAge;
/*     */     }
/*     */     
/*  30 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CrossFilter allowedOrigins(String allowOrigin) {
/*  37 */     if (allowOrigin != null) {
/*  38 */       this.allowedOrigins = allowOrigin;
/*     */     }
/*     */     
/*  41 */     return this;
/*     */   }
/*     */   
/*     */   public CrossFilter allowedMethods(String allowMethods) {
/*  45 */     this.allowedMethods = allowMethods;
/*  46 */     return this;
/*     */   }
/*     */   
/*     */   public CrossFilter allowedHeaders(String allowHeaders) {
/*  50 */     this.allowedHeaders = allowHeaders;
/*  51 */     return this;
/*     */   }
/*     */   
/*     */   public CrossFilter allowCredentials(boolean allowCredentials) {
/*  55 */     this.allowCredentials = allowCredentials;
/*  56 */     return this;
/*     */   }
/*     */   
/*     */   public CrossFilter exposedHeaders(String exposeHeaders) {
/*  60 */     this.exposedHeaders = exposeHeaders;
/*  61 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void doFilter(Context ctx, FilterChain chain) throws Throwable {
/*  66 */     doFilter0(ctx, chain);
/*     */     
/*  68 */     if (!ctx.getHandled()) {
/*  69 */       chain.doFilter(ctx);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void doFilter0(Context ctx, FilterChain chain) throws Throwable {
/*  74 */     if (ctx.getHandled()) {
/*     */       return;
/*     */     }
/*     */     
/*  78 */     String origin = ctx.header("Origin");
/*     */     
/*  80 */     if (Utils.isEmpty(origin)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  86 */     ctx.headerSet("Access-Control-Max-Age", String.valueOf(this.maxAge));
/*     */ 
/*     */     
/*  89 */     if (Utils.isNotEmpty(this.allowedHeaders)) {
/*  90 */       if ("*".equals(this.allowedHeaders)) {
/*  91 */         String requestHeaders = ctx.header("Access-Control-Request-Headers");
/*     */         
/*  93 */         if (Utils.isNotEmpty(requestHeaders)) {
/*  94 */           ctx.headerSet("Access-Control-Allow-Headers", requestHeaders);
/*     */         }
/*     */       } else {
/*  97 */         ctx.headerSet("Access-Control-Allow-Headers", this.allowedHeaders);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 102 */     if (Utils.isNotEmpty(this.allowedMethods)) {
/* 103 */       if ("*".equals(this.allowedMethods)) {
/* 104 */         String requestMethod = ctx.header("Access-Control-Request-Method");
/*     */ 
/*     */         
/* 107 */         if (Utils.isEmpty(requestMethod)) {
/* 108 */           requestMethod = ctx.method();
/*     */         }
/*     */         
/* 111 */         if (Utils.isNotEmpty(requestMethod)) {
/* 112 */           ctx.headerSet("Access-Control-Allow-Methods", requestMethod);
/*     */         }
/*     */       } else {
/* 115 */         ctx.headerSet("Access-Control-Allow-Methods", this.allowedMethods);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 120 */     if (Utils.isNotEmpty(this.allowedOrigins) && (
/* 121 */       "*".equals(this.allowedOrigins) || this.allowedOrigins.contains(origin))) {
/* 122 */       ctx.headerSet("Access-Control-Allow-Origin", origin);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 127 */     if (this.allowCredentials) {
/* 128 */       ctx.headerSet("Access-Control-Allow-Credentials", "true");
/*     */     }
/*     */ 
/*     */     
/* 132 */     if (Utils.isNotEmpty(this.exposedHeaders)) {
/* 133 */       ctx.headerSet("Access-Control-Expose-Headers", this.exposedHeaders);
/*     */     }
/*     */     
/* 136 */     if (MethodType.OPTIONS.name.equalsIgnoreCase(ctx.method()))
/* 137 */       ctx.setHandled(true); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\cors\CrossFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */