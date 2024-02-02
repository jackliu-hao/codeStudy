/*     */ package io.undertow.protocols.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ abstract class Http2HeaderBlockParser
/*     */   extends Http2PushBackParser
/*     */   implements HpackDecoder.HeaderEmitter
/*     */ {
/*  43 */   private final HeaderMap headerMap = new HeaderMap();
/*     */   
/*     */   private boolean beforeHeadersHandled = false;
/*     */   private final HpackDecoder decoder;
/*  47 */   private int frameRemaining = -1;
/*     */   
/*     */   private boolean invalid = false;
/*     */   
/*     */   private boolean processingPseudoHeaders = true;
/*     */   
/*     */   private final boolean client;
/*     */   private final int maxHeaders;
/*     */   private final int maxHeaderListSize;
/*     */   private int currentPadding;
/*     */   private final int streamId;
/*     */   private int headerSize;
/*     */   private static final Set<HttpString> SERVER_HEADERS;
/*     */   
/*     */   static {
/*  62 */     Set<HttpString> server = new HashSet<>();
/*  63 */     server.add(Http2Channel.METHOD);
/*  64 */     server.add(Http2Channel.AUTHORITY);
/*  65 */     server.add(Http2Channel.SCHEME);
/*  66 */     server.add(Http2Channel.PATH);
/*  67 */     SERVER_HEADERS = Collections.unmodifiableSet(server);
/*     */   }
/*     */   
/*     */   Http2HeaderBlockParser(int frameLength, HpackDecoder decoder, boolean client, int maxHeaders, int streamId, int maxHeaderListSize) {
/*  71 */     super(frameLength);
/*  72 */     this.decoder = decoder;
/*  73 */     this.client = client;
/*  74 */     this.maxHeaders = maxHeaders;
/*  75 */     this.streamId = streamId;
/*  76 */     this.maxHeaderListSize = maxHeaderListSize;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser header) throws IOException {
/*  81 */     boolean continuationFramesComing = Bits.anyAreClear(header.flags, 4);
/*  82 */     if (this.frameRemaining == -1) {
/*  83 */       this.frameRemaining = header.length;
/*     */     }
/*  85 */     boolean moreDataThisFrame = (resource.remaining() < this.frameRemaining);
/*  86 */     int pos = resource.position();
/*  87 */     int readInBeforeHeader = 0;
/*     */     try {
/*  89 */       if (!this.beforeHeadersHandled) {
/*  90 */         if (!handleBeforeHeader(resource, header)) {
/*     */           return;
/*     */         }
/*  93 */         this.currentPadding = getPaddingLength();
/*  94 */         readInBeforeHeader = resource.position() - pos;
/*     */       } 
/*  96 */       this.beforeHeadersHandled = true;
/*  97 */       this.decoder.setHeaderEmitter(this);
/*  98 */       int oldLimit = -1;
/*  99 */       if (this.currentPadding > 0) {
/* 100 */         int actualData = this.frameRemaining - readInBeforeHeader - this.currentPadding;
/* 101 */         if (actualData < 0) {
/* 102 */           throw new ConnectionErrorException(1);
/*     */         }
/* 104 */         if (resource.remaining() > actualData) {
/* 105 */           oldLimit = resource.limit();
/* 106 */           resource.limit(resource.position() + actualData);
/*     */         } 
/*     */       } 
/*     */       try {
/* 110 */         this.decoder.decode(resource, (moreDataThisFrame || continuationFramesComing));
/* 111 */       } catch (HpackException e) {
/* 112 */         throw new ConnectionErrorException(e.getCloseCode(), e);
/*     */       } 
/*     */       
/* 115 */       if (this.maxHeaders > 0 && this.headerMap.size() > this.maxHeaders) {
/* 116 */         throw new StreamErrorException(6);
/*     */       }
/* 118 */       if (oldLimit != -1) {
/* 119 */         if (resource.remaining() == 0) {
/* 120 */           int paddingInBuffer = oldLimit - resource.limit();
/* 121 */           this.currentPadding -= paddingInBuffer;
/* 122 */           resource.limit(oldLimit);
/* 123 */           resource.position(oldLimit);
/*     */         } else {
/* 125 */           resource.limit(oldLimit);
/*     */         } 
/*     */       }
/*     */     } finally {
/* 129 */       int used = resource.position() - pos;
/* 130 */       this.frameRemaining -= used;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   HeaderMap getHeaderMap() {
/* 138 */     return this.headerMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void emitHeader(HttpString name, String value, boolean neverIndex) throws HpackException {
/* 143 */     if (this.maxHeaderListSize > 0) {
/* 144 */       this.headerSize += name.length() + value.length() + 32;
/* 145 */       if (this.headerSize > this.maxHeaderListSize) {
/* 146 */         throw new HpackException(UndertowMessages.MESSAGES.headerBlockTooLarge(), 1);
/*     */       }
/*     */     } 
/* 149 */     if (this.maxHeaders > 0 && this.headerMap.size() > this.maxHeaders) {
/*     */       return;
/*     */     }
/* 152 */     this.headerMap.add(name, value);
/*     */     
/* 154 */     if (name.length() == 0) {
/* 155 */       throw UndertowMessages.MESSAGES.invalidHeader();
/*     */     }
/* 157 */     if (name.equals(Headers.TRANSFER_ENCODING)) {
/* 158 */       throw new HpackException(1);
/*     */     }
/* 160 */     if (name.byteAt(0) == 58) {
/* 161 */       if (this.client) {
/* 162 */         if (!name.equals(Http2Channel.STATUS)) {
/* 163 */           this.invalid = true;
/*     */         }
/*     */       }
/* 166 */       else if (!SERVER_HEADERS.contains(name)) {
/* 167 */         this.invalid = true;
/*     */       } 
/*     */       
/* 170 */       if (!this.processingPseudoHeaders) {
/* 171 */         throw new HpackException(UndertowMessages.MESSAGES.pseudoHeaderInWrongOrder(name), 1);
/*     */       }
/*     */     } else {
/* 174 */       this.processingPseudoHeaders = false;
/*     */     } 
/* 176 */     for (int i = 0; i < name.length(); i++) {
/* 177 */       byte c = name.byteAt(i);
/* 178 */       if (c >= 65 && c <= 90) {
/* 179 */         this.invalid = true;
/* 180 */         UndertowLogger.REQUEST_LOGGER.debugf("Malformed request, header %s contains uppercase characters", name);
/* 181 */       } else if (c != 58 && !Connectors.isValidTokenCharacter(c)) {
/* 182 */         this.invalid = true;
/* 183 */         UndertowLogger.REQUEST_LOGGER.debugf("Malformed request, header %s contains invalid token character", name);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void moreData(int data) {
/* 191 */     super.moreData(data);
/* 192 */     this.frameRemaining += data;
/*     */   }
/*     */   
/*     */   public boolean isInvalid() {
/* 196 */     return this.invalid;
/*     */   }
/*     */   
/*     */   public int getStreamId() {
/* 200 */     return this.streamId;
/*     */   }
/*     */   
/*     */   protected abstract boolean handleBeforeHeader(ByteBuffer paramByteBuffer, Http2FrameHeaderParser paramHttp2FrameHeaderParser);
/*     */   
/*     */   protected abstract int getPaddingLength();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2HeaderBlockParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */