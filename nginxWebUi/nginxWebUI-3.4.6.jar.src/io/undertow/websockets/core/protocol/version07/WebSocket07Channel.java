/*     */ package io.undertow.websockets.core.protocol.version07;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
/*     */ import io.undertow.websockets.core.StreamSinkFrameChannel;
/*     */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*     */ import io.undertow.websockets.core.WebSocketChannel;
/*     */ import io.undertow.websockets.core.WebSocketException;
/*     */ import io.undertow.websockets.core.WebSocketFrame;
/*     */ import io.undertow.websockets.core.WebSocketFrameCorruptedException;
/*     */ import io.undertow.websockets.core.WebSocketFrameType;
/*     */ import io.undertow.websockets.core.WebSocketLogger;
/*     */ import io.undertow.websockets.core.WebSocketMessages;
/*     */ import io.undertow.websockets.core.WebSocketVersion;
/*     */ import io.undertow.websockets.extensions.ExtensionFunction;
/*     */ import java.io.Closeable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Set;
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
/*     */ public class WebSocket07Channel
/*     */   extends WebSocketChannel
/*     */ {
/*     */   private int fragmentedFramesCount;
/*     */   
/*     */   private enum State
/*     */   {
/*  51 */     READING_FIRST,
/*  52 */     READING_SECOND,
/*  53 */     READING_EXTENDED_SIZE1,
/*  54 */     READING_EXTENDED_SIZE2,
/*  55 */     READING_EXTENDED_SIZE3,
/*  56 */     READING_EXTENDED_SIZE4,
/*  57 */     READING_EXTENDED_SIZE5,
/*  58 */     READING_EXTENDED_SIZE6,
/*  59 */     READING_EXTENDED_SIZE7,
/*  60 */     READING_EXTENDED_SIZE8,
/*  61 */     READING_MASK_1,
/*  62 */     READING_MASK_2,
/*  63 */     READING_MASK_3,
/*  64 */     READING_MASK_4,
/*  65 */     DONE;
/*     */   }
/*     */ 
/*     */   
/*  69 */   private final ByteBuffer lengthBuffer = ByteBuffer.allocate(8);
/*     */ 
/*     */   
/*     */   private UTF8Checker checker;
/*     */ 
/*     */   
/*     */   protected static final byte OPCODE_CONT = 0;
/*     */ 
/*     */   
/*     */   protected static final byte OPCODE_TEXT = 1;
/*     */   
/*     */   protected static final byte OPCODE_BINARY = 2;
/*     */   
/*     */   protected static final byte OPCODE_CLOSE = 8;
/*     */   
/*     */   protected static final byte OPCODE_PING = 9;
/*     */   
/*     */   protected static final byte OPCODE_PONG = 10;
/*     */ 
/*     */   
/*     */   public WebSocket07Channel(StreamConnection channel, ByteBufferPool bufferPool, String wsUrl, String subProtocol, boolean client, boolean allowExtensions, ExtensionFunction extensionFunction, Set<WebSocketChannel> openConnections, OptionMap options) {
/*  90 */     super(channel, bufferPool, WebSocketVersion.V08, wsUrl, subProtocol, client, allowExtensions, extensionFunction, openConnections, options);
/*     */   }
/*     */ 
/*     */   
/*     */   protected WebSocketChannel.PartialFrame receiveFrame() {
/*  95 */     return (WebSocketChannel.PartialFrame)new WebSocketFrameHeader();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void markReadsBroken(Throwable cause) {
/* 100 */     super.markReadsBroken(cause);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void closeSubChannels() {
/* 105 */     IoUtils.safeClose((Closeable)this.fragmentedChannel);
/*     */   }
/*     */ 
/*     */   
/*     */   protected StreamSinkFrameChannel createStreamSinkChannel(WebSocketFrameType type) {
/* 110 */     switch (type) {
/*     */       case READING_FIRST:
/* 112 */         return new WebSocket07TextFrameSinkChannel(this);
/*     */       case READING_SECOND:
/* 114 */         return new WebSocket07BinaryFrameSinkChannel(this);
/*     */       case READING_EXTENDED_SIZE1:
/* 116 */         return new WebSocket07CloseFrameSinkChannel(this);
/*     */       case READING_EXTENDED_SIZE2:
/* 118 */         return new WebSocket07PongFrameSinkChannel(this);
/*     */       case READING_EXTENDED_SIZE3:
/* 120 */         return new WebSocket07PingFrameSinkChannel(this);
/*     */     } 
/* 122 */     throw WebSocketMessages.MESSAGES.unsupportedFrameType(type);
/*     */   }
/*     */   
/*     */   class WebSocketFrameHeader
/*     */     implements WebSocketFrame
/*     */   {
/*     */     private boolean frameFinalFlag;
/*     */     private int frameRsv;
/*     */     private int frameOpcode;
/*     */     private int maskingKey;
/*     */     private boolean frameMasked;
/*     */     private long framePayloadLength;
/* 134 */     private WebSocket07Channel.State state = WebSocket07Channel.State.READING_FIRST;
/*     */     
/*     */     private int framePayloadLen1;
/*     */     private boolean done = false;
/*     */     
/*     */     public StreamSourceFrameChannel getChannel(PooledByteBuffer pooled) {
/* 140 */       StreamSourceFrameChannel channel = createChannel(pooled);
/* 141 */       if (this.frameFinalFlag) {
/* 142 */         channel.finalFrame();
/*     */       } else {
/* 144 */         WebSocket07Channel.this.fragmentedChannel = channel;
/*     */       } 
/* 146 */       return channel;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public StreamSourceFrameChannel createChannel(PooledByteBuffer pooled) {
/* 154 */       if (this.frameOpcode == 9) {
/* 155 */         if (this.frameMasked) {
/* 156 */           return new WebSocket07PingFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, new Masker(this.maskingKey), pooled, this.framePayloadLength);
/*     */         }
/* 158 */         return new WebSocket07PingFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, pooled, this.framePayloadLength);
/*     */       } 
/*     */       
/* 161 */       if (this.frameOpcode == 10) {
/* 162 */         if (this.frameMasked) {
/* 163 */           return new WebSocket07PongFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, new Masker(this.maskingKey), pooled, this.framePayloadLength);
/*     */         }
/* 165 */         return new WebSocket07PongFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, pooled, this.framePayloadLength);
/*     */       } 
/*     */       
/* 168 */       if (this.frameOpcode == 8) {
/* 169 */         if (this.frameMasked) {
/* 170 */           return new WebSocket07CloseFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, new Masker(this.maskingKey), pooled, this.framePayloadLength);
/*     */         }
/* 172 */         return new WebSocket07CloseFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, pooled, this.framePayloadLength);
/*     */       } 
/*     */ 
/*     */       
/* 176 */       if (this.frameOpcode == 1) {
/*     */         
/* 178 */         UTF8Checker checker = WebSocket07Channel.this.checker;
/* 179 */         if (checker == null) {
/* 180 */           checker = new UTF8Checker();
/*     */         }
/*     */         
/* 183 */         if (!this.frameFinalFlag) {
/*     */           
/* 185 */           WebSocket07Channel.this.checker = checker;
/*     */         } else {
/*     */           
/* 188 */           WebSocket07Channel.this.checker = null;
/*     */         } 
/*     */         
/* 191 */         if (this.frameMasked) {
/* 192 */           return new WebSocket07TextFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, new Masker(this.maskingKey), checker, pooled, this.framePayloadLength);
/*     */         }
/* 194 */         return new WebSocket07TextFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, checker, pooled, this.framePayloadLength);
/*     */       } 
/* 196 */       if (this.frameOpcode == 2) {
/* 197 */         if (this.frameMasked) {
/* 198 */           return new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, new Masker(this.maskingKey), pooled, this.framePayloadLength);
/*     */         }
/* 200 */         return new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, pooled, this.framePayloadLength);
/*     */       } 
/* 202 */       if (this.frameOpcode == 0) {
/* 203 */         throw new RuntimeException();
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 210 */       if (WebSocket07Channel.this.hasReservedOpCode) {
/* 211 */         if (this.frameMasked) {
/* 212 */           return new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, new Masker(this.maskingKey), pooled, this.framePayloadLength);
/*     */         }
/* 214 */         return new WebSocket07BinaryFrameSourceChannel(WebSocket07Channel.this, this.frameRsv, this.frameFinalFlag, pooled, this.framePayloadLength);
/*     */       } 
/*     */       
/* 217 */       throw WebSocketMessages.MESSAGES.unsupportedOpCode(this.frameOpcode);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void handle(ByteBuffer buffer) throws WebSocketException {
/* 224 */       if (!buffer.hasRemaining()) {
/*     */         return;
/*     */       }
/* 227 */       while (this.state != WebSocket07Channel.State.DONE) {
/*     */         byte b;
/* 229 */         switch (this.state) {
/*     */           
/*     */           case READING_FIRST:
/* 232 */             b = buffer.get();
/* 233 */             this.frameFinalFlag = ((b & 0x80) != 0);
/* 234 */             this.frameRsv = (b & 0x70) >> 4;
/* 235 */             this.frameOpcode = b & 0xF;
/*     */             
/* 237 */             if (WebSocketLogger.REQUEST_LOGGER.isDebugEnabled()) {
/* 238 */               WebSocketLogger.REQUEST_LOGGER.decodingFrameWithOpCode(this.frameOpcode);
/*     */             }
/* 240 */             this.state = WebSocket07Channel.State.READING_SECOND;
/*     */             
/* 242 */             WebSocket07Channel.this.lengthBuffer.clear();
/*     */           case READING_SECOND:
/* 244 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 247 */             b = buffer.get();
/*     */ 
/*     */             
/* 250 */             this.frameMasked = ((b & 0x80) != 0);
/* 251 */             this.framePayloadLen1 = b & Byte.MAX_VALUE;
/*     */             
/* 253 */             if (this.frameRsv != 0 && 
/* 254 */               !WebSocket07Channel.this.areExtensionsSupported()) {
/* 255 */               throw WebSocketMessages.MESSAGES.extensionsNotAllowed(this.frameRsv);
/*     */             }
/*     */ 
/*     */             
/* 259 */             if (this.frameOpcode > 7) {
/* 260 */               validateControlFrame();
/*     */             } else {
/* 262 */               validateDataFrame();
/*     */             } 
/* 264 */             if (this.framePayloadLen1 == 126 || this.framePayloadLen1 == 127) {
/* 265 */               this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE1;
/*     */             } else {
/* 267 */               this.framePayloadLength = this.framePayloadLen1;
/* 268 */               if (this.frameMasked) {
/* 269 */                 this.state = WebSocket07Channel.State.READING_MASK_1; continue;
/*     */               } 
/* 271 */               this.state = WebSocket07Channel.State.DONE;
/*     */               continue;
/*     */             } 
/*     */ 
/*     */           
/*     */           case READING_EXTENDED_SIZE1:
/* 277 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 280 */             b = buffer.get();
/* 281 */             WebSocket07Channel.this.lengthBuffer.put(b);
/* 282 */             this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE2;
/*     */           case READING_EXTENDED_SIZE2:
/* 284 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 287 */             b = buffer.get();
/* 288 */             WebSocket07Channel.this.lengthBuffer.put(b);
/*     */             
/* 290 */             if (this.framePayloadLen1 == 126) {
/* 291 */               WebSocket07Channel.this.lengthBuffer.flip();
/*     */               
/* 293 */               this.framePayloadLength = (WebSocket07Channel.this.lengthBuffer.getShort() & 0xFFFF);
/*     */               
/* 295 */               if (this.frameMasked) {
/* 296 */                 this.state = WebSocket07Channel.State.READING_MASK_1; continue;
/*     */               } 
/* 298 */               this.state = WebSocket07Channel.State.DONE;
/*     */               
/*     */               continue;
/*     */             } 
/* 302 */             this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE3;
/*     */           case READING_EXTENDED_SIZE3:
/* 304 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 307 */             b = buffer.get();
/* 308 */             WebSocket07Channel.this.lengthBuffer.put(b);
/*     */             
/* 310 */             this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE4;
/*     */           case READING_EXTENDED_SIZE4:
/* 312 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 315 */             b = buffer.get();
/* 316 */             WebSocket07Channel.this.lengthBuffer.put(b);
/* 317 */             this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE5;
/*     */           case READING_EXTENDED_SIZE5:
/* 319 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 322 */             b = buffer.get();
/* 323 */             WebSocket07Channel.this.lengthBuffer.put(b);
/* 324 */             this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE6;
/*     */           case READING_EXTENDED_SIZE6:
/* 326 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 329 */             b = buffer.get();
/* 330 */             WebSocket07Channel.this.lengthBuffer.put(b);
/* 331 */             this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE7;
/*     */           case READING_EXTENDED_SIZE7:
/* 333 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 336 */             b = buffer.get();
/* 337 */             WebSocket07Channel.this.lengthBuffer.put(b);
/* 338 */             this.state = WebSocket07Channel.State.READING_EXTENDED_SIZE8;
/*     */           case READING_EXTENDED_SIZE8:
/* 340 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 343 */             b = buffer.get();
/* 344 */             WebSocket07Channel.this.lengthBuffer.put(b);
/*     */             
/* 346 */             WebSocket07Channel.this.lengthBuffer.flip();
/* 347 */             this.framePayloadLength = WebSocket07Channel.this.lengthBuffer.getLong();
/* 348 */             if (this.frameMasked) {
/* 349 */               this.state = WebSocket07Channel.State.READING_MASK_1;
/*     */             } else {
/* 351 */               this.state = WebSocket07Channel.State.DONE;
/*     */               continue;
/*     */             } 
/* 354 */             this.state = WebSocket07Channel.State.READING_MASK_1;
/*     */           case READING_MASK_1:
/* 356 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 359 */             b = buffer.get();
/* 360 */             this.maskingKey = b & 0xFF;
/* 361 */             this.state = WebSocket07Channel.State.READING_MASK_2;
/*     */           case READING_MASK_2:
/* 363 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 366 */             b = buffer.get();
/* 367 */             this.maskingKey = this.maskingKey << 8 | b & 0xFF;
/* 368 */             this.state = WebSocket07Channel.State.READING_MASK_3;
/*     */           case READING_MASK_3:
/* 370 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 373 */             b = buffer.get();
/* 374 */             this.maskingKey = this.maskingKey << 8 | b & 0xFF;
/* 375 */             this.state = WebSocket07Channel.State.READING_MASK_4;
/*     */           case READING_MASK_4:
/* 377 */             if (!buffer.hasRemaining()) {
/*     */               return;
/*     */             }
/* 380 */             b = buffer.get();
/* 381 */             this.maskingKey = this.maskingKey << 8 | b & 0xFF;
/* 382 */             this.state = WebSocket07Channel.State.DONE;
/*     */             continue;
/*     */         } 
/* 385 */         throw new IllegalStateException(this.state.toString());
/*     */       } 
/*     */       
/* 388 */       if (this.frameFinalFlag) {
/*     */         
/* 390 */         if (this.frameOpcode != 9 && this.frameOpcode != 10) {
/* 391 */           WebSocket07Channel.this.fragmentedFramesCount = 0;
/*     */         }
/*     */       } else {
/*     */         
/* 395 */         WebSocket07Channel.this.fragmentedFramesCount++;
/*     */       } 
/* 397 */       this.done = true;
/*     */     }
/*     */ 
/*     */     
/*     */     private void validateDataFrame() throws WebSocketFrameCorruptedException {
/* 402 */       if (!WebSocket07Channel.this.isClient() && !this.frameMasked) {
/* 403 */         throw WebSocketMessages.MESSAGES.frameNotMasked();
/*     */       }
/*     */ 
/*     */       
/* 407 */       if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
/* 408 */         throw WebSocketMessages.MESSAGES.reservedOpCodeInDataFrame(this.frameOpcode);
/*     */       }
/*     */ 
/*     */       
/* 412 */       if (WebSocket07Channel.this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
/* 413 */         throw WebSocketMessages.MESSAGES.continuationFrameOutsideFragmented();
/*     */       }
/*     */ 
/*     */       
/* 417 */       if (WebSocket07Channel.this.fragmentedFramesCount != 0 && this.frameOpcode != 0) {
/* 418 */         throw WebSocketMessages.MESSAGES.nonContinuationFrameInsideFragmented();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     private void validateControlFrame() throws WebSocketFrameCorruptedException {
/* 424 */       if (!this.frameFinalFlag) {
/* 425 */         throw WebSocketMessages.MESSAGES.fragmentedControlFrame();
/*     */       }
/*     */ 
/*     */       
/* 429 */       if (this.framePayloadLen1 > 125) {
/* 430 */         throw WebSocketMessages.MESSAGES.toBigControlFrame();
/*     */       }
/*     */ 
/*     */       
/* 434 */       if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
/* 435 */         throw WebSocketMessages.MESSAGES.reservedOpCodeInControlFrame(this.frameOpcode);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 441 */       if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
/* 442 */         throw WebSocketMessages.MESSAGES.controlFrameWithPayloadLen1();
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDone() {
/* 448 */       return this.done;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getFrameLength() {
/* 453 */       return this.framePayloadLength;
/*     */     }
/*     */     
/*     */     int getMaskingKey() {
/* 457 */       return this.maskingKey;
/*     */     }
/*     */ 
/*     */     
/*     */     public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
/* 462 */       if (this.frameOpcode == 0) {
/* 463 */         StreamSourceFrameChannel ret = WebSocket07Channel.this.fragmentedChannel;
/* 464 */         if (this.frameFinalFlag) {
/* 465 */           WebSocket07Channel.this.fragmentedChannel = null;
/*     */         }
/* 467 */         return (AbstractFramedStreamSourceChannel<?, ?, ?>)ret;
/*     */       } 
/* 469 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFinalFragment() {
/* 474 */       return this.frameFinalFlag;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07Channel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */