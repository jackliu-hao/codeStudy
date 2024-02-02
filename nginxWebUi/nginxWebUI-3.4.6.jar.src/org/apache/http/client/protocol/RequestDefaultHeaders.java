/*    */ package org.apache.http.client.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpRequestInterceptor;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*    */ public class RequestDefaultHeaders
/*    */   implements HttpRequestInterceptor
/*    */ {
/*    */   private final Collection<? extends Header> defaultHeaders;
/*    */   
/*    */   public RequestDefaultHeaders(Collection<? extends Header> defaultHeaders) {
/* 59 */     this.defaultHeaders = defaultHeaders;
/*    */   }
/*    */   
/*    */   public RequestDefaultHeaders() {
/* 63 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 69 */     Args.notNull(request, "HTTP request");
/*    */     
/* 71 */     String method = request.getRequestLine().getMethod();
/* 72 */     if (method.equalsIgnoreCase("CONNECT")) {
/*    */       return;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 78 */     Collection<? extends Header> defHeaders = (Collection<? extends Header>)request.getParams().getParameter("http.default-headers");
/*    */     
/* 80 */     if (defHeaders == null) {
/* 81 */       defHeaders = this.defaultHeaders;
/*    */     }
/*    */     
/* 84 */     if (defHeaders != null)
/* 85 */       for (Header defHeader : defHeaders)
/* 86 */         request.addHeader(defHeader);  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestDefaultHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */