/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Http2StreamSourceChannel
/*     */   extends AbstractHttp2StreamSourceChannel
/*     */   implements Http2Stream
/*     */ {
/*     */   private boolean headersEndStream = false;
/*     */   private boolean rst = false;
/*     */   private final HeaderMap headers;
/*     */   private final int streamId;
/*     */   private Http2HeadersStreamSinkChannel response;
/*     */   private int flowControlWindow;
/*     */   private ChannelListener<Http2StreamSourceChannel> completionListener;
/*     */   private int remainingPadding;
/*     */   private boolean ignoreForceClose = false;
/*     */   private long contentLengthRemaining;
/*     */   private TrailersHandler trailersHandler;
/*     */   
/*     */   Http2StreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, HeaderMap headers, int streamId) {
/*  68 */     super(framedChannel, data, frameDataRemaining);
/*  69 */     this.headers = headers;
/*  70 */     this.streamId = streamId;
/*  71 */     this.flowControlWindow = framedChannel.getInitialReceiveWindowSize();
/*  72 */     String contentLengthString = headers.getFirst(Headers.CONTENT_LENGTH);
/*  73 */     if (contentLengthString != null) {
/*  74 */       this.contentLengthRemaining = Long.parseLong(contentLengthString);
/*     */     } else {
/*  76 */       this.contentLengthRemaining = -1L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleHeaderData(FrameHeaderData headerData) {
/*  82 */     Http2FrameHeaderParser data = (Http2FrameHeaderParser)headerData;
/*  83 */     Http2PushBackParser parser = data.getParser();
/*  84 */     if (parser instanceof Http2DataFrameParser) {
/*  85 */       this.remainingPadding = ((Http2DataFrameParser)parser).getPadding();
/*  86 */       if (this.remainingPadding > 0) {
/*     */         try {
/*  88 */           updateFlowControlWindow(this.remainingPadding + 1);
/*  89 */         } catch (IOException e) {
/*  90 */           IoUtils.safeClose((Closeable)getFramedChannel());
/*  91 */           throw new RuntimeException(e);
/*     */         } 
/*     */       }
/*  94 */     } else if (parser instanceof Http2HeadersParser && 
/*  95 */       this.trailersHandler != null) {
/*  96 */       this.trailersHandler.handleTrailers(((Http2HeadersParser)parser).getHeaderMap());
/*     */     } 
/*     */     
/*  99 */     handleFinalFrame(data);
/*     */   }
/*     */ 
/*     */   
/*     */   protected long updateFrameDataRemaining(PooledByteBuffer data, long frameDataRemaining) {
/* 104 */     long actualDataRemaining = frameDataRemaining - this.remainingPadding;
/* 105 */     if (data.getBuffer().remaining() > actualDataRemaining) {
/* 106 */       long paddingThisBuffer = data.getBuffer().remaining() - actualDataRemaining;
/* 107 */       data.getBuffer().limit((int)(data.getBuffer().position() + actualDataRemaining));
/* 108 */       this.remainingPadding = (int)(this.remainingPadding - paddingThisBuffer);
/* 109 */       return frameDataRemaining - paddingThisBuffer;
/*     */     } 
/* 111 */     return frameDataRemaining;
/*     */   }
/*     */   
/*     */   void handleFinalFrame(Http2FrameHeaderParser headerData) {
/* 115 */     Http2FrameHeaderParser data = headerData;
/* 116 */     if (data.type == 0) {
/* 117 */       if (Bits.anyAreSet(data.flags, 1)) {
/* 118 */         lastFrame();
/*     */       }
/* 120 */     } else if (data.type == 1) {
/* 121 */       if (Bits.allAreSet(data.flags, 1)) {
/* 122 */         if (Bits.allAreSet(data.flags, 4)) {
/* 123 */           lastFrame();
/*     */         } else {
/*     */           
/* 126 */           this.headersEndStream = true;
/*     */         } 
/*     */       }
/* 129 */     } else if (this.headersEndStream && data.type == 9 && 
/* 130 */       Bits.anyAreSet(data.flags, 4)) {
/* 131 */       lastFrame();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Http2HeadersStreamSinkChannel getResponseChannel() {
/* 137 */     if (this.response != null) {
/* 138 */       return this.response;
/*     */     }
/* 140 */     this.response = new Http2HeadersStreamSinkChannel(getHttp2Channel(), this.streamId);
/* 141 */     getHttp2Channel().registerStreamSink(this.response);
/* 142 */     return this.response;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 147 */     int read = super.read(dst);
/* 148 */     updateFlowControlWindow(read);
/* 149 */     return read;
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 154 */     long read = super.read(dsts, offset, length);
/* 155 */     updateFlowControlWindow((int)read);
/* 156 */     return read;
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 161 */     long read = super.read(dsts);
/* 162 */     updateFlowControlWindow((int)read);
/* 163 */     return read;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel streamSinkChannel) throws IOException {
/* 168 */     long read = super.transferTo(count, throughBuffer, streamSinkChannel);
/* 169 */     updateFlowControlWindow((int)read + throughBuffer.remaining());
/* 170 */     return read;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 175 */     long read = super.transferTo(position, count, target);
/* 176 */     updateFlowControlWindow((int)read);
/* 177 */     return read;
/*     */   }
/*     */   
/*     */   private void updateFlowControlWindow(int read) throws IOException {
/* 181 */     if (read <= 0) {
/*     */       return;
/*     */     }
/* 184 */     this.flowControlWindow -= read;
/*     */ 
/*     */     
/* 187 */     Http2Channel http2Channel = getHttp2Channel();
/* 188 */     http2Channel.updateReceiveFlowControlWindow(read);
/* 189 */     int initialWindowSize = http2Channel.getInitialReceiveWindowSize();
/*     */     
/* 191 */     if (this.flowControlWindow < initialWindowSize / 2) {
/* 192 */       int delta = initialWindowSize - this.flowControlWindow;
/* 193 */       this.flowControlWindow += delta;
/* 194 */       http2Channel.sendUpdateWindowSize(this.streamId, delta);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void complete() throws IOException {
/* 200 */     super.complete();
/* 201 */     if (this.completionListener != null) {
/* 202 */       ChannelListeners.invokeChannelListener((Channel)this, this.completionListener);
/*     */     }
/*     */   }
/*     */   
/*     */   public HeaderMap getHeaders() {
/* 207 */     return this.headers;
/*     */   }
/*     */   
/*     */   public ChannelListener<Http2StreamSourceChannel> getCompletionListener() {
/* 211 */     return this.completionListener;
/*     */   }
/*     */   
/*     */   public void setCompletionListener(ChannelListener<Http2StreamSourceChannel> completionListener) {
/* 215 */     this.completionListener = completionListener;
/* 216 */     if (isComplete()) {
/* 217 */       ChannelListeners.invokeChannelListener((Channel)this, completionListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   void rstStream(int error) {
/* 223 */     if (this.rst) {
/*     */       return;
/*     */     }
/* 226 */     this.rst = true;
/* 227 */     markStreamBroken();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void channelForciblyClosed() {
/* 232 */     if (this.completionListener != null) {
/* 233 */       this.completionListener.handleEvent((Channel)this);
/*     */     }
/* 235 */     if (!this.ignoreForceClose) {
/* 236 */       getHttp2Channel().sendRstStream(this.streamId, 8);
/*     */     }
/* 238 */     markStreamBroken();
/*     */   }
/*     */   
/*     */   public void setIgnoreForceClose(boolean ignoreForceClose) {
/* 242 */     this.ignoreForceClose = ignoreForceClose;
/*     */   }
/*     */   
/*     */   public boolean isIgnoreForceClose() {
/* 246 */     return this.ignoreForceClose;
/*     */   }
/*     */   
/*     */   public int getStreamId() {
/* 250 */     return this.streamId;
/*     */   }
/*     */   
/*     */   boolean isHeadersEndStream() {
/* 254 */     return this.headersEndStream;
/*     */   }
/*     */   
/*     */   public TrailersHandler getTrailersHandler() {
/* 258 */     return this.trailersHandler;
/*     */   }
/*     */   
/*     */   public void setTrailersHandler(TrailersHandler trailersHandler) {
/* 262 */     this.trailersHandler = trailersHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 267 */     return "Http2StreamSourceChannel{headers=" + this.headers + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void updateContentSize(long frameLength, boolean last) {
/* 278 */     if (this.contentLengthRemaining != -1L) {
/* 279 */       this.contentLengthRemaining -= frameLength;
/* 280 */       if (this.contentLengthRemaining < 0L) {
/* 281 */         UndertowLogger.REQUEST_IO_LOGGER.debugf("Closing stream %s on %s as data length exceeds content size", this.streamId, getFramedChannel());
/* 282 */         getFramedChannel().sendRstStream(this.streamId, 1);
/* 283 */       } else if (last && this.contentLengthRemaining != 0L) {
/* 284 */         UndertowLogger.REQUEST_IO_LOGGER.debugf("Closing stream %s on %s as data length was less than content size", this.streamId, getFramedChannel());
/* 285 */         getFramedChannel().sendRstStream(this.streamId, 1);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static interface TrailersHandler {
/*     */     void handleTrailers(HeaderMap param1HeaderMap);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2StreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */