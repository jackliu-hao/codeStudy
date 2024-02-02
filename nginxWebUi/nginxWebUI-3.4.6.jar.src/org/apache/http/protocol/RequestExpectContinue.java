/*    */ package org.apache.http.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpEntity;
/*    */ import org.apache.http.HttpEntityEnclosingRequest;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpRequestInterceptor;
/*    */ import org.apache.http.HttpVersion;
/*    */ import org.apache.http.ProtocolVersion;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RequestExpectContinue
/*    */   implements HttpRequestInterceptor
/*    */ {
/*    */   private final boolean activeByDefault;
/*    */   
/*    */   @Deprecated
/*    */   public RequestExpectContinue() {
/* 62 */     this(false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RequestExpectContinue(boolean activeByDefault) {
/* 70 */     this.activeByDefault = activeByDefault;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 76 */     Args.notNull(request, "HTTP request");
/*    */     
/* 78 */     if (!request.containsHeader("Expect") && 
/* 79 */       request instanceof HttpEntityEnclosingRequest) {
/* 80 */       ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
/* 81 */       HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
/*    */       
/* 83 */       if (entity != null && entity.getContentLength() != 0L && !ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/*    */         
/* 85 */         boolean active = request.getParams().getBooleanParameter("http.protocol.expect-continue", this.activeByDefault);
/*    */         
/* 87 */         if (active)
/* 88 */           request.addHeader("Expect", "100-continue"); 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\RequestExpectContinue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */