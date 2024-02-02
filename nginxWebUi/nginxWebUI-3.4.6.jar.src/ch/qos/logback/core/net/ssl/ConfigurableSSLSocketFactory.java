/*    */ package ch.qos.logback.core.net.ssl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.Socket;
/*    */ import java.net.UnknownHostException;
/*    */ import javax.net.SocketFactory;
/*    */ import javax.net.ssl.SSLSocket;
/*    */ import javax.net.ssl.SSLSocketFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConfigurableSSLSocketFactory
/*    */   extends SocketFactory
/*    */ {
/*    */   private final SSLParametersConfiguration parameters;
/*    */   private final SSLSocketFactory delegate;
/*    */   
/*    */   public ConfigurableSSLSocketFactory(SSLParametersConfiguration parameters, SSLSocketFactory delegate) {
/* 50 */     this.parameters = parameters;
/* 51 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
/* 59 */     SSLSocket socket = (SSLSocket)this.delegate.createSocket(address, port, localAddress, localPort);
/* 60 */     this.parameters.configure(new SSLConfigurableSocket(socket));
/* 61 */     return socket;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createSocket(InetAddress host, int port) throws IOException {
/* 69 */     SSLSocket socket = (SSLSocket)this.delegate.createSocket(host, port);
/* 70 */     this.parameters.configure(new SSLConfigurableSocket(socket));
/* 71 */     return socket;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
/* 79 */     SSLSocket socket = (SSLSocket)this.delegate.createSocket(host, port, localHost, localPort);
/* 80 */     this.parameters.configure(new SSLConfigurableSocket(socket));
/* 81 */     return socket;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
/* 89 */     SSLSocket socket = (SSLSocket)this.delegate.createSocket(host, port);
/* 90 */     this.parameters.configure(new SSLConfigurableSocket(socket));
/* 91 */     return socket;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\ConfigurableSSLSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */