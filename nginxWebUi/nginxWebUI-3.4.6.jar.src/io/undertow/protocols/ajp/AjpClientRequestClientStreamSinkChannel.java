/*     */ package io.undertow.protocols.ajp;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.client.ProxiedRequestAttachments;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HexConverter;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import java.io.IOException;
/*     */ import java.nio.BufferOverflowException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AjpClientRequestClientStreamSinkChannel
/*     */   extends AbstractAjpClientStreamSinkChannel
/*     */ {
/*     */   private final ChannelListener<AjpClientRequestClientStreamSinkChannel> finishListener;
/*     */   public static final int DEFAULT_MAX_DATA_SIZE = 8192;
/*     */   private final HeaderMap headers;
/*     */   private final String path;
/*     */   private final HttpString method;
/*     */   private final HttpString protocol;
/*     */   private final Attachable attachable;
/*     */   private boolean firstFrameWritten = false;
/*     */   private long dataSize;
/*  71 */   private int requestedChunkSize = -1;
/*     */   
/*     */   private SendFrameHeader header;
/*     */   
/*     */   private boolean discardMode = false;
/*     */ 
/*     */   
/*     */   AjpClientRequestClientStreamSinkChannel(AjpClientChannel channel, ChannelListener<AjpClientRequestClientStreamSinkChannel> finishListener, HeaderMap headers, String path, HttpString method, HttpString protocol, Attachable attachable) {
/*  79 */     super(channel);
/*  80 */     this.finishListener = finishListener;
/*  81 */     this.headers = headers;
/*  82 */     this.path = path;
/*  83 */     this.method = method;
/*  84 */     this.protocol = protocol;
/*  85 */     this.attachable = attachable;
/*     */   }
/*     */ 
/*     */   
/*     */   private SendFrameHeader createFrameHeaderImpl() {
/*  90 */     if (this.discardMode) {
/*  91 */       getBuffer().clear();
/*  92 */       getBuffer().flip();
/*  93 */       return new SendFrameHeader((PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.wrap(new byte[0])));
/*     */     } 
/*  95 */     PooledByteBuffer pooledHeaderBuffer = ((AjpClientChannel)getChannel()).getBufferPool().allocate();
/*     */     
/*     */     try {
/*  98 */       ByteBuffer buffer = pooledHeaderBuffer.getBuffer();
/*  99 */       ByteBuffer dataBuffer = getBuffer();
/* 100 */       int dataInBuffer = dataBuffer.remaining();
/* 101 */       if (!this.firstFrameWritten && this.requestedChunkSize == 0)
/*     */       {
/* 103 */         return new SendFrameHeader(dataInBuffer, null);
/*     */       }
/* 105 */       int maxData = ((AjpClientChannel)getChannel()).getSettings().get(UndertowOptions.MAX_AJP_PACKET_SIZE, 8192) - 6;
/*     */       
/* 107 */       if (!this.firstFrameWritten) {
/* 108 */         String path, queryString, contentLength = this.headers.getFirst(Headers.CONTENT_LENGTH);
/* 109 */         if (contentLength != null) {
/* 110 */           this.dataSize = Long.parseLong(contentLength);
/* 111 */           this.requestedChunkSize = maxData;
/* 112 */           if (dataInBuffer > this.dataSize) {
/* 113 */             throw UndertowMessages.MESSAGES.fixedLengthOverflow();
/*     */           }
/* 115 */         } else if (isWritesShutdown() && !this.headers.contains(Headers.TRANSFER_ENCODING)) {
/*     */           
/* 117 */           this.headers.put(Headers.CONTENT_LENGTH, dataInBuffer);
/* 118 */           this.dataSize = dataInBuffer;
/* 119 */           this.requestedChunkSize = maxData;
/*     */         } else {
/* 121 */           this.headers.put(Headers.TRANSFER_ENCODING, Headers.CHUNKED.toString());
/* 122 */           this.dataSize = -1L;
/* 123 */           this.requestedChunkSize = 0;
/*     */         } 
/*     */         
/* 126 */         this.firstFrameWritten = true;
/*     */ 
/*     */         
/* 129 */         int qsIndex = this.path.indexOf('?');
/* 130 */         if (qsIndex == -1) {
/* 131 */           path = this.path;
/* 132 */           queryString = null;
/*     */         } else {
/* 134 */           path = this.path.substring(0, qsIndex);
/* 135 */           queryString = this.path.substring(qsIndex + 1);
/*     */         } 
/*     */         
/* 138 */         buffer.put((byte)18);
/* 139 */         buffer.put((byte)52);
/* 140 */         buffer.put((byte)0);
/* 141 */         buffer.put((byte)0);
/* 142 */         buffer.put((byte)2);
/* 143 */         boolean storeMethod = false;
/* 144 */         Integer methodNp = AjpConstants.HTTP_METHODS_MAP.get(this.method);
/* 145 */         if (methodNp == null) {
/* 146 */           methodNp = Integer.valueOf(255);
/* 147 */           storeMethod = true;
/*     */         } 
/* 149 */         buffer.put((byte)methodNp.intValue());
/* 150 */         AjpUtils.putHttpString(buffer, this.protocol);
/* 151 */         AjpUtils.putString(buffer, path);
/* 152 */         AjpUtils.putString(buffer, AjpUtils.notNull((String)this.attachable.getAttachment(ProxiedRequestAttachments.REMOTE_ADDRESS)));
/* 153 */         AjpUtils.putString(buffer, AjpUtils.notNull((String)this.attachable.getAttachment(ProxiedRequestAttachments.REMOTE_HOST)));
/* 154 */         AjpUtils.putString(buffer, AjpUtils.notNull((String)this.attachable.getAttachment(ProxiedRequestAttachments.SERVER_NAME)));
/* 155 */         AjpUtils.putInt(buffer, AjpUtils.notNull((Integer)this.attachable.getAttachment(ProxiedRequestAttachments.SERVER_PORT)));
/* 156 */         buffer.put((byte)(AjpUtils.notNull((Boolean)this.attachable.getAttachment(ProxiedRequestAttachments.IS_SSL)) ? 1 : 0));
/*     */         
/* 158 */         int headers = 0;
/*     */         
/* 160 */         HeaderMap responseHeaders = this.headers;
/* 161 */         for (HttpString name : responseHeaders.getHeaderNames()) {
/* 162 */           headers += responseHeaders.get(name).size();
/*     */         }
/*     */         
/* 165 */         AjpUtils.putInt(buffer, headers);
/*     */ 
/*     */         
/* 168 */         for (HttpString header : responseHeaders.getHeaderNames()) {
/* 169 */           for (String headerValue : responseHeaders.get(header)) {
/* 170 */             Integer headerCode = AjpConstants.HEADER_MAP.get(header);
/* 171 */             if (headerCode != null) {
/* 172 */               AjpUtils.putInt(buffer, headerCode.intValue());
/*     */             } else {
/* 174 */               AjpUtils.putHttpString(buffer, header);
/*     */             } 
/* 176 */             AjpUtils.putString(buffer, headerValue);
/*     */           } 
/*     */         } 
/*     */         
/* 180 */         if (queryString != null) {
/* 181 */           buffer.put((byte)5);
/* 182 */           AjpUtils.putString(buffer, queryString);
/*     */         } 
/* 184 */         String remoteUser = (String)this.attachable.getAttachment(ProxiedRequestAttachments.REMOTE_USER);
/* 185 */         if (remoteUser != null) {
/* 186 */           buffer.put((byte)3);
/* 187 */           AjpUtils.putString(buffer, remoteUser);
/*     */         } 
/* 189 */         String authType = (String)this.attachable.getAttachment(ProxiedRequestAttachments.AUTH_TYPE);
/* 190 */         if (authType != null) {
/* 191 */           buffer.put((byte)4);
/* 192 */           AjpUtils.putString(buffer, authType);
/*     */         } 
/* 194 */         String route = (String)this.attachable.getAttachment(ProxiedRequestAttachments.ROUTE);
/* 195 */         if (route != null) {
/* 196 */           buffer.put((byte)6);
/* 197 */           AjpUtils.putString(buffer, route);
/*     */         } 
/* 199 */         String sslCert = (String)this.attachable.getAttachment(ProxiedRequestAttachments.SSL_CERT);
/* 200 */         if (sslCert != null) {
/* 201 */           buffer.put((byte)7);
/* 202 */           AjpUtils.putString(buffer, sslCert);
/*     */         } 
/* 204 */         String sslCypher = (String)this.attachable.getAttachment(ProxiedRequestAttachments.SSL_CYPHER);
/* 205 */         if (sslCypher != null) {
/* 206 */           buffer.put((byte)8);
/* 207 */           AjpUtils.putString(buffer, sslCypher);
/*     */         } 
/* 209 */         byte[] sslSession = (byte[])this.attachable.getAttachment(ProxiedRequestAttachments.SSL_SESSION_ID);
/* 210 */         if (sslSession != null) {
/* 211 */           buffer.put((byte)9);
/* 212 */           AjpUtils.putString(buffer, HexConverter.convertToHexString(sslSession));
/*     */         } 
/* 214 */         Integer sslKeySize = (Integer)this.attachable.getAttachment(ProxiedRequestAttachments.SSL_KEY_SIZE);
/* 215 */         if (sslKeySize != null) {
/* 216 */           buffer.put((byte)11);
/* 217 */           AjpUtils.putString(buffer, sslKeySize.toString());
/*     */         } 
/* 219 */         String secret = (String)this.attachable.getAttachment(ProxiedRequestAttachments.SECRET);
/* 220 */         if (secret != null) {
/* 221 */           buffer.put((byte)12);
/* 222 */           AjpUtils.putString(buffer, secret);
/*     */         } 
/*     */         
/* 225 */         if (storeMethod) {
/* 226 */           buffer.put((byte)13);
/* 227 */           AjpUtils.putString(buffer, this.method.toString());
/*     */         } 
/* 229 */         buffer.put((byte)-1);
/*     */         
/* 231 */         int dataLength = buffer.position() - 4;
/* 232 */         buffer.put(2, (byte)(dataLength >> 8 & 0xFF));
/* 233 */         buffer.put(3, (byte)(dataLength & 0xFF));
/*     */       } 
/* 235 */       if (this.dataSize == 0L) {
/*     */         
/* 237 */         buffer.flip();
/* 238 */         return new SendFrameHeader(pooledHeaderBuffer);
/* 239 */       }  if (this.requestedChunkSize > 0) {
/*     */         
/* 241 */         if (isWritesShutdown() && dataInBuffer == 0) {
/* 242 */           buffer.put((byte)18);
/* 243 */           buffer.put((byte)52);
/* 244 */           buffer.put((byte)0);
/* 245 */           buffer.put((byte)2);
/* 246 */           buffer.put((byte)0);
/* 247 */           buffer.put((byte)0);
/* 248 */           buffer.flip();
/* 249 */           return new SendFrameHeader(pooledHeaderBuffer);
/*     */         } 
/* 251 */         int remaining = dataInBuffer;
/* 252 */         remaining = Math.min(remaining, maxData);
/* 253 */         remaining = Math.min(remaining, this.requestedChunkSize);
/* 254 */         int bodySize = remaining + 2;
/* 255 */         buffer.put((byte)18);
/* 256 */         buffer.put((byte)52);
/* 257 */         buffer.put((byte)(bodySize >> 8 & 0xFF));
/* 258 */         buffer.put((byte)(bodySize & 0xFF));
/* 259 */         buffer.put((byte)(remaining >> 8 & 0xFF));
/* 260 */         buffer.put((byte)(remaining & 0xFF));
/* 261 */         this.requestedChunkSize = 0;
/* 262 */         if (remaining < dataInBuffer) {
/* 263 */           dataBuffer.limit(getBuffer().position() + remaining);
/* 264 */           buffer.flip();
/* 265 */           return new SendFrameHeader(dataInBuffer - remaining, pooledHeaderBuffer, (this.dataSize < 0L));
/*     */         } 
/* 267 */         buffer.flip();
/* 268 */         return new SendFrameHeader(0, pooledHeaderBuffer, (this.dataSize < 0L));
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 273 */       buffer.flip();
/* 274 */       if (buffer.remaining() == 0) {
/* 275 */         pooledHeaderBuffer.close();
/* 276 */         return new SendFrameHeader(dataInBuffer, null, true);
/*     */       } 
/* 278 */       dataBuffer.limit(dataBuffer.position());
/* 279 */       return new SendFrameHeader(dataInBuffer, pooledHeaderBuffer, true);
/*     */     }
/* 281 */     catch (BufferOverflowException e) {
/*     */       
/* 283 */       pooledHeaderBuffer.close();
/* 284 */       markBroken();
/* 285 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   SendFrameHeader generateSendFrameHeader() {
/* 290 */     this.header = createFrameHeaderImpl();
/* 291 */     return this.header;
/*     */   }
/*     */   
/*     */   void chunkRequested(int size) throws IOException {
/* 295 */     this.requestedChunkSize = size;
/* 296 */     ((AjpClientChannel)getChannel()).recalculateHeldFrames();
/*     */   }
/*     */   
/*     */   public void startDiscard() {
/* 300 */     this.discardMode = true;
/*     */     try {
/* 302 */       ((AjpClientChannel)getChannel()).recalculateHeldFrames();
/* 303 */     } catch (IOException e) {
/* 304 */       markBroken();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected final SendFrameHeader createFrameHeader() {
/* 310 */     SendFrameHeader header = this.header;
/* 311 */     this.header = null;
/* 312 */     return header;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleFlushComplete(boolean finalFrame) {
/* 317 */     super.handleFlushComplete(finalFrame);
/*     */     
/* 319 */     if (finalFrame) {
/* 320 */       ((AjpClientChannel)getChannel()).sinkDone();
/*     */     }
/* 322 */     if (finalFrame && this.finishListener != null) {
/* 323 */       this.finishListener.handleEvent((Channel)this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void channelForciblyClosed() throws IOException {
/* 329 */     super.channelForciblyClosed();
/* 330 */     ((AjpClientChannel)getChannel()).sinkDone();
/* 331 */     this.finishListener.handleEvent((Channel)this);
/*     */   }
/*     */   
/*     */   public void clearHeader() {
/* 335 */     this.header = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpClientRequestClientStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */