/*     */ package io.undertow.server.protocol.proxy;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.protocols.ssl.UndertowXnioSsl;
/*     */ import io.undertow.server.DelegateOpenListener;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.util.NetworkUtils;
/*     */ import io.undertow.util.PooledAdaptor;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.PushBackStreamSourceConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ import org.xnio.ssl.SslConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ProxyProtocolReadListener
/*     */   implements ChannelListener<StreamSourceChannel>
/*     */ {
/*     */   private static final int MAX_HEADER_LENGTH = 107;
/*  40 */   private static final byte[] NAME = "PROXY ".getBytes(StandardCharsets.US_ASCII);
/*     */   
/*     */   private static final String UNKNOWN = "UNKNOWN";
/*     */   private static final String TCP4 = "TCP4";
/*     */   private static final String TCP6 = "TCP6";
/*  45 */   private static final byte[] SIG = new byte[] { 13, 10, 13, 10, 0, 13, 10, 81, 85, 73, 84, 10 };
/*     */   
/*     */   private final StreamConnection streamConnection;
/*     */   
/*     */   private final OpenListener openListener;
/*     */   private final UndertowXnioSsl ssl;
/*     */   private final ByteBufferPool bufferPool;
/*     */   private final OptionMap sslOptionMap;
/*     */   private int byteCount;
/*     */   private String protocol;
/*     */   private InetAddress sourceAddress;
/*     */   private InetAddress destAddress;
/*  57 */   private int sourcePort = -1;
/*  58 */   private int destPort = -1;
/*  59 */   private StringBuilder stringBuilder = new StringBuilder();
/*     */   
/*     */   private boolean carriageReturnSeen = false;
/*     */   private boolean parsingUnknown = false;
/*     */   
/*     */   ProxyProtocolReadListener(StreamConnection streamConnection, OpenListener openListener, UndertowXnioSsl ssl, ByteBufferPool bufferPool, OptionMap sslOptionMap) {
/*  65 */     this.streamConnection = streamConnection;
/*  66 */     this.openListener = openListener;
/*  67 */     this.ssl = ssl;
/*  68 */     this.bufferPool = bufferPool;
/*  69 */     this.sslOptionMap = sslOptionMap;
/*  70 */     if (bufferPool.getBufferSize() < 107) {
/*  71 */       throw UndertowMessages.MESSAGES.bufferPoolTooSmall(107);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleEvent(StreamSourceChannel streamSourceChannel) {
/*  77 */     PooledByteBuffer buffer = this.bufferPool.allocate();
/*  78 */     AtomicBoolean freeBuffer = new AtomicBoolean(true);
/*     */     try {
/*  80 */       int res = streamSourceChannel.read(buffer.getBuffer());
/*  81 */       if (res == -1) {
/*  82 */         IoUtils.safeClose((Closeable)this.streamConnection); return;
/*     */       } 
/*  84 */       if (res == 0) {
/*     */         return;
/*     */       }
/*  87 */       buffer.getBuffer().flip();
/*     */       
/*  89 */       if (buffer.getBuffer().hasRemaining()) {
/*  90 */         byte firstByte = buffer.getBuffer().get();
/*  91 */         this.byteCount++;
/*  92 */         if (firstByte == SIG[0]) {
/*  93 */           parseProxyProtocolV2(buffer, freeBuffer);
/*  94 */         } else if ((char)firstByte == NAME[0]) {
/*  95 */           parseProxyProtocolV1(buffer, freeBuffer);
/*     */         } else {
/*  97 */           throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/*     */       return;
/* 103 */     } catch (IOException e) {
/* 104 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 105 */       IoUtils.safeClose((Closeable)this.streamConnection);
/* 106 */     } catch (Exception e) {
/* 107 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(e));
/* 108 */       IoUtils.safeClose((Closeable)this.streamConnection);
/*     */     } finally {
/* 110 */       if (freeBuffer.get()) {
/* 111 */         buffer.close();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseProxyProtocolV2(PooledByteBuffer buffer, AtomicBoolean freeBuffer) throws Exception {
/*     */     byte[] sourceAddressBytes, dstAddressBytes;
/* 119 */     while (this.byteCount < SIG.length) {
/* 120 */       byte c = buffer.getBuffer().get();
/*     */ 
/*     */       
/* 123 */       if (c != SIG[this.byteCount]) {
/* 124 */         throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */       }
/* 126 */       this.byteCount++;
/*     */     } 
/*     */     
/* 129 */     byte ver_cmd = buffer.getBuffer().get();
/* 130 */     byte fam = buffer.getBuffer().get();
/* 131 */     int len = buffer.getBuffer().getShort() & 0xFFFF;
/*     */     
/* 133 */     if ((ver_cmd & 0xF0) != 32) {
/* 134 */       throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */     }
/*     */     
/* 137 */     switch (ver_cmd & 0xF) {
/*     */       case 1:
/* 139 */         switch (fam) {
/*     */           case 17:
/* 141 */             if (len < 12) {
/* 142 */               throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */             }
/*     */             
/* 145 */             sourceAddressBytes = new byte[4];
/* 146 */             buffer.getBuffer().get(sourceAddressBytes);
/* 147 */             this.sourceAddress = InetAddress.getByAddress(sourceAddressBytes);
/*     */             
/* 149 */             dstAddressBytes = new byte[4];
/* 150 */             buffer.getBuffer().get(dstAddressBytes);
/* 151 */             this.destAddress = InetAddress.getByAddress(dstAddressBytes);
/*     */             
/* 153 */             this.sourcePort = buffer.getBuffer().getShort() & 0xFFFF;
/* 154 */             this.destPort = buffer.getBuffer().getShort() & 0xFFFF;
/*     */             
/* 156 */             if (len > 12) {
/* 157 */               int skipAhead = len - 12;
/* 158 */               int currentPosition = buffer.getBuffer().position();
/* 159 */               buffer.getBuffer().position(currentPosition + skipAhead);
/*     */             } 
/*     */             break;
/*     */ 
/*     */ 
/*     */           
/*     */           case 33:
/* 166 */             if (len < 36) {
/* 167 */               throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */             }
/*     */             
/* 170 */             sourceAddressBytes = new byte[16];
/* 171 */             buffer.getBuffer().get(sourceAddressBytes);
/* 172 */             this.sourceAddress = InetAddress.getByAddress(sourceAddressBytes);
/*     */             
/* 174 */             dstAddressBytes = new byte[16];
/* 175 */             buffer.getBuffer().get(dstAddressBytes);
/* 176 */             this.destAddress = InetAddress.getByAddress(dstAddressBytes);
/*     */             
/* 178 */             this.sourcePort = buffer.getBuffer().getShort() & 0xFFFF;
/* 179 */             this.destPort = buffer.getBuffer().getShort() & 0xFFFF;
/*     */             
/* 181 */             if (len > 36) {
/* 182 */               int skipAhead = len - 36;
/* 183 */               int currentPosition = buffer.getBuffer().position();
/* 184 */               buffer.getBuffer().position(currentPosition + skipAhead);
/*     */             } 
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 191 */         throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */ 
/*     */ 
/*     */       
/*     */       case 0:
/* 196 */         if (len > 0) {
/* 197 */           int skipAhead = len;
/* 198 */           int currentPosition = buffer.getBuffer().position();
/* 199 */           buffer.getBuffer().position(currentPosition + skipAhead);
/*     */         } 
/*     */         
/* 202 */         if (buffer.getBuffer().hasRemaining()) {
/* 203 */           freeBuffer.set(false);
/* 204 */           proxyAccept(null, null, buffer);
/*     */         } else {
/* 206 */           proxyAccept(null, null, null);
/*     */         } 
/*     */         return;
/*     */       default:
/* 210 */         throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */     } 
/*     */ 
/*     */     
/* 214 */     SocketAddress s = new InetSocketAddress(this.sourceAddress, this.sourcePort);
/* 215 */     SocketAddress d = new InetSocketAddress(this.destAddress, this.destPort);
/* 216 */     if (buffer.getBuffer().hasRemaining()) {
/* 217 */       freeBuffer.set(false);
/* 218 */       proxyAccept(s, d, buffer);
/*     */     } else {
/* 220 */       proxyAccept(s, d, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseProxyProtocolV1(PooledByteBuffer buffer, AtomicBoolean freeBuffer) throws Exception {
/* 226 */     while (buffer.getBuffer().hasRemaining()) {
/* 227 */       char c = (char)buffer.getBuffer().get();
/* 228 */       if (this.byteCount < NAME.length)
/*     */       
/* 230 */       { if (c != NAME[this.byteCount]) {
/* 231 */           throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */         } }
/*     */       
/* 234 */       else if (this.parsingUnknown)
/*     */       
/*     */       { 
/* 237 */         if (c == '\r')
/* 238 */         { this.carriageReturnSeen = true; }
/* 239 */         else { if (c == '\n') {
/* 240 */             if (!this.carriageReturnSeen) {
/* 241 */               throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */             }
/*     */             
/* 244 */             if (buffer.getBuffer().hasRemaining()) {
/* 245 */               freeBuffer.set(false);
/* 246 */               proxyAccept(null, null, buffer);
/*     */             } else {
/* 248 */               proxyAccept(null, null, null);
/*     */             }  return;
/*     */           } 
/* 251 */           if (this.carriageReturnSeen)
/* 252 */             throw UndertowMessages.MESSAGES.invalidProxyHeader();  }
/*     */          }
/* 254 */       else { if (this.carriageReturnSeen) {
/* 255 */           if (c == '\n') {
/*     */             
/* 257 */             SocketAddress s = new InetSocketAddress(this.sourceAddress, this.sourcePort);
/* 258 */             SocketAddress d = new InetSocketAddress(this.destAddress, this.destPort);
/* 259 */             if (buffer.getBuffer().hasRemaining()) {
/* 260 */               freeBuffer.set(false);
/* 261 */               proxyAccept(s, d, buffer);
/*     */             } else {
/* 263 */               proxyAccept(s, d, null);
/*     */             } 
/*     */             return;
/*     */           } 
/* 267 */           throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */         } 
/* 269 */         switch (c) {
/*     */           
/*     */           case ' ':
/* 272 */             if (this.sourcePort != -1 || this.stringBuilder.length() == 0)
/*     */             {
/* 274 */               throw UndertowMessages.MESSAGES.invalidProxyHeader(); } 
/* 275 */             if (this.protocol == null) {
/* 276 */               this.protocol = this.stringBuilder.toString();
/* 277 */               this.stringBuilder.setLength(0);
/* 278 */               if (this.protocol.equals("UNKNOWN")) {
/* 279 */                 this.parsingUnknown = true; break;
/* 280 */               }  if (!this.protocol.equals("TCP4") && !this.protocol.equals("TCP6"))
/* 281 */                 throw UndertowMessages.MESSAGES.invalidProxyHeader();  break;
/*     */             } 
/* 283 */             if (this.sourceAddress == null) {
/* 284 */               this.sourceAddress = parseAddress(this.stringBuilder.toString(), this.protocol);
/* 285 */               this.stringBuilder.setLength(0); break;
/* 286 */             }  if (this.destAddress == null) {
/* 287 */               this.destAddress = parseAddress(this.stringBuilder.toString(), this.protocol);
/* 288 */               this.stringBuilder.setLength(0); break;
/*     */             } 
/* 290 */             this.sourcePort = Integer.parseInt(this.stringBuilder.toString());
/* 291 */             this.stringBuilder.setLength(0);
/*     */             break;
/*     */           
/*     */           case '\r':
/* 295 */             if (this.destPort == -1 && this.sourcePort != -1 && !this.carriageReturnSeen && this.stringBuilder.length() > 0) {
/* 296 */               this.destPort = Integer.parseInt(this.stringBuilder.toString());
/* 297 */               this.stringBuilder.setLength(0);
/* 298 */               this.carriageReturnSeen = true; break;
/* 299 */             }  if (this.protocol == null) {
/* 300 */               if ("UNKNOWN".equals(this.stringBuilder.toString())) {
/* 301 */                 this.parsingUnknown = true;
/* 302 */                 this.carriageReturnSeen = true;
/*     */               }  break;
/*     */             } 
/* 305 */             throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */ 
/*     */           
/*     */           case '\n':
/* 309 */             throw UndertowMessages.MESSAGES.invalidProxyHeader();
/*     */           default:
/* 311 */             this.stringBuilder.append(c);
/*     */             break;
/*     */         } 
/*     */          }
/*     */       
/* 316 */       this.byteCount++;
/* 317 */       if (this.byteCount == 107) {
/* 318 */         throw UndertowMessages.MESSAGES.headerSizeToLarge();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void proxyAccept(SocketAddress source, SocketAddress dest, PooledByteBuffer additionalData) {
/*     */     SslConnection sslConnection;
/* 326 */     StreamConnection streamConnection = this.streamConnection;
/* 327 */     if (source != null) {
/* 328 */       streamConnection = new AddressWrappedConnection(streamConnection, source, dest);
/*     */     }
/* 330 */     if (this.ssl != null) {
/*     */ 
/*     */       
/* 333 */       if (additionalData != null) {
/* 334 */         PushBackStreamSourceConduit conduit = new PushBackStreamSourceConduit(streamConnection.getSourceChannel().getConduit());
/* 335 */         conduit.pushBack((Pooled)new PooledAdaptor(additionalData));
/* 336 */         streamConnection.getSourceChannel().setConduit((StreamSourceConduit)conduit);
/*     */       } 
/* 338 */       SslConnection sslConnection1 = this.ssl.wrapExistingConnection(streamConnection, (this.sslOptionMap == null) ? OptionMap.EMPTY : this.sslOptionMap, false);
/* 339 */       sslConnection = sslConnection1;
/*     */       
/* 341 */       callOpenListener((StreamConnection)sslConnection, null);
/*     */     } else {
/* 343 */       callOpenListener((StreamConnection)sslConnection, additionalData);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void callOpenListener(StreamConnection streamConnection, PooledByteBuffer buffer) {
/* 349 */     if (this.openListener instanceof DelegateOpenListener) {
/* 350 */       ((DelegateOpenListener)this.openListener).handleEvent(streamConnection, buffer);
/*     */     } else {
/* 352 */       if (buffer != null) {
/* 353 */         PushBackStreamSourceConduit conduit = new PushBackStreamSourceConduit(streamConnection.getSourceChannel().getConduit());
/* 354 */         conduit.pushBack((Pooled)new PooledAdaptor(buffer));
/* 355 */         streamConnection.getSourceChannel().setConduit((StreamSourceConduit)conduit);
/*     */       } 
/* 357 */       this.openListener.handleEvent((Channel)streamConnection);
/*     */     } 
/*     */   }
/*     */   
/*     */   static InetAddress parseAddress(String addressString, String protocol) throws IOException {
/* 362 */     if (protocol.equals("TCP4")) {
/* 363 */       return NetworkUtils.parseIpv4Address(addressString);
/*     */     }
/* 365 */     return NetworkUtils.parseIpv6Address(addressString);
/*     */   }
/*     */   
/*     */   private static final class AddressWrappedConnection
/*     */     extends StreamConnection
/*     */   {
/*     */     private final StreamConnection delegate;
/*     */     private final SocketAddress source;
/*     */     private final SocketAddress dest;
/*     */     
/*     */     AddressWrappedConnection(StreamConnection delegate, SocketAddress source, SocketAddress dest) {
/* 376 */       super(delegate.getIoThread());
/* 377 */       this.delegate = delegate;
/* 378 */       this.source = source;
/* 379 */       this.dest = dest;
/* 380 */       setSinkConduit(delegate.getSinkChannel().getConduit());
/* 381 */       setSourceConduit(delegate.getSourceChannel().getConduit());
/*     */     }
/*     */ 
/*     */     
/*     */     protected void notifyWriteClosed() {
/* 386 */       IoUtils.safeClose((Closeable)this.delegate.getSinkChannel());
/*     */     }
/*     */ 
/*     */     
/*     */     protected void notifyReadClosed() {
/* 391 */       IoUtils.safeClose((Closeable)this.delegate.getSourceChannel());
/*     */     }
/*     */ 
/*     */     
/*     */     public SocketAddress getPeerAddress() {
/* 396 */       return this.source;
/*     */     }
/*     */ 
/*     */     
/*     */     public SocketAddress getLocalAddress() {
/* 401 */       return this.dest;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\proxy\ProxyProtocolReadListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */