/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.CloseListenerSettable;
/*     */ import org.xnio.channels.Configurable;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ 
/*     */ 
/*     */ public abstract class StreamConnection
/*     */   extends Connection
/*     */   implements CloseListenerSettable<StreamConnection>
/*     */ {
/*     */   private static final ChannelListener<? super StreamConnection> INVOKED_CLOSE_LISTENER_FLAG = connection -> {
/*     */     
/*     */     };
/*     */   private ConduitStreamSourceChannel sourceChannel;
/*     */   private ConduitStreamSinkChannel sinkChannel;
/*     */   private AtomicReference<ChannelListener<? super StreamConnection>> closeListener;
/*     */   
/*     */   protected StreamConnection(XnioIoThread thread) {
/*  55 */     super(thread);
/*  56 */     this.closeListener = new AtomicReference<>();
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super StreamConnection> listener) {
/*     */     ChannelListener<? super StreamConnection> currentListener;
/*     */     ChannelListener<? super StreamConnection> newListener;
/*     */     do {
/*  63 */       newListener = listener;
/*  64 */       currentListener = this.closeListener.get();
/*  65 */       if (currentListener == null)
/*     */         continue; 
/*  67 */       if (currentListener == INVOKED_CLOSE_LISTENER_FLAG) {
/*  68 */         ChannelListeners.invokeChannelListener(this, listener);
/*     */         return;
/*     */       } 
/*  71 */       newListener = mergeListeners(currentListener, listener);
/*     */     
/*     */     }
/*  74 */     while (!this.closeListener.compareAndSet(currentListener, newListener));
/*     */   }
/*     */   
/*     */   private final ChannelListener<? super StreamConnection> mergeListeners(ChannelListener<? super StreamConnection> listener1, ChannelListener<? super StreamConnection> listener2) {
/*  78 */     return channel -> {
/*     */         listener1.handleEvent(channel);
/*     */         listener2.handleEvent(channel);
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   protected void notifyReadClosed() {
/*     */     try {
/*  87 */       getSourceChannel().shutdownReads();
/*  88 */     } catch (IOException e) {
/*  89 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void notifyWriteClosed() {
/*     */     try {
/*  95 */       getSinkChannel().shutdownWrites();
/*  96 */     } catch (IOException e) {
/*  97 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public ChannelListener<? super StreamConnection> getCloseListener() {
/* 102 */     return this.closeListener.get();
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends StreamConnection> getCloseSetter() {
/* 106 */     return (ChannelListener.Setter<? extends StreamConnection>)new CloseListenerSettable.Setter(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setSourceConduit(StreamSourceConduit conduit) {
/* 115 */     this.sourceChannel = (conduit == null) ? null : new ConduitStreamSourceChannel((Configurable)this, conduit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setSinkConduit(StreamSinkConduit conduit) {
/* 124 */     this.sinkChannel = (conduit == null) ? null : new ConduitStreamSinkChannel((Configurable)this, conduit);
/*     */   }
/*     */ 
/*     */   
/*     */   void invokeCloseListener() {
/* 129 */     ChannelListener<? super StreamConnection> listener = this.closeListener.getAndSet(INVOKED_CLOSE_LISTENER_FLAG);
/* 130 */     ChannelListeners.invokeChannelListener(this, listener);
/*     */   }
/*     */   
/*     */   private static <T> T notNull(T orig) throws IllegalStateException {
/* 134 */     if (orig == null) {
/* 135 */       throw Messages.msg.channelNotAvailable();
/*     */     }
/* 137 */     return orig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConduitStreamSourceChannel getSourceChannel() {
/* 146 */     return notNull(this.sourceChannel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConduitStreamSinkChannel getSinkChannel() {
/* 155 */     return notNull(this.sinkChannel);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\StreamConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */