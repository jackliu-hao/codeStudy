/*      */ package org.xnio;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*      */ import org.xnio._private.Messages;
/*      */ import org.xnio.channels.AcceptingChannel;
/*      */ import org.xnio.channels.Channels;
/*      */ import org.xnio.channels.ConnectedChannel;
/*      */ import org.xnio.channels.StreamSinkChannel;
/*      */ import org.xnio.channels.StreamSourceChannel;
/*      */ import org.xnio.channels.SuspendableReadChannel;
/*      */ import org.xnio.channels.SuspendableWriteChannel;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class ChannelListeners
/*      */ {
/*   52 */   private static final ChannelListener<Channel> NULL_LISTENER = new ChannelListener<Channel>()
/*      */     {
/*      */       public void handleEvent(Channel channel) {}
/*      */       
/*      */       public String toString() {
/*   57 */         return "Null channel listener";
/*      */       }
/*      */     };
/*   60 */   private static final ChannelListener.Setter<?> NULL_SETTER = new ChannelListener.Setter<Channel>()
/*      */     {
/*      */       public void set(ChannelListener<? super Channel> channelListener) {}
/*      */       
/*      */       public String toString() {
/*   65 */         return "Null channel listener setter";
/*      */       }
/*      */     };
/*   68 */   private static ChannelListener<Channel> CLOSING_CHANNEL_LISTENER = new ChannelListener<Channel>() {
/*      */       public void handleEvent(Channel channel) {
/*   70 */         IoUtils.safeClose(channel);
/*      */       }
/*      */       
/*      */       public String toString() {
/*   74 */         return "Closing channel listener";
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> boolean invokeChannelListener(T channel, ChannelListener<? super T> channelListener) {
/*   90 */     if (channelListener != null)
/*   91 */       try { Messages.listenerMsg.tracef("Invoking listener %s on channel %s", channelListener, channel);
/*   92 */         channelListener.handleEvent(channel); }
/*   93 */       catch (Throwable t)
/*   94 */       { Messages.listenerMsg.listenerException(t);
/*   95 */         return false; }
/*      */        
/*   97 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> void invokeChannelListener(Executor executor, T channel, ChannelListener<? super T> channelListener) {
/*      */     try {
/*  110 */       executor.execute(getChannelListenerTask(channel, channelListener));
/*  111 */     } catch (RejectedExecutionException ree) {
/*  112 */       invokeChannelListener(channel, channelListener);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> void invokeChannelExceptionHandler(T channel, ChannelExceptionHandler<? super T> exceptionHandler, IOException exception) {
/*      */     try {
/*  126 */       exceptionHandler.handleException(channel, exception);
/*  127 */     } catch (Throwable t) {
/*  128 */       Messages.listenerMsg.exceptionHandlerException(t);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> Runnable getChannelListenerTask(final T channel, final ChannelListener<? super T> channelListener) {
/*  141 */     return new Runnable() {
/*      */         public String toString() {
/*  143 */           return "Channel listener task for " + channel + " -> " + channelListener;
/*      */         }
/*      */         
/*      */         public void run() {
/*  147 */           ChannelListeners.invokeChannelListener(channel, channelListener);
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> Runnable getChannelListenerTask(final T channel, final ChannelListener.SimpleSetter<T> setter) {
/*  161 */     return new Runnable() {
/*      */         public String toString() {
/*  163 */           return "Channel listener task for " + channel + " -> " + setter;
/*      */         }
/*      */         
/*      */         public void run() {
/*  167 */           ChannelListeners.invokeChannelListener(channel, setter.get());
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ChannelListener<Channel> closingChannelListener() {
/*  178 */     return CLOSING_CHANNEL_LISTENER;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ChannelListener<Channel> closingChannelListener(final Closeable resource) {
/*  188 */     return new ChannelListener<Channel>() {
/*      */         public void handleEvent(Channel channel) {
/*  190 */           IoUtils.safeClose(resource);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  194 */           return "Closing channel listener for " + resource;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ChannelListener<Channel> closingChannelListener(Closeable... resources) {
/*  206 */     return new ChannelListener<Channel>() {
/*      */         public void handleEvent(Channel channel) {
/*  208 */           IoUtils.safeClose(resources);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  212 */           return "Closing channel listener for " + resources.length + " items";
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> ChannelListener<T> closingChannelListener(final ChannelListener<T> delegate, final Closeable resource) {
/*  225 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*  227 */           IoUtils.safeClose(resource);
/*  228 */           delegate.handleEvent(channel);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  232 */           return "Closing channel listener for " + resource + " -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> ChannelListener<T> closingChannelListener(final ChannelListener<T> delegate, Closeable... resources) {
/*  245 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*  247 */           IoUtils.safeClose(resources);
/*  248 */           delegate.handleEvent(channel);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  252 */           return "Closing channel listener for " + resources.length + " items -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ChannelListener<Channel> nullChannelListener() {
/*  263 */     return NULL_LISTENER;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ChannelExceptionHandler<Channel> closingChannelExceptionHandler() {
/*  272 */     return CLOSING_HANDLER;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends ConnectedChannel> ChannelListener<AcceptingChannel<C>> openListenerAdapter(final ChannelListener<? super C> openListener) {
/*  283 */     if (openListener == null) {
/*  284 */       throw Messages.msg.nullParameter("openListener");
/*      */     }
/*  286 */     return new ChannelListener<AcceptingChannel<C>>() {
/*      */         public void handleEvent(AcceptingChannel<C> channel) {
/*      */           try {
/*  289 */             ConnectedChannel connectedChannel = channel.accept();
/*  290 */             if (connectedChannel != null) {
/*  291 */               ChannelListeners.invokeChannelListener(connectedChannel, openListener);
/*      */             }
/*  293 */           } catch (IOException e) {
/*  294 */             Messages.listenerMsg.acceptFailed(channel, e);
/*      */           } 
/*      */         }
/*      */         
/*      */         public String toString() {
/*  299 */           return "Accepting listener for " + openListener;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static <T extends Channel, C> ChannelListener.Setter<T> getSetter(final C channel, final AtomicReferenceFieldUpdater<C, ChannelListener> updater) {
/*  317 */     return new ChannelListener.Setter<T>() {
/*      */         public void set(ChannelListener<? super T> channelListener) {
/*  319 */           updater.set(channel, channelListener);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  323 */           return "Atomic reference field updater setter for " + updater;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> ChannelListener.Setter<T> getSetter(final AtomicReference<ChannelListener<? super T>> atomicReference) {
/*  337 */     return new ChannelListener.Setter<T>() {
/*      */         public void set(ChannelListener<? super T> channelListener) {
/*  339 */           atomicReference.set(channelListener);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  343 */           return "Atomic reference setter (currently=" + atomicReference.get() + ")";
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> ChannelListener.Setter<T> getDelegatingSetter(ChannelListener.Setter<? extends Channel> target, T realChannel) {
/*  357 */     return (target == null) ? null : new DelegatingSetter<>(target, realChannel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> ChannelListener.Setter<T> nullSetter() {
/*  368 */     return (ChannelListener.Setter)NULL_SETTER;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> ChannelListener<T> executorChannelListener(final ChannelListener<T> listener, final Executor executor) {
/*  381 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*      */           try {
/*  384 */             executor.execute(ChannelListeners.getChannelListenerTask((Channel)channel, listener));
/*  385 */           } catch (RejectedExecutionException e) {
/*  386 */             Messages.listenerMsg.executorSubmitFailed(e, (Channel)channel);
/*  387 */             IoUtils.safeClose((Closeable)channel);
/*      */           } 
/*      */         }
/*      */         
/*      */         public String toString() {
/*  392 */           return "Executor channel listener -> " + listener;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends SuspendableWriteChannel> ChannelListener<T> flushingChannelListener(final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
/*  409 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*      */           boolean result;
/*      */           try {
/*  413 */             result = channel.flush();
/*  414 */           } catch (IOException e) {
/*  415 */             channel.suspendWrites();
/*  416 */             ChannelListeners.invokeChannelExceptionHandler((Channel)channel, exceptionHandler, e);
/*      */             return;
/*      */           } 
/*  419 */           if (result) {
/*  420 */             Channels.setWriteListener((SuspendableWriteChannel)channel, delegate);
/*  421 */             ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */           } else {
/*  423 */             Channels.setWriteListener((SuspendableWriteChannel)channel, this);
/*  424 */             channel.resumeWrites();
/*      */           } 
/*      */         }
/*      */         
/*      */         public String toString() {
/*  429 */           return "Flushing channel listener -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends SuspendableWriteChannel> ChannelListener<T> writeShutdownChannelListener(final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
/*  445 */     final ChannelListener<T> flushingListener = flushingChannelListener(delegate, exceptionHandler);
/*  446 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*      */           try {
/*  449 */             channel.shutdownWrites();
/*  450 */           } catch (IOException e) {
/*  451 */             ChannelListeners.invokeChannelExceptionHandler((Channel)channel, exceptionHandler, e);
/*      */             return;
/*      */           } 
/*  454 */           ChannelListeners.invokeChannelListener((Channel)channel, flushingListener);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  458 */           return "Write shutdown channel listener -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends StreamSinkChannel> ChannelListener<T> writingChannelListener(final Pooled<ByteBuffer> pooled, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
/*  476 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*  478 */           ByteBuffer buffer = pooled.getResource();
/*      */           
/*  480 */           boolean ok = false; while (true) {
/*      */             int result;
/*      */             try {
/*  483 */               result = channel.write(buffer);
/*  484 */               ok = true;
/*  485 */             } catch (IOException e) {
/*  486 */               channel.suspendWrites();
/*  487 */               pooled.free();
/*  488 */               ChannelListeners.invokeChannelExceptionHandler((Channel)channel, exceptionHandler, e);
/*      */               return;
/*      */             } finally {
/*  491 */               if (!ok) {
/*  492 */                 pooled.free();
/*      */               }
/*      */             } 
/*  495 */             if (result == 0) {
/*  496 */               Channels.setWriteListener((SuspendableWriteChannel)channel, this);
/*  497 */               channel.resumeWrites();
/*      */               return;
/*      */             } 
/*  500 */             if (!buffer.hasRemaining()) {
/*  501 */               pooled.free();
/*  502 */               ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */               return;
/*      */             } 
/*      */           }  } public String toString() {
/*  506 */           return "Writing channel listener -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends org.xnio.channels.WritableMessageChannel> ChannelListener<T> sendingChannelListener(final Pooled<ByteBuffer> pooled, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
/*  524 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*  526 */           ByteBuffer buffer = pooled.getResource();
/*  527 */           boolean free = true;
/*      */           try {
/*  529 */             if (!(free = channel.send(buffer))) {
/*  530 */               Channels.setWriteListener((SuspendableWriteChannel)channel, this);
/*  531 */               channel.resumeWrites();
/*      */               return;
/*      */             } 
/*  534 */           } catch (IOException e) {
/*  535 */             channel.suspendWrites();
/*  536 */             pooled.free();
/*  537 */             ChannelListeners.invokeChannelExceptionHandler((Channel)channel, exceptionHandler, e);
/*      */             return;
/*      */           } finally {
/*  540 */             if (free) pooled.free(); 
/*      */           } 
/*  542 */           ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  546 */           return "Sending channel listener -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends StreamSinkChannel> ChannelListener<T> fileSendingChannelListener(final FileChannel source, final long position, final long count, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
/*  566 */     if (count == 0L) {
/*  567 */       return (ChannelListener)delegatingChannelListener((ChannelListener)delegate);
/*      */     }
/*  569 */     return new ChannelListener<T>() {
/*  570 */         private long p = position;
/*  571 */         private long cnt = count;
/*      */ 
/*      */         
/*      */         public void handleEvent(T channel) {
/*  575 */           long cnt = this.cnt;
/*  576 */           long p = this.p; try {
/*      */             do {
/*      */               long result;
/*      */               try {
/*  580 */                 result = channel.transferFrom(source, p, cnt);
/*  581 */               } catch (IOException e) {
/*  582 */                 ChannelListeners.invokeChannelExceptionHandler((Channel)channel, exceptionHandler, e);
/*      */                 return;
/*      */               } 
/*  585 */               if (result == 0L) {
/*  586 */                 Channels.setWriteListener((SuspendableWriteChannel)channel, this);
/*  587 */                 channel.resumeWrites();
/*      */                 return;
/*      */               } 
/*  590 */               p += result;
/*  591 */               cnt -= result;
/*  592 */             } while (cnt > 0L);
/*      */             
/*  594 */             ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */             return;
/*      */           } finally {
/*  597 */             this.p = p;
/*  598 */             this.cnt = cnt;
/*      */           } 
/*      */         }
/*      */         
/*      */         public String toString() {
/*  603 */           return "File sending channel listener (" + source + ") -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends StreamSourceChannel> ChannelListener<T> fileReceivingChannelListener(final FileChannel target, final long position, final long count, final ChannelListener<? super T> delegate, final ChannelExceptionHandler<? super T> exceptionHandler) {
/*  623 */     if (count == 0L) {
/*  624 */       return (ChannelListener)delegatingChannelListener((ChannelListener)delegate);
/*      */     }
/*  626 */     return new ChannelListener<T>() {
/*  627 */         private long p = position;
/*  628 */         private long cnt = count;
/*      */ 
/*      */         
/*      */         public void handleEvent(T channel) {
/*  632 */           long cnt = this.cnt;
/*  633 */           long p = this.p; try {
/*      */             do {
/*      */               long result;
/*      */               try {
/*  637 */                 result = channel.transferTo(p, cnt, target);
/*  638 */               } catch (IOException e) {
/*  639 */                 ChannelListeners.invokeChannelExceptionHandler((Channel)channel, exceptionHandler, e);
/*      */                 return;
/*      */               } 
/*  642 */               if (result == 0L) {
/*  643 */                 Channels.setReadListener((SuspendableReadChannel)channel, this);
/*  644 */                 channel.resumeReads();
/*      */                 return;
/*      */               } 
/*  647 */               p += result;
/*  648 */               cnt -= result;
/*  649 */             } while (cnt > 0L);
/*      */             
/*  651 */             ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */             return;
/*      */           } finally {
/*  654 */             this.p = p;
/*  655 */             this.cnt = cnt;
/*      */           } 
/*      */         }
/*      */         
/*      */         public String toString() {
/*  660 */           return "File receiving channel listener (" + target + ") -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends Channel> ChannelListener<T> delegatingChannelListener(final ChannelListener<? super T> delegate) {
/*  673 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*  675 */           ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  679 */           return "Delegating channel listener -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <C extends Channel, T extends Channel> ChannelListener<C> delegatingChannelListener(T channel, ChannelListener.SimpleSetter<T> setter) {
/*  694 */     return new SetterDelegatingListener<>(setter, channel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends SuspendableWriteChannel> ChannelListener<T> writeSuspendingChannelListener(final ChannelListener<? super T> delegate) {
/*  705 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*  707 */           channel.suspendWrites();
/*  708 */           ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  712 */           return "Write-suspending channel listener -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends SuspendableReadChannel> ChannelListener<T> readSuspendingChannelListener(final ChannelListener<? super T> delegate) {
/*  725 */     return new ChannelListener<T>() {
/*      */         public void handleEvent(T channel) {
/*  727 */           channel.suspendReads();
/*  728 */           ChannelListeners.invokeChannelListener((Channel)channel, delegate);
/*      */         }
/*      */         
/*      */         public String toString() {
/*  732 */           return "Read-suspending channel listener -> " + delegate;
/*      */         }
/*      */       };
/*      */   }
/*      */   
/*      */   static final class TransferListener<I extends StreamSourceChannel, O extends StreamSinkChannel> implements ChannelListener<Channel> {
/*      */     private final Pooled<ByteBuffer> pooledBuffer;
/*      */     private final I source;
/*      */     private final O sink;
/*      */     private final ChannelListener<? super I> sourceListener;
/*      */     private final ChannelListener<? super O> sinkListener;
/*      */     private final ChannelExceptionHandler<? super O> writeExceptionHandler;
/*      */     private final ChannelExceptionHandler<? super I> readExceptionHandler;
/*      */     private long count;
/*      */     private volatile int state;
/*      */     
/*      */     TransferListener(long count, Pooled<ByteBuffer> pooledBuffer, I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super O> writeExceptionHandler, ChannelExceptionHandler<? super I> readExceptionHandler, int state) {
/*  749 */       this.count = count;
/*  750 */       this.pooledBuffer = pooledBuffer;
/*  751 */       this.source = source;
/*  752 */       this.sink = sink;
/*  753 */       this.sourceListener = sourceListener;
/*  754 */       this.sinkListener = sinkListener;
/*  755 */       this.writeExceptionHandler = writeExceptionHandler;
/*  756 */       this.readExceptionHandler = readExceptionHandler;
/*  757 */       this.state = state;
/*      */     }
/*      */     
/*      */     public void handleEvent(Channel channel) {
/*  761 */       ByteBuffer buffer = this.pooledBuffer.getResource();
/*  762 */       int state = this.state;
/*      */       
/*  764 */       long count = this.count;
/*      */ 
/*      */ 
/*      */       
/*  768 */       switch (state) {
/*      */         case 0:
/*      */           while (true) {
/*      */             long lres;
/*      */             try {
/*  773 */               lres = this.source.transferTo(count, buffer, (StreamSinkChannel)this.sink);
/*  774 */             } catch (IOException e) {
/*  775 */               readFailed(e);
/*      */               return;
/*      */             } 
/*  778 */             if (lres == 0L && !buffer.hasRemaining()) {
/*  779 */               this.count = count;
/*      */               return;
/*      */             } 
/*  782 */             if (lres == -1L) {
/*      */               
/*  784 */               if (count == Long.MAX_VALUE) {
/*      */                 
/*  786 */                 done();
/*      */                 return;
/*      */               } 
/*  789 */               readFailed(new EOFException());
/*      */               
/*      */               return;
/*      */             } 
/*  793 */             if (count != Long.MAX_VALUE) {
/*  794 */               count -= lres;
/*      */             }
/*  796 */             while (buffer.hasRemaining()) {
/*      */               int ires; try {
/*  798 */                 ires = this.sink.write(buffer);
/*  799 */               } catch (IOException e) {
/*  800 */                 writeFailed(e);
/*      */                 return;
/*      */               } 
/*  803 */               if (count != Long.MAX_VALUE) {
/*  804 */                 count -= ires;
/*      */               }
/*  806 */               if (ires == 0) {
/*  807 */                 this.count = count;
/*  808 */                 this.state = 1;
/*  809 */                 this.source.suspendReads();
/*  810 */                 this.sink.resumeWrites();
/*      */                 
/*      */                 return;
/*      */               } 
/*      */             } 
/*  815 */             if (count == 0L) {
/*  816 */               done();
/*      */               return;
/*      */             } 
/*      */           } 
/*      */         
/*      */         case 1:
/*      */           while (true) {
/*      */             long lres;
/*  824 */             while (buffer.hasRemaining()) {
/*      */               int ires; try {
/*  826 */                 ires = this.sink.write(buffer);
/*  827 */               } catch (IOException e) {
/*  828 */                 writeFailed(e);
/*      */                 return;
/*      */               } 
/*  831 */               if (count != Long.MAX_VALUE) {
/*  832 */                 count -= ires;
/*      */               }
/*  834 */               if (ires == 0) {
/*      */                 return;
/*      */               }
/*      */             } 
/*      */             try {
/*  839 */               lres = this.source.transferTo(count, buffer, (StreamSinkChannel)this.sink);
/*  840 */             } catch (IOException e) {
/*  841 */               readFailed(e);
/*      */               return;
/*      */             } 
/*  844 */             if (lres == 0L && !buffer.hasRemaining()) {
/*  845 */               this.count = count;
/*  846 */               this.state = 0;
/*  847 */               this.sink.suspendWrites();
/*  848 */               this.source.resumeReads();
/*      */               return;
/*      */             } 
/*  851 */             if (lres == -1L) {
/*      */               
/*  853 */               if (count == Long.MAX_VALUE) {
/*      */                 
/*  855 */                 done();
/*      */                 return;
/*      */               } 
/*  858 */               readFailed(new EOFException());
/*      */               
/*      */               return;
/*      */             } 
/*  862 */             if (count != Long.MAX_VALUE) {
/*  863 */               count -= lres;
/*      */             }
/*      */             
/*  866 */             if (count == 0L) {
/*  867 */               done();
/*      */               return;
/*      */             } 
/*      */           } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private void writeFailed(IOException e) {
/*      */       try {
/*  878 */         this.source.suspendReads();
/*  879 */         this.sink.suspendWrites();
/*  880 */         ChannelListeners.invokeChannelExceptionHandler(this.sink, this.writeExceptionHandler, e);
/*      */       } finally {
/*  882 */         this.pooledBuffer.free();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void readFailed(IOException e) {
/*      */       try {
/*  888 */         this.source.suspendReads();
/*  889 */         this.sink.suspendWrites();
/*  890 */         ChannelListeners.invokeChannelExceptionHandler(this.source, this.readExceptionHandler, e);
/*      */       } finally {
/*  892 */         this.pooledBuffer.free();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void done() {
/*      */       try {
/*  898 */         ChannelListener<? super I> sourceListener = this.sourceListener;
/*  899 */         ChannelListener<? super O> sinkListener = this.sinkListener;
/*  900 */         I source = this.source;
/*  901 */         O sink = this.sink;
/*      */         
/*  903 */         Channels.setReadListener((SuspendableReadChannel)source, sourceListener);
/*  904 */         if (sourceListener == null) {
/*  905 */           source.suspendReads();
/*      */         } else {
/*  907 */           source.wakeupReads();
/*      */         } 
/*      */         
/*  910 */         Channels.setWriteListener((SuspendableWriteChannel)sink, sinkListener);
/*  911 */         if (sinkListener == null) {
/*  912 */           sink.suspendWrites();
/*      */         } else {
/*  914 */           sink.wakeupWrites();
/*      */         } 
/*      */       } finally {
/*  917 */         this.pooledBuffer.free();
/*      */       } 
/*      */     }
/*      */     
/*      */     public String toString() {
/*  922 */       return "Transfer channel listener (" + this.source + " to " + this.sink + ") -> (" + this.sourceListener + " and " + this.sinkListener + ")";
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(I source, O sink, Pool<ByteBuffer> pool) {
/*  937 */     initiateTransfer(Long.MAX_VALUE, source, sink, (ChannelListener)CLOSING_CHANNEL_LISTENER, (ChannelListener)CLOSING_CHANNEL_LISTENER, (ChannelExceptionHandler)CLOSING_HANDLER, (ChannelExceptionHandler)CLOSING_HANDLER, pool);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(long count, I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super I> readExceptionHandler, ChannelExceptionHandler<? super O> writeExceptionHandler, Pool<ByteBuffer> pool) {
/*  954 */     if (pool == null) {
/*  955 */       throw Messages.msg.nullParameter("pool");
/*      */     }
/*  957 */     Pooled<ByteBuffer> allocated = pool.allocate();
/*  958 */     boolean free = true;
/*      */     try {
/*  960 */       ByteBuffer buffer = allocated.getResource();
/*      */       while (true) {
/*      */         long transferred;
/*      */         try {
/*  964 */           transferred = source.transferTo(count, buffer, (StreamSinkChannel)sink);
/*  965 */         } catch (IOException e) {
/*  966 */           invokeChannelExceptionHandler(source, readExceptionHandler, e);
/*      */           return;
/*      */         } 
/*  969 */         if (transferred == 0L && !buffer.hasRemaining()) {
/*      */           break;
/*      */         }
/*  972 */         if (transferred == -1L) {
/*  973 */           if (count == Long.MAX_VALUE) {
/*  974 */             Channels.setReadListener((SuspendableReadChannel)source, sourceListener);
/*  975 */             if (sourceListener == null) {
/*  976 */               source.suspendReads();
/*      */             } else {
/*  978 */               source.wakeupReads();
/*      */             } 
/*      */             
/*  981 */             Channels.setWriteListener((SuspendableWriteChannel)sink, sinkListener);
/*  982 */             if (sinkListener == null) {
/*  983 */               sink.suspendWrites();
/*      */             } else {
/*  985 */               sink.wakeupWrites();
/*      */             } 
/*      */           } else {
/*  988 */             source.suspendReads();
/*  989 */             sink.suspendWrites();
/*  990 */             invokeChannelExceptionHandler(source, readExceptionHandler, new EOFException());
/*      */           } 
/*      */           return;
/*      */         } 
/*  994 */         if (count != Long.MAX_VALUE) {
/*  995 */           count -= transferred;
/*      */         }
/*  997 */         while (buffer.hasRemaining()) {
/*      */           int res;
/*      */           try {
/* 1000 */             res = sink.write(buffer);
/* 1001 */           } catch (IOException e) {
/* 1002 */             invokeChannelExceptionHandler(sink, writeExceptionHandler, e);
/*      */             return;
/*      */           } 
/* 1005 */           if (res == 0) {
/*      */             
/* 1007 */             TransferListener<I, O> transferListener = new TransferListener<>(count, allocated, source, sink, sourceListener, sinkListener, writeExceptionHandler, readExceptionHandler, 1);
/* 1008 */             source.suspendReads();
/* 1009 */             source.getReadSetter().set((ChannelListener)transferListener);
/* 1010 */             sink.getWriteSetter().set((ChannelListener)transferListener);
/* 1011 */             sink.resumeWrites();
/* 1012 */             free = false; return;
/*      */           } 
/* 1014 */           if (count != Long.MAX_VALUE) {
/* 1015 */             count -= res;
/*      */           }
/*      */         } 
/* 1018 */         if (count == 0L) {
/*      */           
/* 1020 */           Channels.setReadListener((SuspendableReadChannel)source, sourceListener);
/* 1021 */           if (sourceListener == null) {
/* 1022 */             source.suspendReads();
/*      */           } else {
/* 1024 */             source.wakeupReads();
/*      */           } 
/*      */           
/* 1027 */           Channels.setWriteListener((SuspendableWriteChannel)sink, sinkListener);
/* 1028 */           if (sinkListener == null) {
/* 1029 */             sink.suspendWrites();
/*      */           } else {
/* 1031 */             sink.wakeupWrites();
/*      */           } 
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/* 1037 */       TransferListener<I, O> listener = new TransferListener<>(count, allocated, source, sink, sourceListener, sinkListener, writeExceptionHandler, readExceptionHandler, 0);
/* 1038 */       sink.suspendWrites();
/* 1039 */       sink.getWriteSetter().set((ChannelListener)listener);
/* 1040 */       source.getReadSetter().set((ChannelListener)listener);
/* 1041 */       source.resumeReads();
/* 1042 */       free = false;
/*      */       return;
/*      */     } finally {
/* 1045 */       if (free) allocated.free();
/*      */     
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends StreamSourceChannel> ChannelListener<T> drainListener(long bytes, ChannelListener<? super T> finishListener, ChannelExceptionHandler<? super T> exceptionHandler) {
/* 1060 */     return new DrainListener<>(finishListener, exceptionHandler, bytes);
/*      */   }
/*      */   
/*      */   private static class DelegatingSetter<T extends Channel> implements ChannelListener.Setter<T> {
/*      */     private final ChannelListener.Setter<? extends Channel> setter;
/*      */     private final T realChannel;
/*      */     
/*      */     DelegatingSetter(ChannelListener.Setter<? extends Channel> setter, T realChannel) {
/* 1068 */       this.setter = setter;
/* 1069 */       this.realChannel = realChannel;
/*      */     }
/*      */     
/*      */     public void set(ChannelListener<? super T> channelListener) {
/* 1073 */       this.setter.set((channelListener == null) ? null : new ChannelListeners.DelegatingChannelListener<>(channelListener, this.realChannel));
/*      */     }
/*      */     
/*      */     public String toString() {
/* 1077 */       return "Delegating setter -> " + this.setter;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class DelegatingChannelListener<T extends Channel>
/*      */     implements ChannelListener<Channel> {
/*      */     private final ChannelListener<? super T> channelListener;
/*      */     private final T realChannel;
/*      */     
/*      */     public DelegatingChannelListener(ChannelListener<? super T> channelListener, T realChannel) {
/* 1087 */       this.channelListener = channelListener;
/* 1088 */       this.realChannel = realChannel;
/*      */     }
/*      */     
/*      */     public void handleEvent(Channel channel) {
/* 1092 */       ChannelListeners.invokeChannelListener(this.realChannel, this.channelListener);
/*      */     }
/*      */     
/*      */     public String toString() {
/* 1096 */       return "Delegating channel listener -> " + this.channelListener;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class SetterDelegatingListener<C extends Channel, T extends Channel>
/*      */     implements ChannelListener<C> {
/*      */     private final ChannelListener.SimpleSetter<T> setter;
/*      */     private final T channel;
/*      */     
/*      */     public SetterDelegatingListener(ChannelListener.SimpleSetter<T> setter, T channel) {
/* 1106 */       this.setter = setter;
/* 1107 */       this.channel = channel;
/*      */     }
/*      */     
/*      */     public void handleEvent(C channel) {
/* 1111 */       ChannelListeners.invokeChannelListener(this.channel, this.setter.get());
/*      */     }
/*      */     
/*      */     public String toString() {
/* 1115 */       return "Setter delegating channel listener -> " + this.setter;
/*      */     }
/*      */   }
/*      */   
/* 1119 */   private static final ChannelExceptionHandler<Channel> CLOSING_HANDLER = new ChannelExceptionHandler<Channel>() {
/*      */       public void handleException(Channel channel, IOException exception) {
/* 1121 */         IoUtils.safeClose(channel);
/*      */       }
/*      */     };
/*      */   
/*      */   private static class DrainListener<T extends StreamSourceChannel> implements ChannelListener<T> {
/*      */     private final ChannelListener<? super T> finishListener;
/*      */     private final ChannelExceptionHandler<? super T> exceptionHandler;
/*      */     private long count;
/*      */     
/*      */     private DrainListener(ChannelListener<? super T> finishListener, ChannelExceptionHandler<? super T> exceptionHandler, long count) {
/* 1131 */       this.finishListener = finishListener;
/* 1132 */       this.exceptionHandler = exceptionHandler;
/* 1133 */       this.count = count;
/*      */     }
/*      */     
/*      */     public void handleEvent(T channel) {
/*      */       try {
/* 1138 */         long count = this.count;
/*      */         
/*      */         try {
/*      */           while (true) {
/* 1142 */             long res = Channels.drain((StreamSourceChannel)channel, count);
/* 1143 */             if (res == -1L || res == count) {
/* 1144 */               this.count = 0L;
/* 1145 */               ChannelListeners.invokeChannelListener((Channel)channel, (ChannelListener)this.finishListener); return;
/*      */             } 
/* 1147 */             if (res == 0L)
/*      */               return; 
/* 1149 */             if (count < Long.MAX_VALUE)
/*      */             {
/* 1151 */               count -= res;
/*      */             }
/*      */           } 
/*      */         } finally {
/* 1155 */           this.count = count;
/*      */         } 
/* 1157 */       } catch (IOException e) {
/* 1158 */         this.count = 0L;
/* 1159 */         if (this.exceptionHandler != null) {
/* 1160 */           ChannelListeners.invokeChannelExceptionHandler((Channel)channel, (ChannelExceptionHandler)this.exceptionHandler, e);
/*      */         } else {
/* 1162 */           IoUtils.safeShutdownReads((SuspendableReadChannel)channel);
/*      */         } 
/*      */         return;
/*      */       } 
/*      */     }
/*      */     public String toString() {
/* 1168 */       return "Draining channel listener (" + this.count + " bytes) -> " + this.finishListener;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ChannelListeners.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */