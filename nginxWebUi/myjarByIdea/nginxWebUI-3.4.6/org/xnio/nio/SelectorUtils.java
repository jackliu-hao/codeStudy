package org.xnio.nio;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.TimeUnit;
import org.xnio.Xnio;

final class SelectorUtils {
   private SelectorUtils() {
   }

   public static void await(NioXnio nioXnio, SelectableChannel channel, int op) throws IOException {
      if (NioXnio.IS_HP_UX) {
         await(nioXnio, channel, op, 1L, TimeUnit.SECONDS);
      } else {
         Xnio.checkBlockingAllowed();
         Selector selector = nioXnio.getSelector();

         SelectionKey selectionKey;
         try {
            selectionKey = channel.register(selector, op);
         } catch (ClosedChannelException var6) {
            return;
         }

         selector.select();
         selector.selectedKeys().clear();
         if (Thread.currentThread().isInterrupted()) {
            throw Log.log.interruptedIO();
         } else {
            selectionKey.cancel();
            selector.selectNow();
         }
      }
   }

   public static void await(NioXnio nioXnio, SelectableChannel channel, int op, long time, TimeUnit unit) throws IOException {
      if (time <= 0L) {
         await(nioXnio, channel, op);
      } else {
         Xnio.checkBlockingAllowed();
         Selector selector = nioXnio.getSelector();

         SelectionKey selectionKey;
         try {
            selectionKey = channel.register(selector, op);
         } catch (ClosedChannelException var10) {
            return;
         }

         long timeoutInMillis = unit.toMillis(time);
         selector.select(timeoutInMillis == 0L ? 1L : timeoutInMillis);
         selector.selectedKeys().clear();
         if (Thread.currentThread().isInterrupted()) {
            throw Log.log.interruptedIO();
         } else {
            selectionKey.cancel();
            selector.selectNow();
         }
      }
   }
}
