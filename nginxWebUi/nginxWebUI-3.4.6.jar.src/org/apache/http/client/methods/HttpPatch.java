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
/*    */ 
/*    */ public class HttpPatch
/*    */   extends HttpEntityEnclosingRequestBase
/*    */ {
/*    */   public static final String METHOD_NAME = "PATCH";
/*    */   
/*    */   public HttpPatch() {}
/*    */   
/*    */   public HttpPatch(URI uri) {
/* 61 */     setURI(uri);
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpPatch(String uri) {
/* 66 */     setURI(URI.create(uri));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMethod() {
/* 71 */     return "PATCH";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpPatch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */