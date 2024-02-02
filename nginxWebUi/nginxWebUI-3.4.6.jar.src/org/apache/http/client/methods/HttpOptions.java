/*    */ package org.apache.http.client.methods;
/*    */ 
/*    */ import java.net.URI;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HeaderElement;
/*    */ import org.apache.http.HeaderIterator;
/*    */ import org.apache.http.HttpResponse;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpOptions
/*    */   extends HttpRequestBase
/*    */ {
/*    */   public static final String METHOD_NAME = "OPTIONS";
/*    */   
/*    */   public HttpOptions() {}
/*    */   
/*    */   public HttpOptions(URI uri) {
/* 67 */     setURI(uri);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpOptions(String uri) {
/* 75 */     setURI(URI.create(uri));
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMethod() {
/* 80 */     return "OPTIONS";
/*    */   }
/*    */   
/*    */   public Set<String> getAllowedMethods(HttpResponse response) {
/* 84 */     Args.notNull(response, "HTTP response");
/*    */     
/* 86 */     HeaderIterator it = response.headerIterator("Allow");
/* 87 */     Set<String> methods = new HashSet<String>();
/* 88 */     while (it.hasNext()) {
/* 89 */       Header header = it.nextHeader();
/* 90 */       HeaderElement[] elements = header.getElements();
/* 91 */       for (HeaderElement element : elements) {
/* 92 */         methods.add(element.getName());
/*    */       }
/*    */     } 
/* 95 */     return methods;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */