/*    */ package cn.hutool.http.ssl;
/*    */ 
/*    */ import cn.hutool.core.io.IORuntimeException;
/*    */ import cn.hutool.core.net.SSLUtil;
/*    */ import cn.hutool.core.util.ArrayUtil;
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.Socket;
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
/*    */ public class CustomProtocolsSSLFactory
/*    */   extends SSLSocketFactory
/*    */ {
/*    */   private final String[] protocols;
/*    */   private final SSLSocketFactory base;
/*    */   
/*    */   public CustomProtocolsSSLFactory(String... protocols) throws IORuntimeException {
/* 30 */     this.protocols = protocols;
/* 31 */     this.base = SSLUtil.createSSLContext(null).getSocketFactory();
/*    */   }
/*    */ 
/*    */   
/*    */   public String[] getDefaultCipherSuites() {
/* 36 */     return this.base.getDefaultCipherSuites();
/*    */   }
/*    */ 
/*    */   
/*    */   public String[] getSupportedCipherSuites() {
/* 41 */     return this.base.getSupportedCipherSuites();
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket() throws IOException {
/* 46 */     SSLSocket sslSocket = (SSLSocket)this.base.createSocket();
/* 47 */     resetProtocols(sslSocket);
/* 48 */     return sslSocket;
/*    */   }
/*    */ 
/*    */   
/*    */   public SSLSocket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
/* 53 */     SSLSocket socket = (SSLSocket)this.base.createSocket(s, host, port, autoClose);
/* 54 */     resetProtocols(socket);
/* 55 */     return socket;
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket(String host, int port) throws IOException {
/* 60 */     SSLSocket socket = (SSLSocket)this.base.createSocket(host, port);
/* 61 */     resetProtocols(socket);
/* 62 */     return socket;
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
/* 67 */     SSLSocket socket = (SSLSocket)this.base.createSocket(host, port, localHost, localPort);
/* 68 */     resetProtocols(socket);
/* 69 */     return socket;
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket(InetAddress host, int port) throws IOException {
/* 74 */     SSLSocket socket = (SSLSocket)this.base.createSocket(host, port);
/* 75 */     resetProtocols(socket);
/* 76 */     return socket;
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
/* 81 */     SSLSocket socket = (SSLSocket)this.base.createSocket(address, port, localAddress, localPort);
/* 82 */     resetProtocols(socket);
/* 83 */     return socket;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void resetProtocols(SSLSocket socket) {
/* 92 */     if (ArrayUtil.isNotEmpty((Object[])this.protocols))
/* 93 */       socket.setEnabledProtocols(this.protocols); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\ssl\CustomProtocolsSSLFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */