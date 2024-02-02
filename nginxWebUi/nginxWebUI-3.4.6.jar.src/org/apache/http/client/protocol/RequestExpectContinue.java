/*    */ package org.apache.http.client.protocol;
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
/*    */ import org.apache.http.client.config.RequestConfig;
/*    */ import org.apache.http.protocol.HttpContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RequestExpectContinue
/*    */   implements HttpRequestInterceptor
/*    */ {
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 66 */     Args.notNull(request, "HTTP request");
/*    */     
/* 68 */     if (!request.containsHeader("Expect") && 
/* 69 */       request instanceof HttpEntityEnclosingRequest) {
/* 70 */       ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
/* 71 */       HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
/*    */       
/* 73 */       if (entity != null && entity.getContentLength() != 0L && !ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/*    */         
/* 75 */         HttpClientContext clientContext = HttpClientContext.adapt(context);
/* 76 */         RequestConfig config = clientContext.getRequestConfig();
/* 77 */         if (config.isExpectContinueEnabled())
/* 78 */           request.addHeader("Expect", "100-continue"); 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestExpectContinue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */