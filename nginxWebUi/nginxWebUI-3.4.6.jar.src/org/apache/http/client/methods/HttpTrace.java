/*    */ package org.apache.http.client.methods;
/*    */ 
/*    */ import java.net.URI;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpTrace
/*    */   extends HttpRequestBase
/*    */ {
/*    */   public static final String METHOD_NAME = "TRACE";
/*    */   
/*    */   public HttpTrace() {}
/*    */   
/*    */   public HttpTrace(URI uri) {
/* 60 */     setURI(uri);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpTrace(String uri) {
/* 68 */     setURI(URI.create(uri));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMethod() {
/* 73 */     return "TRACE";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpTrace.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */