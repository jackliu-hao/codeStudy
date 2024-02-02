/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
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
/*     */ public abstract class AbstractServerConnection
/*     */   extends ServerConnection
/*     */ {
/*     */   protected final StreamConnection channel;
/*     */   protected final CloseSetter closeSetter;
/*     */   protected final ByteBufferPool bufferPool;
/*     */   protected final HttpHandler rootHandler;
/*     */   protected final OptionMap undertowOptions;
/*     */   protected final StreamSourceConduit originalSourceConduit;
/*     */   protected final StreamSinkConduit originalSinkConduit;
/*  52 */   protected final List<ServerConnection.CloseListener> closeListeners = new LinkedList<>();
/*     */ 
/*     */   
/*     */   protected HttpServerExchange current;
/*     */ 
/*     */   
/*     */   private final int bufferSize;
/*     */   
/*     */   private XnioBufferPoolAdaptor poolAdaptor;
/*     */   
/*     */   protected PooledByteBuffer extraBytes;
/*     */ 
/*     */   
/*     */   public AbstractServerConnection(StreamConnection channel, ByteBufferPool bufferPool, HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize) {
/*  66 */     this.channel = channel;
/*  67 */     this.bufferPool = bufferPool;
/*  68 */     this.rootHandler = rootHandler;
/*  69 */     this.undertowOptions = undertowOptions;
/*  70 */     this.bufferSize = bufferSize;
/*  71 */     this.closeSetter = new CloseSetter();
/*  72 */     if (channel != null) {
/*  73 */       this.originalSinkConduit = channel.getSinkChannel().getConduit();
/*  74 */       this.originalSourceConduit = channel.getSourceChannel().getConduit();
/*  75 */       channel.setCloseListener(this.closeSetter);
/*     */     } else {
/*  77 */       this.originalSinkConduit = null;
/*  78 */       this.originalSourceConduit = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Pool<ByteBuffer> getBufferPool() {
/*  84 */     if (this.poolAdaptor == null) {
/*  85 */       this.poolAdaptor = new XnioBufferPoolAdaptor(getByteBufferPool());
/*     */     }
/*  87 */     return this.poolAdaptor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpHandler getRootHandler() {
/*  96 */     return this.rootHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufferPool getByteBufferPool() {
/* 106 */     return this.bufferPool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamConnection getChannel() {
/* 115 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<ServerConnection> getCloseSetter() {
/* 120 */     return this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 125 */     return this.channel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 130 */     if (this.channel == null) {
/* 131 */       return null;
/*     */     }
/* 133 */     return this.channel.getIoThread();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 139 */     return this.channel.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 144 */     return this.channel.supportsOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 149 */     return (T)this.channel.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 154 */     return (T)this.channel.setOption(option, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 159 */     this.channel.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/* 164 */     return this.channel.getPeerAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 169 */     return (A)this.channel.getPeerAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 174 */     return this.channel.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 179 */     return (A)this.channel.getLocalAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getUndertowOptions() {
/* 184 */     return this.undertowOptions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/* 192 */     return this.bufferSize;
/*     */   }
/*     */   
/*     */   public PooledByteBuffer getExtraBytes() {
/* 196 */     if (this.extraBytes != null && !this.extraBytes.getBuffer().hasRemaining()) {
/* 197 */       this.extraBytes.close();
/* 198 */       this.extraBytes = null;
/* 199 */       return null;
/*     */     } 
/* 201 */     return this.extraBytes;
/*     */   }
/*     */   
/*     */   public void setExtraBytes(PooledByteBuffer extraBytes) {
/* 205 */     this.extraBytes = extraBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamSourceConduit getOriginalSourceConduit() {
/* 212 */     return this.originalSourceConduit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamSinkConduit getOriginalSinkConduit() {
/* 219 */     return this.originalSinkConduit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConduitState resetChannel() {
/* 230 */     ConduitState ret = new ConduitState(this.channel.getSinkChannel().getConduit(), this.channel.getSourceChannel().getConduit());
/* 231 */     this.channel.getSinkChannel().setConduit(this.originalSinkConduit);
/* 232 */     this.channel.getSourceChannel().setConduit(this.originalSourceConduit);
/* 233 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearChannel() {
/* 241 */     this.channel.getSinkChannel().setConduit(this.originalSinkConduit);
/* 242 */     this.channel.getSourceChannel().setConduit(this.originalSourceConduit);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void restoreChannel(ConduitState state) {
/* 251 */     this.channel.getSinkChannel().setConduit(state.sink);
/* 252 */     this.channel.getSourceChannel().setConduit(state.source);
/*     */   }
/*     */   
/*     */   public static class ConduitState {
/*     */     final StreamSinkConduit sink;
/*     */     final StreamSourceConduit source;
/*     */     
/*     */     private ConduitState(StreamSinkConduit sink, StreamSourceConduit source) {
/* 260 */       this.sink = sink;
/* 261 */       this.source = source;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static StreamSinkConduit sink(ConduitState state) {
/* 266 */     return state.sink;
/*     */   }
/*     */   
/*     */   protected static StreamSourceConduit source(ConduitState state) {
/* 270 */     return state.source;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCloseListener(ServerConnection.CloseListener listener) {
/* 275 */     this.closeListeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   protected ConduitStreamSinkChannel getSinkChannel() {
/* 280 */     return this.channel.getSinkChannel();
/*     */   }
/*     */ 
/*     */   
/*     */   protected ConduitStreamSourceChannel getSourceChannel() {
/* 285 */     return this.channel.getSourceChannel();
/*     */   }
/*     */   
/*     */   protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
/* 289 */     throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*     */   }
/*     */   
/*     */   protected void maxEntitySizeUpdated(HttpServerExchange exchange) {}
/*     */   
/*     */   private class CloseSetter
/*     */     implements ChannelListener.Setter<ServerConnection>, ChannelListener<StreamConnection>
/*     */   {
/*     */     private ChannelListener<? super ServerConnection> listener;
/*     */     
/*     */     private CloseSetter() {}
/*     */     
/*     */     public void set(ChannelListener<? super ServerConnection> listener) {
/* 302 */       this.listener = listener;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleEvent(StreamConnection channel) {
/*     */       try {
/* 308 */         for (ServerConnection.CloseListener l : AbstractServerConnection.this.closeListeners) {
/*     */           try {
/* 310 */             l.closed(AbstractServerConnection.this);
/* 311 */           } catch (Throwable e) {
/* 312 */             UndertowLogger.REQUEST_LOGGER.exceptionInvokingCloseListener(l, e);
/*     */           } 
/*     */         } 
/* 315 */         if (AbstractServerConnection.this.current != null) {
/* 316 */           AbstractServerConnection.this.current.endExchange();
/*     */         }
/* 318 */         ChannelListeners.invokeChannelListener((Channel)AbstractServerConnection.this, this.listener);
/*     */       } finally {
/* 320 */         if (AbstractServerConnection.this.extraBytes != null) {
/* 321 */           AbstractServerConnection.this.extraBytes.close();
/* 322 */           AbstractServerConnection.this.extraBytes = null;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\AbstractServerConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */