/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio._private.Messages;
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
/*     */ public class AssembledChannel
/*     */   implements CloseableChannel
/*     */ {
/*     */   private final SuspendableReadChannel readChannel;
/*     */   private final SuspendableWriteChannel writeChannel;
/*  44 */   private final ChannelListener.SimpleSetter<AssembledChannel> closeSetter = new ChannelListener.SimpleSetter();
/*     */   
/*  46 */   private final ChannelListener<CloseableChannel> listener = new ChannelListener<CloseableChannel>()
/*     */     {
/*     */       public void handleEvent(CloseableChannel channel) {
/*  49 */         AssembledChannel obj = AssembledChannel.this;
/*     */         while (true) {
/*  51 */           int oldState = AssembledChannel.stateUpdater.get(obj);
/*  52 */           if (oldState == 3) {
/*     */             return;
/*     */           }
/*  55 */           int newState = oldState;
/*  56 */           if (channel == AssembledChannel.this.readChannel) {
/*  57 */             newState |= 0x1;
/*     */           }
/*  59 */           if (channel == AssembledChannel.this.writeChannel) {
/*  60 */             newState |= 0x2;
/*     */           }
/*  62 */           if (AssembledChannel.stateUpdater.compareAndSet(obj, oldState, newState)) {
/*  63 */             if (newState == 3)
/*  64 */               ChannelListeners.invokeChannelListener(obj, AssembledChannel.this.closeSetter.get()); 
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       }
/*  69 */     }; private volatile int state = 0;
/*     */ 
/*     */   
/*  72 */   private static final AtomicIntegerFieldUpdater<AssembledChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(AssembledChannel.class, "state");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AssembledChannel(SuspendableReadChannel readChannel, SuspendableWriteChannel writeChannel) {
/*  81 */     this.readChannel = readChannel;
/*  82 */     this.writeChannel = writeChannel;
/*  83 */     if (readChannel.getWorker() != writeChannel.getWorker()) {
/*  84 */       throw Messages.msg.differentWorkers();
/*     */     }
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends CloseableChannel> getCloseSetter() {
/*  89 */     this.readChannel.getCloseSetter().set(this.listener);
/*  90 */     this.writeChannel.getCloseSetter().set(this.listener);
/*  91 */     return (ChannelListener.Setter)this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/*  96 */     return this.readChannel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 101 */     return this.readChannel.getIoThread();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 106 */       this.readChannel.close();
/* 107 */       this.writeChannel.close();
/*     */     } finally {
/* 109 */       IoUtils.safeClose(this.readChannel);
/* 110 */       IoUtils.safeClose(this.writeChannel);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 115 */     return (this.readChannel.isOpen() && this.writeChannel.isOpen());
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 119 */     return (this.readChannel.supportsOption(option) || this.writeChannel.supportsOption(option));
/*     */   }
/*     */   
/*     */   private static <T> T nonNullOrFirst(T one, T two) {
/* 123 */     return (one != null) ? one : two;
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 127 */     return nonNullOrFirst(this.readChannel.getOption(option), this.writeChannel.getOption(option));
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 131 */     return nonNullOrFirst(this.readChannel.setOption(option, value), this.writeChannel.setOption(option, value));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */