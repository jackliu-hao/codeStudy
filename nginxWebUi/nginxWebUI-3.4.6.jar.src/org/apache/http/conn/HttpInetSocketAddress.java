/*    */ package org.apache.http.conn;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import org.apache.http.HttpHost;
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
/*    */ @Deprecated
/*    */ public class HttpInetSocketAddress
/*    */   extends InetSocketAddress
/*    */ {
/*    */   private static final long serialVersionUID = -6650701828361907957L;
/*    */   private final HttpHost httphost;
/*    */   
/*    */   public HttpInetSocketAddress(HttpHost httphost, InetAddress addr, int port) {
/* 51 */     super(addr, port);
/* 52 */     Args.notNull(httphost, "HTTP host");
/* 53 */     this.httphost = httphost;
/*    */   }
/*    */   
/*    */   public HttpHost getHttpHost() {
/* 57 */     return this.httphost;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 62 */     return this.httphost.getHostName() + ":" + getPort();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\HttpInetSocketAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */