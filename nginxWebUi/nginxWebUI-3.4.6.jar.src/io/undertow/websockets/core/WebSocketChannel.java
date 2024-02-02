/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import io.undertow.conduits.IdleTimeoutConduit;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedChannel;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
/*     */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*     */ import io.undertow.server.protocol.framed.FramePriority;
/*     */ import io.undertow.websockets.extensions.ExtensionFunction;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*     */ public abstract class WebSocketChannel
/*     */   extends AbstractFramedChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel>
/*     */ {
/*     */   private final boolean client;
/*     */   private final WebSocketVersion version;
/*     */   private final String wsUrl;
/*     */   private volatile boolean closeFrameReceived;
/*     */   private volatile boolean closeFrameSent;
/*     */   private volatile boolean closeInitiatedByRemotePeer;
/*  64 */   private volatile int closeCode = -1;
/*     */   
/*     */   private volatile String closeReason;
/*     */   
/*     */   private final String subProtocol;
/*     */   
/*     */   protected final boolean extensionsSupported;
/*     */   
/*     */   protected final ExtensionFunction extensionFunction;
/*     */   
/*     */   protected final boolean hasReservedOpCode;
/*     */   private volatile PartialFrame partialFrame;
/*  76 */   private final Map<String, Object> attributes = Collections.synchronizedMap(new HashMap<>());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected StreamSourceFrameChannel fragmentedChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Set<WebSocketChannel> peerConnections;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WebSocketChannel(StreamConnection connectedStreamChannel, ByteBufferPool bufferPool, WebSocketVersion version, String wsUrl, String subProtocol, boolean client, boolean extensionsSupported, final ExtensionFunction extensionFunction, Set<WebSocketChannel> peerConnections, OptionMap options) {
/*  98 */     super(connectedStreamChannel, bufferPool, new WebSocketFramePriority(), null, options);
/*  99 */     this.client = client;
/* 100 */     this.version = version;
/* 101 */     this.wsUrl = wsUrl;
/* 102 */     this.extensionsSupported = extensionsSupported;
/* 103 */     this.extensionFunction = extensionFunction;
/* 104 */     this.hasReservedOpCode = extensionFunction.hasExtensionOpCode();
/* 105 */     this.subProtocol = subProtocol;
/* 106 */     this.peerConnections = peerConnections;
/* 107 */     addCloseTask(new ChannelListener<WebSocketChannel>()
/*     */         {
/*     */           public void handleEvent(WebSocketChannel channel) {
/* 110 */             extensionFunction.dispose();
/* 111 */             WebSocketChannel.this.peerConnections.remove(WebSocketChannel.this);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected Collection<AbstractFramedStreamSourceChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel>> getReceivers() {
/* 118 */     if (this.fragmentedChannel == null) {
/* 119 */       return Collections.emptyList();
/*     */     }
/* 121 */     return Collections.singleton(this.fragmentedChannel);
/*     */   }
/*     */ 
/*     */   
/*     */   protected IdleTimeoutConduit createIdleTimeoutChannel(StreamConnection connectedStreamChannel) {
/* 126 */     return new IdleTimeoutConduit(connectedStreamChannel)
/*     */       {
/*     */         protected void doClose() {
/* 129 */           WebSockets.sendClose(1001, (String)null, WebSocketChannel.this, (WebSocketCallback<Void>)null);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isLastFrameSent() {
/* 136 */     return this.closeFrameSent;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isLastFrameReceived() {
/* 141 */     return this.closeFrameReceived;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void markReadsBroken(Throwable cause) {
/* 146 */     super.markReadsBroken(cause);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void lastDataRead() {
/* 151 */     if (!this.closeFrameReceived && !this.closeFrameSent) {
/*     */ 
/*     */ 
/*     */       
/* 155 */       this.closeFrameReceived = true;
/*     */       try {
/* 157 */         sendClose();
/* 158 */       } catch (IOException e) {
/* 159 */         IoUtils.safeClose((Closeable)this);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean isReadsBroken() {
/* 165 */     return super.isReadsBroken();
/*     */   }
/*     */ 
/*     */   
/*     */   protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException {
/* 170 */     if (this.partialFrame == null) {
/* 171 */       this.partialFrame = receiveFrame();
/*     */     }
/*     */     try {
/* 174 */       this.partialFrame.handle(data);
/* 175 */     } catch (WebSocketException e) {
/*     */ 
/*     */       
/* 178 */       WebSockets.sendClose((new CloseMessage(1002, e.getMessage())).toByteBuffer(), this, (WebSocketCallback<Void>)null);
/* 179 */       markReadsBroken(e);
/* 180 */       if (WebSocketLogger.REQUEST_LOGGER.isDebugEnabled()) {
/* 181 */         WebSocketLogger.REQUEST_LOGGER.debugf(e, "receive failed due to Exception", new Object[0]);
/*     */       }
/*     */       
/* 184 */       throw new IOException(e);
/*     */     } 
/* 186 */     if (this.partialFrame.isDone()) {
/* 187 */       PartialFrame p = this.partialFrame;
/* 188 */       this.partialFrame = null;
/* 189 */       return p;
/*     */     } 
/* 191 */     return null;
/*     */   }
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
/*     */   protected StreamSourceFrameChannel createChannel(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) {
/* 206 */     PartialFrame partialFrame = (PartialFrame)frameHeaderData;
/* 207 */     StreamSourceFrameChannel channel = partialFrame.getChannel(frameData);
/* 208 */     if (channel.getType() == WebSocketFrameType.CLOSE) {
/* 209 */       if (!this.closeFrameSent) {
/* 210 */         this.closeInitiatedByRemotePeer = true;
/*     */       }
/* 212 */       this.closeFrameReceived = true;
/*     */     } 
/* 214 */     return channel;
/*     */   }
/*     */   
/*     */   public final boolean setAttribute(String key, Object value) {
/* 218 */     if (value == null) {
/* 219 */       return (this.attributes.remove(key) != null);
/*     */     }
/* 221 */     return (this.attributes.put(key, value) == null);
/*     */   }
/*     */ 
/*     */   
/*     */   public final Object getAttribute(String key) {
/* 226 */     return this.attributes.get(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean areExtensionsSupported() {
/* 233 */     return this.extensionsSupported;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleBrokenSourceChannel(Throwable e) {
/* 238 */     if (e instanceof java.io.UnsupportedEncodingException) {
/* 239 */       getFramePriority().immediateCloseFrame();
/* 240 */       WebSockets.sendClose((new CloseMessage(1007, e.getMessage())).toByteBuffer(), this, (WebSocketCallback<Void>)null);
/* 241 */     } else if (e instanceof WebSocketInvalidCloseCodeException) {
/* 242 */       WebSockets.sendClose((new CloseMessage(1002, e.getMessage())).toByteBuffer(), this, (WebSocketCallback<Void>)null);
/* 243 */     } else if (e instanceof WebSocketFrameCorruptedException) {
/* 244 */       getFramePriority().immediateCloseFrame();
/* 245 */       WebSockets.sendClose((new CloseMessage(1002, e.getMessage())).toByteBuffer(), this, (WebSocketCallback<Void>)null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleBrokenSinkChannel(Throwable e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Set<String> getSubProtocols() {
/* 259 */     return Collections.singleton(this.subProtocol);
/*     */   }
/*     */   
/*     */   public String getSubProtocol() {
/* 263 */     return this.subProtocol;
/*     */   }
/*     */   
/*     */   public boolean isCloseFrameReceived() {
/* 267 */     return this.closeFrameReceived;
/*     */   }
/*     */   
/*     */   public boolean isCloseFrameSent() {
/* 271 */     return this.closeFrameSent;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestScheme() {
/* 280 */     if (getUrl().startsWith("wss:")) {
/* 281 */       return "wss";
/*     */     }
/* 283 */     return "ws";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 291 */     return "wss".equals(getRequestScheme());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getUrl() {
/* 300 */     return this.wsUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketVersion getVersion() {
/* 309 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InetSocketAddress getSourceAddress() {
/* 318 */     return (InetSocketAddress)getPeerAddress(InetSocketAddress.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InetSocketAddress getDestinationAddress() {
/* 327 */     return (InetSocketAddress)getLocalAddress(InetSocketAddress.class);
/*     */   }
/*     */   
/*     */   public boolean isClient() {
/* 331 */     return this.client;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final StreamSinkFrameChannel send(WebSocketFrameType type) throws IOException {
/* 342 */     if (this.closeFrameSent || (this.closeFrameReceived && type != WebSocketFrameType.CLOSE)) {
/* 343 */       throw WebSocketMessages.MESSAGES.channelClosed();
/*     */     }
/* 345 */     if (isWritesBroken()) {
/* 346 */       throw WebSocketMessages.MESSAGES.streamIsBroken();
/*     */     }
/*     */ 
/*     */     
/* 350 */     StreamSinkFrameChannel ch = createStreamSinkChannel(type);
/* 351 */     getFramePriority().addToOrderQueue(ch);
/* 352 */     if (type == WebSocketFrameType.CLOSE) {
/* 353 */       this.closeFrameSent = true;
/*     */     }
/* 355 */     return ch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendClose() throws IOException {
/* 362 */     this.closeReason = "";
/* 363 */     this.closeCode = 1000;
/* 364 */     StreamSinkFrameChannel closeChannel = send(WebSocketFrameType.CLOSE);
/* 365 */     closeChannel.shutdownWrites();
/* 366 */     if (!closeChannel.flush()) {
/* 367 */       closeChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<StreamSinkChannel>()
/*     */             {
/*     */               public void handleException(StreamSinkChannel channel, IOException exception)
/*     */               {
/* 371 */                 IoUtils.safeClose((Closeable)WebSocketChannel.this);
/*     */               }
/*     */             }));
/*     */       
/* 375 */       closeChannel.resumeWrites();
/*     */     } 
/*     */   }
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
/*     */   protected WebSocketFramePriority getFramePriority() {
/* 389 */     return (WebSocketFramePriority)super.getFramePriority();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<WebSocketChannel> getPeerConnections() {
/* 399 */     return Collections.unmodifiableSet(this.peerConnections);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCloseInitiatedByRemotePeer() {
/* 407 */     return this.closeInitiatedByRemotePeer;
/*     */   }
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
/*     */   public String getCloseReason() {
/* 436 */     return this.closeReason;
/*     */   }
/*     */   
/*     */   public void setCloseReason(String closeReason) {
/* 440 */     this.closeReason = closeReason;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCloseCode() {
/* 448 */     return this.closeCode;
/*     */   }
/*     */   
/*     */   public void setCloseCode(int closeCode) {
/* 452 */     this.closeCode = closeCode;
/*     */   }
/*     */   
/*     */   public ExtensionFunction getExtensionFunction() {
/* 456 */     return this.extensionFunction;
/*     */   }
/*     */   
/*     */   protected abstract PartialFrame receiveFrame();
/*     */   
/*     */   protected abstract StreamSinkFrameChannel createStreamSinkChannel(WebSocketFrameType paramWebSocketFrameType);
/*     */   
/*     */   public static interface PartialFrame extends FrameHeaderData {
/*     */     StreamSourceFrameChannel getChannel(PooledByteBuffer param1PooledByteBuffer);
/*     */     
/*     */     void handle(ByteBuffer param1ByteBuffer) throws WebSocketException;
/*     */     
/*     */     boolean isDone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */