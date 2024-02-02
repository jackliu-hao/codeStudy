package org.xnio.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Set;
import org.xnio.Option;
import org.xnio.Options;

final class NioSocketStreamConnection extends AbstractNioStreamConnection {
   private final ChannelClosed closedHandle;
   private final NioSocketConduit conduit;
   private static final Set<Option<?>> OPTIONS;

   NioSocketStreamConnection(WorkerThread workerThread, SelectionKey key, ChannelClosed closedHandle) {
      super(workerThread);
      this.conduit = new NioSocketConduit(workerThread, key, this);
      key.attach(this.conduit);
      this.closedHandle = closedHandle;
      this.setSinkConduit(this.conduit);
      this.setSourceConduit(this.conduit);
   }

   public SocketAddress getPeerAddress() {
      Socket socket = this.conduit.getSocketChannel().socket();
      return new InetSocketAddress(socket.getInetAddress(), socket.getPort());
   }

   public SocketAddress getLocalAddress() {
      Socket socket = this.conduit.getSocketChannel().socket();
      return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
   }

   public boolean supportsOption(Option<?> option) {
      return OPTIONS.contains(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (option == Options.CLOSE_ABORT) {
         return option.cast(this.conduit.getSocketChannel().socket().getSoLinger() == 0);
      } else if (option == Options.IP_TRAFFIC_CLASS) {
         return option.cast(this.conduit.getSocketChannel().socket().getTrafficClass());
      } else if (option == Options.KEEP_ALIVE) {
         return option.cast(this.conduit.getSocketChannel().socket().getKeepAlive());
      } else if (option == Options.READ_TIMEOUT) {
         return option.cast(this.conduit.getReadTimeout());
      } else if (option == Options.RECEIVE_BUFFER) {
         return option.cast(this.conduit.getSocketChannel().socket().getReceiveBufferSize());
      } else if (option == Options.SEND_BUFFER) {
         return option.cast(this.conduit.getSocketChannel().socket().getSendBufferSize());
      } else if (option == Options.TCP_NODELAY) {
         return option.cast(this.conduit.getSocketChannel().socket().getTcpNoDelay());
      } else if (option == Options.TCP_OOB_INLINE) {
         return option.cast(this.conduit.getSocketChannel().socket().getOOBInline());
      } else {
         return option == Options.WRITE_TIMEOUT ? option.cast(this.conduit.getWriteTimeout()) : null;
      }
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      Object result;
      if (option == Options.CLOSE_ABORT) {
         result = option.cast(this.conduit.getSocketChannel().socket().getSoLinger() == 0);
         this.conduit.getSocketChannel().socket().setSoLinger((Boolean)Options.CLOSE_ABORT.cast(value, Boolean.FALSE), 0);
      } else if (option == Options.IP_TRAFFIC_CLASS) {
         result = option.cast(this.conduit.getSocketChannel().socket().getTrafficClass());
         this.conduit.getSocketChannel().socket().setTrafficClass((Integer)Options.IP_TRAFFIC_CLASS.cast(value));
      } else if (option == Options.KEEP_ALIVE) {
         result = option.cast(this.conduit.getSocketChannel().socket().getKeepAlive());
         this.conduit.getSocketChannel().socket().setKeepAlive((Boolean)Options.KEEP_ALIVE.cast(value, Boolean.FALSE));
      } else if (option == Options.READ_TIMEOUT) {
         result = option.cast(this.conduit.getAndSetReadTimeout(value == null ? 0 : (Integer)Options.READ_TIMEOUT.cast(value)));
      } else if (option == Options.RECEIVE_BUFFER) {
         result = option.cast(this.conduit.getSocketChannel().socket().getReceiveBufferSize());
         this.conduit.getSocketChannel().socket().setReceiveBufferSize((Integer)Options.RECEIVE_BUFFER.cast(value));
      } else if (option == Options.SEND_BUFFER) {
         result = option.cast(this.conduit.getSocketChannel().socket().getSendBufferSize());
         this.conduit.getSocketChannel().socket().setSendBufferSize((Integer)Options.SEND_BUFFER.cast(value));
      } else if (option == Options.TCP_NODELAY) {
         result = option.cast(this.conduit.getSocketChannel().socket().getTcpNoDelay());
         this.conduit.getSocketChannel().socket().setTcpNoDelay((Boolean)Options.TCP_NODELAY.cast(value, Boolean.FALSE));
      } else if (option == Options.TCP_OOB_INLINE) {
         result = option.cast(this.conduit.getSocketChannel().socket().getOOBInline());
         this.conduit.getSocketChannel().socket().setOOBInline((Boolean)Options.TCP_OOB_INLINE.cast(value, Boolean.FALSE));
      } else {
         if (option != Options.WRITE_TIMEOUT) {
            return null;
         }

         result = option.cast(this.conduit.getAndSetWriteTimeout(value == null ? 0 : (Integer)Options.WRITE_TIMEOUT.cast(value)));
      }

      return result;
   }

   protected void closeAction() throws IOException {
      boolean var6 = false;

      ChannelClosed closedHandle;
      label68: {
         try {
            var6 = true;
            this.conduit.cancelKey(false);
            this.conduit.getSocketChannel().close();
            var6 = false;
            break label68;
         } catch (ClosedChannelException var7) {
            var6 = false;
         } finally {
            if (var6) {
               ChannelClosed closedHandle = this.closedHandle;
               if (closedHandle != null) {
                  closedHandle.channelClosed();
               }

            }
         }

         closedHandle = this.closedHandle;
         if (closedHandle != null) {
            closedHandle.channelClosed();
         }

         return;
      }

      closedHandle = this.closedHandle;
      if (closedHandle != null) {
         closedHandle.channelClosed();
      }

   }

   protected void notifyWriteClosed() {
      this.conduit.writeTerminated();
      super.notifyWriteClosed();
   }

   protected void notifyReadClosed() {
      this.conduit.readTerminated();
      super.notifyReadClosed();
   }

   SocketChannel getChannel() {
      return this.conduit.getSocketChannel();
   }

   NioSocketConduit getConduit() {
      return this.conduit;
   }

   static {
      OPTIONS = Option.setBuilder().add(Options.CLOSE_ABORT).add(Options.IP_TRAFFIC_CLASS).add(Options.KEEP_ALIVE).add(Options.READ_TIMEOUT).add(Options.RECEIVE_BUFFER).add(Options.SEND_BUFFER).add(Options.TCP_NODELAY).add(Options.TCP_OOB_INLINE).add(Options.WRITE_TIMEOUT).create();
   }
}
