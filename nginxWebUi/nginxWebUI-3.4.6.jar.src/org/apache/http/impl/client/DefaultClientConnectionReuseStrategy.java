/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HeaderIterator;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.impl.DefaultConnectionReuseStrategy;
/*    */ import org.apache.http.message.BasicHeaderIterator;
/*    */ import org.apache.http.message.BasicTokenIterator;
/*    */ import org.apache.http.protocol.HttpContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultClientConnectionReuseStrategy
/*    */   extends DefaultConnectionReuseStrategy
/*    */ {
/* 44 */   public static final DefaultClientConnectionReuseStrategy INSTANCE = new DefaultClientConnectionReuseStrategy();
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean keepAlive(HttpResponse response, HttpContext context) {
/* 49 */     HttpRequest request = (HttpRequest)context.getAttribute("http.request");
/* 50 */     if (request != null) {
/* 51 */       Header[] connHeaders = request.getHeaders("Connection");
/* 52 */       if (connHeaders.length != 0) {
/* 53 */         BasicTokenIterator basicTokenIterator = new BasicTokenIterator((HeaderIterator)new BasicHeaderIterator(connHeaders, null));
/* 54 */         while (basicTokenIterator.hasNext()) {
/* 55 */           String token = basicTokenIterator.nextToken();
/* 56 */           if ("Close".equalsIgnoreCase(token)) {
/* 57 */             return false;
/*    */           }
/*    */         } 
/*    */       } 
/*    */     } 
/* 62 */     return super.keepAlive(response, context);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultClientConnectionReuseStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */