/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class RequestContent
/*     */   implements HttpRequestInterceptor
/*     */ {
/*     */   private final boolean overwrite;
/*     */   
/*     */   public RequestContent() {
/*  65 */     this(false);
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
/*     */   public RequestContent(boolean overwrite) {
/*  81 */     this.overwrite = overwrite;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/*  87 */     Args.notNull(request, "HTTP request");
/*  88 */     if (request instanceof HttpEntityEnclosingRequest) {
/*  89 */       if (this.overwrite) {
/*  90 */         request.removeHeaders("Transfer-Encoding");
/*  91 */         request.removeHeaders("Content-Length");
/*     */       } else {
/*  93 */         if (request.containsHeader("Transfer-Encoding")) {
/*  94 */           throw new ProtocolException("Transfer-encoding header already present");
/*     */         }
/*  96 */         if (request.containsHeader("Content-Length")) {
/*  97 */           throw new ProtocolException("Content-Length header already present");
/*     */         }
/*     */       } 
/* 100 */       ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
/* 101 */       HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
/* 102 */       if (entity == null) {
/* 103 */         request.addHeader("Content-Length", "0");
/*     */         
/*     */         return;
/*     */       } 
/* 107 */       if (entity.isChunked() || entity.getContentLength() < 0L) {
/* 108 */         if (ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/* 109 */           throw new ProtocolException("Chunked transfer encoding not allowed for " + ver);
/*     */         }
/*     */         
/* 112 */         request.addHeader("Transfer-Encoding", "chunked");
/*     */       } else {
/* 114 */         request.addHeader("Content-Length", Long.toString(entity.getContentLength()));
/*     */       } 
/*     */       
/* 117 */       if (entity.getContentType() != null && !request.containsHeader("Content-Type"))
/*     */       {
/* 119 */         request.addHeader(entity.getContentType());
/*     */       }
/*     */       
/* 122 */       if (entity.getContentEncoding() != null && !request.containsHeader("Content-Encoding"))
/*     */       {
/* 124 */         request.addHeader(entity.getContentEncoding());
/*     */       }
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\RequestContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */