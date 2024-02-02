/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpRequest;
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
/*     */ class RequestEntityProxy
/*     */   implements HttpEntity
/*     */ {
/*     */   private final HttpEntity original;
/*     */   
/*     */   static void enhance(HttpEntityEnclosingRequest request) {
/*  46 */     HttpEntity entity = request.getEntity();
/*  47 */     if (entity != null && !entity.isRepeatable() && !isEnhanced(entity)) {
/*  48 */       request.setEntity(new RequestEntityProxy(entity));
/*     */     }
/*     */   }
/*     */   
/*     */   static boolean isEnhanced(HttpEntity entity) {
/*  53 */     return entity instanceof RequestEntityProxy;
/*     */   }
/*     */   
/*     */   static boolean isRepeatable(HttpRequest request) {
/*  57 */     if (request instanceof HttpEntityEnclosingRequest) {
/*  58 */       HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
/*  59 */       if (entity != null) {
/*  60 */         if (isEnhanced(entity)) {
/*  61 */           RequestEntityProxy proxy = (RequestEntityProxy)entity;
/*  62 */           if (!proxy.isConsumed()) {
/*  63 */             return true;
/*     */           }
/*     */         } 
/*  66 */         return entity.isRepeatable();
/*     */       } 
/*     */     } 
/*  69 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean consumed = false;
/*     */ 
/*     */   
/*     */   RequestEntityProxy(HttpEntity original) {
/*  77 */     this.original = original;
/*     */   }
/*     */   
/*     */   public HttpEntity getOriginal() {
/*  81 */     return this.original;
/*     */   }
/*     */   
/*     */   public boolean isConsumed() {
/*  85 */     return this.consumed;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/*  90 */     return this.original.isRepeatable();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isChunked() {
/*  95 */     return this.original.isChunked();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getContentLength() {
/* 100 */     return this.original.getContentLength();
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getContentType() {
/* 105 */     return this.original.getContentType();
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getContentEncoding() {
/* 110 */     return this.original.getContentEncoding();
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream getContent() throws IOException, IllegalStateException {
/* 115 */     return this.original.getContent();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream outStream) throws IOException {
/* 120 */     this.consumed = true;
/* 121 */     this.original.writeTo(outStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStreaming() {
/* 126 */     return this.original.isStreaming();
/*     */   }
/*     */ 
/*     */   
/*     */   public void consumeContent() throws IOException {
/* 131 */     this.consumed = true;
/* 132 */     this.original.consumeContent();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 137 */     StringBuilder sb = new StringBuilder("RequestEntityProxy{");
/* 138 */     sb.append(this.original);
/* 139 */     sb.append('}');
/* 140 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\RequestEntityProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */