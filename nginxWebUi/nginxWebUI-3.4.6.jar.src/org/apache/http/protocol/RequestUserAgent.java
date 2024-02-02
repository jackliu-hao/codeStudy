/*    */ package org.apache.http.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpRequestInterceptor;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.params.HttpParams;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RequestUserAgent
/*    */   implements HttpRequestInterceptor
/*    */ {
/*    */   private final String userAgent;
/*    */   
/*    */   public RequestUserAgent(String userAgent) {
/* 55 */     this.userAgent = userAgent;
/*    */   }
/*    */   
/*    */   public RequestUserAgent() {
/* 59 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 65 */     Args.notNull(request, "HTTP request");
/* 66 */     if (!request.containsHeader("User-Agent")) {
/* 67 */       String s = null;
/* 68 */       HttpParams params = request.getParams();
/* 69 */       if (params != null) {
/* 70 */         s = (String)params.getParameter("http.useragent");
/*    */       }
/* 72 */       if (s == null) {
/* 73 */         s = this.userAgent;
/*    */       }
/* 75 */       if (s != null)
/* 76 */         request.addHeader("User-Agent", s); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\RequestUserAgent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */