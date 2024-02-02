/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
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
/*     */ public final class BasicHttpProcessor
/*     */   implements HttpProcessor, HttpRequestInterceptorList, HttpResponseInterceptorList, Cloneable
/*     */ {
/*  57 */   protected final List<HttpRequestInterceptor> requestInterceptors = new ArrayList<HttpRequestInterceptor>();
/*  58 */   protected final List<HttpResponseInterceptor> responseInterceptors = new ArrayList<HttpResponseInterceptor>();
/*     */ 
/*     */   
/*     */   public void addRequestInterceptor(HttpRequestInterceptor itcp) {
/*  62 */     if (itcp == null) {
/*     */       return;
/*     */     }
/*  65 */     this.requestInterceptors.add(itcp);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRequestInterceptor(HttpRequestInterceptor itcp, int index) {
/*  71 */     if (itcp == null) {
/*     */       return;
/*     */     }
/*  74 */     this.requestInterceptors.add(index, itcp);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addResponseInterceptor(HttpResponseInterceptor itcp, int index) {
/*  80 */     if (itcp == null) {
/*     */       return;
/*     */     }
/*  83 */     this.responseInterceptors.add(index, itcp);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeRequestInterceptorByClass(Class<? extends HttpRequestInterceptor> clazz) {
/*  88 */     Iterator<HttpRequestInterceptor> it = this.requestInterceptors.iterator();
/*  89 */     while (it.hasNext()) {
/*  90 */       Object request = it.next();
/*  91 */       if (request.getClass().equals(clazz)) {
/*  92 */         it.remove();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeResponseInterceptorByClass(Class<? extends HttpResponseInterceptor> clazz) {
/*  99 */     Iterator<HttpResponseInterceptor> it = this.responseInterceptors.iterator();
/* 100 */     while (it.hasNext()) {
/* 101 */       Object request = it.next();
/* 102 */       if (request.getClass().equals(clazz)) {
/* 103 */         it.remove();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public final void addInterceptor(HttpRequestInterceptor interceptor) {
/* 109 */     addRequestInterceptor(interceptor);
/*     */   }
/*     */   
/*     */   public final void addInterceptor(HttpRequestInterceptor interceptor, int index) {
/* 113 */     addRequestInterceptor(interceptor, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRequestInterceptorCount() {
/* 118 */     return this.requestInterceptors.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpRequestInterceptor getRequestInterceptor(int index) {
/* 123 */     if (index < 0 || index >= this.requestInterceptors.size()) {
/* 124 */       return null;
/*     */     }
/* 126 */     return this.requestInterceptors.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearRequestInterceptors() {
/* 131 */     this.requestInterceptors.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addResponseInterceptor(HttpResponseInterceptor itcp) {
/* 136 */     if (itcp == null) {
/*     */       return;
/*     */     }
/* 139 */     this.responseInterceptors.add(itcp);
/*     */   }
/*     */   
/*     */   public final void addInterceptor(HttpResponseInterceptor interceptor) {
/* 143 */     addResponseInterceptor(interceptor);
/*     */   }
/*     */   
/*     */   public final void addInterceptor(HttpResponseInterceptor interceptor, int index) {
/* 147 */     addResponseInterceptor(interceptor, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getResponseInterceptorCount() {
/* 152 */     return this.responseInterceptors.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponseInterceptor getResponseInterceptor(int index) {
/* 157 */     if (index < 0 || index >= this.responseInterceptors.size()) {
/* 158 */       return null;
/*     */     }
/* 160 */     return this.responseInterceptors.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearResponseInterceptors() {
/* 165 */     this.responseInterceptors.clear();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInterceptors(List<?> list) {
/* 187 */     Args.notNull(list, "Inteceptor list");
/* 188 */     this.requestInterceptors.clear();
/* 189 */     this.responseInterceptors.clear();
/* 190 */     for (Object obj : list) {
/* 191 */       if (obj instanceof HttpRequestInterceptor) {
/* 192 */         addInterceptor((HttpRequestInterceptor)obj);
/*     */       }
/* 194 */       if (obj instanceof HttpResponseInterceptor) {
/* 195 */         addInterceptor((HttpResponseInterceptor)obj);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearInterceptors() {
/* 204 */     clearRequestInterceptors();
/* 205 */     clearResponseInterceptors();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpRequest request, HttpContext context) throws IOException, HttpException {
/* 213 */     for (HttpRequestInterceptor interceptor : this.requestInterceptors) {
/* 214 */       interceptor.process(request, context);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpResponse response, HttpContext context) throws IOException, HttpException {
/* 223 */     for (HttpResponseInterceptor interceptor : this.responseInterceptors) {
/* 224 */       interceptor.process(response, context);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void copyInterceptors(BasicHttpProcessor target) {
/* 235 */     target.requestInterceptors.clear();
/* 236 */     target.requestInterceptors.addAll(this.requestInterceptors);
/* 237 */     target.responseInterceptors.clear();
/* 238 */     target.responseInterceptors.addAll(this.responseInterceptors);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicHttpProcessor copy() {
/* 247 */     BasicHttpProcessor clone = new BasicHttpProcessor();
/* 248 */     copyInterceptors(clone);
/* 249 */     return clone;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 254 */     BasicHttpProcessor clone = (BasicHttpProcessor)super.clone();
/* 255 */     copyInterceptors(clone);
/* 256 */     return clone;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\BasicHttpProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */