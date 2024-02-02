/*     */ package io.undertow.websockets.core.protocol.version07;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import io.undertow.websockets.core.StreamSinkFrameChannel;
/*     */ import io.undertow.websockets.core.WebSocketFrameType;
/*     */ import io.undertow.websockets.core.WebSocketMessages;
/*     */ import io.undertow.websockets.extensions.ExtensionFunction;
/*     */ import io.undertow.websockets.extensions.NoopExtensionFunction;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.ThreadLocalRandom;
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
/*     */ public abstract class WebSocket07FrameSinkChannel
/*     */   extends StreamSinkFrameChannel
/*     */ {
/*     */   private final Masker masker;
/*     */   private volatile boolean dataWritten = false;
/*     */   protected final ExtensionFunction extensionFunction;
/*     */   
/*     */   protected WebSocket07FrameSinkChannel(WebSocket07Channel wsChannel, WebSocketFrameType type) {
/*  46 */     super(wsChannel, type);
/*     */     
/*  48 */     if (wsChannel.isClient()) {
/*  49 */       this.masker = new Masker(0);
/*     */     } else {
/*  51 */       this.masker = null;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     if (wsChannel.areExtensionsSupported() && (type == WebSocketFrameType.TEXT || type == WebSocketFrameType.BINARY)) {
/*  58 */       this.extensionFunction = wsChannel.getExtensionFunction();
/*  59 */       setRsv(this.extensionFunction.writeRsv(0));
/*     */     } else {
/*  61 */       this.extensionFunction = NoopExtensionFunction.INSTANCE;
/*  62 */       setRsv(0);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleFlushComplete(boolean finalFrame) {
/*  68 */     this.dataWritten = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte opCode() {
/*  76 */     if (this.dataWritten) {
/*  77 */       return 0;
/*     */     }
/*  79 */     switch (getType()) {
/*     */       case CONTINUATION:
/*  81 */         return 0;
/*     */       case TEXT:
/*  83 */         return 1;
/*     */       case BINARY:
/*  85 */         return 2;
/*     */       case CLOSE:
/*  87 */         return 8;
/*     */       case PING:
/*  89 */         return 9;
/*     */       case PONG:
/*  91 */         return 10;
/*     */     } 
/*  93 */     throw WebSocketMessages.MESSAGES.unsupportedFrameType(getType());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected SendFrameHeader createFrameHeader() {
/*  99 */     byte b0 = 0;
/*     */ 
/*     */     
/* 102 */     if (isFinalFrameQueued()) {
/* 103 */       b0 = (byte)(b0 | 0x80);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 109 */     byte opCode = opCode();
/*     */     
/* 111 */     int rsv = (opCode == 0) ? 0 : getRsv();
/* 112 */     b0 = (byte)(b0 | (rsv & 0x7) << 4);
/* 113 */     b0 = (byte)(b0 | opCode & 0xF);
/*     */     
/* 115 */     ByteBuffer header = ByteBuffer.allocate(14);
/*     */     
/* 117 */     byte maskKey = 0;
/* 118 */     if (this.masker != null) {
/* 119 */       maskKey = (byte)(maskKey | 0x80);
/*     */     }
/*     */     
/* 122 */     long payloadSize = getBuffer().remaining();
/*     */     
/* 124 */     if (payloadSize > 125L && opCode == 9) {
/* 125 */       throw WebSocketMessages.MESSAGES.invalidPayloadLengthForPing(payloadSize);
/*     */     }
/*     */     
/* 128 */     if (payloadSize <= 125L) {
/* 129 */       header.put(b0);
/* 130 */       header.put((byte)(int)((payloadSize | maskKey) & 0xFFL));
/* 131 */     } else if (payloadSize <= 65535L) {
/* 132 */       header.put(b0);
/* 133 */       header.put((byte)((0x7E | maskKey) & 0xFF));
/* 134 */       header.put((byte)(int)(payloadSize >>> 8L & 0xFFL));
/* 135 */       header.put((byte)(int)(payloadSize & 0xFFL));
/*     */     } else {
/* 137 */       header.put(b0);
/* 138 */       header.put((byte)((Byte.MAX_VALUE | maskKey) & 0xFF));
/* 139 */       header.putLong(payloadSize);
/*     */     } 
/*     */     
/* 142 */     if (this.masker != null) {
/* 143 */       int maskingKey = ThreadLocalRandom.current().nextInt();
/* 144 */       header.put((byte)(maskingKey >> 24 & 0xFF));
/* 145 */       header.put((byte)(maskingKey >> 16 & 0xFF));
/* 146 */       header.put((byte)(maskingKey >> 8 & 0xFF));
/* 147 */       header.put((byte)(maskingKey & 0xFF));
/* 148 */       this.masker.setMaskingKey(maskingKey);
/*     */       
/* 150 */       ByteBuffer buf = getBuffer();
/* 151 */       this.masker.beforeWrite(buf, buf.position(), buf.remaining());
/*     */     } 
/*     */     
/* 154 */     header.flip();
/*     */     
/* 156 */     return new SendFrameHeader(0, (PooledByteBuffer)new ImmediatePooledByteBuffer(header));
/*     */   }
/*     */ 
/*     */   
/*     */   protected PooledByteBuffer preWriteTransform(PooledByteBuffer body) {
/*     */     try {
/* 162 */       return super.preWriteTransform(this.extensionFunction.transformForWrite(body, this, isFinalFrameQueued()));
/* 163 */     } catch (IOException e) {
/* 164 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 165 */       markBroken();
/* 166 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07FrameSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */