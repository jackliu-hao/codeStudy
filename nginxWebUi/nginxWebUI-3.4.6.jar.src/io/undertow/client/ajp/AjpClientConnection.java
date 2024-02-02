/*     */ package io.undertow.client.ajp;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.ClientResponse;
/*     */ import io.undertow.client.ClientStatistics;
/*     */ import io.undertow.client.UndertowClientMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.protocols.ajp.AbstractAjpClientStreamSourceChannel;
/*     */ import io.undertow.protocols.ajp.AjpClientChannel;
/*     */ import io.undertow.protocols.ajp.AjpClientRequestClientStreamSinkChannel;
/*     */ import io.undertow.protocols.ajp.AjpClientResponseStreamSourceChannel;
/*     */ import io.undertow.util.AbstractAttachable;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ class AjpClientConnection
/*     */   extends AbstractAttachable
/*     */   implements Closeable, ClientConnection
/*     */ {
/*  73 */   public final ChannelListener<AjpClientRequestClientStreamSinkChannel> requestFinishListener = new ChannelListener<AjpClientRequestClientStreamSinkChannel>()
/*     */     {
/*     */       public void handleEvent(AjpClientRequestClientStreamSinkChannel channel)
/*     */       {
/*  77 */         if (AjpClientConnection.this.currentRequest != null)
/*  78 */           AjpClientConnection.this.currentRequest.terminateRequest(); 
/*     */       }
/*     */     };
/*     */   
/*  82 */   public final ChannelListener<AjpClientResponseStreamSourceChannel> responseFinishedListener = new ChannelListener<AjpClientResponseStreamSourceChannel>()
/*     */     {
/*     */       public void handleEvent(AjpClientResponseStreamSourceChannel channel) {
/*  85 */         if (AjpClientConnection.this.currentRequest != null) {
/*  86 */           AjpClientConnection.this.currentRequest.terminateResponse();
/*     */         }
/*     */       }
/*     */     };
/*     */   
/*  91 */   private static final Logger log = Logger.getLogger(AjpClientConnection.class);
/*     */   
/*  93 */   private final Deque<AjpClientExchange> pendingQueue = new ArrayDeque<>();
/*     */   
/*     */   private AjpClientExchange currentRequest;
/*     */   
/*     */   private final OptionMap options;
/*     */   
/*     */   private final AjpClientChannel connection;
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*     */   
/*     */   private static final int UPGRADED = 268435456;
/*     */   private static final int UPGRADE_REQUESTED = 536870912;
/*     */   private static final int CLOSE_REQ = 1073741824;
/*     */   private static final int CLOSED = -2147483648;
/*     */   private int state;
/* 108 */   private final ChannelListener.SimpleSetter<AjpClientConnection> closeSetter = new ChannelListener.SimpleSetter();
/*     */   private final ClientStatistics clientStatistics;
/* 110 */   private final List<ChannelListener<ClientConnection>> closeListeners = new CopyOnWriteArrayList<>();
/*     */   
/*     */   AjpClientConnection(AjpClientChannel connection, OptionMap options, ByteBufferPool bufferPool, ClientStatistics clientStatistics) {
/* 113 */     this.clientStatistics = clientStatistics;
/* 114 */     this.options = options;
/* 115 */     this.connection = connection;
/* 116 */     this.bufferPool = bufferPool;
/*     */     
/* 118 */     connection.addCloseTask(new ChannelListener<AjpClientChannel>()
/*     */         {
/*     */           public void handleEvent(AjpClientChannel channel) {
/* 121 */             AjpClientConnection.log.debugf("connection to %s closed", AjpClientConnection.this.getPeerAddress());
/* 122 */             AjpClientConnection ajpClientConnection = AjpClientConnection.this; ajpClientConnection.state = ajpClientConnection.state | Integer.MIN_VALUE;
/* 123 */             ChannelListeners.invokeChannelListener((Channel)AjpClientConnection.this, AjpClientConnection.this.closeSetter.get());
/* 124 */             for (ChannelListener<ClientConnection> listener : (Iterable<ChannelListener<ClientConnection>>)AjpClientConnection.this.closeListeners) {
/* 125 */               listener.handleEvent((Channel)AjpClientConnection.this);
/*     */             }
/* 127 */             AjpClientExchange pending = AjpClientConnection.this.pendingQueue.poll();
/* 128 */             while (pending != null) {
/* 129 */               pending.setFailed(new ClosedChannelException());
/* 130 */               pending = AjpClientConnection.this.pendingQueue.poll();
/*     */             } 
/* 132 */             if (AjpClientConnection.this.currentRequest != null) {
/* 133 */               AjpClientConnection.this.currentRequest.setFailed(new ClosedChannelException());
/* 134 */               AjpClientConnection.this.currentRequest = null;
/*     */             } 
/*     */           }
/*     */         });
/* 138 */     connection.getReceiveSetter().set(new ClientReceiveListener());
/* 139 */     connection.resumeReceives();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 144 */     return this.bufferPool;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/* 150 */     return this.connection.getPeerAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 155 */     return (A)this.connection.getPeerAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AjpClientConnection> getCloseSetter() {
/* 160 */     return (ChannelListener.Setter<? extends AjpClientConnection>)this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 165 */     return this.connection.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 170 */     return (A)this.connection.getLocalAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 175 */     return this.connection.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 180 */     return this.connection.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 185 */     return this.connection.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 190 */     return this.connection.supportsOption(option);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 196 */     return (T)this.connection.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 201 */     return (T)this.connection.setOption(option, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUpgraded() {
/* 206 */     return Bits.anyAreSet(this.state, 805306368);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPushSupported() {
/* 211 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMultiplexingSupported() {
/* 216 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientStatistics getStatistics() {
/* 221 */     return this.clientStatistics;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUpgradeSupported() {
/* 226 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCloseListener(ChannelListener<ClientConnection> listener) {
/* 231 */     this.closeListeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendRequest(ClientRequest request, ClientCallback<ClientExchange> clientCallback) {
/* 236 */     if (Bits.anyAreSet(this.state, -268435456)) {
/* 237 */       clientCallback.failed(UndertowClientMessages.MESSAGES.invalidConnectionState());
/*     */       return;
/*     */     } 
/* 240 */     AjpClientExchange ajpClientExchange = new AjpClientExchange(clientCallback, request, this);
/* 241 */     if (this.currentRequest == null) {
/* 242 */       initiateRequest(ajpClientExchange);
/*     */     } else {
/* 244 */       this.pendingQueue.add(ajpClientExchange);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPingSupported() {
/* 250 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPing(ClientConnection.PingListener listener, long timeout, TimeUnit timeUnit) {
/* 255 */     this.connection.sendPing(listener, timeout, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   private void initiateRequest(AjpClientExchange ajpClientExchange) {
/* 260 */     this.currentRequest = ajpClientExchange;
/* 261 */     ClientRequest request = ajpClientExchange.getRequest();
/*     */     
/* 263 */     String connectionString = request.getRequestHeaders().getFirst(Headers.CONNECTION);
/* 264 */     if (connectionString != null) {
/* 265 */       if (Headers.CLOSE.equalToString(connectionString)) {
/* 266 */         this.state |= 0x40000000;
/*     */       }
/* 268 */     } else if (request.getProtocol() != Protocols.HTTP_1_1) {
/* 269 */       this.state |= 0x40000000;
/*     */     } 
/* 271 */     if (request.getRequestHeaders().contains(Headers.UPGRADE)) {
/* 272 */       this.state |= 0x20000000;
/*     */     }
/*     */     
/* 275 */     long length = 0L;
/* 276 */     String fixedLengthString = request.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/* 277 */     String transferEncodingString = request.getRequestHeaders().getLast(Headers.TRANSFER_ENCODING);
/*     */     
/* 279 */     if (fixedLengthString != null) {
/* 280 */       length = Long.parseLong(fixedLengthString);
/* 281 */     } else if (transferEncodingString != null) {
/* 282 */       length = -1L;
/*     */     } 
/*     */     
/* 285 */     AjpClientRequestClientStreamSinkChannel sinkChannel = this.connection.sendRequest(request.getMethod(), request.getPath(), request.getProtocol(), request.getRequestHeaders(), (Attachable)request, this.requestFinishListener);
/* 286 */     this.currentRequest.setRequestChannel(sinkChannel);
/*     */     
/* 288 */     ajpClientExchange.invokeReadReadyCallback(ajpClientExchange);
/* 289 */     if (length == 0L) {
/*     */       
/*     */       try {
/*     */         
/* 293 */         sinkChannel.shutdownWrites();
/* 294 */         if (!sinkChannel.flush()) {
/* 295 */           handleFailedFlush(sinkChannel);
/*     */         }
/* 297 */       } catch (Throwable t) {
/* 298 */         handleError((t instanceof IOException) ? (IOException)t : new IOException(t));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleFailedFlush(AjpClientRequestClientStreamSinkChannel sinkChannel) {
/* 304 */     sinkChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<StreamSinkChannel>()
/*     */           {
/*     */             public void handleException(StreamSinkChannel channel, IOException exception) {
/* 307 */               AjpClientConnection.this.handleError(exception);
/*     */             }
/*     */           }));
/* 310 */     sinkChannel.resumeWrites();
/*     */   }
/*     */   
/*     */   private void handleError(IOException exception) {
/* 314 */     this.currentRequest.setFailed(exception);
/* 315 */     IoUtils.safeClose((Closeable)this.connection);
/*     */   }
/*     */   
/*     */   public StreamConnection performUpgrade() throws IOException {
/* 319 */     throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 323 */     log.debugf("close called on connection to %s", getPeerAddress());
/* 324 */     if (Bits.anyAreSet(this.state, -2147483648)) {
/*     */       return;
/*     */     }
/* 327 */     this.state |= 0xC0000000;
/* 328 */     this.connection.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void requestDone() {
/* 335 */     this.currentRequest = null;
/*     */     
/* 337 */     if (Bits.anyAreSet(this.state, 1073741824)) {
/* 338 */       IoUtils.safeClose((Closeable)this.connection);
/* 339 */     } else if (Bits.anyAreSet(this.state, 536870912)) {
/* 340 */       IoUtils.safeClose((Closeable)this.connection);
/*     */       
/*     */       return;
/*     */     } 
/* 344 */     AjpClientExchange next = this.pendingQueue.poll();
/*     */     
/* 346 */     if (next != null) {
/* 347 */       initiateRequest(next);
/*     */     }
/*     */   }
/*     */   
/*     */   public void requestClose() {
/* 352 */     this.state |= 0x40000000;
/*     */   }
/*     */   
/*     */   class ClientReceiveListener
/*     */     implements ChannelListener<AjpClientChannel>
/*     */   {
/*     */     public void handleEvent(AjpClientChannel channel) {
/*     */       try {
/* 360 */         AbstractAjpClientStreamSourceChannel result = (AbstractAjpClientStreamSourceChannel)channel.receive();
/* 361 */         if (result == null) {
/* 362 */           if (!channel.isOpen())
/*     */           {
/*     */             
/* 365 */             AjpClientConnection.this.getIoThread().execute(new Runnable()
/*     */                 {
/*     */                   public void run() {
/* 368 */                     if (AjpClientConnection.this.currentRequest != null) {
/* 369 */                       AjpClientConnection.this.currentRequest.setFailed(new ClosedChannelException());
/*     */                     }
/*     */                   }
/*     */                 });
/*     */           }
/*     */           
/*     */           return;
/*     */         } 
/* 377 */         if (result instanceof AjpClientResponseStreamSourceChannel) {
/* 378 */           AjpClientResponseStreamSourceChannel response = (AjpClientResponseStreamSourceChannel)result;
/* 379 */           response.setFinishListener(AjpClientConnection.this.responseFinishedListener);
/* 380 */           ClientResponse cr = new ClientResponse(response.getStatusCode(), response.getReasonPhrase(), AjpClientConnection.this.currentRequest.getRequest().getProtocol(), response.getHeaders());
/* 381 */           if (response.getStatusCode() == 100) {
/* 382 */             AjpClientConnection.this.currentRequest.setContinueResponse(cr);
/*     */           } else {
/* 384 */             AjpClientConnection.this.currentRequest.setResponseChannel(response);
/* 385 */             AjpClientConnection.this.currentRequest.setResponse(cr);
/*     */           } 
/*     */         } else {
/*     */           
/* 389 */           Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
/*     */         }
/*     */       
/* 392 */       } catch (Throwable e) {
/* 393 */         UndertowLogger.CLIENT_LOGGER.exceptionProcessingRequest(e);
/* 394 */         IoUtils.safeClose((Closeable)AjpClientConnection.this.connection);
/* 395 */         if (AjpClientConnection.this.currentRequest != null)
/* 396 */           AjpClientConnection.this.currentRequest.setFailed((e instanceof IOException) ? (IOException)e : new IOException(e)); 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ajp\AjpClientConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */