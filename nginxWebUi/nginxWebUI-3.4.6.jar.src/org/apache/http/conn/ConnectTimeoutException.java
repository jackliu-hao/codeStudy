/*    */ package org.apache.http.conn;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InterruptedIOException;
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
/*    */ 
/*    */ 
/*    */ public class ConnectTimeoutException
/*    */   extends InterruptedIOException
/*    */ {
/*    */   private static final long serialVersionUID = -4816682903149535989L;
/*    */   private final HttpHost host;
/*    */   
/*    */   public ConnectTimeoutException() {
/* 55 */     this.host = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConnectTimeoutException(String message) {
/* 62 */     super(message);
/* 63 */     this.host = null;
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
/*    */   public ConnectTimeoutException(IOException cause, HttpHost host, InetAddress... remoteAddresses) {
/* 75 */     super("Connect to " + ((host != null) ? host.toHostString() : "remote host") + ((remoteAddresses != null && remoteAddresses.length > 0) ? (" " + Arrays.<InetAddress>asList(remoteAddresses)) : "") + ((cause != null && cause.getMessage() != null) ? (" failed: " + cause.getMessage()) : " timed out"));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 81 */     this.host = host;
/* 82 */     initCause(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpHost getHost() {
/* 89 */     return this.host;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ConnectTimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */