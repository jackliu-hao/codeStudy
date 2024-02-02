package io.undertow.client;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

public interface ClientConnection extends Channel {
   void sendRequest(ClientRequest var1, ClientCallback<ClientExchange> var2);

   StreamConnection performUpgrade() throws IOException;

   ByteBufferPool getBufferPool();

   SocketAddress getPeerAddress();

   <A extends SocketAddress> A getPeerAddress(Class<A> var1);

   ChannelListener.Setter<? extends ClientConnection> getCloseSetter();

   SocketAddress getLocalAddress();

   <A extends SocketAddress> A getLocalAddress(Class<A> var1);

   XnioWorker getWorker();

   XnioIoThread getIoThread();

   boolean isOpen();

   boolean supportsOption(Option<?> var1);

   <T> T getOption(Option<T> var1) throws IOException;

   <T> T setOption(Option<T> var1, T var2) throws IllegalArgumentException, IOException;

   boolean isUpgraded();

   boolean isPushSupported();

   boolean isMultiplexingSupported();

   ClientStatistics getStatistics();

   boolean isUpgradeSupported();

   void addCloseListener(ChannelListener<ClientConnection> var1);

   default boolean isPingSupported() {
      return false;
   }

   default void sendPing(PingListener listener, long timeout, TimeUnit timeUnit) {
      listener.failed(UndertowMessages.MESSAGES.pingNotSupported());
   }

   public interface PingListener {
      void acknowledged();

      void failed(IOException var1);
   }
}
