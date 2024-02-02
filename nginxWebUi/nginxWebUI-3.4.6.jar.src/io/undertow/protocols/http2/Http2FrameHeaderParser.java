/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
/*     */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.xnio.Bits;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class Http2FrameHeaderParser
/*     */   implements FrameHeaderData
/*     */ {
/*  49 */   final byte[] header = new byte[9];
/*  50 */   int read = 0;
/*     */   
/*     */   int length;
/*     */   
/*     */   int type;
/*     */   int flags;
/*     */   int streamId;
/*  57 */   Http2PushBackParser parser = null;
/*  58 */   Http2HeadersParser continuationParser = null;
/*     */   
/*     */   private static final int SECOND_RESERVED_MASK = -129;
/*     */   private Http2Channel http2Channel;
/*     */   
/*     */   Http2FrameHeaderParser(Http2Channel http2Channel, Http2HeadersParser continuationParser) {
/*  64 */     this.http2Channel = http2Channel;
/*  65 */     this.continuationParser = continuationParser;
/*     */   }
/*     */   
/*     */   public boolean handle(ByteBuffer byteBuffer) throws IOException {
/*  69 */     if (this.parser == null) {
/*  70 */       if (!parseFrameHeader(byteBuffer)) {
/*  71 */         return false;
/*     */       }
/*  73 */       if (this.continuationParser != null && this.type != 9) {
/*  74 */         throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.expectedContinuationFrame());
/*     */       }
/*  76 */       switch (this.type) {
/*     */         case 0:
/*  78 */           if (this.streamId == 0) {
/*  79 */             throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(0));
/*     */           }
/*  81 */           this.parser = new Http2DataFrameParser(this.length);
/*     */           break;
/*     */         
/*     */         case 1:
/*  85 */           if (this.streamId == 0) {
/*  86 */             throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(1));
/*     */           }
/*  88 */           this.parser = new Http2HeadersParser(this.length, this.http2Channel.getDecoder(), this.http2Channel.isClient(), this.http2Channel.getMaxHeaders(), this.streamId, this.http2Channel.getMaxHeaderListSize());
/*  89 */           if (Bits.allAreClear(this.flags, 4)) {
/*  90 */             this.continuationParser = (Http2HeadersParser)this.parser;
/*     */           }
/*     */           break;
/*     */         
/*     */         case 3:
/*  95 */           if (this.length != 4) {
/*  96 */             throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
/*     */           }
/*  98 */           this.parser = new Http2RstStreamParser(this.length);
/*     */           break;
/*     */         
/*     */         case 9:
/* 102 */           if (this.continuationParser == null) {
/* 103 */             this.http2Channel.sendGoAway(1);
/* 104 */             throw UndertowMessages.MESSAGES.http2ContinuationFrameNotExpected();
/*     */           } 
/* 106 */           if (this.continuationParser.getStreamId() != this.streamId) {
/* 107 */             this.http2Channel.sendGoAway(1);
/* 108 */             throw UndertowMessages.MESSAGES.http2ContinuationFrameNotExpected();
/*     */           } 
/* 110 */           this.parser = this.continuationParser;
/* 111 */           this.continuationParser.moreData(this.length);
/*     */           break;
/*     */         
/*     */         case 5:
/* 115 */           this.parser = new Http2PushPromiseParser(this.length, this.http2Channel.getDecoder(), this.http2Channel.isClient(), this.http2Channel.getMaxHeaders(), this.streamId, this.http2Channel.getMaxHeaderListSize());
/* 116 */           if (Bits.allAreClear(this.flags, 4)) {
/* 117 */             this.continuationParser = (Http2HeadersParser)this.parser;
/*     */           }
/*     */           break;
/*     */         
/*     */         case 7:
/* 122 */           if (this.streamId != 0) {
/* 123 */             throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(7));
/*     */           }
/* 125 */           this.parser = new Http2GoAwayParser(this.length);
/*     */           break;
/*     */         
/*     */         case 6:
/* 129 */           if (this.length != 8) {
/* 130 */             throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.invalidPingSize());
/*     */           }
/* 132 */           if (this.streamId != 0) {
/* 133 */             throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(6));
/*     */           }
/* 135 */           this.parser = new Http2PingParser(this.length);
/*     */           break;
/*     */ 
/*     */         
/*     */         case 4:
/* 140 */           if (this.length % 6 != 0) {
/* 141 */             throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
/*     */           }
/* 143 */           if (this.streamId != 0) {
/* 144 */             throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(4));
/*     */           }
/* 146 */           this.parser = new Http2SettingsParser(this.length);
/*     */           break;
/*     */         
/*     */         case 8:
/* 150 */           if (this.length != 4) {
/* 151 */             throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
/*     */           }
/* 153 */           this.parser = new Http2WindowUpdateParser(this.length);
/*     */           break;
/*     */         
/*     */         case 2:
/* 157 */           if (this.length != 5) {
/* 158 */             throw new ConnectionErrorException(6, UndertowMessages.MESSAGES.incorrectFrameSize());
/*     */           }
/* 160 */           if (this.streamId == 0) {
/* 161 */             throw new ConnectionErrorException(1, UndertowMessages.MESSAGES.streamIdMustNotBeZeroForFrameType(2));
/*     */           }
/* 163 */           this.parser = new Http2PriorityParser(this.length);
/*     */           break;
/*     */         
/*     */         default:
/* 167 */           this.parser = new Http2DiscardParser(this.length);
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 172 */     this.parser.parse(byteBuffer, this);
/* 173 */     if (this.continuationParser != null && 
/* 174 */       Bits.anyAreSet(this.flags, 4)) {
/* 175 */       this.continuationParser = null;
/*     */     }
/*     */     
/* 178 */     return this.parser.isFinished();
/*     */   }
/*     */   
/*     */   private boolean parseFrameHeader(ByteBuffer byteBuffer) {
/* 182 */     while (this.read < 9 && byteBuffer.hasRemaining()) {
/* 183 */       this.header[this.read++] = byteBuffer.get();
/*     */     }
/* 185 */     if (this.read != 9) {
/* 186 */       return false;
/*     */     }
/* 188 */     this.length = (this.header[0] & 0xFF) << 16;
/* 189 */     this.length += (this.header[1] & 0xFF) << 8;
/* 190 */     this.length += this.header[2] & 0xFF;
/* 191 */     this.type = this.header[3] & 0xFF;
/* 192 */     this.flags = this.header[4] & 0xFF;
/* 193 */     this.streamId = (this.header[5] & 0xFFFFFF7F & 0xFF) << 24;
/* 194 */     this.streamId += (this.header[6] & 0xFF) << 16;
/* 195 */     this.streamId += (this.header[7] & 0xFF) << 8;
/* 196 */     this.streamId += this.header[8] & 0xFF;
/* 197 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getFrameLength() {
/* 203 */     if (this.type != 0) {
/* 204 */       return 0L;
/*     */     }
/* 206 */     return this.length;
/*     */   }
/*     */   
/*     */   int getActualLength() {
/* 210 */     return this.length;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel() {
/* 216 */     if (this.type == 0 || this.type == 9 || this.type == 2) {
/*     */       Http2StreamSourceChannel http2StreamSourceChannel;
/*     */       
/* 219 */       if (Bits.anyAreSet(this.flags, 1)) {
/* 220 */         http2StreamSourceChannel = this.http2Channel.removeStreamSource(this.streamId);
/* 221 */       } else if (this.type == 9) {
/* 222 */         http2StreamSourceChannel = this.http2Channel.getIncomingStream(this.streamId);
/* 223 */         if (http2StreamSourceChannel != null && http2StreamSourceChannel.isHeadersEndStream() && Bits.anyAreSet(this.flags, 4)) {
/* 224 */           this.http2Channel.removeStreamSource(this.streamId);
/*     */         }
/*     */       } else {
/* 227 */         http2StreamSourceChannel = this.http2Channel.getIncomingStream(this.streamId);
/*     */       } 
/* 229 */       if (this.type == 0 && http2StreamSourceChannel != null) {
/* 230 */         Http2DataFrameParser dataFrameParser = (Http2DataFrameParser)this.parser;
/* 231 */         http2StreamSourceChannel.updateContentSize(getFrameLength() - dataFrameParser.getPadding(), Bits.anyAreSet(this.flags, 1));
/*     */       } 
/* 233 */       return http2StreamSourceChannel;
/* 234 */     }  if (this.type == 1) {
/*     */ 
/*     */       
/* 237 */       Http2StreamSourceChannel channel = this.http2Channel.getIncomingStream(this.streamId);
/* 238 */       if (channel != null) {
/* 239 */         if (Bits.anyAreClear(this.flags, 1)) {
/*     */           
/* 241 */           UndertowLogger.REQUEST_IO_LOGGER.debug("Received HTTP/2 trailers header without end stream set");
/* 242 */           this.http2Channel.sendGoAway(1);
/*     */         } 
/* 244 */         if (!channel.isHeadersEndStream() && Bits.anyAreSet(this.flags, 4)) {
/* 245 */           this.http2Channel.removeStreamSource(this.streamId);
/*     */         }
/*     */       } 
/* 248 */       return channel;
/*     */     } 
/* 250 */     return null;
/*     */   }
/*     */   
/*     */   Http2PushBackParser getParser() {
/* 254 */     return this.parser;
/*     */   }
/*     */   
/*     */   Http2HeadersParser getContinuationParser() {
/* 258 */     return this.continuationParser;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2FrameHeaderParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */