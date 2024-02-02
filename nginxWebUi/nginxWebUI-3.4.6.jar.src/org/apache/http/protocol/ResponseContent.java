/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
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
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class ResponseContent
/*     */   implements HttpResponseInterceptor
/*     */ {
/*     */   private final boolean overwrite;
/*     */   
/*     */   public ResponseContent() {
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
/*     */   public ResponseContent(boolean overwrite) {
/*  81 */     this.overwrite = overwrite;
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
/*     */   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
/*  94 */     Args.notNull(response, "HTTP response");
/*  95 */     if (this.overwrite) {
/*  96 */       response.removeHeaders("Transfer-Encoding");
/*  97 */       response.removeHeaders("Content-Length");
/*     */     } else {
/*  99 */       if (response.containsHeader("Transfer-Encoding")) {
/* 100 */         throw new ProtocolException("Transfer-encoding header already present");
/*     */       }
/* 102 */       if (response.containsHeader("Content-Length")) {
/* 103 */         throw new ProtocolException("Content-Length header already present");
/*     */       }
/*     */     } 
/* 106 */     ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
/* 107 */     HttpEntity entity = response.getEntity();
/* 108 */     if (entity != null) {
/* 109 */       long len = entity.getContentLength();
/* 110 */       if (entity.isChunked() && !ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/* 111 */         response.addHeader("Transfer-Encoding", "chunked");
/* 112 */       } else if (len >= 0L) {
/* 113 */         response.addHeader("Content-Length", Long.toString(entity.getContentLength()));
/*     */       } 
/*     */       
/* 116 */       if (entity.getContentType() != null && !response.containsHeader("Content-Type"))
/*     */       {
/* 118 */         response.addHeader(entity.getContentType());
/*     */       }
/*     */       
/* 121 */       if (entity.getContentEncoding() != null && !response.containsHeader("Content-Encoding"))
/*     */       {
/* 123 */         response.addHeader(entity.getContentEncoding());
/*     */       }
/*     */     } else {
/* 126 */       int status = response.getStatusLine().getStatusCode();
/* 127 */       if (status != 204 && status != 304 && status != 205)
/*     */       {
/*     */         
/* 130 */         response.addHeader("Content-Length", "0");
/*     */       }
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\ResponseContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */