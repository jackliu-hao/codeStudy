/*    */ package org.apache.http.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import org.apache.http.HttpConnection;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpHost;
/*    */ import org.apache.http.HttpInetConnection;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpRequestInterceptor;
/*    */ import org.apache.http.HttpVersion;
/*    */ import org.apache.http.ProtocolException;
/*    */ import org.apache.http.ProtocolVersion;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RequestTargetHost
/*    */   implements HttpRequestInterceptor
/*    */ {
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 62 */     Args.notNull(request, "HTTP request");
/*    */     
/* 64 */     HttpCoreContext coreContext = HttpCoreContext.adapt(context);
/*    */     
/* 66 */     ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
/* 67 */     String method = request.getRequestLine().getMethod();
/* 68 */     if (method.equalsIgnoreCase("CONNECT") && ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/*    */       return;
/*    */     }
/*    */     
/* 72 */     if (!request.containsHeader("Host")) {
/* 73 */       HttpHost targetHost = coreContext.getTargetHost();
/* 74 */       if (targetHost == null) {
/* 75 */         HttpConnection conn = coreContext.getConnection();
/* 76 */         if (conn instanceof HttpInetConnection) {
/*    */ 
/*    */           
/* 79 */           InetAddress address = ((HttpInetConnection)conn).getRemoteAddress();
/* 80 */           int port = ((HttpInetConnection)conn).getRemotePort();
/* 81 */           if (address != null) {
/* 82 */             targetHost = new HttpHost(address.getHostName(), port);
/*    */           }
/*    */         } 
/* 85 */         if (targetHost == null) {
/* 86 */           if (ver.lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/*    */             return;
/*    */           }
/* 89 */           throw new ProtocolException("Target host missing");
/*    */         } 
/*    */       } 
/* 92 */       request.addHeader("Host", targetHost.toHostString());
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\RequestTargetHost.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */