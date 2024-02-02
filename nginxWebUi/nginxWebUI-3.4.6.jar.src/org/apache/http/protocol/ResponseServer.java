/*    */ package org.apache.http.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.HttpResponseInterceptor;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class ResponseServer
/*    */   implements HttpResponseInterceptor
/*    */ {
/*    */   private final String originServer;
/*    */   
/*    */   public ResponseServer(String originServer) {
/* 55 */     this.originServer = originServer;
/*    */   }
/*    */   
/*    */   public ResponseServer() {
/* 59 */     this(null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
/* 65 */     Args.notNull(response, "HTTP response");
/* 66 */     if (!response.containsHeader("Server") && 
/* 67 */       this.originServer != null)
/* 68 */       response.addHeader("Server", this.originServer); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\ResponseServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */