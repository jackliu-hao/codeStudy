package org.xnio.nio;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.Options;

final class NioPipeStreamConnection extends AbstractNioStreamConnection {
   private final Pipe.SourceChannel sourceChannel;
   private final Pipe.SinkChannel sinkChannel;
   private final NioPipeSourceConduit sourceConduit;
   private final NioPipeSinkConduit sinkConduit;

   NioPipeStreamConnection(WorkerThread workerThread, SelectionKey sourceKey, SelectionKey sinkKey) {
      super(workerThread);
      if (sourceKey != null) {
         this.setSourceConduit(this.sourceConduit = new NioPipeSourceConduit(workerThread, sourceKey, this));
         sourceKey.attach(this.sourceConduit);
         this.sourceChannel = (Pipe.SourceChannel)sourceKey.channel();
      } else {
         this.sourceConduit = null;
         this.sourceChannel = null;
      }

      if (sinkKey != null) {
         this.setSinkConduit(this.sinkConduit = new NioPipeSinkConduit(workerThread, sinkKey, this));
         sinkKey.attach(this.sinkConduit);
         this.sinkChannel = (Pipe.SinkChannel)sinkKey.channel();
      } else {
         this.sinkConduit = null;
         this.sinkChannel = null;
      }

   }

   public SocketAddress getPeerAddress() {
      return null;
   }

   public SocketAddress getLocalAddress() {
      return null;
   }

   protected boolean readClosed() {
      return super.readClosed();
   }

   protected boolean writeClosed() {
      return super.writeClosed();
   }

   protected void notifyWriteClosed() {
      NioPipeSinkConduit conduit = this.sinkConduit;
      if (conduit != null) {
         conduit.writeTerminated();
      }

   }

   protected void notifyReadClosed() {
      NioPipeSourceConduit conduit = this.sourceConduit;
      if (conduit != null) {
         conduit.readTerminated();
      }

   }

   private void cancelKey(NioHandle handle) {
      if (handle != null) {
         handle.cancelKey(false);
      }

   }

   private void closeChannel(Channel channel) throws IOException {
      if (channel != null) {
         try {
            channel.close();
         } catch (ClosedChannelException var3) {
         }
      }

   }

   protected void closeAction() throws IOException {
      try {
         this.cancelKey(this.sourceConduit);
         this.cancelKey(this.sinkConduit);
         this.closeChannel(this.sourceChannel);
         this.closeChannel(this.sinkChannel);
      } finally {
         IoUtils.safeClose((Closeable)this.sourceChannel);
         IoUtils.safeClose((Closeable)this.sinkChannel);
      }

   }

   public boolean supportsOption(Option<?> option) {
      return option == Options.READ_TIMEOUT && this.sourceConduit != null || option == Options.WRITE_TIMEOUT && this.sinkConduit != null;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (option == Options.READ_TIMEOUT) {
         NioPipeSourceConduit conduit = this.sourceConduit;
         return conduit == null ? null : option.cast(conduit.getReadTimeout());
      } else if (option == Options.WRITE_TIMEOUT) {
         NioPipeSinkConduit conduit = this.sinkConduit;
         return conduit == null ? null : option.cast(conduit.getWriteTimeout());
      } else {
         return null;
      }
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      Object result;
      if (option == Options.READ_TIMEOUT) {
         NioPipeSourceConduit conduit = this.sourceConduit;
         result = conduit == null ? null : option.cast(conduit.getAndSetReadTimeout(value == null ? 0 : (Integer)Options.READ_TIMEOUT.cast(value)));
      } else {
         if (option != Options.WRITE_TIMEOUT) {
            return null;
         }

         NioPipeSinkConduit conduit = this.sinkConduit;
         result = conduit == null ? null : option.cast(conduit.getAndSetWriteTimeout(value == null ? 0 : (Integer)Options.WRITE_TIMEOUT.cast(value)));
      }

      return result;
   }

   Pipe.SourceChannel getSourcePipeChannel() {
      return this.sourceChannel;
   }

   Pipe.SinkChannel getSinkPipeChannel() {
      return this.sinkChannel;
   }
}
