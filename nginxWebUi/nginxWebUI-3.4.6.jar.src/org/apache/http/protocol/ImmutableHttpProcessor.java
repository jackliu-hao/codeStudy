/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public final class ImmutableHttpProcessor
/*     */   implements HttpProcessor
/*     */ {
/*     */   private final HttpRequestInterceptor[] requestInterceptors;
/*     */   private final HttpResponseInterceptor[] responseInterceptors;
/*     */   
/*     */   public ImmutableHttpProcessor(HttpRequestInterceptor[] requestInterceptors, HttpResponseInterceptor[] responseInterceptors) {
/*  55 */     if (requestInterceptors != null) {
/*  56 */       int l = requestInterceptors.length;
/*  57 */       this.requestInterceptors = new HttpRequestInterceptor[l];
/*  58 */       System.arraycopy(requestInterceptors, 0, this.requestInterceptors, 0, l);
/*     */     } else {
/*  60 */       this.requestInterceptors = new HttpRequestInterceptor[0];
/*     */     } 
/*  62 */     if (responseInterceptors != null) {
/*  63 */       int l = responseInterceptors.length;
/*  64 */       this.responseInterceptors = new HttpResponseInterceptor[l];
/*  65 */       System.arraycopy(responseInterceptors, 0, this.responseInterceptors, 0, l);
/*     */     } else {
/*  67 */       this.responseInterceptors = new HttpResponseInterceptor[0];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ImmutableHttpProcessor(List<HttpRequestInterceptor> requestInterceptors, List<HttpResponseInterceptor> responseInterceptors) {
/*  78 */     if (requestInterceptors != null) {
/*  79 */       int l = requestInterceptors.size();
/*  80 */       this.requestInterceptors = requestInterceptors.<HttpRequestInterceptor>toArray(new HttpRequestInterceptor[l]);
/*     */     } else {
/*  82 */       this.requestInterceptors = new HttpRequestInterceptor[0];
/*     */     } 
/*  84 */     if (responseInterceptors != null) {
/*  85 */       int l = responseInterceptors.size();
/*  86 */       this.responseInterceptors = responseInterceptors.<HttpResponseInterceptor>toArray(new HttpResponseInterceptor[l]);
/*     */     } else {
/*  88 */       this.responseInterceptors = new HttpResponseInterceptor[0];
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ImmutableHttpProcessor(HttpRequestInterceptorList requestInterceptors, HttpResponseInterceptorList responseInterceptors) {
/* 100 */     if (requestInterceptors != null) {
/* 101 */       int count = requestInterceptors.getRequestInterceptorCount();
/* 102 */       this.requestInterceptors = new HttpRequestInterceptor[count];
/* 103 */       for (int i = 0; i < count; i++) {
/* 104 */         this.requestInterceptors[i] = requestInterceptors.getRequestInterceptor(i);
/*     */       }
/*     */     } else {
/* 107 */       this.requestInterceptors = new HttpRequestInterceptor[0];
/*     */     } 
/* 109 */     if (responseInterceptors != null) {
/* 110 */       int count = responseInterceptors.getResponseInterceptorCount();
/* 111 */       this.responseInterceptors = new HttpResponseInterceptor[count];
/* 112 */       for (int i = 0; i < count; i++) {
/* 113 */         this.responseInterceptors[i] = responseInterceptors.getResponseInterceptor(i);
/*     */       }
/*     */     } else {
/* 116 */       this.responseInterceptors = new HttpResponseInterceptor[0];
/*     */     } 
/*     */   }
/*     */   
/*     */   public ImmutableHttpProcessor(HttpRequestInterceptor... requestInterceptors) {
/* 121 */     this(requestInterceptors, (HttpResponseInterceptor[])null);
/*     */   }
/*     */   
/*     */   public ImmutableHttpProcessor(HttpResponseInterceptor... responseInterceptors) {
/* 125 */     this((HttpRequestInterceptor[])null, responseInterceptors);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpRequest request, HttpContext context) throws IOException, HttpException {
/* 132 */     for (HttpRequestInterceptor requestInterceptor : this.requestInterceptors) {
/* 133 */       requestInterceptor.process(request, context);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpResponse response, HttpContext context) throws IOException, HttpException {
/* 141 */     for (HttpResponseInterceptor responseInterceptor : this.responseInterceptors)
/* 142 */       responseInterceptor.process(response, context); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\ImmutableHttpProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */