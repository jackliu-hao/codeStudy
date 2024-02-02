/*     */ package org.noear.solon.web.cors;
/*     */ 
/*     */ import org.noear.solon.Solon;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.Handler;
/*     */ import org.noear.solon.core.handle.MethodType;
/*     */ import org.noear.solon.web.cors.annotation.CrossOrigin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CrossHandler
/*     */   implements Handler
/*     */ {
/*     */   public CrossHandler() {}
/*     */   
/*     */   public CrossHandler(CrossOrigin anno) {
/*  22 */     maxAge(anno.maxAge());
/*     */     
/*  24 */     allowedOrigins(Solon.cfg().getByParse(anno.origins()));
/*  25 */     allowCredentials(anno.credentials());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  30 */   protected int maxAge = 3600;
/*     */   
/*  32 */   protected String allowedOrigins = "*";
/*     */   
/*  34 */   protected String allowedMethods = "*";
/*  35 */   protected String allowedHeaders = "*";
/*     */   
/*     */   protected boolean allowCredentials = true;
/*     */   
/*     */   protected String exposedHeaders;
/*     */   
/*     */   public CrossHandler maxAge(int maxAge) {
/*  42 */     if (maxAge >= 0) {
/*  43 */       this.maxAge = maxAge;
/*     */     }
/*     */     
/*  46 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CrossHandler allowedOrigins(String allowOrigin) {
/*  53 */     if (allowOrigin != null) {
/*  54 */       this.allowedOrigins = allowOrigin;
/*     */     }
/*     */     
/*  57 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler allowedMethods(String allowMethods) {
/*  61 */     this.allowedMethods = allowMethods;
/*  62 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler allowedHeaders(String allowHeaders) {
/*  66 */     this.allowedHeaders = allowHeaders;
/*  67 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler allowCredentials(boolean allowCredentials) {
/*  71 */     this.allowCredentials = allowCredentials;
/*  72 */     return this;
/*     */   }
/*     */   
/*     */   public CrossHandler exposedHeaders(String exposeHeaders) {
/*  76 */     this.exposedHeaders = exposeHeaders;
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handle(Context ctx) throws Throwable {
/*  82 */     if (ctx.getHandled()) {
/*     */       return;
/*     */     }
/*     */     
/*  86 */     String origin = ctx.header("Origin");
/*     */     
/*  88 */     if (Utils.isEmpty(origin)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  94 */     ctx.headerSet("Access-Control-Max-Age", String.valueOf(this.maxAge));
/*     */ 
/*     */     
/*  97 */     if (Utils.isNotEmpty(this.allowedHeaders)) {
/*  98 */       if ("*".equals(this.allowedHeaders)) {
/*  99 */         String requestHeaders = ctx.header("Access-Control-Request-Headers");
/*     */         
/* 101 */         if (Utils.isNotEmpty(requestHeaders)) {
/* 102 */           ctx.headerSet("Access-Control-Allow-Headers", requestHeaders);
/*     */         }
/*     */       } else {
/* 105 */         ctx.headerSet("Access-Control-Allow-Headers", this.allowedHeaders);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 110 */     if (Utils.isNotEmpty(this.allowedMethods)) {
/* 111 */       if ("*".equals(this.allowedMethods)) {
/* 112 */         String requestMethod = ctx.header("Access-Control-Request-Method");
/*     */ 
/*     */         
/* 115 */         if (Utils.isEmpty(requestMethod)) {
/* 116 */           requestMethod = ctx.method();
/*     */         }
/*     */         
/* 119 */         if (Utils.isNotEmpty(requestMethod)) {
/* 120 */           ctx.headerSet("Access-Control-Allow-Methods", requestMethod);
/*     */         }
/*     */       } else {
/* 123 */         ctx.headerSet("Access-Control-Allow-Methods", this.allowedMethods);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 128 */     if (Utils.isNotEmpty(this.allowedOrigins) && (
/* 129 */       "*".equals(this.allowedOrigins) || this.allowedOrigins.contains(origin))) {
/* 130 */       ctx.headerSet("Access-Control-Allow-Origin", origin);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 135 */     if (this.allowCredentials) {
/* 136 */       ctx.headerSet("Access-Control-Allow-Credentials", "true");
/*     */     }
/*     */ 
/*     */     
/* 140 */     if (Utils.isNotEmpty(this.exposedHeaders)) {
/* 141 */       ctx.headerSet("Access-Control-Expose-Headers", this.exposedHeaders);
/*     */     }
/*     */     
/* 144 */     if (MethodType.OPTIONS.name.equalsIgnoreCase(ctx.method()))
/* 145 */       ctx.setHandled(true); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\cors\CrossHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */