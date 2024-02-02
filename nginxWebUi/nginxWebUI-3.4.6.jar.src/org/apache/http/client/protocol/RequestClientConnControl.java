/*    */ package org.apache.http.client.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpRequestInterceptor;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.conn.routing.RouteInfo;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RequestClientConnControl
/*    */   implements HttpRequestInterceptor
/*    */ {
/* 54 */   private final Log log = LogFactory.getLog(getClass());
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static final String PROXY_CONN_DIRECTIVE = "Proxy-Connection";
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 65 */     Args.notNull(request, "HTTP request");
/*    */     
/* 67 */     String method = request.getRequestLine().getMethod();
/* 68 */     if (method.equalsIgnoreCase("CONNECT")) {
/* 69 */       request.setHeader("Proxy-Connection", "Keep-Alive");
/*    */       
/*    */       return;
/*    */     } 
/* 73 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*    */ 
/*    */     
/* 76 */     RouteInfo route = clientContext.getHttpRoute();
/* 77 */     if (route == null) {
/* 78 */       this.log.debug("Connection route not set in the context");
/*    */       
/*    */       return;
/*    */     } 
/* 82 */     if ((route.getHopCount() == 1 || route.isTunnelled()) && 
/* 83 */       !request.containsHeader("Connection")) {
/* 84 */       request.addHeader("Connection", "Keep-Alive");
/*    */     }
/*    */     
/* 87 */     if (route.getHopCount() == 2 && !route.isTunnelled() && 
/* 88 */       !request.containsHeader("Proxy-Connection"))
/* 89 */       request.addHeader("Proxy-Connection", "Keep-Alive"); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestClientConnControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */