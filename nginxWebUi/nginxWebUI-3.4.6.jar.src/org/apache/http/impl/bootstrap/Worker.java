/*    */ package org.apache.http.impl.bootstrap;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.ExceptionLogger;
/*    */ import org.apache.http.HttpServerConnection;
/*    */ import org.apache.http.protocol.BasicHttpContext;
/*    */ import org.apache.http.protocol.HttpContext;
/*    */ import org.apache.http.protocol.HttpCoreContext;
/*    */ import org.apache.http.protocol.HttpService;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class Worker
/*    */   implements Runnable
/*    */ {
/*    */   private final HttpService httpservice;
/*    */   private final HttpServerConnection conn;
/*    */   private final ExceptionLogger exceptionLogger;
/*    */   
/*    */   Worker(HttpService httpservice, HttpServerConnection conn, ExceptionLogger exceptionLogger) {
/* 51 */     this.httpservice = httpservice;
/* 52 */     this.conn = conn;
/* 53 */     this.exceptionLogger = exceptionLogger;
/*    */   }
/*    */   
/*    */   public HttpServerConnection getConnection() {
/* 57 */     return this.conn;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/* 63 */       BasicHttpContext localContext = new BasicHttpContext();
/* 64 */       HttpCoreContext context = HttpCoreContext.adapt((HttpContext)localContext);
/* 65 */       while (!Thread.interrupted() && this.conn.isOpen()) {
/* 66 */         this.httpservice.handleRequest(this.conn, (HttpContext)context);
/* 67 */         localContext.clear();
/*    */       } 
/* 69 */       this.conn.close();
/* 70 */     } catch (Exception ex) {
/* 71 */       this.exceptionLogger.log(ex);
/*    */     } finally {
/*    */       try {
/* 74 */         this.conn.shutdown();
/* 75 */       } catch (IOException ex) {
/* 76 */         this.exceptionLogger.log(ex);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\bootstrap\Worker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */