/*    */ package ch.qos.logback.core.net.ssl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.ServerSocket;
/*    */ import javax.net.ServerSocketFactory;
/*    */ import javax.net.ssl.SSLServerSocket;
/*    */ import javax.net.ssl.SSLServerSocketFactory;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConfigurableSSLServerSocketFactory
/*    */   extends ServerSocketFactory
/*    */ {
/*    */   private final SSLParametersConfiguration parameters;
/*    */   private final SSLServerSocketFactory delegate;
/*    */   
/*    */   public ConfigurableSSLServerSocketFactory(SSLParametersConfiguration parameters, SSLServerSocketFactory delegate) {
/* 49 */     this.parameters = parameters;
/* 50 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
/* 58 */     SSLServerSocket socket = (SSLServerSocket)this.delegate.createServerSocket(port, backlog, ifAddress);
/* 59 */     this.parameters.configure(new SSLConfigurableServerSocket(socket));
/* 60 */     return socket;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServerSocket createServerSocket(int port, int backlog) throws IOException {
/* 68 */     SSLServerSocket socket = (SSLServerSocket)this.delegate.createServerSocket(port, backlog);
/* 69 */     this.parameters.configure(new SSLConfigurableServerSocket(socket));
/* 70 */     return socket;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ServerSocket createServerSocket(int port) throws IOException {
/* 78 */     SSLServerSocket socket = (SSLServerSocket)this.delegate.createServerSocket(port);
/* 79 */     this.parameters.configure(new SSLConfigurableServerSocket(socket));
/* 80 */     return socket;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\ConfigurableSSLServerSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */