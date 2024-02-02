/*     */ package io.undertow.protocols.ajp;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedChannel;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
/*     */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
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
/*     */ public class AjpClientChannel
/*     */   extends AbstractFramedChannel<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel>
/*     */ {
/*     */   private final AjpResponseParser ajpParser;
/*     */   private AjpClientResponseStreamSourceChannel source;
/*     */   private AjpClientRequestClientStreamSinkChannel sink;
/*  67 */   private final List<ClientConnection.PingListener> pingListeners = new ArrayList<>();
/*     */ 
/*     */   
/*     */   boolean sinkDone = true;
/*     */ 
/*     */   
/*     */   boolean sourceDone = true;
/*     */ 
/*     */   
/*     */   private boolean lastFrameSent;
/*     */ 
/*     */   
/*     */   private boolean lastFrameReceived;
/*     */ 
/*     */ 
/*     */   
/*     */   public AjpClientChannel(StreamConnection connectedStreamChannel, ByteBufferPool bufferPool, OptionMap settings) {
/*  84 */     super(connectedStreamChannel, bufferPool, AjpClientFramePriority.INSTANCE, null, settings);
/*  85 */     this.ajpParser = new AjpResponseParser();
/*     */   }
/*     */ 
/*     */   
/*     */   protected AbstractAjpClientStreamSourceChannel createChannel(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) throws IOException {
/*  90 */     if (frameHeaderData instanceof SendHeadersResponse) {
/*  91 */       SendHeadersResponse h = (SendHeadersResponse)frameHeaderData;
/*  92 */       AjpClientResponseStreamSourceChannel sourceChannel = new AjpClientResponseStreamSourceChannel(this, h.headers, h.statusCode, h.reasonPhrase, frameData, (int)frameHeaderData.getFrameLength());
/*  93 */       this.source = sourceChannel;
/*  94 */       return sourceChannel;
/*  95 */     }  if (frameHeaderData instanceof RequestBodyChunk) {
/*  96 */       RequestBodyChunk r = (RequestBodyChunk)frameHeaderData;
/*  97 */       this.sink.chunkRequested(r.getLength());
/*  98 */       frameData.close();
/*  99 */       return null;
/* 100 */     }  if (frameHeaderData instanceof CpongResponse) {
/* 101 */       synchronized (this.pingListeners) {
/* 102 */         for (ClientConnection.PingListener i : this.pingListeners) {
/*     */           try {
/* 104 */             i.acknowledged();
/* 105 */           } catch (Throwable t) {
/* 106 */             UndertowLogger.ROOT_LOGGER.debugf("Exception notifying ping listener", t);
/*     */           } 
/*     */         } 
/* 109 */         this.pingListeners.clear();
/*     */       } 
/* 111 */       return null;
/*     */     } 
/* 113 */     frameData.close();
/* 114 */     throw new RuntimeException("TODO: unknown frame");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FrameHeaderData parseFrame(ByteBuffer data) throws IOException {
/* 121 */     this.ajpParser.parse(data);
/* 122 */     if (this.ajpParser.isComplete()) {
/*     */       try {
/* 124 */         AjpResponseParser parser = this.ajpParser;
/* 125 */         if (parser.prefix == 4)
/* 126 */           return new SendHeadersResponse(parser.statusCode, parser.reasonPhrase, parser.headers); 
/* 127 */         if (parser.prefix == 6)
/* 128 */           return new RequestBodyChunk(parser.readBodyChunkSize); 
/* 129 */         if (parser.prefix == 3)
/* 130 */           return new SendBodyChunk(parser.currentIntegerPart + 1); 
/* 131 */         if (parser.prefix == 5) {
/* 132 */           boolean persistent = (parser.currentIntegerPart != 0);
/* 133 */           if (!persistent) {
/* 134 */             this.lastFrameReceived = true;
/* 135 */             this.lastFrameSent = true;
/*     */           } 
/* 137 */           return new EndResponse();
/* 138 */         }  if (parser.prefix == 9) {
/* 139 */           return new CpongResponse();
/*     */         }
/* 141 */         UndertowLogger.ROOT_LOGGER.debug("UNKOWN FRAME");
/*     */       } finally {
/*     */         
/* 144 */         this.ajpParser.reset();
/*     */       } 
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */   
/*     */   public AjpClientRequestClientStreamSinkChannel sendRequest(HttpString method, String path, HttpString protocol, HeaderMap headers, Attachable attachable, ChannelListener<AjpClientRequestClientStreamSinkChannel> finishListener) {
/* 151 */     if (!this.sinkDone || !this.sourceDone) {
/* 152 */       throw UndertowMessages.MESSAGES.ajpRequestAlreadyInProgress();
/*     */     }
/* 154 */     this.sinkDone = false;
/* 155 */     this.sourceDone = false;
/* 156 */     AjpClientRequestClientStreamSinkChannel ajpClientRequestStreamSinkChannel = new AjpClientRequestClientStreamSinkChannel(this, finishListener, headers, path, method, protocol, attachable);
/* 157 */     this.sink = ajpClientRequestStreamSinkChannel;
/* 158 */     this.source = null;
/* 159 */     return ajpClientRequestStreamSinkChannel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isLastFrameReceived() {
/* 164 */     return this.lastFrameReceived;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isLastFrameSent() {
/* 169 */     return this.lastFrameSent;
/*     */   }
/*     */   
/*     */   protected void lastDataRead() {
/* 173 */     if (!this.lastFrameSent) {
/* 174 */       markReadsBroken(new ClosedChannelException());
/* 175 */       markWritesBroken(new ClosedChannelException());
/*     */     } 
/* 177 */     this.lastFrameReceived = true;
/* 178 */     this.lastFrameSent = true;
/* 179 */     IoUtils.safeClose((Closeable)this);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleBrokenSourceChannel(Throwable e) {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleBrokenSinkChannel(Throwable e) {}
/*     */ 
/*     */   
/*     */   protected void closeSubChannels() {
/* 192 */     IoUtils.safeClose(new Closeable[] { (Closeable)this.source, (Closeable)this.sink });
/*     */   }
/*     */ 
/*     */   
/*     */   protected Collection<AbstractFramedStreamSourceChannel<AjpClientChannel, AbstractAjpClientStreamSourceChannel, AbstractAjpClientStreamSinkChannel>> getReceivers() {
/* 197 */     if (this.source == null) {
/* 198 */       return Collections.emptyList();
/*     */     }
/* 200 */     return Collections.singleton(this.source);
/*     */   }
/*     */   
/*     */   protected OptionMap getSettings() {
/* 204 */     return super.getSettings();
/*     */   }
/*     */   
/*     */   void sinkDone() {
/* 208 */     this.sinkDone = true;
/* 209 */     if (this.sourceDone) {
/* 210 */       this.sink = null;
/* 211 */       this.source = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   void sourceDone() {
/* 216 */     this.sourceDone = true;
/* 217 */     if (this.sinkDone) {
/* 218 */       this.sink = null;
/* 219 */       this.source = null;
/*     */     } else {
/* 221 */       this.sink.startDiscard();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 227 */     return (super.isOpen() && !this.lastFrameSent && !this.lastFrameReceived);
/*     */   }
/*     */ 
/*     */   
/*     */   protected synchronized void recalculateHeldFrames() throws IOException {
/* 232 */     super.recalculateHeldFrames();
/*     */   }
/*     */   
/*     */   public void sendPing(final ClientConnection.PingListener pingListener, long timeout, TimeUnit timeUnit) {
/* 236 */     AjpClientCPingStreamSinkChannel pingChannel = new AjpClientCPingStreamSinkChannel(this);
/*     */     try {
/* 238 */       pingChannel.shutdownWrites();
/* 239 */       if (!pingChannel.flush()) {
/* 240 */         pingChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<AbstractAjpClientStreamSinkChannel>()
/*     */               {
/*     */                 public void handleException(AbstractAjpClientStreamSinkChannel channel, IOException exception) {
/* 243 */                   pingListener.failed(exception);
/* 244 */                   synchronized (AjpClientChannel.this.pingListeners) {
/* 245 */                     AjpClientChannel.this.pingListeners.remove(pingListener);
/*     */                   } 
/*     */                 }
/*     */               }));
/* 249 */         pingChannel.resumeWrites();
/*     */       } 
/* 251 */     } catch (IOException e) {
/* 252 */       pingListener.failed(e);
/*     */       
/*     */       return;
/*     */     } 
/* 256 */     synchronized (this.pingListeners) {
/* 257 */       this.pingListeners.add(pingListener);
/*     */     } 
/* 259 */     getIoThread().executeAfter(() -> { synchronized (this.pingListeners) { if (this.pingListeners.contains(pingListener)) { this.pingListeners.remove(pingListener); pingListener.failed(UndertowMessages.MESSAGES.pingTimeout()); }  }  }timeout, timeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   class SendHeadersResponse
/*     */     implements FrameHeaderData
/*     */   {
/*     */     private final int statusCode;
/*     */ 
/*     */     
/*     */     private final String reasonPhrase;
/*     */     
/*     */     private final HeaderMap headers;
/*     */ 
/*     */     
/*     */     SendHeadersResponse(int statusCode, String reasonPhrase, HeaderMap headers) {
/* 276 */       this.statusCode = statusCode;
/* 277 */       this.reasonPhrase = reasonPhrase;
/* 278 */       this.headers = headers;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public long getFrameLength() {
/* 284 */       return 0L;
/*     */     }
/*     */ 
/*     */     
/*     */     public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
/* 289 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   class RequestBodyChunk
/*     */     implements FrameHeaderData
/*     */   {
/*     */     private final int length;
/*     */     
/*     */     RequestBodyChunk(int length) {
/* 299 */       this.length = length;
/*     */     }
/*     */     
/*     */     public int getLength() {
/* 303 */       return this.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getFrameLength() {
/* 308 */       return 0L;
/*     */     }
/*     */ 
/*     */     
/*     */     public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
/* 313 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   class SendBodyChunk
/*     */     implements FrameHeaderData
/*     */   {
/*     */     private final int length;
/*     */     
/*     */     SendBodyChunk(int length) {
/* 323 */       this.length = length;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getFrameLength() {
/* 328 */       return this.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
/* 333 */       return AjpClientChannel.this.source;
/*     */     }
/*     */   }
/*     */   
/*     */   class EndResponse
/*     */     implements FrameHeaderData
/*     */   {
/*     */     public long getFrameLength() {
/* 341 */       return 0L;
/*     */     }
/*     */ 
/*     */     
/*     */     public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
/* 346 */       return AjpClientChannel.this.source;
/*     */     }
/*     */   }
/*     */   
/*     */   class CpongResponse
/*     */     implements FrameHeaderData
/*     */   {
/*     */     public long getFrameLength() {
/* 354 */       return 0L;
/*     */     }
/*     */ 
/*     */     
/*     */     public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
/* 359 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpClientChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */