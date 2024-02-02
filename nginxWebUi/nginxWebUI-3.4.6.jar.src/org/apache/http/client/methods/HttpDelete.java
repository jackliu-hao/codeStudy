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
/*    */ public class HttpDelete
/*    */   extends HttpRequestBase
/*    */ {
/*    */   public static final String METHOD_NAME = "DELETE";
/*    */   
/*    */   public HttpDelete() {}
/*    */   
/*    */   public HttpDelete(URI uri) {
/* 58 */     setURI(uri);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpDelete(String uri) {
/* 66 */     setURI(URI.create(uri));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMethod() {
/* 71 */     return "DELETE";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpDelete.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */