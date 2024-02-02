package io.undertow.client.http;

import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.client.ALPNClientSelector;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientProvider;
import io.undertow.client.http2.Http2ClientProvider;
import io.undertow.connector.ByteBufferPool;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.ssl.SslConnection;
import org.xnio.ssl.XnioSsl;

public class HttpClientProvider implements ClientProvider {
   public Set<String> handlesSchemes() {
      return new HashSet(Arrays.asList("http", "https"));
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioWorker)worker, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioIoThread)ioThread, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      if (uri.getScheme().equals("https")) {
         if (ssl == null) {
            listener.failed(UndertowMessages.MESSAGES.sslWasNull());
            return;
         }

         OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
         if (bindAddress == null) {
            ssl.openSslConnection(worker, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(this.createNotifier(listener), (Object)null);
         } else {
            ssl.openSslConnection(worker, bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(this.createNotifier(listener), (Object)null);
         }
      } else if (bindAddress == null) {
         worker.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri), options).addNotifier(this.createNotifier(listener), (Object)null);
      } else {
         worker.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri), (ChannelListener)null, options).addNotifier(this.createNotifier(listener), (Object)null);
      }

   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      if (uri.getScheme().equals("https")) {
         if (ssl == null) {
            listener.failed(UndertowMessages.MESSAGES.sslWasNull());
            return;
         }

         OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
         if (bindAddress == null) {
            ssl.openSslConnection(ioThread, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(this.createNotifier(listener), (Object)null);
         } else {
            ssl.openSslConnection(ioThread, bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, bufferPool, tlsOptions, uri), tlsOptions).addNotifier(this.createNotifier(listener), (Object)null);
         }
      } else if (bindAddress == null) {
         ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri), options).addNotifier(this.createNotifier(listener), (Object)null);
      } else {
         ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri), (ChannelListener)null, options).addNotifier(this.createNotifier(listener), (Object)null);
      }

   }

   private IoFuture.Notifier<StreamConnection, Object> createNotifier(final ClientCallback<ClientConnection> listener) {
      return new IoFuture.Notifier<StreamConnection, Object>() {
         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
            if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
               listener.failed(ioFuture.getException());
            }

         }
      };
   }

   private ChannelListener<StreamConnection> createOpenListener(final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, final URI uri) {
      return new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection connection) {
            HttpClientProvider.this.handleConnected(connection, listener, bufferPool, options, uri);
         }
      };
   }

   private void handleConnected(StreamConnection connection, final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, URI uri) {
      boolean h2 = options.get(UndertowOptions.ENABLE_HTTP2, false);
      if (connection instanceof SslConnection && h2) {
         List<ALPNClientSelector.ALPNProtocol> protocolList = new ArrayList();
         if (h2) {
            protocolList.add(Http2ClientProvider.alpnProtocol(listener, uri, bufferPool, options));
         }

         ALPNClientSelector.runAlpn((SslConnection)connection, new ChannelListener<SslConnection>() {
            public void handleEvent(SslConnection connection) {
               listener.completed(new HttpClientConnection(connection, options, bufferPool));
            }
         }, listener, (ALPNClientSelector.ALPNProtocol[])protocolList.toArray(new ALPNClientSelector.ALPNProtocol[protocolList.size()]));
      } else {
         if (connection instanceof SslConnection) {
            try {
               ((SslConnection)connection).startHandshake();
            } catch (Throwable var8) {
               listener.failed(var8 instanceof IOException ? (IOException)var8 : new IOException(var8));
            }
         }

         listener.completed(new HttpClientConnection(connection, options, bufferPool));
      }

   }
}
