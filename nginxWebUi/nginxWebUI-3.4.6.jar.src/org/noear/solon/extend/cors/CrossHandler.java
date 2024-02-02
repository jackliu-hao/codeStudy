/*     */ package org.noear.solon.extend.cors;
/*     */ 
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.extend.cors.annotation.CrossOrigin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class CrossHandler
/*     */   implements Handler
/*     */ {
/*     */   public CrossHandler() {}
/*     */   
/*     */   public CrossHandler(CrossOrigin anno) {
/*  23 */     maxAge(anno.maxAge());
/*     */     
/*  25 */     allowedOrigins(Solon.cfg().getByParse(anno.origins()));
/*  26 */     allowCredentials(anno.credentials());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  31 */   protected int maxAge = 3600;
/*     */   
/*  33 */   protected String allowedOrigins = "*";
/*     */   
/*  35 */   protected String allowedMethods = "*";
/*  36 */   protected String allowedHeaders = "*";
/*     */   
/*     */   protected boolean allowCredentials = true;
/*     */   
/*     */   protected String exposedHeaders;
/*     */   
/*     */   public CrossHandler maxAge(int maxAge) {
/*  43 */     if (maxAge >= 0) {
/*  44 */       this.maxAge = maxAge;
/*     */     }
/*     */     
/*  47 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CrossHandler allowedOrigins(String allowOrigin) {
/*  54 */     if (allowOrigin != null) {
/*  55 */       this.allowedOrigins = allowOrigin;
/*     */     }
/*     */     
/*  58 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler allowedMethods(String allowMethods) {
/*  62 */     this.allowedMethods = allowMethods;
/*  63 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler allowedHeaders(String allowHeaders) {
/*  67 */     this.allowedHeaders = allowHeaders;
/*  68 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler allowCredentials(boolean allowCredentials) {
/*  72 */     this.allowCredentials = allowCredentials;
/*  73 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler exposedHeaders(String exposeHeaders) {
/*  77 */     this.exposedHeaders = exposeHeaders;
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handle(Context ctx) throws Throwable {
/*  83 */     if (ctx.getHandled()) {
/*     */       return;
/*     */     }
/*     */     
/*  87 */     String origin = ctx.header("Origin");
/*     */     
/*  89 */     if (Utils.isEmpty(origin)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  95 */     ctx.headerSet("Access-Control-Max-Age", String.valueOf(this.maxAge));
/*     */ 
/*     */     
/*  98 */     if (Utils.isNotEmpty(this.allowedHeaders)) {
/*  99 */       if ("*".equals(this.allowedHeaders)) {
/* 100 */         String requestHeaders = ctx.header("Access-Control-Request-Headers");
/*     */         
/* 102 */         if (Utils.isNotEmpty(requestHeaders)) {
/* 103 */           ctx.headerSet("Access-Control-Allow-Headers", requestHeaders);
/*     */         }
/*     */       } else {
/* 106 */         ctx.headerSet("Access-Control-Allow-Headers", this.allowedHeaders);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 111 */     if (Utils.isNotEmpty(this.allowedMethods)) {
/* 112 */       if ("*".equals(this.allowedMethods)) {
/* 113 */         String requestMethod = ctx.header("Access-Control-Request-Method");
/*     */ 
/*     */         
/* 116 */         if (Utils.isEmpty(requestMethod)) {
/* 117 */           requestMethod = ctx.method();
/*     */         }
/*     */         
/* 120 */         if (Utils.isNotEmpty(requestMethod)) {
/* 121 */           ctx.headerSet("Access-Control-Allow-Methods", requestMethod);
/*     */         }
/*     */       } else {
/* 124 */         ctx.headerSet("Access-Control-Allow-Methods", this.allowedMethods);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 129 */     if (Utils.isNotEmpty(this.allowedOrigins) && (
/* 130 */       "*".equals(this.allowedOrigins) || this.allowedOrigins.contains(origin))) {
/* 131 */       ctx.headerSet("Access-Control-Allow-Origin", origin);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 136 */     if (this.allowCredentials) {
/* 137 */       ctx.headerSet("Access-Control-Allow-Credentials", "true");
/*     */     }
/*     */ 
/*     */     
/* 141 */     if (Utils.isNotEmpty(this.exposedHeaders)) {
/* 142 */       ctx.headerSet("Access-Control-Expose-Headers", this.exposedHeaders);
/*     */     }
/*     */     
/* 145 */     if (MethodType.OPTIONS.name.equalsIgnoreCase(ctx.method()))
/* 146 */       ctx.setHandled(true); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\extend\cors\CrossHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */