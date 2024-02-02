/*    */ package org.apache.http.conn;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.ConnectException;
/*    */ import java.net.InetAddress;
/*    */ import java.util.Arrays;
/*    */ import org.apache.http.HttpHost;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpHostConnectException
/*    */   extends ConnectException
/*    */ {
/*    */   private static final long serialVersionUID = -3194482710275220224L;
/*    */   private final HttpHost host;
/*    */   
/*    */   @Deprecated
/*    */   public HttpHostConnectException(HttpHost host, ConnectException cause) {
/* 54 */     this(cause, host, (InetAddress[])null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpHostConnectException(IOException cause, HttpHost host, InetAddress... remoteAddresses) {
/* 66 */     super("Connect to " + ((host != null) ? host.toHostString() : "remote host") + ((remoteAddresses != null && remoteAddresses.length > 0) ? (" " + Arrays.<InetAddress>asList(remoteAddresses)) : "") + ((cause != null && cause.getMessage() != null) ? (" failed: " + cause.getMessage()) : " refused"));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 72 */     this.host = host;
/* 73 */     initCause(cause);
/*    */   }
/*    */   
/*    */   public HttpHost getHost() {
/* 77 */     return this.host;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\HttpHostConnectException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */