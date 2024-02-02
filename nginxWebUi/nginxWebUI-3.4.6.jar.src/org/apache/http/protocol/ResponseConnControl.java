/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.HttpVersion;
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
/*     */ public class ResponseConnControl
/*     */   implements HttpResponseInterceptor
/*     */ {
/*     */   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
/*  63 */     Args.notNull(response, "HTTP response");
/*     */     
/*  65 */     HttpCoreContext corecontext = HttpCoreContext.adapt(context);
/*     */ 
/*     */     
/*  68 */     int status = response.getStatusLine().getStatusCode();
/*  69 */     if (status == 400 || status == 408 || status == 411 || status == 413 || status == 414 || status == 503 || status == 501) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  76 */       response.setHeader("Connection", "Close");
/*     */       return;
/*     */     } 
/*  79 */     Header explicit = response.getFirstHeader("Connection");
/*  80 */     if (explicit != null && "Close".equalsIgnoreCase(explicit.getValue())) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  86 */     HttpEntity entity = response.getEntity();
/*  87 */     if (entity != null) {
/*  88 */       ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
/*  89 */       if (entity.getContentLength() < 0L && (!entity.isChunked() || ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0))) {
/*     */         
/*  91 */         response.setHeader("Connection", "Close");
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*  96 */     HttpRequest request = corecontext.getRequest();
/*  97 */     if (request != null) {
/*  98 */       Header header = request.getFirstHeader("Connection");
/*  99 */       if (header != null) {
/* 100 */         response.setHeader("Connection", header.getValue());
/* 101 */       } else if (request.getProtocolVersion().lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/* 102 */         response.setHeader("Connection", "Close");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\ResponseConnControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */