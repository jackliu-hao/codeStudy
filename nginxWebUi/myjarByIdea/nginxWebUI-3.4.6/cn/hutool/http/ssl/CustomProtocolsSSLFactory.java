package cn.hutool.http.ssl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.net.SSLUtil;
import cn.hutool.core.util.ArrayUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class CustomProtocolsSSLFactory extends SSLSocketFactory {
   private final String[] protocols;
   private final SSLSocketFactory base;

   public CustomProtocolsSSLFactory(String... protocols) throws IORuntimeException {
      this.protocols = protocols;
      this.base = SSLUtil.createSSLContext((String)null).getSocketFactory();
   }

   public String[] getDefaultCipherSuites() {
      return this.base.getDefaultCipherSuites();
   }

   public String[] getSupportedCipherSuites() {
      return this.base.getSupportedCipherSuites();
   }

   public Socket createSocket() throws IOException {
      SSLSocket sslSocket = (SSLSocket)this.base.createSocket();
      this.resetProtocols(sslSocket);
      return sslSocket;
   }

   public SSLSocket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
      SSLSocket socket = (SSLSocket)this.base.createSocket(s, host, port, autoClose);
      this.resetProtocols(socket);
      return socket;
   }

   public Socket createSocket(String host, int port) throws IOException {
      SSLSocket socket = (SSLSocket)this.base.createSocket(host, port);
      this.resetProtocols(socket);
      return socket;
   }

   public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
      SSLSocket socket = (SSLSocket)this.base.createSocket(host, port, localHost, localPort);
      this.resetProtocols(socket);
      return socket;
   }

   public Socket createSocket(InetAddress host, int port) throws IOException {
      SSLSocket socket = (SSLSocket)this.base.createSocket(host, port);
      this.resetProtocols(socket);
      return socket;
   }

   public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
      SSLSocket socket = (SSLSocket)this.base.createSocket(address, port, localAddress, localPort);
      this.resetProtocols(socket);
      return socket;
   }

   private void resetProtocols(SSLSocket socket) {
      if (ArrayUtil.isNotEmpty((Object[])this.protocols)) {
         socket.setEnabledProtocols(this.protocols);
      }

   }
}
