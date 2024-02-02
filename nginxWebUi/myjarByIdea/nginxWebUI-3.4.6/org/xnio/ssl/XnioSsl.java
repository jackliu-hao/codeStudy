package org.xnio.ssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.Xnio;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.ConnectedSslStreamChannel;

public abstract class XnioSsl {
   private static final InetSocketAddress ANY_INET_ADDRESS = new InetSocketAddress(0);
   protected final Xnio xnio;
   protected final SSLContext sslContext;

   protected XnioSsl(Xnio xnio, SSLContext sslContext, OptionMap optionMap) {
      this.xnio = xnio;
      this.sslContext = sslContext;
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super ConnectedSslStreamChannel> openListener, OptionMap optionMap) {
      return this.connectSsl(worker, ANY_INET_ADDRESS, destination, openListener, (ChannelListener)null, optionMap);
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super ConnectedSslStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.connectSsl(worker, ANY_INET_ADDRESS, destination, openListener, bindListener, optionMap);
   }

   /** @deprecated */
   @Deprecated
   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super ConnectedSslStreamChannel> openListener, OptionMap optionMap) {
      return this.connectSsl(worker, bindAddress, destination, openListener, (ChannelListener)null, optionMap);
   }

   /** @deprecated */
   @Deprecated
   public abstract IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker var1, InetSocketAddress var2, InetSocketAddress var3, ChannelListener<? super ConnectedSslStreamChannel> var4, ChannelListener<? super BoundChannel> var5, OptionMap var6);

   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
      return this.openSslConnection((XnioWorker)worker, ANY_INET_ADDRESS, destination, openListener, (ChannelListener)null, optionMap);
   }

   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
      return this.openSslConnection((XnioIoThread)ioThread, ANY_INET_ADDRESS, destination, openListener, (ChannelListener)null, optionMap);
   }

   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.openSslConnection(worker, ANY_INET_ADDRESS, destination, openListener, bindListener, optionMap);
   }

   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.openSslConnection(ioThread, ANY_INET_ADDRESS, destination, openListener, bindListener, optionMap);
   }

   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
      return this.openSslConnection((XnioWorker)worker, bindAddress, destination, openListener, (ChannelListener)null, optionMap);
   }

   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, OptionMap optionMap) {
      return this.openSslConnection((XnioIoThread)ioThread, bindAddress, destination, openListener, (ChannelListener)null, optionMap);
   }

   public abstract IoFuture<SslConnection> openSslConnection(XnioWorker var1, InetSocketAddress var2, InetSocketAddress var3, ChannelListener<? super SslConnection> var4, ChannelListener<? super BoundChannel> var5, OptionMap var6);

   public abstract IoFuture<SslConnection> openSslConnection(XnioIoThread var1, InetSocketAddress var2, InetSocketAddress var3, ChannelListener<? super SslConnection> var4, ChannelListener<? super BoundChannel> var5, OptionMap var6);

   /** @deprecated */
   @Deprecated
   public abstract AcceptingChannel<ConnectedSslStreamChannel> createSslTcpServer(XnioWorker var1, InetSocketAddress var2, ChannelListener<? super AcceptingChannel<ConnectedSslStreamChannel>> var3, OptionMap var4) throws IOException;

   public abstract AcceptingChannel<SslConnection> createSslConnectionServer(XnioWorker var1, InetSocketAddress var2, ChannelListener<? super AcceptingChannel<SslConnection>> var3, OptionMap var4) throws IOException;
}
