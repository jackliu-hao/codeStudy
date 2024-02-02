/*     */ package io.undertow.channels;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
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
/*     */ public abstract class DetachableStreamSourceChannel
/*     */   implements StreamSourceChannel
/*     */ {
/*     */   protected final StreamSourceChannel delegate;
/*     */   protected ChannelListener.SimpleSetter<DetachableStreamSourceChannel> readSetter;
/*     */   protected ChannelListener.SimpleSetter<DetachableStreamSourceChannel> closeSetter;
/*     */   
/*     */   public DetachableStreamSourceChannel(StreamSourceChannel delegate) {
/*  53 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract boolean isFinished();
/*     */   
/*     */   public void resumeReads() {
/*  60 */     if (isFinished()) {
/*     */       return;
/*     */     }
/*  63 */     this.delegate.resumeReads();
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  67 */     if (isFinished()) {
/*  68 */       return -1L;
/*     */     }
/*  70 */     return this.delegate.transferTo(position, count, target);
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/*  74 */     if (isFinished()) {
/*     */       return;
/*     */     }
/*  77 */     this.delegate.awaitReadable();
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/*  81 */     if (isFinished()) {
/*     */       return;
/*     */     }
/*  84 */     this.delegate.suspendReads();
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  88 */     if (isFinished()) {
/*  89 */       return -1L;
/*     */     }
/*  91 */     return this.delegate.transferTo(count, throughBuffer, target);
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/*  95 */     return this.delegate.getWorker();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/*  99 */     if (isFinished()) {
/* 100 */       return false;
/*     */     }
/* 102 */     return this.delegate.isReadResumed();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 107 */     if (isFinished()) {
/* 108 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 110 */     return (T)this.delegate.setOption(option, value);
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 114 */     return this.delegate.supportsOption(option);
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 118 */     if (isFinished()) {
/*     */       return;
/*     */     }
/* 121 */     this.delegate.shutdownReads();
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter() {
/* 125 */     if (this.readSetter == null) {
/* 126 */       this.readSetter = new ChannelListener.SimpleSetter();
/* 127 */       if (!isFinished()) {
/* 128 */         if (this.delegate instanceof ConduitStreamSourceChannel) {
/* 129 */           ((ConduitStreamSourceChannel)this.delegate).setReadListener(new SetterDelegatingListener((ChannelListener.SimpleSetter)this.readSetter, this));
/*     */         } else {
/* 131 */           this.delegate.getReadSetter().set(new SetterDelegatingListener((ChannelListener.SimpleSetter)this.readSetter, this));
/*     */         } 
/*     */       }
/*     */     } 
/* 135 */     return (ChannelListener.Setter)this.readSetter;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 139 */     if (isFinished()) {
/* 140 */       return false;
/*     */     }
/* 142 */     return this.delegate.isOpen();
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 146 */     if (isFinished()) {
/* 147 */       return -1L;
/*     */     }
/* 149 */     return this.delegate.read(dsts);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 153 */     if (isFinished()) {
/* 154 */       return -1L;
/*     */     }
/* 156 */     return this.delegate.read(dsts, offset, length);
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 160 */     if (isFinished()) {
/*     */       return;
/*     */     }
/* 163 */     this.delegate.wakeupReads();
/*     */   }
/*     */   
/*     */   public XnioExecutor getReadThread() {
/* 167 */     return this.delegate.getReadThread();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 171 */     if (isFinished()) {
/* 172 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 174 */     this.delegate.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter() {
/* 178 */     if (this.closeSetter == null) {
/* 179 */       this.closeSetter = new ChannelListener.SimpleSetter();
/* 180 */       if (!isFinished()) {
/* 181 */         if (this.delegate instanceof ConduitStreamSourceChannel) {
/* 182 */           ((ConduitStreamSourceChannel)this.delegate).setCloseListener(ChannelListeners.delegatingChannelListener((Channel)this, this.closeSetter));
/*     */         } else {
/* 184 */           this.delegate.getCloseSetter().set(ChannelListeners.delegatingChannelListener((Channel)this, this.closeSetter));
/*     */         } 
/*     */       }
/*     */     } 
/* 188 */     return (ChannelListener.Setter)this.closeSetter;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 192 */     if (isFinished()) {
/*     */       return;
/*     */     }
/* 195 */     this.delegate.close();
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 199 */     if (isFinished()) {
/* 200 */       throw UndertowMessages.MESSAGES.streamIsClosed();
/*     */     }
/* 202 */     return (T)this.delegate.getOption(option);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 206 */     if (isFinished()) {
/* 207 */       return -1;
/*     */     }
/* 209 */     return this.delegate.read(dst);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 214 */     return this.delegate.getIoThread();
/*     */   }
/*     */   
/*     */   private static class SetterDelegatingListener
/*     */     implements ChannelListener<StreamSourceChannel>
/*     */   {
/*     */     private final ChannelListener.SimpleSetter<StreamSourceChannel> setter;
/*     */     private final StreamSourceChannel channel;
/*     */     
/*     */     SetterDelegatingListener(ChannelListener.SimpleSetter<StreamSourceChannel> setter, StreamSourceChannel channel) {
/* 224 */       this.setter = setter;
/* 225 */       this.channel = channel;
/*     */     }
/*     */     
/*     */     public void handleEvent(StreamSourceChannel channel) {
/* 229 */       ChannelListener<? super StreamSourceChannel> channelListener = this.setter.get();
/* 230 */       if (channelListener != null) {
/* 231 */         ChannelListeners.invokeChannelListener((Channel)this.channel, channelListener);
/*     */       } else {
/* 233 */         UndertowLogger.REQUEST_LOGGER.debugf("suspending reads on %s to prevent listener runaway", channel);
/* 234 */         channel.suspendReads();
/*     */       } 
/*     */     }
/*     */     
/*     */     public String toString() {
/* 239 */       return "Setter delegating channel listener -> " + this.setter;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\channels\DetachableStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */