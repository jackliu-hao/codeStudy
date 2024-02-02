package org.xnio;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.channels.CloseableChannel;
import org.xnio.channels.ConnectedChannel;

public abstract class Connection implements CloseableChannel, ConnectedChannel {
   protected final XnioIoThread thread;
   private volatile int state;
   private static final int FLAG_READ_CLOSED = 1;
   private static final int FLAG_WRITE_CLOSED = 2;
   private static final AtomicIntegerFieldUpdater<Connection> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(Connection.class, "state");

   protected Connection(XnioIoThread thread) {
      this.thread = thread;
   }

   private static <A extends SocketAddress> A castAddress(Class<A> type, SocketAddress address) {
      return type.isInstance(address) ? (SocketAddress)type.cast(address) : null;
   }

   public final <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return castAddress(type, this.getPeerAddress());
   }

   public final <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return castAddress(type, this.getLocalAddress());
   }

   public final XnioWorker getWorker() {
      return this.thread.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.thread;
   }

   protected boolean readClosed() {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreSet(oldVal, 1)) {
            return false;
         }

         newVal = oldVal | 1;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      if (Bits.allAreSet(newVal, 3)) {
         try {
            this.closeAction();
         } catch (Throwable var4) {
         }

         this.invokeCloseListener();
      }

      return true;
   }

   protected boolean writeClosed() {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreSet(oldVal, 2)) {
            return false;
         }

         newVal = oldVal | 2;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      if (Bits.allAreSet(newVal, 3)) {
         try {
            this.closeAction();
         } catch (Throwable var4) {
         }

         this.invokeCloseListener();
      }

      return true;
   }

   public final void close() throws IOException {
      int oldVal;
      int newVal;
      do {
         oldVal = this.state;
         if (Bits.allAreSet(oldVal, 3)) {
            return;
         }

         newVal = oldVal | 1 | 2;
      } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

      try {
         this.closeAction();
      } finally {
         if (Bits.allAreClear(oldVal, 2)) {
            try {
               this.notifyWriteClosed();
            } catch (Throwable var12) {
            }
         }

         if (Bits.allAreClear(oldVal, 1)) {
            try {
               this.notifyReadClosed();
            } catch (Throwable var11) {
            }
         }

         this.invokeCloseListener();
      }

   }

   public boolean isReadShutdown() {
      return Bits.allAreSet(this.state, 1);
   }

   public boolean isWriteShutdown() {
      return Bits.allAreSet(this.state, 2);
   }

   public boolean isOpen() {
      return Bits.anyAreClear(this.state, 3);
   }

   protected abstract void notifyWriteClosed();

   protected abstract void notifyReadClosed();

   abstract void invokeCloseListener();

   protected void closeAction() throws IOException {
   }

   public boolean supportsOption(Option<?> option) {
      return false;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return null;
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return null;
   }
}
