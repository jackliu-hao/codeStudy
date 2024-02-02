/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponseInterceptor;
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
/*     */ public class HttpProcessorBuilder
/*     */ {
/*     */   private ChainBuilder<HttpRequestInterceptor> requestChainBuilder;
/*     */   private ChainBuilder<HttpResponseInterceptor> responseChainBuilder;
/*     */   
/*     */   public static HttpProcessorBuilder create() {
/*  44 */     return new HttpProcessorBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ChainBuilder<HttpRequestInterceptor> getRequestChainBuilder() {
/*  52 */     if (this.requestChainBuilder == null) {
/*  53 */       this.requestChainBuilder = new ChainBuilder<HttpRequestInterceptor>();
/*     */     }
/*  55 */     return this.requestChainBuilder;
/*     */   }
/*     */   
/*     */   private ChainBuilder<HttpResponseInterceptor> getResponseChainBuilder() {
/*  59 */     if (this.responseChainBuilder == null) {
/*  60 */       this.responseChainBuilder = new ChainBuilder<HttpResponseInterceptor>();
/*     */     }
/*  62 */     return this.responseChainBuilder;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addFirst(HttpRequestInterceptor e) {
/*  66 */     if (e == null) {
/*  67 */       return this;
/*     */     }
/*  69 */     getRequestChainBuilder().addFirst(e);
/*  70 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addLast(HttpRequestInterceptor e) {
/*  74 */     if (e == null) {
/*  75 */       return this;
/*     */     }
/*  77 */     getRequestChainBuilder().addLast(e);
/*  78 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder add(HttpRequestInterceptor e) {
/*  82 */     return addLast(e);
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addAllFirst(HttpRequestInterceptor... e) {
/*  86 */     if (e == null) {
/*  87 */       return this;
/*     */     }
/*  89 */     getRequestChainBuilder().addAllFirst(e);
/*  90 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addAllLast(HttpRequestInterceptor... e) {
/*  94 */     if (e == null) {
/*  95 */       return this;
/*     */     }
/*  97 */     getRequestChainBuilder().addAllLast(e);
/*  98 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addAll(HttpRequestInterceptor... e) {
/* 102 */     return addAllLast(e);
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addFirst(HttpResponseInterceptor e) {
/* 106 */     if (e == null) {
/* 107 */       return this;
/*     */     }
/* 109 */     getResponseChainBuilder().addFirst(e);
/* 110 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addLast(HttpResponseInterceptor e) {
/* 114 */     if (e == null) {
/* 115 */       return this;
/*     */     }
/* 117 */     getResponseChainBuilder().addLast(e);
/* 118 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder add(HttpResponseInterceptor e) {
/* 122 */     return addLast(e);
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addAllFirst(HttpResponseInterceptor... e) {
/* 126 */     if (e == null) {
/* 127 */       return this;
/*     */     }
/* 129 */     getResponseChainBuilder().addAllFirst(e);
/* 130 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addAllLast(HttpResponseInterceptor... e) {
/* 134 */     if (e == null) {
/* 135 */       return this;
/*     */     }
/* 137 */     getResponseChainBuilder().addAllLast(e);
/* 138 */     return this;
/*     */   }
/*     */   
/*     */   public HttpProcessorBuilder addAll(HttpResponseInterceptor... e) {
/* 142 */     return addAllLast(e);
/*     */   }
/*     */   
/*     */   public HttpProcessor build() {
/* 146 */     return new ImmutableHttpProcessor((this.requestChainBuilder != null) ? this.requestChainBuilder.build() : null, (this.responseChainBuilder != null) ? this.responseChainBuilder.build() : null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\HttpProcessorBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */