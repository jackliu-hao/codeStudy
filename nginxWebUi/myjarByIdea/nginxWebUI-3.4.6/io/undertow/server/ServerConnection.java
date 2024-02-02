package io.undertow.server;

import io.undertow.connector.ByteBufferPool;
import io.undertow.util.AbstractAttachable;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLSession;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.ConnectedChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;

public abstract class ServerConnection extends AbstractAttachable implements ConnectedChannel {
   /** @deprecated */
   @Deprecated
   public abstract Pool<ByteBuffer> getBufferPool();

   public abstract ByteBufferPool getByteBufferPool();

   public abstract XnioWorker getWorker();

   public abstract XnioIoThread getIoThread();

   public abstract HttpServerExchange sendOutOfBandResponse(HttpServerExchange var1);

   public abstract boolean isContinueResponseSupported();

   public abstract void terminateRequestChannel(HttpServerExchange var1);

   public abstract boolean isOpen();

   public abstract boolean supportsOption(Option<?> var1);

   public abstract <T> T getOption(Option<T> var1) throws IOException;

   public abstract <T> T setOption(Option<T> var1, T var2) throws IllegalArgumentException, IOException;

   public abstract void close() throws IOException;

   public SSLSession getSslSession() {
      return null;
   }

   public abstract SocketAddress getPeerAddress();

   public abstract <A extends SocketAddress> A getPeerAddress(Class<A> var1);

   public abstract SocketAddress getLocalAddress();

   public abstract <A extends SocketAddress> A getLocalAddress(Class<A> var1);

   public abstract OptionMap getUndertowOptions();

   public abstract int getBufferSize();

   public abstract SSLSessionInfo getSslSessionInfo();

   public abstract void setSslSessionInfo(SSLSessionInfo var1);

   public abstract void addCloseListener(CloseListener var1);

   protected abstract StreamConnection upgradeChannel();

   protected abstract ConduitStreamSinkChannel getSinkChannel();

   protected abstract ConduitStreamSourceChannel getSourceChannel();

   protected abstract StreamSinkConduit getSinkConduit(HttpServerExchange var1, StreamSinkConduit var2);

   protected abstract boolean isUpgradeSupported();

   protected abstract boolean isConnectSupported();

   protected abstract void exchangeComplete(HttpServerExchange var1);

   protected abstract void setUpgradeListener(HttpUpgradeListener var1);

   protected abstract void setConnectListener(HttpUpgradeListener var1);

   protected abstract void maxEntitySizeUpdated(HttpServerExchange var1);

   public abstract String getTransportProtocol();

   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders) {
      return false;
   }

   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders, HttpHandler handler) {
      return false;
   }

   public boolean isPushSupported() {
      return false;
   }

   public abstract boolean isRequestTrailerFieldsSupported();

   public interface CloseListener {
      void closed(ServerConnection var1);
   }
}
