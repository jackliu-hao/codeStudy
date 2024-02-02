/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.logging.Handler;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.SuspendableReadChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class IoUtils
/*     */ {
/*  56 */   private static final Executor NULL_EXECUTOR = new Executor() {
/*  57 */       private final String string = String.format("null executor <%s>", new Object[] { Integer.toHexString(hashCode()) });
/*     */ 
/*     */       
/*     */       public void execute(Runnable command) {}
/*     */ 
/*     */       
/*     */       public String toString() {
/*  64 */         return this.string;
/*     */       }
/*     */     };
/*  67 */   private static final Executor DIRECT_EXECUTOR = new Executor() {
/*  68 */       private final String string = String.format("direct executor <%s>", new Object[] { Integer.toHexString(hashCode()) });
/*     */       
/*     */       public void execute(Runnable command) {
/*  71 */         command.run();
/*     */       }
/*     */       
/*     */       public String toString() {
/*  75 */         return this.string;
/*     */       }
/*     */     };
/*  78 */   private static final Closeable NULL_CLOSEABLE = new Closeable() {
/*  79 */       private final String string = String.format("null closeable <%s>", new Object[] { Integer.toHexString(hashCode()) });
/*     */ 
/*     */       
/*     */       public void close() throws IOException {}
/*     */       
/*     */       public String toString() {
/*  85 */         return this.string;
/*     */       }
/*     */     };
/*  88 */   private static final Cancellable NULL_CANCELLABLE = new Cancellable() {
/*     */       public Cancellable cancel() {
/*  90 */         return this;
/*     */       }
/*     */     };
/*     */   
/*  94 */   private static final ResultNotifier RESULT_NOTIFIER = new ResultNotifier();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Executor directExecutor() {
/* 104 */     return DIRECT_EXECUTOR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Executor nullExecutor() {
/* 113 */     return NULL_EXECUTOR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Closeable nullCloseable() {
/* 123 */     return NULL_CLOSEABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(AutoCloseable resource) {
/*     */     
/* 133 */     try { if (resource != null) {
/* 134 */         Messages.closeMsg.closingResource(resource);
/* 135 */         resource.close();
/*     */       }  }
/* 137 */     catch (ClosedChannelException closedChannelException) {  }
/* 138 */     catch (Throwable t)
/* 139 */     { Messages.closeMsg.resourceCloseFailed(t, resource); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(Closeable resource) {
/*     */     try {
/* 150 */       if (resource != null) {
/* 151 */         Messages.closeMsg.closingResource(resource);
/* 152 */         resource.close();
/*     */       } 
/* 154 */     } catch (ClosedChannelException ignored) {
/* 155 */       Messages.msg.tracef("safeClose, ignoring ClosedChannelException exception", new Object[0]);
/* 156 */     } catch (Throwable t) {
/* 157 */       Messages.closeMsg.resourceCloseFailed(t, resource);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(Closeable... resources) {
/* 167 */     for (Closeable resource : resources) {
/* 168 */       safeClose(resource);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(Socket resource) {
/*     */     
/* 179 */     try { if (resource != null) {
/* 180 */         Messages.closeMsg.closingResource(resource);
/* 181 */         resource.close();
/*     */       }  }
/* 183 */     catch (ClosedChannelException closedChannelException) {  }
/* 184 */     catch (Throwable t)
/* 185 */     { Messages.closeMsg.resourceCloseFailed(t, resource); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(DatagramSocket resource) {
/*     */     try {
/* 196 */       if (resource != null) {
/* 197 */         Messages.closeMsg.closingResource(resource);
/* 198 */         resource.close();
/*     */       } 
/* 200 */     } catch (Throwable t) {
/* 201 */       Messages.closeMsg.resourceCloseFailed(t, resource);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(Selector resource) {
/*     */     
/* 212 */     try { if (resource != null) {
/* 213 */         Messages.closeMsg.closingResource(resource);
/* 214 */         resource.close();
/*     */       }  }
/* 216 */     catch (ClosedChannelException closedChannelException) {  }
/* 217 */     catch (Throwable t)
/* 218 */     { Messages.closeMsg.resourceCloseFailed(t, resource); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(ServerSocket resource) {
/*     */     
/* 229 */     try { if (resource != null) {
/* 230 */         Messages.closeMsg.closingResource(resource);
/* 231 */         resource.close();
/*     */       }  }
/* 233 */     catch (ClosedChannelException closedChannelException) {  }
/* 234 */     catch (Throwable t)
/* 235 */     { Messages.closeMsg.resourceCloseFailed(t, resource); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(ZipFile resource) {
/*     */     try {
/* 246 */       if (resource != null) {
/* 247 */         Messages.closeMsg.closingResource(resource);
/* 248 */         resource.close();
/*     */       } 
/* 250 */     } catch (Throwable t) {
/* 251 */       Messages.closeMsg.resourceCloseFailed(t, resource);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(Handler resource) {
/*     */     try {
/* 262 */       if (resource != null) {
/* 263 */         Messages.closeMsg.closingResource(resource);
/* 264 */         resource.close();
/*     */       } 
/* 266 */     } catch (Throwable t) {
/* 267 */       Messages.closeMsg.resourceCloseFailed(t, resource);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeClose(IoFuture<? extends Closeable> futureResource) {
/* 278 */     if (futureResource != null) {
/* 279 */       futureResource.cancel().addNotifier(closingNotifier(), null);
/*     */     }
/*     */   }
/*     */   
/* 283 */   private static final IoFuture.Notifier<Object, Closeable> ATTACHMENT_CLOSING_NOTIFIER = new IoFuture.Notifier<Object, Closeable>() {
/*     */       public void notify(IoFuture<?> future, Closeable attachment) {
/* 285 */         IoUtils.safeClose(attachment);
/*     */       }
/*     */     };
/*     */   
/* 289 */   private static final IoFuture.Notifier<Closeable, Void> CLOSING_NOTIFIER = new IoFuture.HandlingNotifier<Closeable, Void>() {
/*     */       public void handleDone(Closeable result, Void attachment) {
/* 291 */         IoUtils.safeClose(result);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IoFuture.Notifier<Object, Closeable> attachmentClosingNotifier() {
/* 301 */     return ATTACHMENT_CLOSING_NOTIFIER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IoFuture.Notifier<Closeable, Void> closingNotifier() {
/* 310 */     return CLOSING_NOTIFIER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> IoFuture.Notifier<T, Void> runnableNotifier(final Runnable runnable) {
/* 321 */     return new IoFuture.Notifier<T, Void>() {
/*     */         public void notify(IoFuture<? extends T> future, Void attachment) {
/* 323 */           runnable.run();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> IoFuture.Notifier<T, Result<T>> resultNotifier() {
/* 337 */     return RESULT_NOTIFIER;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Channel> IoFuture.Notifier<T, ChannelListener<? super T>> channelListenerNotifier() {
/* 348 */     return CHANNEL_LISTENER_NOTIFIER;
/*     */   }
/*     */ 
/*     */   
/* 352 */   private static final IoFuture.Notifier CHANNEL_LISTENER_NOTIFIER = new IoFuture.HandlingNotifier<Channel, ChannelListener<? super Channel>>()
/*     */     {
/*     */       public void handleDone(Channel channel, ChannelListener<Channel> channelListener) {
/* 355 */         channelListener.handleEvent(channel);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Future<T> getFuture(final IoFuture<T> ioFuture) {
/* 366 */     return new Future<T>() {
/*     */         public boolean cancel(boolean mayInterruptIfRunning) {
/* 368 */           ioFuture.cancel();
/* 369 */           return (ioFuture.await() == IoFuture.Status.CANCELLED);
/*     */         }
/*     */         
/*     */         public boolean isCancelled() {
/* 373 */           return (ioFuture.getStatus() == IoFuture.Status.CANCELLED);
/*     */         }
/*     */         
/*     */         public boolean isDone() {
/* 377 */           return (ioFuture.getStatus() == IoFuture.Status.DONE);
/*     */         }
/*     */         
/*     */         public T get() throws InterruptedException, ExecutionException {
/*     */           try {
/* 382 */             return ioFuture.getInterruptibly();
/* 383 */           } catch (IOException e) {
/* 384 */             throw new ExecutionException(e);
/*     */           } 
/*     */         }
/*     */         
/*     */         public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
/*     */           try {
/* 390 */             if (ioFuture.awaitInterruptibly(timeout, unit) == IoFuture.Status.WAITING) {
/* 391 */               throw Messages.msg.opTimedOut();
/*     */             }
/* 393 */             return ioFuture.getInterruptibly();
/* 394 */           } catch (IOException e) {
/* 395 */             throw new ExecutionException(e);
/*     */           } 
/*     */         }
/*     */         
/*     */         public String toString() {
/* 400 */           return String.format("java.util.concurrent.Future wrapper <%s> for %s", new Object[] { Integer.toHexString(hashCode()), this.val$ioFuture });
/*     */         }
/*     */       };
/*     */   }
/*     */   
/* 405 */   private static final IoFuture.Notifier<Object, CountDownLatch> COUNT_DOWN_NOTIFIER = new IoFuture.Notifier<Object, CountDownLatch>() {
/*     */       public void notify(IoFuture<?> future, CountDownLatch latch) {
/* 407 */         latch.countDown();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void awaitAll(IoFuture<?>... futures) {
/* 417 */     int len = futures.length;
/* 418 */     CountDownLatch cdl = new CountDownLatch(len);
/* 419 */     for (IoFuture<?> future : futures) {
/* 420 */       future.addNotifier(COUNT_DOWN_NOTIFIER, cdl);
/*     */     }
/* 422 */     boolean intr = false;
/*     */     try {
/* 424 */       while (cdl.getCount() > 0L) {
/*     */         try {
/* 426 */           cdl.await();
/* 427 */         } catch (InterruptedException e) {
/* 428 */           intr = true;
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 432 */       if (intr) {
/* 433 */         Thread.currentThread().interrupt();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void awaitAllInterruptibly(IoFuture<?>... futures) throws InterruptedException {
/* 445 */     int len = futures.length;
/* 446 */     CountDownLatch cdl = new CountDownLatch(len);
/* 447 */     for (IoFuture<?> future : futures) {
/* 448 */       future.addNotifier(COUNT_DOWN_NOTIFIER, cdl);
/*     */     }
/* 450 */     cdl.await();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <I, O> IoFuture<? extends O> cast(IoFuture<I> parent, Class<O> type) {
/* 463 */     return new CastingIoFuture<>(parent, type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void safeShutdownReads(SuspendableReadChannel channel) {
/* 472 */     if (channel != null) {
/*     */       try {
/* 474 */         channel.shutdownReads();
/* 475 */       } catch (IOException e) {
/* 476 */         Messages.closeMsg.resourceReadShutdownFailed(null, null);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long transfer(ReadableByteChannel source, long count, ByteBuffer throughBuffer, WritableByteChannel sink) throws IOException {
/* 498 */     long total = 0L;
/* 499 */     throughBuffer.limit(0);
/* 500 */     while (total < count) {
/* 501 */       throughBuffer.compact();
/*     */       try {
/* 503 */         if (count - total < throughBuffer.remaining()) {
/* 504 */           throughBuffer.limit((int)(count - total));
/*     */         }
/* 506 */         long l = source.read(throughBuffer);
/* 507 */         if (l <= 0L) {
/* 508 */           return (total == 0L) ? l : total;
/*     */         }
/*     */       } finally {
/* 511 */         throughBuffer.flip();
/*     */       } 
/* 513 */       long res = sink.write(throughBuffer);
/* 514 */       if (res == 0L) {
/* 515 */         return total;
/*     */       }
/* 517 */       total += res;
/*     */     } 
/* 519 */     return total;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class CastingIoFuture<O, I>
/*     */     implements IoFuture<O>
/*     */   {
/*     */     private final IoFuture<I> parent;
/*     */     private final Class<O> type;
/*     */     
/*     */     private CastingIoFuture(IoFuture<I> parent, Class<O> type) {
/* 530 */       this.parent = parent;
/* 531 */       this.type = type;
/*     */     }
/*     */     
/*     */     public IoFuture<O> cancel() {
/* 535 */       this.parent.cancel();
/* 536 */       return this;
/*     */     }
/*     */     
/*     */     public IoFuture.Status getStatus() {
/* 540 */       return this.parent.getStatus();
/*     */     }
/*     */     
/*     */     public IoFuture.Status await() {
/* 544 */       return this.parent.await();
/*     */     }
/*     */     
/*     */     public IoFuture.Status await(long time, TimeUnit timeUnit) {
/* 548 */       return this.parent.await(time, timeUnit);
/*     */     }
/*     */     
/*     */     public IoFuture.Status awaitInterruptibly() throws InterruptedException {
/* 552 */       return this.parent.awaitInterruptibly();
/*     */     }
/*     */     
/*     */     public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
/* 556 */       return this.parent.awaitInterruptibly(time, timeUnit);
/*     */     }
/*     */     
/*     */     public O get() throws IOException, CancellationException {
/* 560 */       return this.type.cast(this.parent.get());
/*     */     }
/*     */     
/*     */     public O getInterruptibly() throws IOException, InterruptedException, CancellationException {
/* 564 */       return this.type.cast(this.parent.getInterruptibly());
/*     */     }
/*     */     
/*     */     public IOException getException() throws IllegalStateException {
/* 568 */       return this.parent.getException();
/*     */     }
/*     */     
/*     */     public <A> IoFuture<O> addNotifier(final IoFuture.Notifier<? super O, A> notifier, A attachment) {
/* 572 */       this.parent.addNotifier(new IoFuture.Notifier<I, A>() {
/*     */             public void notify(IoFuture<? extends I> future, A attachment) {
/* 574 */               notifier.notify(IoUtils.CastingIoFuture.this, attachment);
/*     */             }
/*     */           }attachment);
/* 577 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> IoFuture.Notifier<T, FutureResult<T>> getManagerNotifier() {
/* 589 */     return MANAGER_NOTIFIER;
/*     */   }
/*     */ 
/*     */   
/* 593 */   private static final ManagerNotifier MANAGER_NOTIFIER = new ManagerNotifier<>();
/*     */   
/*     */   private static class ManagerNotifier<T extends Channel> extends IoFuture.HandlingNotifier<T, FutureResult<T>> {
/*     */     public void handleCancelled(FutureResult<T> manager) {
/* 597 */       manager.setCancelled();
/*     */     }
/*     */     private ManagerNotifier() {}
/*     */     public void handleFailed(IOException exception, FutureResult<T> manager) {
/* 601 */       manager.setException(exception);
/*     */     }
/*     */     
/*     */     public void handleDone(T result, FutureResult<T> manager) {
/* 605 */       manager.setResult(result);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends Channel> ChannelSource<T> getRetryingChannelSource(ChannelSource<T> delegate, int maxTries) throws IllegalArgumentException {
/* 619 */     if (maxTries < 1) {
/* 620 */       throw Messages.msg.minRange("maxTries", 1);
/*     */     }
/* 622 */     return new RetryingChannelSource<>(maxTries, delegate);
/*     */   }
/*     */   
/*     */   private static class RetryingNotifier<T extends Channel>
/*     */     extends IoFuture.HandlingNotifier<T, Result<T>> {
/*     */     private volatile int remaining;
/*     */     private final int maxTries;
/*     */     private final Result<T> result;
/*     */     private final ChannelSource<T> delegate;
/*     */     private final ChannelListener<? super T> openListener;
/*     */     
/*     */     RetryingNotifier(int maxTries, Result<T> result, ChannelSource<T> delegate, ChannelListener<? super T> openListener) {
/* 634 */       this.maxTries = maxTries;
/* 635 */       this.result = result;
/* 636 */       this.delegate = delegate;
/* 637 */       this.openListener = openListener;
/* 638 */       this.remaining = maxTries;
/*     */     }
/*     */     
/*     */     public void handleFailed(IOException exception, Result<T> attachment) {
/* 642 */       if (this.remaining-- == 0) {
/* 643 */         this.result.setException(new IOException("Failed to create channel after " + this.maxTries + " tries", exception));
/*     */         return;
/*     */       } 
/* 646 */       tryOne(attachment);
/*     */     }
/*     */     
/*     */     public void handleCancelled(Result<T> attachment) {
/* 650 */       this.result.setCancelled();
/*     */     }
/*     */     
/*     */     public void handleDone(T data, Result<T> attachment) {
/* 654 */       this.result.setResult(data);
/*     */     }
/*     */     
/*     */     void tryOne(Result<T> attachment) {
/* 658 */       IoFuture<? extends T> ioFuture = this.delegate.open(this.openListener);
/* 659 */       ioFuture.addNotifier(this, attachment);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class RetryingChannelSource<T extends Channel>
/*     */     implements ChannelSource<T> {
/*     */     private final int maxTries;
/*     */     private final ChannelSource<T> delegate;
/*     */     
/*     */     RetryingChannelSource(int maxTries, ChannelSource<T> delegate) {
/* 669 */       this.maxTries = maxTries;
/* 670 */       this.delegate = delegate;
/*     */     }
/*     */     
/*     */     public IoFuture<T> open(ChannelListener<? super T> openListener) {
/* 674 */       FutureResult<T> result = new FutureResult<>();
/* 675 */       IoUtils.RetryingNotifier<T> notifier = new IoUtils.RetryingNotifier<>(this.maxTries, result, this.delegate, openListener);
/* 676 */       notifier.tryOne(result);
/* 677 */       return result.getIoFuture();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Cancellable closingCancellable(Closeable c) {
/* 688 */     return new ClosingCancellable(c);
/*     */   }
/*     */   
/*     */   private static class ClosingCancellable
/*     */     implements Cancellable {
/*     */     private final Closeable c;
/*     */     
/*     */     ClosingCancellable(Closeable c) {
/* 696 */       this.c = c;
/*     */     }
/*     */     
/*     */     public Cancellable cancel() {
/* 700 */       IoUtils.safeClose(this.c);
/* 701 */       return this;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Cancellable nullCancellable() {
/* 711 */     return NULL_CANCELLABLE;
/*     */   }
/*     */   
/*     */   private static class ResultNotifier<T> extends IoFuture.HandlingNotifier<T, Result<T>> { private ResultNotifier() {}
/*     */     
/*     */     public void handleCancelled(Result<T> result) {
/* 717 */       result.setCancelled();
/*     */     }
/*     */     
/*     */     public void handleFailed(IOException exception, Result<T> result) {
/* 721 */       result.setException(exception);
/*     */     }
/*     */     
/*     */     public void handleDone(T value, Result<T> result) {
/* 725 */       result.setResult(value);
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Random getThreadLocalRandom() {
/* 735 */     return ThreadLocalRandom.current();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\IoUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */