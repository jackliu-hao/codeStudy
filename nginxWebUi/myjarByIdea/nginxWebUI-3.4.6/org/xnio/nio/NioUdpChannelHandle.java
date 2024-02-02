package org.xnio.nio;

import java.io.Closeable;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import org.xnio.Bits;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;

final class NioUdpChannelHandle extends NioHandle {
   private final NioUdpChannel channel;

   NioUdpChannelHandle(WorkerThread workerThread, SelectionKey selectionKey, NioUdpChannel channel) {
      super(workerThread, selectionKey);
      this.channel = channel;
   }

   void handleReady(int ops) {
      try {
         if (ops == 0) {
            SelectionKey key = this.getSelectionKey();
            int interestOps = key.interestOps();
            if (interestOps == 0) {
               this.forceTermination();
               return;
            }

            ops = interestOps;
         }

         if (Bits.allAreSet(ops, 1)) {
            try {
               ChannelListeners.invokeChannelListener(this.channel, this.channel.getReadListener());
            } catch (Throwable var5) {
            }
         }

         if (Bits.allAreSet(ops, 4)) {
            try {
               ChannelListeners.invokeChannelListener(this.channel, this.channel.getWriteListener());
            } catch (Throwable var4) {
            }
         }
      } catch (CancelledKeyException var6) {
      }

   }

   void forceTermination() {
      IoUtils.safeClose((Closeable)this.channel);
   }

   void terminated() {
      this.channel.invokeCloseHandler();
   }
}
