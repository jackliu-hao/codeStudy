/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.Set;
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
/*     */ final class NioSocketStreamConnection
/*     */   extends AbstractNioStreamConnection
/*     */ {
/*     */   private final ChannelClosed closedHandle;
/*     */   private final NioSocketConduit conduit;
/*     */   
/*     */   NioSocketStreamConnection(WorkerThread workerThread, SelectionKey key, ChannelClosed closedHandle) {
/*  41 */     super(workerThread);
/*  42 */     this.conduit = new NioSocketConduit(workerThread, key, this);
/*  43 */     key.attach(this.conduit);
/*  44 */     this.closedHandle = closedHandle;
/*  45 */     setSinkConduit(this.conduit);
/*  46 */     setSourceConduit(this.conduit);
/*     */   }
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/*  50 */     Socket socket = this.conduit.getSocketChannel().socket();
/*  51 */     return new InetSocketAddress(socket.getInetAddress(), socket.getPort());
/*     */   }
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/*  55 */     Socket socket = this.conduit.getSocketChannel().socket();
/*  56 */     return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
/*     */   }
/*     */   
/*  59 */   private static final Set<Option<?>> OPTIONS = Option.setBuilder()
/*  60 */     .add(Options.CLOSE_ABORT)
/*  61 */     .add(Options.IP_TRAFFIC_CLASS)
/*  62 */     .add(Options.KEEP_ALIVE)
/*  63 */     .add(Options.READ_TIMEOUT)
/*  64 */     .add(Options.RECEIVE_BUFFER)
/*  65 */     .add(Options.SEND_BUFFER)
/*  66 */     .add(Options.TCP_NODELAY)
/*  67 */     .add(Options.TCP_OOB_INLINE)
/*  68 */     .add(Options.WRITE_TIMEOUT)
/*  69 */     .create();
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/*  72 */     return OPTIONS.contains(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/*  76 */     if (option == Options.CLOSE_ABORT)
/*  77 */       return (T)option.cast(Boolean.valueOf((this.conduit.getSocketChannel().socket().getSoLinger() == 0))); 
/*  78 */     if (option == Options.IP_TRAFFIC_CLASS)
/*  79 */       return (T)option.cast(Integer.valueOf(this.conduit.getSocketChannel().socket().getTrafficClass())); 
/*  80 */     if (option == Options.KEEP_ALIVE)
/*  81 */       return (T)option.cast(Boolean.valueOf(this.conduit.getSocketChannel().socket().getKeepAlive())); 
/*  82 */     if (option == Options.READ_TIMEOUT)
/*  83 */       return (T)option.cast(Integer.valueOf(this.conduit.getReadTimeout())); 
/*  84 */     if (option == Options.RECEIVE_BUFFER)
/*  85 */       return (T)option.cast(Integer.valueOf(this.conduit.getSocketChannel().socket().getReceiveBufferSize())); 
/*  86 */     if (option == Options.SEND_BUFFER)
/*  87 */       return (T)option.cast(Integer.valueOf(this.conduit.getSocketChannel().socket().getSendBufferSize())); 
/*  88 */     if (option == Options.TCP_NODELAY)
/*  89 */       return (T)option.cast(Boolean.valueOf(this.conduit.getSocketChannel().socket().getTcpNoDelay())); 
/*  90 */     if (option == Options.TCP_OOB_INLINE)
/*  91 */       return (T)option.cast(Boolean.valueOf(this.conduit.getSocketChannel().socket().getOOBInline())); 
/*  92 */     if (option == Options.WRITE_TIMEOUT) {
/*  93 */       return (T)option.cast(Integer.valueOf(this.conduit.getWriteTimeout()));
/*     */     }
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/*     */     T result;
/* 101 */     if (option == Options.CLOSE_ABORT) {
/* 102 */       result = (T)option.cast(Boolean.valueOf((this.conduit.getSocketChannel().socket().getSoLinger() == 0)));
/* 103 */       this.conduit.getSocketChannel().socket().setSoLinger(((Boolean)Options.CLOSE_ABORT.cast(value, Boolean.FALSE)).booleanValue(), 0);
/* 104 */     } else if (option == Options.IP_TRAFFIC_CLASS) {
/* 105 */       result = (T)option.cast(Integer.valueOf(this.conduit.getSocketChannel().socket().getTrafficClass()));
/* 106 */       this.conduit.getSocketChannel().socket().setTrafficClass(((Integer)Options.IP_TRAFFIC_CLASS.cast(value)).intValue());
/* 107 */     } else if (option == Options.KEEP_ALIVE) {
/* 108 */       result = (T)option.cast(Boolean.valueOf(this.conduit.getSocketChannel().socket().getKeepAlive()));
/* 109 */       this.conduit.getSocketChannel().socket().setKeepAlive(((Boolean)Options.KEEP_ALIVE.cast(value, Boolean.FALSE)).booleanValue());
/* 110 */     } else if (option == Options.READ_TIMEOUT) {
/* 111 */       result = (T)option.cast(Integer.valueOf(this.conduit.getAndSetReadTimeout((value == null) ? 0 : ((Integer)Options.READ_TIMEOUT.cast(value)).intValue())));
/* 112 */     } else if (option == Options.RECEIVE_BUFFER) {
/* 113 */       result = (T)option.cast(Integer.valueOf(this.conduit.getSocketChannel().socket().getReceiveBufferSize()));
/* 114 */       this.conduit.getSocketChannel().socket().setReceiveBufferSize(((Integer)Options.RECEIVE_BUFFER.cast(value)).intValue());
/* 115 */     } else if (option == Options.SEND_BUFFER) {
/* 116 */       result = (T)option.cast(Integer.valueOf(this.conduit.getSocketChannel().socket().getSendBufferSize()));
/* 117 */       this.conduit.getSocketChannel().socket().setSendBufferSize(((Integer)Options.SEND_BUFFER.cast(value)).intValue());
/* 118 */     } else if (option == Options.TCP_NODELAY) {
/* 119 */       result = (T)option.cast(Boolean.valueOf(this.conduit.getSocketChannel().socket().getTcpNoDelay()));
/* 120 */       this.conduit.getSocketChannel().socket().setTcpNoDelay(((Boolean)Options.TCP_NODELAY.cast(value, Boolean.FALSE)).booleanValue());
/* 121 */     } else if (option == Options.TCP_OOB_INLINE) {
/* 122 */       result = (T)option.cast(Boolean.valueOf(this.conduit.getSocketChannel().socket().getOOBInline()));
/* 123 */       this.conduit.getSocketChannel().socket().setOOBInline(((Boolean)Options.TCP_OOB_INLINE.cast(value, Boolean.FALSE)).booleanValue());
/* 124 */     } else if (option == Options.WRITE_TIMEOUT) {
/* 125 */       result = (T)option.cast(Integer.valueOf(this.conduit.getAndSetWriteTimeout((value == null) ? 0 : ((Integer)Options.WRITE_TIMEOUT.cast(value)).intValue())));
/*     */     } else {
/* 127 */       return null;
/*     */     } 
/* 129 */     return result;
/*     */   }
/*     */   
/*     */   protected void closeAction() throws IOException {
/*     */     
/* 134 */     try { this.conduit.cancelKey(false);
/* 135 */       this.conduit.getSocketChannel().close(); }
/* 136 */     catch (ClosedChannelException closedChannelException) {  }
/*     */     finally
/* 138 */     { ChannelClosed closedHandle = this.closedHandle;
/* 139 */       if (closedHandle != null) closedHandle.channelClosed();  }
/*     */   
/*     */   }
/*     */   
/*     */   protected void notifyWriteClosed() {
/* 144 */     this.conduit.writeTerminated();
/* 145 */     super.notifyWriteClosed();
/*     */   }
/*     */   
/*     */   protected void notifyReadClosed() {
/* 149 */     this.conduit.readTerminated();
/* 150 */     super.notifyReadClosed();
/*     */   }
/*     */   
/*     */   SocketChannel getChannel() {
/* 154 */     return this.conduit.getSocketChannel();
/*     */   }
/*     */   
/*     */   NioSocketConduit getConduit() {
/* 158 */     return this.conduit;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\NioSocketStreamConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */