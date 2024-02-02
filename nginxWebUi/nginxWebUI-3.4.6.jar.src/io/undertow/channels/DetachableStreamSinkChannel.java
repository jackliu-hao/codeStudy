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
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
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
/*     */ public abstract class DetachableStreamSinkChannel
/*     */   implements StreamSinkChannel
/*     */ {
/*     */   protected final StreamSinkChannel delegate;
/*     */   protected ChannelListener.SimpleSetter<DetachableStreamSinkChannel> writeSetter;
/*     */   protected ChannelListener.SimpleSetter<DetachableStreamSinkChannel> closeSetter;
/*     */   
/*     */   public DetachableStreamSinkChannel(StreamSinkChannel delegate) {
/*  52 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract boolean isFinished();
/*     */   
/*     */   public void suspendWrites() {
/*  59 */     if (isFinished()) {
/*     */       return;
/*     */     }
/*  62 */     this.delegate.suspendWrites();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWriteResumed() {
/*  68 */     if (isFinished()) {
/*  69 */       return false;
/*     */     }
/*  71 */     return this.delegate.isWriteResumed();
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/*  76 */     if (isFinished()) {
/*     */       return;
/*     */     }
/*  79 */     this.delegate.shutdownWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/*  84 */     if (isFinished()) {
/*  85 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/*  87 */     this.delegate.awaitWritable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/*  92 */     if (isFinished()) {
/*  93 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/*  95 */     this.delegate.awaitWritable(time, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioExecutor getWriteThread() {
/* 100 */     return this.delegate.getWriteThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 105 */     return (!isFinished() && this.delegate.isOpen());
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 110 */     if (isFinished())
/* 111 */       return;  this.delegate.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 116 */     if (isFinished()) {
/* 117 */       return true;
/*     */     }
/* 119 */     return this.delegate.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 124 */     if (isFinished()) {
/* 125 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 127 */     return this.delegate.transferFrom(src, position, count);
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 132 */     if (isFinished()) {
/* 133 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 135 */     return this.delegate.transferFrom(source, count, throughBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends StreamSinkChannel> getWriteSetter() {
/* 140 */     if (this.writeSetter == null) {
/* 141 */       this.writeSetter = new ChannelListener.SimpleSetter();
/* 142 */       if (!isFinished()) {
/* 143 */         if (this.delegate instanceof ConduitStreamSinkChannel) {
/* 144 */           ((ConduitStreamSinkChannel)this.delegate).setWriteListener(new SetterDelegatingListener((ChannelListener.SimpleSetter)this.writeSetter, this));
/*     */         } else {
/* 146 */           this.delegate.getWriteSetter().set(new SetterDelegatingListener((ChannelListener.SimpleSetter)this.writeSetter, this));
/*     */         } 
/*     */       }
/*     */     } 
/* 150 */     return (ChannelListener.Setter)this.writeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends StreamSinkChannel> getCloseSetter() {
/* 155 */     if (this.closeSetter == null) {
/* 156 */       this.closeSetter = new ChannelListener.SimpleSetter();
/* 157 */       if (!isFinished()) {
/* 158 */         this.delegate.getCloseSetter().set(ChannelListeners.delegatingChannelListener((Channel)this, this.closeSetter));
/*     */       }
/*     */     } 
/* 161 */     return (ChannelListener.Setter)this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 166 */     return this.delegate.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 171 */     return this.delegate.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 176 */     if (isFinished()) {
/* 177 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 179 */     return this.delegate.write(srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 184 */     if (isFinished()) {
/* 185 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 187 */     return this.delegate.write(srcs);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 192 */     if (isFinished()) {
/* 193 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 195 */     return this.delegate.writeFinal(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 200 */     if (isFinished()) {
/* 201 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 203 */     return this.delegate.writeFinal(srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 208 */     if (isFinished()) {
/* 209 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 211 */     return this.delegate.writeFinal(srcs);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 216 */     return this.delegate.supportsOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 221 */     if (isFinished()) {
/* 222 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 224 */     return (T)this.delegate.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 229 */     if (isFinished()) {
/* 230 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 232 */     return (T)this.delegate.setOption(option, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 237 */     if (isFinished()) {
/* 238 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 240 */     return this.delegate.write(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 245 */     if (isFinished()) {
/*     */       return;
/*     */     }
/* 248 */     this.delegate.resumeWrites();
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupWrites() {
/* 253 */     if (isFinished()) {
/*     */       return;
/*     */     }
/* 256 */     this.delegate.wakeupWrites();
/*     */   }
/*     */   
/*     */   public void responseDone() {
/* 260 */     if (this.delegate instanceof ConduitStreamSinkChannel) {
/* 261 */       ((ConduitStreamSinkChannel)this.delegate).setCloseListener(null);
/* 262 */       ((ConduitStreamSinkChannel)this.delegate).setWriteListener(null);
/*     */     } else {
/* 264 */       this.delegate.getCloseSetter().set(null);
/* 265 */       this.delegate.getWriteSetter().set(null);
/*     */     } 
/* 267 */     if (this.delegate.isWriteResumed())
/* 268 */       this.delegate.suspendWrites(); 
/*     */   }
/*     */   
/*     */   private static class SetterDelegatingListener
/*     */     implements ChannelListener<StreamSinkChannel>
/*     */   {
/*     */     private final ChannelListener.SimpleSetter<StreamSinkChannel> setter;
/*     */     private final StreamSinkChannel channel;
/*     */     
/*     */     SetterDelegatingListener(ChannelListener.SimpleSetter<StreamSinkChannel> setter, StreamSinkChannel channel) {
/* 278 */       this.setter = setter;
/* 279 */       this.channel = channel;
/*     */     }
/*     */     
/*     */     public void handleEvent(StreamSinkChannel channel) {
/* 283 */       ChannelListener<? super StreamSinkChannel> channelListener = this.setter.get();
/* 284 */       if (channelListener != null) {
/* 285 */         ChannelListeners.invokeChannelListener((Channel)this.channel, channelListener);
/*     */       } else {
/* 287 */         UndertowLogger.REQUEST_LOGGER.debugf("suspending writes on %s to prevent listener runaway", channel);
/* 288 */         channel.suspendWrites();
/*     */       } 
/*     */     }
/*     */     
/*     */     public String toString() {
/* 293 */       return "Setter delegating channel listener -> " + this.setter;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\channels\DetachableStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */