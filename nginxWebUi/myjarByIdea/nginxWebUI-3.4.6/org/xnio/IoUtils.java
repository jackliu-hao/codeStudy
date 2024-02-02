package org.xnio;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.WritableByteChannel;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.zip.ZipFile;
import org.xnio._private.Messages;
import org.xnio.channels.SuspendableReadChannel;

public final class IoUtils {
   private static final Executor NULL_EXECUTOR = new Executor() {
      private final String string = String.format("null executor <%s>", Integer.toHexString(this.hashCode()));

      public void execute(Runnable command) {
      }

      public String toString() {
         return this.string;
      }
   };
   private static final Executor DIRECT_EXECUTOR = new Executor() {
      private final String string = String.format("direct executor <%s>", Integer.toHexString(this.hashCode()));

      public void execute(Runnable command) {
         command.run();
      }

      public String toString() {
         return this.string;
      }
   };
   private static final Closeable NULL_CLOSEABLE = new Closeable() {
      private final String string = String.format("null closeable <%s>", Integer.toHexString(this.hashCode()));

      public void close() throws IOException {
      }

      public String toString() {
         return this.string;
      }
   };
   private static final Cancellable NULL_CANCELLABLE = new Cancellable() {
      public Cancellable cancel() {
         return this;
      }
   };
   private static final ResultNotifier RESULT_NOTIFIER = new ResultNotifier();
   private static final IoFuture.Notifier<Object, Closeable> ATTACHMENT_CLOSING_NOTIFIER = new IoFuture.Notifier<Object, Closeable>() {
      public void notify(IoFuture<?> future, Closeable attachment) {
         IoUtils.safeClose(attachment);
      }
   };
   private static final IoFuture.Notifier<Closeable, Void> CLOSING_NOTIFIER = new IoFuture.HandlingNotifier<Closeable, Void>() {
      public void handleDone(Closeable result, Void attachment) {
         IoUtils.safeClose(result);
      }
   };
   private static final IoFuture.Notifier CHANNEL_LISTENER_NOTIFIER = new IoFuture.HandlingNotifier<Channel, ChannelListener<? super Channel>>() {
      public void handleDone(Channel channel, ChannelListener channelListener) {
         channelListener.handleEvent(channel);
      }
   };
   private static final IoFuture.Notifier<Object, CountDownLatch> COUNT_DOWN_NOTIFIER = new IoFuture.Notifier<Object, CountDownLatch>() {
      public void notify(IoFuture<?> future, CountDownLatch latch) {
         latch.countDown();
      }
   };
   private static final ManagerNotifier MANAGER_NOTIFIER = new ManagerNotifier();

   private IoUtils() {
   }

   public static Executor directExecutor() {
      return DIRECT_EXECUTOR;
   }

   public static Executor nullExecutor() {
      return NULL_EXECUTOR;
   }

   public static Closeable nullCloseable() {
      return NULL_CLOSEABLE;
   }

   public static void safeClose(AutoCloseable resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (ClosedChannelException var2) {
      } catch (Throwable var3) {
         Messages.closeMsg.resourceCloseFailed(var3, resource);
      }

   }

   public static void safeClose(Closeable resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (ClosedChannelException var2) {
         Messages.msg.tracef("safeClose, ignoring ClosedChannelException exception", new Object[0]);
      } catch (Throwable var3) {
         Messages.closeMsg.resourceCloseFailed(var3, resource);
      }

   }

   public static void safeClose(Closeable... resources) {
      Closeable[] var1 = resources;
      int var2 = resources.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Closeable resource = var1[var3];
         safeClose(resource);
      }

   }

   public static void safeClose(Socket resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (ClosedChannelException var2) {
      } catch (Throwable var3) {
         Messages.closeMsg.resourceCloseFailed(var3, resource);
      }

   }

   public static void safeClose(DatagramSocket resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (Throwable var2) {
         Messages.closeMsg.resourceCloseFailed(var2, resource);
      }

   }

   public static void safeClose(Selector resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (ClosedChannelException var2) {
      } catch (Throwable var3) {
         Messages.closeMsg.resourceCloseFailed(var3, resource);
      }

   }

   public static void safeClose(ServerSocket resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (ClosedChannelException var2) {
      } catch (Throwable var3) {
         Messages.closeMsg.resourceCloseFailed(var3, resource);
      }

   }

   public static void safeClose(ZipFile resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (Throwable var2) {
         Messages.closeMsg.resourceCloseFailed(var2, resource);
      }

   }

   public static void safeClose(Handler resource) {
      try {
         if (resource != null) {
            Messages.closeMsg.closingResource(resource);
            resource.close();
         }
      } catch (Throwable var2) {
         Messages.closeMsg.resourceCloseFailed(var2, resource);
      }

   }

   public static void safeClose(IoFuture<? extends Closeable> futureResource) {
      if (futureResource != null) {
         futureResource.cancel().addNotifier(closingNotifier(), (Object)null);
      }

   }

   public static IoFuture.Notifier<Object, Closeable> attachmentClosingNotifier() {
      return ATTACHMENT_CLOSING_NOTIFIER;
   }

   public static IoFuture.Notifier<Closeable, Void> closingNotifier() {
      return CLOSING_NOTIFIER;
   }

   public static <T> IoFuture.Notifier<T, Void> runnableNotifier(final Runnable runnable) {
      return new IoFuture.Notifier<T, Void>() {
         public void notify(IoFuture<? extends T> future, Void attachment) {
            runnable.run();
         }
      };
   }

   public static <T> IoFuture.Notifier<T, Result<T>> resultNotifier() {
      return RESULT_NOTIFIER;
   }

   public static <T extends Channel> IoFuture.Notifier<T, ChannelListener<? super T>> channelListenerNotifier() {
      return CHANNEL_LISTENER_NOTIFIER;
   }

   public static <T> Future<T> getFuture(final IoFuture<T> ioFuture) {
      return new Future<T>() {
         public boolean cancel(boolean mayInterruptIfRunning) {
            ioFuture.cancel();
            return ioFuture.await() == IoFuture.Status.CANCELLED;
         }

         public boolean isCancelled() {
            return ioFuture.getStatus() == IoFuture.Status.CANCELLED;
         }

         public boolean isDone() {
            return ioFuture.getStatus() == IoFuture.Status.DONE;
         }

         public T get() throws InterruptedException, ExecutionException {
            try {
               return ioFuture.getInterruptibly();
            } catch (IOException var2) {
               throw new ExecutionException(var2);
            }
         }

         public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            try {
               if (ioFuture.awaitInterruptibly(timeout, unit) == IoFuture.Status.WAITING) {
                  throw Messages.msg.opTimedOut();
               } else {
                  return ioFuture.getInterruptibly();
               }
            } catch (IOException var5) {
               throw new ExecutionException(var5);
            }
         }

         public String toString() {
            return String.format("java.util.concurrent.Future wrapper <%s> for %s", Integer.toHexString(this.hashCode()), ioFuture);
         }
      };
   }

   public static void awaitAll(IoFuture<?>... futures) {
      int len = futures.length;
      CountDownLatch cdl = new CountDownLatch(len);
      IoFuture[] var3 = futures;
      int var4 = futures.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         IoFuture<?> future = var3[var5];
         future.addNotifier(COUNT_DOWN_NOTIFIER, cdl);
      }

      boolean intr = false;

      try {
         while(cdl.getCount() > 0L) {
            try {
               cdl.await();
            } catch (InterruptedException var10) {
               intr = true;
            }
         }
      } finally {
         if (intr) {
            Thread.currentThread().interrupt();
         }

      }

   }

   public static void awaitAllInterruptibly(IoFuture<?>... futures) throws InterruptedException {
      int len = futures.length;
      CountDownLatch cdl = new CountDownLatch(len);
      IoFuture[] var3 = futures;
      int var4 = futures.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         IoFuture<?> future = var3[var5];
         future.addNotifier(COUNT_DOWN_NOTIFIER, cdl);
      }

      cdl.await();
   }

   public static <I, O> IoFuture<? extends O> cast(IoFuture<I> parent, Class<O> type) {
      return new CastingIoFuture(parent, type);
   }

   public static void safeShutdownReads(SuspendableReadChannel channel) {
      if (channel != null) {
         try {
            channel.shutdownReads();
         } catch (IOException var2) {
            Messages.closeMsg.resourceReadShutdownFailed((Throwable)null, (Object)null);
         }
      }

   }

   public static long transfer(ReadableByteChannel source, long count, ByteBuffer throughBuffer, WritableByteChannel sink) throws IOException {
      long total = 0L;
      throughBuffer.limit(0);

      while(total < count) {
         throughBuffer.compact();

         long res;
         label78: {
            long var9;
            try {
               if (count - total < (long)throughBuffer.remaining()) {
                  throughBuffer.limit((int)(count - total));
               }

               res = (long)source.read(throughBuffer);
               if (res > 0L) {
                  break label78;
               }

               var9 = total == 0L ? res : total;
            } finally {
               throughBuffer.flip();
            }

            return var9;
         }

         res = (long)sink.write(throughBuffer);
         if (res == 0L) {
            return total;
         }

         total += res;
      }

      return total;
   }

   public static <T> IoFuture.Notifier<T, FutureResult<T>> getManagerNotifier() {
      return MANAGER_NOTIFIER;
   }

   public static <T extends Channel> ChannelSource<T> getRetryingChannelSource(ChannelSource<T> delegate, int maxTries) throws IllegalArgumentException {
      if (maxTries < 1) {
         throw Messages.msg.minRange("maxTries", 1);
      } else {
         return new RetryingChannelSource(maxTries, delegate);
      }
   }

   public static Cancellable closingCancellable(Closeable c) {
      return new ClosingCancellable(c);
   }

   public static Cancellable nullCancellable() {
      return NULL_CANCELLABLE;
   }

   public static Random getThreadLocalRandom() {
      return ThreadLocalRandom.current();
   }

   private static class ResultNotifier<T> extends IoFuture.HandlingNotifier<T, Result<T>> {
      private ResultNotifier() {
      }

      public void handleCancelled(Result<T> result) {
         result.setCancelled();
      }

      public void handleFailed(IOException exception, Result<T> result) {
         result.setException(exception);
      }

      public void handleDone(T value, Result<T> result) {
         result.setResult(value);
      }

      // $FF: synthetic method
      ResultNotifier(Object x0) {
         this();
      }
   }

   private static class ClosingCancellable implements Cancellable {
      private final Closeable c;

      ClosingCancellable(Closeable c) {
         this.c = c;
      }

      public Cancellable cancel() {
         IoUtils.safeClose(this.c);
         return this;
      }
   }

   private static class RetryingChannelSource<T extends Channel> implements ChannelSource<T> {
      private final int maxTries;
      private final ChannelSource<T> delegate;

      RetryingChannelSource(int maxTries, ChannelSource<T> delegate) {
         this.maxTries = maxTries;
         this.delegate = delegate;
      }

      public IoFuture<T> open(ChannelListener<? super T> openListener) {
         FutureResult<T> result = new FutureResult();
         RetryingNotifier<T> notifier = new RetryingNotifier(this.maxTries, result, this.delegate, openListener);
         notifier.tryOne(result);
         return result.getIoFuture();
      }
   }

   private static class RetryingNotifier<T extends Channel> extends IoFuture.HandlingNotifier<T, Result<T>> {
      private volatile int remaining;
      private final int maxTries;
      private final Result<T> result;
      private final ChannelSource<T> delegate;
      private final ChannelListener<? super T> openListener;

      RetryingNotifier(int maxTries, Result<T> result, ChannelSource<T> delegate, ChannelListener<? super T> openListener) {
         this.maxTries = maxTries;
         this.result = result;
         this.delegate = delegate;
         this.openListener = openListener;
         this.remaining = maxTries;
      }

      public void handleFailed(IOException exception, Result<T> attachment) {
         if (this.remaining-- == 0) {
            this.result.setException(new IOException("Failed to create channel after " + this.maxTries + " tries", exception));
         } else {
            this.tryOne(attachment);
         }
      }

      public void handleCancelled(Result<T> attachment) {
         this.result.setCancelled();
      }

      public void handleDone(T data, Result<T> attachment) {
         this.result.setResult(data);
      }

      void tryOne(Result<T> attachment) {
         IoFuture<? extends T> ioFuture = this.delegate.open(this.openListener);
         ioFuture.addNotifier(this, attachment);
      }
   }

   private static class ManagerNotifier<T extends Channel> extends IoFuture.HandlingNotifier<T, FutureResult<T>> {
      private ManagerNotifier() {
      }

      public void handleCancelled(FutureResult<T> manager) {
         manager.setCancelled();
      }

      public void handleFailed(IOException exception, FutureResult<T> manager) {
         manager.setException(exception);
      }

      public void handleDone(T result, FutureResult<T> manager) {
         manager.setResult(result);
      }

      // $FF: synthetic method
      ManagerNotifier(Object x0) {
         this();
      }
   }

   private static class CastingIoFuture<O, I> implements IoFuture<O> {
      private final IoFuture<I> parent;
      private final Class<O> type;

      private CastingIoFuture(IoFuture<I> parent, Class<O> type) {
         this.parent = parent;
         this.type = type;
      }

      public IoFuture<O> cancel() {
         this.parent.cancel();
         return this;
      }

      public IoFuture.Status getStatus() {
         return this.parent.getStatus();
      }

      public IoFuture.Status await() {
         return this.parent.await();
      }

      public IoFuture.Status await(long time, TimeUnit timeUnit) {
         return this.parent.await(time, timeUnit);
      }

      public IoFuture.Status awaitInterruptibly() throws InterruptedException {
         return this.parent.awaitInterruptibly();
      }

      public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
         return this.parent.awaitInterruptibly(time, timeUnit);
      }

      public O get() throws IOException, CancellationException {
         return this.type.cast(this.parent.get());
      }

      public O getInterruptibly() throws IOException, InterruptedException, CancellationException {
         return this.type.cast(this.parent.getInterruptibly());
      }

      public IOException getException() throws IllegalStateException {
         return this.parent.getException();
      }

      public <A> IoFuture<O> addNotifier(final IoFuture.Notifier<? super O, A> notifier, A attachment) {
         this.parent.addNotifier(new IoFuture.Notifier<I, A>() {
            public void notify(IoFuture<? extends I> future, A attachment) {
               notifier.notify(CastingIoFuture.this, attachment);
            }
         }, attachment);
         return this;
      }

      // $FF: synthetic method
      CastingIoFuture(IoFuture x0, Class x1, Object x2) {
         this(x0, x1);
      }
   }
}
