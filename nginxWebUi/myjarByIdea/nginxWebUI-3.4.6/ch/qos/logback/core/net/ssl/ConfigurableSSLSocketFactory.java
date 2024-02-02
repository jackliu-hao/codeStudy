package ch.qos.logback.core.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ConfigurableSSLSocketFactory extends SocketFactory {
   private final SSLParametersConfiguration parameters;
   private final SSLSocketFactory delegate;

   public ConfigurableSSLSocketFactory(SSLParametersConfiguration parameters, SSLSocketFactory delegate) {
      this.parameters = parameters;
      this.delegate = delegate;
   }

   public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
      SSLSocket socket = (SSLSocket)this.delegate.createSocket(address, port, localAddress, localPort);
      this.parameters.configure(new SSLConfigurableSocket(socket));
      return socket;
   }

   public Socket createSocket(InetAddress host, int port) throws IOException {
      SSLSocket socket = (SSLSocket)this.delegate.createSocket(host, port);
      this.parameters.configure(new SSLConfigurableSocket(socket));
      return socket;
   }

   public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
      SSLSocket socket = (SSLSocket)this.delegate.createSocket(host, port, localHost, localPort);
      this.parameters.configure(new SSLConfigurableSocket(socket));
      return socket;
   }

   public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
      SSLSocket socket = (SSLSocket)this.delegate.createSocket(host, port);
      this.parameters.configure(new SSLConfigurableSocket(socket));
      return socket;
   }
}
