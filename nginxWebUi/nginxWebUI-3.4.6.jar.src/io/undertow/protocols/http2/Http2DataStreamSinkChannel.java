/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
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
/*     */ public class Http2DataStreamSinkChannel
/*     */   extends Http2StreamSinkChannel
/*     */   implements Http2Stream
/*     */ {
/*     */   private final HeaderMap headers;
/*     */   private boolean first = true;
/*     */   private final HpackEncoder encoder;
/*     */   private volatile ChannelListener<Http2DataStreamSinkChannel> completionListener;
/*     */   private final int frameType;
/*     */   private boolean completionListenerReady;
/*     */   private volatile boolean completionListenerFailure;
/*     */   private TrailersProducer trailersProducer;
/*     */   
/*     */   Http2DataStreamSinkChannel(Http2Channel channel, int streamId, int frameType) {
/*  51 */     this(channel, streamId, new HeaderMap(), frameType);
/*     */   }
/*     */   
/*     */   Http2DataStreamSinkChannel(Http2Channel channel, int streamId, HeaderMap headers, int frameType) {
/*  55 */     super(channel, streamId);
/*  56 */     this.encoder = channel.getEncoder();
/*  57 */     this.headers = headers;
/*  58 */     this.frameType = frameType;
/*     */   }
/*     */   
/*     */   public TrailersProducer getTrailersProducer() {
/*  62 */     return this.trailersProducer;
/*     */   }
/*     */   
/*     */   public void setTrailersProducer(TrailersProducer trailersProducer) {
/*  66 */     this.trailersProducer = trailersProducer;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SendFrameHeader createFrameHeaderImpl() {
/*  72 */     int dataPaddingBytes = ((Http2Channel)getChannel()).getPaddingBytes();
/*  73 */     int attempted = getBuffer().remaining() + dataPaddingBytes + ((dataPaddingBytes > 0) ? 1 : 0);
/*  74 */     int fcWindow = grabFlowControlBytes(attempted);
/*  75 */     if (fcWindow == 0 && getBuffer().hasRemaining())
/*     */     {
/*  77 */       return new SendFrameHeader(getBuffer().remaining(), null);
/*     */     }
/*  79 */     if (fcWindow <= dataPaddingBytes + 1)
/*     */     {
/*  81 */       if (getBuffer().remaining() >= fcWindow) {
/*     */         
/*  83 */         dataPaddingBytes = 0;
/*  84 */       } else if (getBuffer().remaining() == dataPaddingBytes) {
/*     */         
/*  86 */         dataPaddingBytes = 1;
/*     */       } else {
/*  88 */         dataPaddingBytes = fcWindow - getBuffer().remaining() - 1;
/*     */       } 
/*     */     }
/*     */     
/*  92 */     boolean finalFrame = (isFinalFrameQueued() && fcWindow >= getBuffer().remaining() + ((dataPaddingBytes > 0) ? (dataPaddingBytes + 1) : 0));
/*  93 */     PooledByteBuffer firstHeaderBuffer = ((Http2Channel)getChannel()).getBufferPool().allocate();
/*  94 */     PooledByteBuffer[] allHeaderBuffers = null;
/*  95 */     ByteBuffer firstBuffer = firstHeaderBuffer.getBuffer();
/*  96 */     boolean firstFrame = false;
/*     */     
/*  98 */     HeaderMap trailers = null;
/*  99 */     if (finalFrame && this.trailersProducer != null) {
/* 100 */       trailers = this.trailersProducer.getTrailers();
/* 101 */       if (trailers != null && trailers.size() == 0) {
/* 102 */         trailers = null;
/*     */       }
/*     */     } 
/*     */     
/* 106 */     if (this.first) {
/* 107 */       firstFrame = true;
/* 108 */       this.first = false;
/*     */       
/* 110 */       firstBuffer.put((byte)0);
/* 111 */       firstBuffer.put((byte)0);
/* 112 */       firstBuffer.put((byte)0);
/* 113 */       firstBuffer.put((byte)this.frameType);
/* 114 */       firstBuffer.put((byte)0);
/*     */       
/* 116 */       Http2ProtocolUtils.putInt(firstBuffer, getStreamId());
/*     */       
/* 118 */       int paddingBytes = ((Http2Channel)getChannel()).getPaddingBytes();
/* 119 */       if (paddingBytes > 0) {
/* 120 */         firstBuffer.put((byte)(paddingBytes & 0xFF));
/*     */       }
/* 122 */       writeBeforeHeaderBlock(firstBuffer);
/* 123 */       HeaderMap headers = this.headers;
/* 124 */       HpackEncoder.State result = this.encoder.encode(headers, firstBuffer);
/* 125 */       PooledByteBuffer current = firstHeaderBuffer;
/* 126 */       int headerFrameLength = firstBuffer.position() - 9 + paddingBytes;
/* 127 */       firstBuffer.put(0, (byte)(headerFrameLength >> 16 & 0xFF));
/* 128 */       firstBuffer.put(1, (byte)(headerFrameLength >> 8 & 0xFF));
/* 129 */       firstBuffer.put(2, (byte)(headerFrameLength & 0xFF));
/* 130 */       firstBuffer.put(4, (byte)(((isFinalFrameQueued() && !getBuffer().hasRemaining() && this.frameType == 1 && trailers == null) ? 1 : 0) | ((result == HpackEncoder.State.COMPLETE) ? 4 : 0) | ((paddingBytes > 0) ? 8 : 0)));
/* 131 */       ByteBuffer byteBuffer = firstBuffer;
/*     */       
/* 133 */       if (byteBuffer.remaining() < paddingBytes) {
/* 134 */         allHeaderBuffers = allocateAll(allHeaderBuffers, current);
/* 135 */         current = allHeaderBuffers[allHeaderBuffers.length - 1];
/* 136 */         byteBuffer = current.getBuffer();
/*     */       } 
/* 138 */       for (int j = 0; j < paddingBytes; j++) {
/* 139 */         byteBuffer.put((byte)0);
/*     */       }
/*     */       
/* 142 */       while (result != HpackEncoder.State.COMPLETE) {
/*     */ 
/*     */         
/* 145 */         allHeaderBuffers = allocateAll(allHeaderBuffers, current);
/* 146 */         current = allHeaderBuffers[allHeaderBuffers.length - 1];
/* 147 */         result = encodeContinuationFrame(headers, current);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 152 */     PooledByteBuffer currentPooled = (allHeaderBuffers == null) ? firstHeaderBuffer : allHeaderBuffers[allHeaderBuffers.length - 1];
/* 153 */     ByteBuffer currentBuffer = currentPooled.getBuffer();
/* 154 */     ByteBuffer trailer = null;
/* 155 */     int remainingInBuffer = 0;
/* 156 */     boolean requiresTrailers = false;
/*     */     
/* 158 */     if (getBuffer().remaining() > 0) {
/* 159 */       if (fcWindow > 0) {
/*     */         
/* 161 */         if (currentBuffer.remaining() < 10) {
/* 162 */           allHeaderBuffers = allocateAll(allHeaderBuffers, currentPooled);
/* 163 */           currentPooled = (allHeaderBuffers == null) ? firstHeaderBuffer : allHeaderBuffers[allHeaderBuffers.length - 1];
/* 164 */           currentBuffer = currentPooled.getBuffer();
/*     */         } 
/* 166 */         int toSend = fcWindow - dataPaddingBytes - ((dataPaddingBytes > 0) ? 1 : 0);
/* 167 */         remainingInBuffer = getBuffer().remaining() - toSend;
/*     */         
/* 169 */         getBuffer().limit(getBuffer().position() + toSend);
/*     */         
/* 171 */         currentBuffer.put((byte)(fcWindow >> 16 & 0xFF));
/* 172 */         currentBuffer.put((byte)(fcWindow >> 8 & 0xFF));
/* 173 */         currentBuffer.put((byte)(fcWindow & 0xFF));
/* 174 */         currentBuffer.put((byte)0);
/* 175 */         if (trailers == null) {
/* 176 */           currentBuffer.put((byte)((finalFrame ? 1 : 0) | ((dataPaddingBytes > 0) ? 8 : 0)));
/*     */         } else {
/* 178 */           if (finalFrame) {
/* 179 */             requiresTrailers = true;
/*     */           }
/* 181 */           currentBuffer.put((byte)((dataPaddingBytes > 0) ? 8 : 0));
/*     */         } 
/* 183 */         Http2ProtocolUtils.putInt(currentBuffer, getStreamId());
/* 184 */         if (dataPaddingBytes > 0) {
/* 185 */           currentBuffer.put((byte)(dataPaddingBytes & 0xFF));
/* 186 */           trailer = ByteBuffer.allocate(dataPaddingBytes);
/*     */         } 
/*     */       } else {
/* 189 */         remainingInBuffer = getBuffer().remaining();
/*     */       } 
/* 191 */     } else if (finalFrame && !firstFrame) {
/* 192 */       currentBuffer.put((byte)(fcWindow >> 16 & 0xFF));
/* 193 */       currentBuffer.put((byte)(fcWindow >> 8 & 0xFF));
/* 194 */       currentBuffer.put((byte)(fcWindow & 0xFF));
/* 195 */       currentBuffer.put((byte)0);
/* 196 */       if (trailers == null) {
/* 197 */         currentBuffer.put((byte)(0x1 | ((dataPaddingBytes > 0) ? 8 : 0)));
/*     */       } else {
/* 199 */         requiresTrailers = true;
/* 200 */         currentBuffer.put((byte)((dataPaddingBytes > 0) ? 8 : 0));
/*     */       } 
/* 202 */       Http2ProtocolUtils.putInt(currentBuffer, getStreamId());
/* 203 */       if (dataPaddingBytes > 0) {
/* 204 */         currentBuffer.put((byte)(dataPaddingBytes & 0xFF));
/* 205 */         trailer = ByteBuffer.allocate(dataPaddingBytes);
/*     */       } 
/* 207 */     } else if (finalFrame && trailers != null) {
/* 208 */       requiresTrailers = true;
/*     */     } 
/*     */     
/* 211 */     if (requiresTrailers) {
/* 212 */       PooledByteBuffer firstTrailerBuffer = ((Http2Channel)getChannel()).getBufferPool().allocate();
/* 213 */       if (trailer != null) {
/* 214 */         firstTrailerBuffer.getBuffer().put(trailer);
/*     */       }
/* 216 */       firstTrailerBuffer.getBuffer().put((byte)0);
/* 217 */       firstTrailerBuffer.getBuffer().put((byte)0);
/* 218 */       firstTrailerBuffer.getBuffer().put((byte)0);
/* 219 */       firstTrailerBuffer.getBuffer().put((byte)1);
/* 220 */       firstTrailerBuffer.getBuffer().put((byte)5);
/*     */       
/* 222 */       Http2ProtocolUtils.putInt(firstTrailerBuffer.getBuffer(), getStreamId());
/* 223 */       HpackEncoder.State result = this.encoder.encode(trailers, firstTrailerBuffer.getBuffer());
/* 224 */       if (result != HpackEncoder.State.COMPLETE) {
/* 225 */         throw UndertowMessages.MESSAGES.http2TrailerToLargeForSingleBuffer();
/*     */       }
/* 227 */       int headerFrameLength = firstTrailerBuffer.getBuffer().position() - 9;
/* 228 */       firstTrailerBuffer.getBuffer().put(0, (byte)(headerFrameLength >> 16 & 0xFF));
/* 229 */       firstTrailerBuffer.getBuffer().put(1, (byte)(headerFrameLength >> 8 & 0xFF));
/* 230 */       firstTrailerBuffer.getBuffer().put(2, (byte)(headerFrameLength & 0xFF));
/* 231 */       firstTrailerBuffer.getBuffer().flip();
/* 232 */       int size = firstTrailerBuffer.getBuffer().remaining();
/* 233 */       trailer = ByteBuffer.allocate(size);
/* 234 */       trailer.put(firstTrailerBuffer.getBuffer());
/* 235 */       trailer.flip();
/* 236 */       firstTrailerBuffer.close();
/*     */     } 
/* 238 */     if (allHeaderBuffers == null) {
/*     */       
/* 240 */       currentBuffer.flip();
/* 241 */       return new SendFrameHeader(remainingInBuffer, currentPooled, false, trailer);
/*     */     } 
/*     */ 
/*     */     
/* 245 */     int length = 0;
/* 246 */     for (int i = 0; i < allHeaderBuffers.length; i++) {
/* 247 */       length += allHeaderBuffers[i].getBuffer().position();
/* 248 */       allHeaderBuffers[i].getBuffer().flip();
/*     */     } 
/*     */     try {
/* 251 */       ByteBuffer newBuf = ByteBuffer.allocate(length);
/*     */       
/* 253 */       for (int j = 0; j < allHeaderBuffers.length; j++) {
/* 254 */         newBuf.put(allHeaderBuffers[j].getBuffer());
/*     */       }
/* 256 */       newBuf.flip();
/* 257 */       return new SendFrameHeader(remainingInBuffer, (PooledByteBuffer)new ImmediatePooledByteBuffer(newBuf), false, trailer);
/*     */     } finally {
/*     */       
/* 260 */       for (int j = 0; j < allHeaderBuffers.length; j++) {
/* 261 */         allHeaderBuffers[j].close();
/*     */       }
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
/*     */   private HpackEncoder.State encodeContinuationFrame(HeaderMap headers, PooledByteBuffer current) {
/* 274 */     ByteBuffer currentBuffer = current.getBuffer();
/* 275 */     currentBuffer.put((byte)0);
/* 276 */     currentBuffer.put((byte)0);
/* 277 */     currentBuffer.put((byte)0);
/* 278 */     currentBuffer.put((byte)9);
/* 279 */     currentBuffer.put((byte)0);
/* 280 */     Http2ProtocolUtils.putInt(currentBuffer, getStreamId());
/* 281 */     HpackEncoder.State result = this.encoder.encode(headers, currentBuffer);
/* 282 */     int contFrameLength = currentBuffer.position() - 9;
/* 283 */     currentBuffer.put(0, (byte)(contFrameLength >> 16 & 0xFF));
/* 284 */     currentBuffer.put(1, (byte)(contFrameLength >> 8 & 0xFF));
/* 285 */     currentBuffer.put(2, (byte)(contFrameLength & 0xFF));
/* 286 */     currentBuffer.put(4, (byte)((result == HpackEncoder.State.COMPLETE) ? 4 : 0));
/* 287 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 292 */     handleFailedChannel();
/* 293 */     return super.write(srcs, offset, length);
/*     */   }
/*     */   
/*     */   private void handleFailedChannel() {
/* 297 */     if (this.completionListenerFailure && this.completionListener != null) {
/* 298 */       ChannelListeners.invokeChannelListener((Channel)this, this.completionListener);
/* 299 */       this.completionListener = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 305 */     handleFailedChannel();
/* 306 */     return super.write(srcs);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 311 */     handleFailedChannel();
/* 312 */     return super.write(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 317 */     handleFailedChannel();
/* 318 */     return super.writeFinal(srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 323 */     handleFailedChannel();
/* 324 */     return super.writeFinal(srcs);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 329 */     handleFailedChannel();
/* 330 */     return super.writeFinal(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 335 */     handleFailedChannel();
/* 336 */     if (this.completionListenerReady && this.completionListener != null) {
/* 337 */       ChannelListeners.invokeChannelListener((Channel)this, this.completionListener);
/* 338 */       this.completionListener = null;
/*     */     } 
/* 340 */     return super.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeBeforeHeaderBlock(ByteBuffer buffer) {}
/*     */ 
/*     */   
/*     */   protected boolean isFlushRequiredOnEmptyBuffer() {
/* 348 */     return this.first;
/*     */   }
/*     */   
/*     */   public HeaderMap getHeaders() {
/* 352 */     return this.headers;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleFlushComplete(boolean finalFrame) {
/* 357 */     super.handleFlushComplete(finalFrame);
/* 358 */     if (finalFrame && 
/* 359 */       this.completionListener != null) {
/* 360 */       this.completionListenerReady = true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void channelForciblyClosed() throws IOException {
/* 367 */     super.channelForciblyClosed();
/* 368 */     if (this.completionListener != null) {
/* 369 */       this.completionListenerFailure = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public ChannelListener<Http2DataStreamSinkChannel> getCompletionListener() {
/* 374 */     return this.completionListener;
/*     */   }
/*     */   
/*     */   public void setCompletionListener(ChannelListener<Http2DataStreamSinkChannel> completionListener) {
/* 378 */     this.completionListener = completionListener;
/*     */   }
/*     */   
/*     */   public static interface TrailersProducer {
/*     */     HeaderMap getTrailers();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2DataStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */