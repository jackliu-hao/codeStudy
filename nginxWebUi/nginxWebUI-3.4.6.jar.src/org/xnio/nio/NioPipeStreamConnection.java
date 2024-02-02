/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.Pipe;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.Options;
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
/*     */ final class NioPipeStreamConnection
/*     */   extends AbstractNioStreamConnection
/*     */ {
/*     */   private final Pipe.SourceChannel sourceChannel;
/*     */   private final Pipe.SinkChannel sinkChannel;
/*     */   private final NioPipeSourceConduit sourceConduit;
/*     */   private final NioPipeSinkConduit sinkConduit;
/*     */   
/*     */   NioPipeStreamConnection(WorkerThread workerThread, SelectionKey sourceKey, SelectionKey sinkKey) {
/*  42 */     super(workerThread);
/*  43 */     if (sourceKey != null) {
/*  44 */       setSourceConduit(this.sourceConduit = new NioPipeSourceConduit(workerThread, sourceKey, this));
/*  45 */       sourceKey.attach(this.sourceConduit);
/*  46 */       this.sourceChannel = (Pipe.SourceChannel)sourceKey.channel();
/*     */     } else {
/*  48 */       this.sourceConduit = null;
/*  49 */       this.sourceChannel = null;
/*     */     } 
/*  51 */     if (sinkKey != null) {
/*  52 */       setSinkConduit(this.sinkConduit = new NioPipeSinkConduit(workerThread, sinkKey, this));
/*  53 */       sinkKey.attach(this.sinkConduit);
/*  54 */       this.sinkChannel = (Pipe.SinkChannel)sinkKey.channel();
/*     */     } else {
/*  56 */       this.sinkConduit = null;
/*  57 */       this.sinkChannel = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/*  62 */     return null;
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/*  66 */     return null;
/*     */   }
/*     */   
/*     */   protected boolean readClosed() {
/*  70 */     return super.readClosed();
/*     */   }
/*     */   
/*     */   protected boolean writeClosed() {
/*  74 */     return super.writeClosed();
/*     */   }
/*     */   
/*     */   protected void notifyWriteClosed() {
/*  78 */     NioPipeSinkConduit conduit = this.sinkConduit;
/*  79 */     if (conduit != null) conduit.writeTerminated(); 
/*     */   }
/*     */   
/*     */   protected void notifyReadClosed() {
/*  83 */     NioPipeSourceConduit conduit = this.sourceConduit;
/*  84 */     if (conduit != null) conduit.readTerminated(); 
/*     */   }
/*     */   
/*     */   private void cancelKey(NioHandle handle) {
/*  88 */     if (handle != null) handle.cancelKey(false); 
/*     */   }
/*     */   
/*     */   private void closeChannel(Channel channel) throws IOException {
/*  92 */     if (channel != null)
/*  93 */       try { channel.close(); }
/*  94 */       catch (ClosedChannelException closedChannelException) {} 
/*     */   }
/*     */   
/*     */   protected void closeAction() throws IOException {
/*     */     try {
/*  99 */       cancelKey(this.sourceConduit);
/* 100 */       cancelKey(this.sinkConduit);
/* 101 */       closeChannel(this.sourceChannel);
/* 102 */       closeChannel(this.sinkChannel);
/*     */     } finally {
/* 104 */       IoUtils.safeClose(this.sourceChannel);
/* 105 */       IoUtils.safeClose(this.sinkChannel);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 110 */     return ((option == Options.READ_TIMEOUT && this.sourceConduit != null) || (option == Options.WRITE_TIMEOUT && this.sinkConduit != null));
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 114 */     if (option == Options.READ_TIMEOUT) {
/* 115 */       NioPipeSourceConduit conduit = this.sourceConduit;
/* 116 */       return (conduit == null) ? null : (T)option.cast(Integer.valueOf(conduit.getReadTimeout()));
/* 117 */     }  if (option == Options.WRITE_TIMEOUT) {
/* 118 */       NioPipeSinkConduit conduit = this.sinkConduit;
/* 119 */       return (conduit == null) ? null : (T)option.cast(Integer.valueOf(conduit.getWriteTimeout()));
/*     */     } 
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/*     */     T result;
/* 127 */     if (option == Options.READ_TIMEOUT) {
/* 128 */       NioPipeSourceConduit conduit = this.sourceConduit;
/* 129 */       result = (conduit == null) ? null : (T)option.cast(Integer.valueOf(conduit.getAndSetReadTimeout((value == null) ? 0 : ((Integer)Options.READ_TIMEOUT.cast(value)).intValue())));
/* 130 */     } else if (option == Options.WRITE_TIMEOUT) {
/* 131 */       NioPipeSinkConduit conduit = this.sinkConduit;
/* 132 */       result = (conduit == null) ? null : (T)option.cast(Integer.valueOf(conduit.getAndSetWriteTimeout((value == null) ? 0 : ((Integer)Options.WRITE_TIMEOUT.cast(value)).intValue())));
/*     */     } else {
/* 134 */       return null;
/*     */     } 
/* 136 */     return result;
/*     */   }
/*     */   
/*     */   Pipe.SourceChannel getSourcePipeChannel() {
/* 140 */     return this.sourceChannel;
/*     */   }
/*     */   
/*     */   Pipe.SinkChannel getSinkPipeChannel() {
/* 144 */     return this.sinkChannel;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioPipeStreamConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */