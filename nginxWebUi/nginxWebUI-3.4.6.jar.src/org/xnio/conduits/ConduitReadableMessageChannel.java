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
/*     */ import org.xnio.channels.ReadListenerSettable;
/*     */ import org.xnio.channels.ReadableMessageChannel;
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
/*     */ public final class ConduitReadableMessageChannel
/*     */   implements ReadableMessageChannel, ReadListenerSettable<ConduitReadableMessageChannel>, CloseListenerSettable<ConduitReadableMessageChannel>, Cloneable
/*     */ {
/*     */   private final Configurable configurable;
/*     */   private MessageSourceConduit conduit;
/*     */   private ChannelListener<? super ConduitReadableMessageChannel> readListener;
/*     */   private ChannelListener<? super ConduitReadableMessageChannel> closeListener;
/*     */   
/*     */   public ConduitReadableMessageChannel(Configurable configurable, MessageSourceConduit conduit) {
/*  53 */     this.configurable = configurable;
/*  54 */     this.conduit = conduit;
/*  55 */     conduit.setReadReadyHandler(new ReadReadyHandler.ChannelListenerHandler<>(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageSourceConduit getConduit() {
/*  64 */     return this.conduit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConduit(MessageSourceConduit conduit) {
/*  73 */     this.conduit = conduit;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/*  77 */     return !this.conduit.isReadShutdown();
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super ConduitReadableMessageChannel> readListener) {
/*  81 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitReadableMessageChannel> getReadListener() {
/*  85 */     return this.readListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super ConduitReadableMessageChannel> closeListener) {
/*  89 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitReadableMessageChannel> getCloseListener() {
/*  93 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitReadableMessageChannel> getReadSetter() {
/*  97 */     return (ChannelListener.Setter<ConduitReadableMessageChannel>)new ReadListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitReadableMessageChannel> getCloseSetter() {
/* 101 */     return (ChannelListener.Setter<ConduitReadableMessageChannel>)new CloseListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 105 */     return this.conduit.getWorker();
/*     */   }
/*     */   
/*     */   public long receive(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 109 */     return this.conduit.receive(dsts, offset, length);
/*     */   }
/*     */   
/*     */   public long receive(ByteBuffer[] dsts) throws IOException {
/* 113 */     return this.conduit.receive(dsts, 0, dsts.length);
/*     */   }
/*     */   
/*     */   public int receive(ByteBuffer dst) throws IOException {
/* 117 */     return this.conduit.receive(dst);
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/* 121 */     this.conduit.suspendReads();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 125 */     this.conduit.resumeReads();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 129 */     return this.conduit.isReadResumed();
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 133 */     this.conduit.wakeupReads();
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 137 */     this.conduit.terminateReads();
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 141 */     this.conduit.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 145 */     this.conduit.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 150 */     return (XnioExecutor)this.conduit.getReadThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 154 */     return this.conduit.getReadThread();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 158 */     this.conduit.terminateReads();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 162 */     return this.configurable.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 166 */     return (T)this.configurable.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 170 */     return (T)this.configurable.setOption(option, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConduitReadableMessageChannel clone() {
/*     */     try {
/* 180 */       return (ConduitReadableMessageChannel)super.clone();
/* 181 */     } catch (CloneNotSupportedException e) {
/* 182 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\ConduitReadableMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */