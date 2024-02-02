/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.CloseListenerSettable;
/*     */ import org.xnio.channels.Configurable;
/*     */ import org.xnio.channels.WritableMessageChannel;
/*     */ import org.xnio.channels.WriteListenerSettable;
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
/*     */ 
/*     */ 
/*     */ public final class ConduitWritableMessageChannel
/*     */   implements WritableMessageChannel, WriteListenerSettable<ConduitWritableMessageChannel>, CloseListenerSettable<ConduitWritableMessageChannel>, Cloneable
/*     */ {
/*     */   private final Configurable configurable;
/*     */   private MessageSinkConduit conduit;
/*     */   private ChannelListener<? super ConduitWritableMessageChannel> writeListener;
/*     */   private ChannelListener<? super ConduitWritableMessageChannel> closeListener;
/*     */   
/*     */   public ConduitWritableMessageChannel(Configurable configurable, MessageSinkConduit conduit) {
/*  53 */     this.configurable = configurable;
/*  54 */     this.conduit = conduit;
/*  55 */     conduit.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler<>(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageSinkConduit getConduit() {
/*  64 */     return this.conduit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConduit(MessageSinkConduit conduit) {
/*  73 */     this.conduit = conduit;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitWritableMessageChannel> getWriteListener() {
/*  77 */     return this.writeListener;
/*     */   }
/*     */   
/*     */   public void setWriteListener(ChannelListener<? super ConduitWritableMessageChannel> writeListener) {
/*  81 */     this.writeListener = writeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitWritableMessageChannel> getCloseListener() {
/*  85 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super ConduitWritableMessageChannel> closeListener) {
/*  89 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitWritableMessageChannel> getWriteSetter() {
/*  93 */     return (ChannelListener.Setter<ConduitWritableMessageChannel>)new WriteListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitWritableMessageChannel> getCloseSetter() {
/*  97 */     return (ChannelListener.Setter<ConduitWritableMessageChannel>)new CloseListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 101 */     this.conduit.suspendWrites();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 105 */     this.conduit.resumeWrites();
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 109 */     this.conduit.wakeupWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 113 */     return this.conduit.isWriteResumed();
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 117 */     this.conduit.awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 121 */     this.conduit.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer dst) throws IOException {
/* 125 */     return this.conduit.send(dst);
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer[] srcs) throws IOException {
/* 129 */     return this.conduit.send(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 133 */     return this.conduit.send(dsts, offs, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer buffer) throws IOException {
/* 138 */     return this.conduit.sendFinal(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer[] buffers) throws IOException {
/* 143 */     return this.conduit.sendFinal(buffers, 0, buffers.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer[] buffers, int offs, int len) throws IOException {
/* 148 */     return this.conduit.sendFinal(buffers, offs, len);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 152 */     return this.conduit.flush();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 156 */     return this.configurable.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 160 */     return (T)this.configurable.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 164 */     return (T)this.configurable.setOption(option, value);
/*     */   }
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 168 */     this.conduit.terminateWrites();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 172 */     return !this.conduit.isWriteShutdown();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 176 */     this.conduit.truncateWrites();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 181 */     return (XnioExecutor)this.conduit.getWriteThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 185 */     return this.conduit.getWriteThread();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 189 */     return this.conduit.getWorker();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConduitWritableMessageChannel clone() {
/*     */     try {
/* 199 */       return (ConduitWritableMessageChannel)super.clone();
/* 200 */     } catch (CloneNotSupportedException e) {
/* 201 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\ConduitWritableMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */