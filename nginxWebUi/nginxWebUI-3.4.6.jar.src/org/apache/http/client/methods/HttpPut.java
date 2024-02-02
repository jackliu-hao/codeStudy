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
/*    */ public class HttpPut
/*    */   extends HttpEntityEnclosingRequestBase
/*    */ {
/*    */   public static final String METHOD_NAME = "PUT";
/*    */   
/*    */   public HttpPut() {}
/*    */   
/*    */   public HttpPut(URI uri) {
/* 57 */     setURI(uri);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpPut(String uri) {
/* 65 */     setURI(URI.create(uri));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMethod() {
/* 70 */     return "PUT";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpPut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */