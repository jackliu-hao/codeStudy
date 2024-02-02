package org.xnio;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.xnio._private.Messages;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.Channels;
import org.xnio.channels.ConnectedChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.channels.SuspendableReadChannel;
import org.xnio.channels.SuspendableWriteChannel;
import org.xnio.channels.WritableMessageChannel;

public final class ChannelListeners {
   private static final ChannelListener<Channel> NULL_LISTENER = new ChannelListener<Channel>() {
      public void handleEvent(Channel channel) {
      }

      public String toString() {
         return "Null channel listener";
      }
   };
   private static final ChannelListener.Setter<?> NULL_SETTER = new ChannelListener.Setter<Channel>() {
      public void set(ChannelListener<? super Channel> channelListener) {
      }

      public String toString() {
         return "Null channel listener setter";
      }
   };
   private static ChannelListener<Channel> CLOSING_CHANNEL_LISTENER = new ChannelListener<Channel>() {
      public void handleEvent(Channel channel) {
         IoUtils.safeClose((Closeable)channel);
      }

      public String toString() {
         return "Closing channel listener";
      }
   };
   private static final ChannelExceptionHandler<Channel> CLOSING_HANDLER = new ChannelExceptionHandler<Channel>() {
      public void handleException(Channel channel, IOException exception) {
         IoUtils.safeClose((Closeable)channel);
      }
   };

   private ChannelListeners() {
   }

   public static <T extends Channel> boolean invokeChannelListener(T channel, ChannelListener<? super T> channelListener) {
      if (channelListener != null) {
         try {
            Messages.listenerMsg.tracef("Invoking listener %s on channel %s", channelListener, channel);
            channelListener.handleEvent(channel);
         } catch (Throwable var3) {
            Messages.listenerMsg.listenerException(var3);
            return false;
         }
      }

      return true;
   }

   public static <T extends Channel> void invokeChannelListener(Executor executor, T channel, ChannelListener<? super T> channelListener) {
      try {
         executor.execute(getChannelListenerTask(channel, channelListener));
      } catch (RejectedExecutionException var4) {
         invokeChannelListener(channel, channelListener);
      }

   }

   public static <T extends Channel> void invokeChannelExceptionHandler(T channel, ChannelExceptionHandler<? super T> exceptionHandler, IOException exception) {
      try {
         exceptionHandler.handleException(channel, exception);
      } catch (Throwable var4) {
         Messages.listenerMsg.exceptionHandlerException(var4);
      }

   }

   public static <T extends Channel> Runnable getChannelListenerTask(final T channel, final ChannelListener<? super T> channelListener) {
      return new Runnable() {
         public String toString() {
            return "Channel listener task for " + channel + " -> " + channelListener;
         }

         public void run() {
            ChannelListeners.invokeChannelListener(channel, channelListener);
         }
      };
   }

   public static <T extends Channel> Runnable getChannelListenerTask(final T channel, final ChannelListener.SimpleSetter<T> setter) {
      return new Runnable() {
         public String toString() {
            return "Channel listener task for " + channel + " -> " + setter;
         }

         public void run() {
            ChannelListeners.invokeChannelListener(channel, setter.get());
         }
      };
   }

   public static ChannelListener<Channel> closingChannelListener() {
      return CLOSING_CHANNEL_LISTENER;
   }

   public static ChannelListener<Channel> closingChannelListener(final Closeable resource) {
      return new ChannelListener<Channel>() {
         public void handleEvent(Channel channel) {
            IoUtils.safeClose(resource);
         }

         public String toString() {
            return "Closing channel listener for " + resource;
         }
      };
   }

   public static ChannelListener<Channel> closingChannelListener(final Closeable... resources) {
      return new ChannelListener<Channel>() {
         public void handleEvent(Channel channel) {
            IoUtils.safeClose(resources);
         }

         public String toString() {
            return "Closing channel listener for " + resources.length + " items";
         }
      };
   }

   public static <T extends Channel> ChannelListener<T> closingChannelListener(final ChannelListener<T> delegate, final Closeable resource) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            IoUtils.safeClose(resource);
            delegate.handleEvent(channel);
         }

         public String toString() {
            return "Closing channel listener for " + resource + " -> " + delegate;
         }
      };
   }

   public static <T extends Channel> ChannelListener<T> closingChannelListener(final ChannelListener<T> delegate, final Closeable... resources) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            IoUtils.safeClose(resources);
            delegate.handleEvent(channel);
         }

         public String toString() {
            return "Closing channel listener for " + resources.length + " items -> " + delegate;
         }
      };
   }

   public static ChannelListener<Channel> nullChannelListener() {
      return NULL_LISTENER;
   }

   public static ChannelExceptionHandler<Channel> closingChannelExceptionHandler() {
      return CLOSING_HANDLER;
   }

   public static <C extends ConnectedChannel> ChannelListener<AcceptingChannel<C>> openListenerAdapter(final ChannelListener<? super C> openListener) {
      if (openListener == null) {
         throw Messages.msg.nullParameter("openListener");
      } else {
         return new ChannelListener<AcceptingChannel<C>>() {
            public void handleEvent(AcceptingChannel<C> channel) {
               try {
                  C accepted = channel.accept();
                  if (accepted != null) {
                     ChannelListeners.invokeChannelListener(accepted, openListener);
                  }
               } catch (IOException var3) {
                  Messages.listenerMsg.acceptFailed(channel, var3);
               }

            }

            public String toString() {
               return "Accepting listener for " + openListener;
            }
         };
      }
   }

   /** @deprecated */
   @Deprecated
   public static <T extends Channel, C> ChannelListener.Setter<T> getSetter(final C channel, final AtomicReferenceFieldUpdater<C, ChannelListener> updater) {
      return new ChannelListener.Setter<T>() {
         public void set(ChannelListener<? super T> channelListener) {
            updater.set(channel, channelListener);
         }

         public String toString() {
            return "Atomic reference field updater setter for " + updater;
         }
      };
   }

   public static <T extends Channel> ChannelListener.Setter<T> getSetter(final AtomicReference<ChannelListener<? super T>> atomicReference) {
      return new ChannelListener.Setter<T>() {
         public void set(ChannelListener<? super T> channelListener) {
            atomicReference.set(channelListener);
         }

         public String toString() {
            return "Atomic reference setter (currently=" + atomicReference.get() + ")";
         }
      };
   }

   public static <T extends Channel> ChannelListener.Setter<T> getDelegatingSetter(ChannelListener.Setter<? extends Channel> target, T realChannel) {
      return target == null ? null : new DelegatingSetter(target, realChannel);
   }

   public static <T extends Channel> ChannelListener.Setter<T> nullSetter() {
      return NULL_SETTER;
   }

   public static <T extends Channel> ChannelListener<T> executorChannelListener(final ChannelListener<T> listener, final Executor executor) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            try {
               executor.execute(ChannelListeners.getChannelListenerTask(channel, listener));
            } catch (RejectedExecutionException var3) {
               Messages.listenerMsg.executorSubmitFailed(var3, channel);
               IoUtils.safeClose((Closeable)channel);
            }

         }

         public String toString() {
            return "Executor channel listener -> " + listener;
         }
      };
   }

   public static <T extends SuspendableWriteChannel> ChannelListener<T> flushingChannelListener(final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            boolean result;
            try {
               result = channel.flush();
            } catch (IOException var4) {
               channel.suspendWrites();
               ChannelListeners.invokeChannelExceptionHandler(channel, exceptionHandler, var4);
               return;
            }

            if (result) {
               Channels.setWriteListener(channel, delegate);
               ChannelListeners.invokeChannelListener(channel, delegate);
            } else {
               Channels.setWriteListener(channel, this);
               channel.resumeWrites();
            }

         }

         public String toString() {
            return "Flushing channel listener -> " + delegate;
         }
      };
   }

   public static <T extends SuspendableWriteChannel> ChannelListener<T> writeShutdownChannelListener(final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
      final ChannelListener<T> flushingListener = flushingChannelListener(delegate, exceptionHandler);
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            try {
               channel.shutdownWrites();
            } catch (IOException var3) {
               ChannelListeners.invokeChannelExceptionHandler(channel, exceptionHandler, var3);
               return;
            }

            ChannelListeners.invokeChannelListener(channel, flushingListener);
         }

         public String toString() {
            return "Write shutdown channel listener -> " + delegate;
         }
      };
   }

   public static <T extends StreamSinkChannel> ChannelListener<T> writingChannelListener(final Pooled<ByteBuffer> pooled, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            ByteBuffer buffer = (ByteBuffer)pooled.getResource();
            boolean ok = false;

            do {
               int result;
               label70: {
                  try {
                     result = channel.write(buffer);
                     ok = true;
                     break label70;
                  } catch (IOException var9) {
                     channel.suspendWrites();
                     pooled.free();
                     ChannelListeners.invokeChannelExceptionHandler(channel, exceptionHandler, var9);
                  } finally {
                     if (!ok) {
                        pooled.free();
                     }

                  }

                  return;
               }

               if (result == 0) {
                  Channels.setWriteListener(channel, this);
                  channel.resumeWrites();
                  return;
               }
            } while(buffer.hasRemaining());

            pooled.free();
            ChannelListeners.invokeChannelListener(channel, delegate);
         }

         public String toString() {
            return "Writing channel listener -> " + delegate;
         }
      };
   }

   public static <T extends WritableMessageChannel> ChannelListener<T> sendingChannelListener(final Pooled<ByteBuffer> pooled, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            ByteBuffer buffer = (ByteBuffer)pooled.getResource();
            boolean free = true;

            try {
               if (!(free = channel.send(buffer))) {
                  Channels.setWriteListener(channel, this);
                  channel.resumeWrites();
                  return;
               }
            } catch (IOException var8) {
               channel.suspendWrites();
               pooled.free();
               ChannelListeners.invokeChannelExceptionHandler(channel, exceptionHandler, var8);
               return;
            } finally {
               if (free) {
                  pooled.free();
               }

            }

            ChannelListeners.invokeChannelListener(channel, delegate);
         }

         public String toString() {
            return "Sending channel listener -> " + delegate;
         }
      };
   }

   public static <T extends StreamSinkChannel> ChannelListener<T> fileSendingChannelListener(final FileChannel source, final long position, final long count, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
      return count == 0L ? delegatingChannelListener(delegate) : new ChannelListener<T>() {
         private long p = position;
         private long cnt = count;

         public void handleEvent(T channel) {
            long cnt = this.cnt;
            long p = this.p;

            try {
               do {
                  long result;
                  try {
                     result = channel.transferFrom(source, p, cnt);
                  } catch (IOException var12) {
                     ChannelListeners.invokeChannelExceptionHandler(channel, exceptionHandler, var12);
                     return;
                  }

                  if (result == 0L) {
                     Channels.setWriteListener(channel, this);
                     channel.resumeWrites();
                     return;
                  }

                  p += result;
                  cnt -= result;
               } while(cnt > 0L);

               ChannelListeners.invokeChannelListener(channel, delegate);
            } finally {
               this.p = p;
               this.cnt = cnt;
            }
         }

         public String toString() {
            return "File sending channel listener (" + source + ") -> " + delegate;
         }
      };
   }

   public static <T extends StreamSourceChannel> ChannelListener<T> fileReceivingChannelListener(final FileChannel target, final long position, final long count, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
      return count == 0L ? delegatingChannelListener(delegate) : new ChannelListener<T>() {
         private long p = position;
         private long cnt = count;

         public void handleEvent(T channel) {
            long cnt = this.cnt;
            long p = this.p;

            try {
               do {
                  long result;
                  try {
                     result = channel.transferTo(p, cnt, target);
                  } catch (IOException var12) {
                     ChannelListeners.invokeChannelExceptionHandler(channel, exceptionHandler, var12);
                     return;
                  }

                  if (result == 0L) {
                     Channels.setReadListener(channel, this);
                     channel.resumeReads();
                     return;
                  }

                  p += result;
                  cnt -= result;
               } while(cnt > 0L);

               ChannelListeners.invokeChannelListener(channel, delegate);
            } finally {
               this.p = p;
               this.cnt = cnt;
            }
         }

         public String toString() {
            return "File receiving channel listener (" + target + ") -> " + delegate;
         }
      };
   }

   public static <T extends Channel> ChannelListener<T> delegatingChannelListener(final ChannelListener<? super T> delegate) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            ChannelListeners.invokeChannelListener(channel, delegate);
         }

         public String toString() {
            return "Delegating channel listener -> " + delegate;
         }
      };
   }

   public static <C extends Channel, T extends Channel> ChannelListener<C> delegatingChannelListener(T channel, ChannelListener.SimpleSetter<T> setter) {
      return new SetterDelegatingListener(setter, channel);
   }

   public static <T extends SuspendableWriteChannel> ChannelListener<T> writeSuspendingChannelListener(final ChannelListener<? super T> delegate) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            channel.suspendWrites();
            ChannelListeners.invokeChannelListener(channel, delegate);
         }

         public String toString() {
            return "Write-suspending channel listener -> " + delegate;
         }
      };
   }

   public static <T extends SuspendableReadChannel> ChannelListener<T> readSuspendingChannelListener(final ChannelListener<? super T> delegate) {
      return new ChannelListener<T>() {
         public void handleEvent(T channel) {
            channel.suspendReads();
            ChannelListeners.invokeChannelListener(channel, delegate);
         }

         public String toString() {
            return "Read-suspending channel listener -> " + delegate;
         }
      };
   }

   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(I source, O sink, Pool<ByteBuffer> pool) {
      initiateTransfer(Long.MAX_VALUE, source, sink, CLOSING_CHANNEL_LISTENER, CLOSING_CHANNEL_LISTENER, CLOSING_HANDLER, CLOSING_HANDLER, pool);
   }

   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(long count, I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super I> readExceptionHandler, ChannelExceptionHandler<? super O> writeExceptionHandler, Pool<ByteBuffer> pool) {
      if (pool == null) {
         throw Messages.msg.nullParameter("pool");
      } else {
         Pooled<ByteBuffer> allocated = pool.allocate();
         boolean free = true;

         try {
            ByteBuffer buffer = (ByteBuffer)allocated.getResource();

            while(true) {
               long transferred;
               try {
                  transferred = source.transferTo(count, buffer, sink);
               } catch (IOException var20) {
                  invokeChannelExceptionHandler(source, readExceptionHandler, var20);
                  return;
               }

               if (transferred == 0L && !buffer.hasRemaining()) {
                  TransferListener<I, O> listener = new TransferListener(count, allocated, source, sink, sourceListener, sinkListener, writeExceptionHandler, readExceptionHandler, 0);
                  sink.suspendWrites();
                  sink.getWriteSetter().set(listener);
                  source.getReadSetter().set(listener);
                  source.resumeReads();
                  free = false;
                  return;
               }

               if (transferred == -1L) {
                  if (count == Long.MAX_VALUE) {
                     Channels.setReadListener(source, sourceListener);
                     if (sourceListener == null) {
                        source.suspendReads();
                     } else {
                        source.wakeupReads();
                     }

                     Channels.setWriteListener(sink, sinkListener);
                     if (sinkListener == null) {
                        sink.suspendWrites();
                     } else {
                        sink.wakeupWrites();
                     }
                     break;
                  } else {
                     source.suspendReads();
                     sink.suspendWrites();
                     invokeChannelExceptionHandler(source, readExceptionHandler, new EOFException());
                     return;
                  }
               }

               if (count != Long.MAX_VALUE) {
                  count -= transferred;
               }

               while(buffer.hasRemaining()) {
                  int res;
                  try {
                     res = sink.write(buffer);
                  } catch (IOException var21) {
                     invokeChannelExceptionHandler(sink, writeExceptionHandler, var21);
                     return;
                  }

                  if (res == 0) {
                     TransferListener<I, O> listener = new TransferListener(count, allocated, source, sink, sourceListener, sinkListener, writeExceptionHandler, readExceptionHandler, 1);
                     source.suspendReads();
                     source.getReadSetter().set(listener);
                     sink.getWriteSetter().set(listener);
                     sink.resumeWrites();
                     free = false;
                     return;
                  }

                  if (count != Long.MAX_VALUE) {
                     count -= (long)res;
                  }
               }

               if (count == 0L) {
                  Channels.setReadListener(source, sourceListener);
                  if (sourceListener == null) {
                     source.suspendReads();
                  } else {
                     source.wakeupReads();
                  }

                  Channels.setWriteListener(sink, sinkListener);
                  if (sinkListener == null) {
                     sink.suspendWrites();
                  } else {
                     sink.wakeupWrites();
                  }

                  return;
               }
            }
         } finally {
            if (free) {
               allocated.free();
            }

         }

      }
   }

   public static <T extends StreamSourceChannel> ChannelListener<T> drainListener(long bytes, ChannelListener<? super T> finishListener, ChannelExceptionHandler<? super T> exceptionHandler) {
      return new DrainListener(finishListener, exceptionHandler, bytes);
   }

   private static class DrainListener<T extends StreamSourceChannel> implements ChannelListener<T> {
      private final ChannelListener<? super T> finishListener;
      private final ChannelExceptionHandler<? super T> exceptionHandler;
      private long count;

      private DrainListener(ChannelListener<? super T> finishListener, ChannelExceptionHandler<? super T> exceptionHandler, long count) {
         this.finishListener = finishListener;
         this.exceptionHandler = exceptionHandler;
         this.count = count;
      }

      public void handleEvent(T channel) {
         try {
            long count = this.count;

            try {
               while(true) {
                  long res = Channels.drain(channel, count);
                  if (res != -1L && res != count) {
                     if (res != 0L) {
                        if (count < Long.MAX_VALUE) {
                           count -= res;
                        }
                        continue;
                     }

                     return;
                  }

                  this.count = 0L;
                  ChannelListeners.invokeChannelListener(channel, this.finishListener);
                  return;
               }
            } finally {
               this.count = count;
            }
         } catch (IOException var10) {
            this.count = 0L;
            if (this.exceptionHandler != null) {
               ChannelListeners.invokeChannelExceptionHandler(channel, this.exceptionHandler, var10);
            } else {
               IoUtils.safeShutdownReads(channel);
            }

         }
      }

      public String toString() {
         return "Draining channel listener (" + this.count + " bytes) -> " + this.finishListener;
      }

      // $FF: synthetic method
      DrainListener(ChannelListener x0, ChannelExceptionHandler x1, long x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static class SetterDelegatingListener<C extends Channel, T extends Channel> implements ChannelListener<C> {
      private final ChannelListener.SimpleSetter<T> setter;
      private final T channel;

      public SetterDelegatingListener(ChannelListener.SimpleSetter<T> setter, T channel) {
         this.setter = setter;
         this.channel = channel;
      }

      public void handleEvent(C channel) {
         ChannelListeners.invokeChannelListener(this.channel, this.setter.get());
      }

      public String toString() {
         return "Setter delegating channel listener -> " + this.setter;
      }
   }

   private static class DelegatingChannelListener<T extends Channel> implements ChannelListener<Channel> {
      private final ChannelListener<? super T> channelListener;
      private final T realChannel;

      public DelegatingChannelListener(ChannelListener<? super T> channelListener, T realChannel) {
         this.channelListener = channelListener;
         this.realChannel = realChannel;
      }

      public void handleEvent(Channel channel) {
         ChannelListeners.invokeChannelListener(this.realChannel, this.channelListener);
      }

      public String toString() {
         return "Delegating channel listener -> " + this.channelListener;
      }
   }

   private static class DelegatingSetter<T extends Channel> implements ChannelListener.Setter<T> {
      private final ChannelListener.Setter<? extends Channel> setter;
      private final T realChannel;

      DelegatingSetter(ChannelListener.Setter<? extends Channel> setter, T realChannel) {
         this.setter = setter;
         this.realChannel = realChannel;
      }

      public void set(ChannelListener<? super T> channelListener) {
         this.setter.set(channelListener == null ? null : new DelegatingChannelListener(channelListener, this.realChannel));
      }

      public String toString() {
         return "Delegating setter -> " + this.setter;
      }
   }

   static final class TransferListener<I extends StreamSourceChannel, O extends StreamSinkChannel> implements ChannelListener<Channel> {
      private final Pooled<ByteBuffer> pooledBuffer;
      private final I source;
      private final O sink;
      private final ChannelListener<? super I> sourceListener;
      private final ChannelListener<? super O> sinkListener;
      private final ChannelExceptionHandler<? super O> writeExceptionHandler;
      private final ChannelExceptionHandler<? super I> readExceptionHandler;
      private long count;
      private volatile int state;

      TransferListener(long count, Pooled<ByteBuffer> pooledBuffer, I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super O> writeExceptionHandler, ChannelExceptionHandler<? super I> readExceptionHandler, int state) {
         this.count = count;
         this.pooledBuffer = pooledBuffer;
         this.source = source;
         this.sink = sink;
         this.sourceListener = sourceListener;
         this.sinkListener = sinkListener;
         this.writeExceptionHandler = writeExceptionHandler;
         this.readExceptionHandler = readExceptionHandler;
         this.state = state;
      }

      public void handleEvent(Channel channel) {
         ByteBuffer buffer = (ByteBuffer)this.pooledBuffer.getResource();
         int state = this.state;
         long count = this.count;
         long lres;
         int ires;
         switch (state) {
            case 0:
               do {
                  try {
                     lres = this.source.transferTo(count, buffer, this.sink);
                  } catch (IOException var12) {
                     this.readFailed(var12);
                     return;
                  }

                  if (lres == 0L && !buffer.hasRemaining()) {
                     this.count = count;
                     return;
                  }

                  if (lres == -1L) {
                     if (count == Long.MAX_VALUE) {
                        this.done();
                        return;
                     }

                     this.readFailed(new EOFException());
                     return;
                  }

                  if (count != Long.MAX_VALUE) {
                     count -= lres;
                  }

                  while(buffer.hasRemaining()) {
                     try {
                        ires = this.sink.write(buffer);
                     } catch (IOException var13) {
                        this.writeFailed(var13);
                        return;
                     }

                     if (count != Long.MAX_VALUE) {
                        count -= (long)ires;
                     }

                     if (ires == 0) {
                        this.count = count;
                        this.state = 1;
                        this.source.suspendReads();
                        this.sink.resumeWrites();
                        return;
                     }
                  }
               } while(count != 0L);

               this.done();
               return;
            case 1:
               do {
                  while(!buffer.hasRemaining()) {
                     try {
                        lres = this.source.transferTo(count, buffer, this.sink);
                     } catch (IOException var11) {
                        this.readFailed(var11);
                        return;
                     }

                     if (lres == 0L && !buffer.hasRemaining()) {
                        this.count = count;
                        this.state = 0;
                        this.sink.suspendWrites();
                        this.source.resumeReads();
                        return;
                     }

                     if (lres == -1L) {
                        if (count == Long.MAX_VALUE) {
                           this.done();
                           return;
                        }

                        this.readFailed(new EOFException());
                        return;
                     }

                     if (count != Long.MAX_VALUE) {
                        count -= lres;
                     }

                     if (count == 0L) {
                        this.done();
                        return;
                     }
                  }

                  try {
                     ires = this.sink.write(buffer);
                  } catch (IOException var10) {
                     this.writeFailed(var10);
                     return;
                  }

                  if (count != Long.MAX_VALUE) {
                     count -= (long)ires;
                  }
               } while(ires != 0);

               return;
            default:
         }
      }

      private void writeFailed(IOException e) {
         try {
            this.source.suspendReads();
            this.sink.suspendWrites();
            ChannelListeners.invokeChannelExceptionHandler(this.sink, this.writeExceptionHandler, e);
         } finally {
            this.pooledBuffer.free();
         }

      }

      private void readFailed(IOException e) {
         try {
            this.source.suspendReads();
            this.sink.suspendWrites();
            ChannelListeners.invokeChannelExceptionHandler(this.source, this.readExceptionHandler, e);
         } finally {
            this.pooledBuffer.free();
         }

      }

      private void done() {
         try {
            ChannelListener<? super I> sourceListener = this.sourceListener;
            ChannelListener<? super O> sinkListener = this.sinkListener;
            I source = this.source;
            O sink = this.sink;
            Channels.setReadListener(source, sourceListener);
            if (sourceListener == null) {
               source.suspendReads();
            } else {
               source.wakeupReads();
            }

            Channels.setWriteListener(sink, sinkListener);
            if (sinkListener == null) {
               sink.suspendWrites();
            } else {
               sink.wakeupWrites();
            }
         } finally {
            this.pooledBuffer.free();
         }

      }

      public String toString() {
         return "Transfer channel listener (" + this.source + " to " + this.sink + ") -> (" + this.sourceListener + " and " + this.sinkListener + ")";
      }
   }
}
