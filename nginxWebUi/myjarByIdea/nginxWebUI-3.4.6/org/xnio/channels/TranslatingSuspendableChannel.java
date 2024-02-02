package org.xnio.channels;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;

/** @deprecated */
@Deprecated
public abstract class TranslatingSuspendableChannel<C extends SuspendableChannel, W extends SuspendableChannel> implements SuspendableChannel, WrappedChannel<W>, ReadListenerSettable<C>, WriteListenerSettable<C>, CloseListenerSettable<C> {
   protected final W channel;
   private ChannelListener<? super C> readListener;
   private ChannelListener<? super C> writeListener;
   private ChannelListener<? super C> closeListener;
   private volatile int state;
   private volatile Thread readWaiter;
   private volatile Thread writeWaiter;
   private static final AtomicIntegerFieldUpdater<TranslatingSuspendableChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(TranslatingSuspendableChannel.class, "state");
   private static final AtomicReferenceFieldUpdater<TranslatingSuspendableChannel, Thread> readWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(TranslatingSuspendableChannel.class, Thread.class, "readWaiter");
   private static final AtomicReferenceFieldUpdater<TranslatingSuspendableChannel, Thread> writeWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(TranslatingSuspendableChannel.class, Thread.class, "writeWaiter");
   private static final int READ_REQUESTED = 1;
   private static final int READ_REQUIRES_WRITE = 2;
   private static final int READ_READY = 4;
   private static final int READ_SHUT_DOWN = 8;
   private static final int READ_REQUIRES_EXT = Bits.intBitMask(11, 15);
   private static final int READ_SINGLE_EXT = 2048;
   private static final int READ_FLAGS = Bits.intBitMask(0, 15);
   private static final int WRITE_REQUESTED = 65536;
   private static final int WRITE_REQUIRES_READ = 131072;
   private static final int WRITE_READY = 262144;
   private static final int WRITE_SHUT_DOWN = 524288;
   private static final int WRITE_COMPLETE = 1048576;
   private static final int WRITE_REQUIRES_EXT = Bits.intBitMask(27, 31);
   private static final int WRITE_SINGLE_EXT = 134217728;
   private static final int WRITE_FLAGS = Bits.intBitMask(16, 31);
   private final ChannelListener<Channel> delegateReadListener = new ChannelListener<Channel>() {
      public void handleEvent(Channel channel) {
         TranslatingSuspendableChannel.this.handleReadable();
      }

      public String toString() {
         return "Read listener for " + TranslatingSuspendableChannel.this;
      }
   };
   private final ChannelListener<Channel> delegateWriteListener = new ChannelListener<Channel>() {
      public void handleEvent(Channel channel) {
         TranslatingSuspendableChannel.this.handleWritable();
      }

      public String toString() {
         return "Write listener for " + TranslatingSuspendableChannel.this;
      }
   };
   private final ChannelListener<Channel> delegateCloseListener = new ChannelListener<Channel>() {
      public void handleEvent(Channel channel) {
         IoUtils.safeClose((Closeable)TranslatingSuspendableChannel.this);
      }

      public String toString() {
         return "Close listener for " + TranslatingSuspendableChannel.this;
      }
   };

   protected TranslatingSuspendableChannel(W channel) {
      if (channel == null) {
         throw Messages.msg.nullParameter("channel");
      } else {
         this.channel = channel;
         channel.getReadSetter().set(this.delegateReadListener);
         channel.getWriteSetter().set(this.delegateWriteListener);
         channel.getCloseSetter().set(this.delegateCloseListener);
      }
   }

   protected void handleReadable() {
      int oldState = this.clearFlags(131072);
      if (Bits.allAreSet(oldState, 131072)) {
         this.unparkWriteWaiters();
         if (Bits.allAreSet(oldState, 65536)) {
            this.channel.wakeupWrites();
         }
      }

      if (Bits.allAreClear(oldState, 4) && Bits.anyAreSet(oldState, 2 | READ_REQUIRES_EXT)) {
         this.channel.suspendReads();
         oldState = this.state;
         if (!Bits.anyAreSet(oldState, 4) && !Bits.allAreClear(oldState, 2 | READ_REQUIRES_EXT)) {
            return;
         }

         this.channel.resumeReads();
      }

      do {
         if (Bits.anyAreSet(oldState, 8)) {
            this.channel.suspendReads();
            return;
         }

         if (Bits.allAreClear(oldState, 1)) {
            this.channel.suspendReads();
            oldState = this.state;
            if (!Bits.allAreSet(oldState, 1)) {
               return;
            }

            this.channel.resumeReads();
         }

         this.unparkReadWaiters();
         ChannelListener<? super C> listener = this.readListener;
         if (listener == null) {
            oldState = this.clearFlag(131073) & -2;
         } else {
            ChannelListeners.invokeChannelListener(this.thisTyped(), listener);
            oldState = this.clearFlags(131072);
         }

         if (Bits.allAreSet(oldState, 131072)) {
            this.unparkWriteWaiters();
            this.channel.wakeupWrites();
         }
      } while(Bits.allAreSet(oldState, 4));

   }

   protected void handleWritable() {
      int oldState = this.clearFlags(2);
      if (Bits.allAreSet(oldState, 2)) {
         this.unparkReadWaiters();
         if (Bits.allAreSet(oldState, 1)) {
            this.channel.wakeupReads();
         }
      }

      if (Bits.allAreClear(oldState, 262144) && Bits.anyAreSet(oldState, 131072 | WRITE_REQUIRES_EXT)) {
         this.channel.suspendWrites();
         oldState = this.state;
         if (!Bits.anyAreSet(oldState, 262144) && !Bits.allAreClear(oldState, 131072 | WRITE_REQUIRES_EXT)) {
            return;
         }

         this.channel.resumeWrites();
      }

      do {
         if (Bits.anyAreSet(oldState, 1048576)) {
            this.channel.suspendWrites();
            return;
         }

         if (Bits.allAreClear(oldState, 65536)) {
            this.channel.suspendWrites();
            oldState = this.state;
            if (!Bits.allAreSet(oldState, 65536)) {
               return;
            }

            this.channel.resumeWrites();
         }

         this.unparkWriteWaiters();
         ChannelListener<? super C> listener = this.writeListener;
         if (listener == null) {
            oldState = this.clearFlags(65538) & -65537;
         } else {
            ChannelListeners.invokeChannelListener(this.thisTyped(), listener);
            oldState = this.clearFlags(2);
         }

         if (Bits.allAreSet(oldState, 2)) {
            this.unparkReadWaiters();
            this.channel.wakeupReads();
         }
      } while(Bits.allAreSet(oldState, 262144));

   }

   protected void handleClosed() {
      ChannelListeners.invokeChannelListener(this.thisTyped(), this.closeListener);
   }

   protected void setReadReady() {
      int oldState = this.setFlags(4);
      this.unparkReadWaiters();
      if (!Bits.allAreSet(oldState, 4)) {
         if (Bits.allAreSet(oldState, 1) && Bits.anyAreSet(oldState, READ_REQUIRES_EXT | 2)) {
            this.channel.wakeupReads();
         }

      }
   }

   protected void clearReadReady() {
      int oldState = this.clearFlags(4);
      if (!Bits.allAreClear(oldState, 4)) {
         if (!Bits.allAreClear(oldState, 1) && !Bits.anyAreSet(oldState, READ_REQUIRES_EXT | 2)) {
            this.channel.resumeReads();
         }

      }
   }

   protected void setReadRequiresWrite() {
      int oldState = this.setFlags(2);
      if (!Bits.allAreSet(oldState, 2)) {
         if (Bits.allAreClear(oldState, 4 | READ_REQUIRES_EXT)) {
            this.channel.resumeWrites();
         }

      }
   }

   protected boolean readRequiresWrite() {
      return Bits.allAreSet(this.state, 2);
   }

   protected void clearReadRequiresWrite() {
      int oldState = this.clearFlags(2);
      if (!Bits.allAreClear(oldState, 2)) {
         if (Bits.allAreClear(oldState, READ_REQUIRES_EXT) && Bits.allAreSet(oldState, 1)) {
            if (Bits.allAreSet(oldState, 4)) {
               this.channel.wakeupReads();
            } else {
               this.channel.resumeReads();
            }
         }

      }
   }

   protected boolean tryAddReadRequiresExternal() {
      int oldState = this.addFlag(READ_REQUIRES_EXT, 2048);
      return (oldState & READ_REQUIRES_EXT) != READ_REQUIRES_EXT;
   }

   protected void removeReadRequiresExternal() {
      this.clearFlag(2048);
   }

   protected boolean setReadShutDown() {
      return (this.setFlags(8) & 524296) == 524288;
   }

   protected void setWriteReady() {
      int oldState = this.setFlags(262144);
      this.unparkWriteWaiters();
      if (!Bits.allAreSet(oldState, 262144)) {
         if (Bits.allAreSet(oldState, 65536) && Bits.anyAreSet(oldState, WRITE_REQUIRES_EXT | 131072)) {
            this.channel.wakeupWrites();
         }

      }
   }

   protected void clearWriteReady() {
      int oldState = this.clearFlags(262144);
      if (!Bits.allAreClear(oldState, 262144)) {
         if (!Bits.allAreClear(oldState, 65536) && !Bits.anyAreSet(oldState, WRITE_REQUIRES_EXT | 131072)) {
            this.channel.resumeWrites();
         }

      }
   }

   protected void setWriteRequiresRead() {
      int oldState = this.setFlags(131072);
      if (!Bits.allAreSet(oldState, 131072)) {
         if (Bits.allAreClear(oldState, 262144 | WRITE_REQUIRES_EXT)) {
            this.channel.resumeReads();
         }

      }
   }

   protected boolean writeRequiresRead() {
      return Bits.allAreSet(this.state, 131072);
   }

   protected void clearWriteRequiresRead() {
      int oldState = this.clearFlags(131072);
      if (!Bits.allAreClear(oldState, 131072)) {
         if (Bits.allAreClear(oldState, WRITE_REQUIRES_EXT) && Bits.allAreSet(oldState, 65536)) {
            if (Bits.allAreSet(oldState, 262144)) {
               this.channel.wakeupWrites();
            } else {
               this.channel.resumeWrites();
            }
         }

      }
   }

   protected boolean tryAddWriteRequiresExternal() {
      int oldState = this.addFlag(WRITE_REQUIRES_EXT, 134217728);
      return (oldState & WRITE_REQUIRES_EXT) != WRITE_REQUIRES_EXT;
   }

   protected void removeWriteRequiresExternal() {
      this.clearFlag(134217728);
   }

   protected boolean setWriteShutDown() {
      return (this.setFlags(524288) & 524296) == 8;
   }

   protected boolean setClosed() {
      return (this.setFlags(524296) & 524296) != 524296;
   }

   protected final C thisTyped() {
      return this;
   }

   public void setReadListener(ChannelListener<? super C> readListener) {
      this.readListener = readListener;
   }

   public ChannelListener<? super C> getReadListener() {
      return this.readListener;
   }

   public void setWriteListener(ChannelListener<? super C> writeListener) {
      this.writeListener = writeListener;
   }

   public ChannelListener<? super C> getWriteListener() {
      return this.writeListener;
   }

   public void setCloseListener(ChannelListener<? super C> closeListener) {
      this.closeListener = closeListener;
   }

   public ChannelListener<? super C> getCloseListener() {
      return this.closeListener;
   }

   public ChannelListener.Setter<C> getCloseSetter() {
      return new CloseListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<C> getReadSetter() {
      return new ReadListenerSettable.Setter(this);
   }

   public ChannelListener.Setter<C> getWriteSetter() {
      return new WriteListenerSettable.Setter(this);
   }

   public void suspendReads() {
      this.clearFlags(1);
   }

   public void resumeReads() {
      int oldState = this.setFlags(1);
      if (!Bits.anyAreSet(oldState, 9)) {
         if (Bits.allAreSet(oldState, 4)) {
            this.channel.wakeupReads();
         } else {
            if (Bits.allAreClear(oldState, READ_REQUIRES_EXT)) {
               if (Bits.allAreSet(oldState, 2)) {
                  this.channel.resumeWrites();
               } else {
                  this.channel.resumeReads();
               }
            }

         }
      }
   }

   public boolean isReadResumed() {
      return Bits.allAreSet(this.state, 1);
   }

   public void wakeupReads() {
      if (!Bits.anyAreSet(this.state, 8)) {
         this.setFlags(1);
         this.channel.wakeupReads();
      }
   }

   public void suspendWrites() {
      this.clearFlags(65536);
   }

   public void resumeWrites() {
      int oldState = this.setFlags(65536);
      if (!Bits.anyAreSet(oldState, 1114112)) {
         if (Bits.allAreSet(oldState, 262144)) {
            this.channel.wakeupWrites();
         } else {
            if (Bits.allAreClear(oldState, WRITE_REQUIRES_EXT)) {
               if (Bits.allAreSet(oldState, 131072)) {
                  this.channel.resumeReads();
               } else {
                  this.channel.resumeWrites();
               }
            }

         }
      }
   }

   public boolean isWriteResumed() {
      return Bits.allAreSet(this.state, 65536);
   }

   public void wakeupWrites() {
      if (!Bits.anyAreSet(this.state, 524288)) {
         this.setFlags(65536);
         this.channel.wakeupWrites();
         this.unparkWriteWaiters();
      }
   }

   public boolean supportsOption(Option<?> option) {
      return this.channel.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.channel.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.channel.setOption(option, value);
   }

   public final boolean flush() throws IOException {
      int oldState = stateUpdater.get(this);
      if (Bits.allAreSet(oldState, 1048576)) {
         return this.channel.flush();
      } else {
         boolean shutDown = Bits.allAreSet(oldState, 524288);
         if (!this.flushAction(shutDown)) {
            return false;
         } else if (!shutDown) {
            return true;
         } else {
            for(int newState = oldState | 1048576; !stateUpdater.compareAndSet(this, oldState, newState); newState = oldState | 1048576) {
               oldState = stateUpdater.get(this);
               if (Bits.allAreSet(oldState, 1048576)) {
                  return this.channel.flush();
               }
            }

            boolean readShutDown = Bits.allAreSet(oldState, 8);

            try {
               this.shutdownWritesComplete(readShutDown);
            } finally {
               if (readShutDown) {
                  ChannelListeners.invokeChannelListener(this.thisTyped(), this.closeListener);
               }

            }

            return this.channel.flush();
         }
      }
   }

   protected boolean flushAction(boolean shutDown) throws IOException {
      return this.channel.flush();
   }

   protected void shutdownWritesComplete(boolean readShutDown) throws IOException {
   }

   public void shutdownReads() throws IOException {
      int old = this.setFlags(8);
      if (Bits.allAreClear(old, 8)) {
         boolean writeComplete = Bits.allAreSet(old, 1048576);

         try {
            this.shutdownReadsAction(writeComplete);
         } finally {
            if (writeComplete) {
               ChannelListeners.invokeChannelListener(this.thisTyped(), this.closeListener);
            }

         }
      }

   }

   protected void shutdownReadsAction(boolean writeComplete) throws IOException {
      this.channel.shutdownReads();
   }

   protected boolean isReadShutDown() {
      return Bits.allAreSet(this.state, 8);
   }

   public void shutdownWrites() throws IOException {
      int old = this.setFlags(524288);
      if (Bits.allAreClear(old, 524288)) {
         this.shutdownWritesAction();
      }

   }

   protected void shutdownWritesAction() throws IOException {
      this.channel.shutdownWrites();
   }

   protected boolean isWriteShutDown() {
      return Bits.allAreSet(this.state, 524288);
   }

   protected boolean isWriteComplete() {
      return Bits.allAreSet(this.state, 1048576);
   }

   public void awaitReadable() throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 12)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)readWaiterUpdater.getAndSet(this, thread);

         try {
            if (!Bits.anyAreSet(oldState = this.state, 12)) {
               if (Bits.allAreSet(oldState, 2)) {
                  this.channel.resumeWrites();
               } else {
                  this.channel.resumeReads();
               }

               LockSupport.park(this);
               if (!thread.isInterrupted()) {
                  return;
               }

               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 12)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)readWaiterUpdater.getAndSet(this, thread);
         long duration = timeUnit.toNanos(time);

         try {
            if (!Bits.anyAreSet(oldState = this.state, 12)) {
               if (Bits.allAreSet(oldState, 2)) {
                  this.channel.resumeWrites();
               } else {
                  this.channel.resumeReads();
               }

               LockSupport.parkNanos(this, duration);
               if (!thread.isInterrupted()) {
                  return;
               }

               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getReadThread() {
      return this.channel.getReadThread();
   }

   public void awaitWritable() throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 786432)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)writeWaiterUpdater.getAndSet(this, thread);

         try {
            if (!Bits.anyAreSet(oldState = this.state, 786432)) {
               if (Bits.allAreSet(oldState, 131072)) {
                  this.channel.resumeReads();
               } else {
                  this.channel.resumeWrites();
               }

               LockSupport.park(this);
               if (!thread.isInterrupted()) {
                  return;
               }

               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      int oldState = this.state;
      if (!Bits.anyAreSet(oldState, 786432)) {
         Thread thread = Thread.currentThread();
         Thread next = (Thread)writeWaiterUpdater.getAndSet(this, thread);
         long duration = timeUnit.toNanos(time);

         try {
            if (Bits.anyAreSet(oldState = this.state, 786432)) {
               return;
            }

            if (Bits.allAreSet(oldState, 131072)) {
               this.channel.resumeReads();
            } else {
               this.channel.resumeWrites();
            }

            LockSupport.parkNanos(this, duration);
            if (thread.isInterrupted()) {
               throw Messages.msg.interruptedIO();
            }
         } finally {
            if (next != null) {
               LockSupport.unpark(next);
            }

         }

      }
   }

   private void unparkReadWaiters() {
      Thread waiter = (Thread)readWaiterUpdater.getAndSet(this, (Object)null);
      if (waiter != null) {
         LockSupport.unpark(waiter);
      }

   }

   private void unparkWriteWaiters() {
      Thread waiter = (Thread)writeWaiterUpdater.getAndSet(this, (Object)null);
      if (waiter != null) {
         LockSupport.unpark(waiter);
      }

   }

   /** @deprecated */
   @Deprecated
   public XnioExecutor getWriteThread() {
      return this.channel.getWriteThread();
   }

   public void close() throws IOException {
      int old = this.setFlags(1572872);
      boolean readShutDown = Bits.allAreSet(old, 8);
      boolean writeShutDown = Bits.allAreSet(old, 1048576);
      if (!readShutDown || !writeShutDown) {
         try {
            this.closeAction(readShutDown, writeShutDown);
         } finally {
            ChannelListeners.invokeChannelListener(this.thisTyped(), this.closeListener);
         }
      }

   }

   protected void closeAction(boolean readShutDown, boolean writeShutDown) throws IOException {
      this.channel.close();
   }

   public boolean isOpen() {
      return !Bits.allAreSet(this.state, 1048584);
   }

   public W getChannel() {
      return this.channel;
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.channel.getIoThread();
   }

   public String toString() {
      return this.getClass().getName() + " around " + this.channel;
   }

   private int setFlags(int flags) {
      int oldState;
      do {
         oldState = this.state;
         if ((oldState & flags) == flags) {
            return oldState;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, oldState | flags));

      return oldState;
   }

   private int clearFlags(int flags) {
      int oldState;
      do {
         oldState = this.state;
         if ((oldState & flags) == 0) {
            return oldState;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, oldState & ~flags));

      return oldState;
   }

   private int addFlag(int mask, int count) {
      int oldState;
      do {
         oldState = this.state;
         if ((oldState & mask) == mask) {
            return oldState;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, oldState + count));

      return oldState;
   }

   private int clearFlag(int count) {
      return stateUpdater.getAndAdd(this, -count);
   }
}
