/*     */ package io.undertow.server.protocol.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Receiver;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import io.undertow.util.FlexBase64;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class Http2UpgradeHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final Set<String> upgradeStrings;
/*     */   
/*     */   public Http2UpgradeHandler(HttpHandler next) {
/*  64 */     this.next = next;
/*  65 */     this.upgradeStrings = Collections.singleton("h2c");
/*     */   }
/*     */   
/*     */   public Http2UpgradeHandler(HttpHandler next, String... upgradeStrings) {
/*  69 */     this.next = next;
/*  70 */     this.upgradeStrings = new HashSet<>(Arrays.asList(upgradeStrings));
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  75 */     String upgrade = exchange.getRequestHeaders().getFirst(Headers.UPGRADE);
/*  76 */     String settings = exchange.getRequestHeaders().getFirst("HTTP2-Settings");
/*  77 */     if (settings != null && upgrade != null && this.upgradeStrings
/*  78 */       .contains(upgrade)) {
/*  79 */       if (HttpContinue.requiresContinueResponse(exchange));
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
/*  97 */       handleUpgradeBody(exchange, upgrade, settings);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 102 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private void handleUpgradeBody(HttpServerExchange exchange, final String upgrade, final String settings) throws Exception {
/* 106 */     if (exchange.isRequestComplete()) {
/* 107 */       handleHttp2Upgrade(exchange, upgrade, settings, null);
/*     */     } else {
/* 109 */       final int maxBufferedSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
/* 110 */       if (exchange.getRequestContentLength() > maxBufferedSize) {
/*     */ 
/*     */         
/* 113 */         this.next.handleRequest(exchange);
/* 114 */       } else if (exchange.getRequestContentLength() > 0L && exchange.getRequestContentLength() < maxBufferedSize) {
/*     */         
/* 116 */         exchange.getRequestReceiver().receiveFullBytes(new Receiver.FullBytesCallback()
/*     */             {
/*     */               public void handle(HttpServerExchange exchange, byte[] message) {
/*     */                 try {
/* 120 */                   Http2UpgradeHandler.this.handleHttp2Upgrade(exchange, upgrade, settings, message);
/* 121 */                 } catch (IOException e) {
/* 122 */                   UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 123 */                   exchange.endExchange();
/*     */                 } 
/*     */               }
/*     */             });
/*     */       } else {
/* 128 */         final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 129 */         exchange.getRequestReceiver().receivePartialBytes(new Receiver.PartialBytesCallback()
/*     */             {
/*     */               public void handle(HttpServerExchange exchange, byte[] message, boolean last) {
/*     */                 try {
/* 133 */                   outputStream.write(message);
/* 134 */                   if (last) {
/* 135 */                     Http2UpgradeHandler.this.handleHttp2Upgrade(exchange, upgrade, settings, outputStream.toByteArray());
/* 136 */                   } else if (outputStream.size() >= maxBufferedSize) {
/* 137 */                     exchange.getRequestReceiver().pause();
/* 138 */                     Connectors.ungetRequestBytes(exchange, new PooledByteBuffer[] { (PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.wrap(this.val$outputStream.toByteArray())) });
/* 139 */                     Connectors.resetRequestChannel(exchange);
/* 140 */                     Http2UpgradeHandler.this.next.handleRequest(exchange);
/*     */                   } 
/* 142 */                 } catch (IOException e) {
/* 143 */                   UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 144 */                   exchange.endExchange();
/* 145 */                 } catch (RuntimeException e) {
/* 146 */                   throw e;
/* 147 */                 } catch (Exception e) {
/* 148 */                   throw new RuntimeException(e);
/*     */                 } 
/*     */               }
/*     */             });
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleHttp2Upgrade(HttpServerExchange exchange, final String upgrade, String settings, final byte[] data) throws IOException {
/* 158 */     final ByteBuffer settingsFrame = FlexBase64.decodeURL(settings);
/* 159 */     exchange.getResponseHeaders().put(Headers.UPGRADE, upgrade);
/* 160 */     exchange.upgradeChannel(new HttpUpgradeListener()
/*     */         {
/*     */           public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
/* 163 */             OptionMap undertowOptions = exchange.getConnection().getUndertowOptions();
/* 164 */             Http2Channel channel = new Http2Channel(streamConnection, upgrade, exchange.getConnection().getByteBufferPool(), null, false, true, true, settingsFrame, undertowOptions);
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
/* 177 */             Http2ReceiveListener receiveListener = new Http2ReceiveListener(new HttpHandler() { public void handleRequest(HttpServerExchange exchange) throws Exception { if (exchange.getRequestHeaders().contains("X-HTTP2-connect-only")) { exchange.endExchange(); return; }  exchange.setProtocol(Protocols.HTTP_2_0); Http2UpgradeHandler.this.next.handleRequest(exchange); } }, undertowOptions, exchange.getConnection().getBufferSize(), null);
/* 178 */             channel.getReceiveSetter().set(receiveListener);
/* 179 */             receiveListener.handleInitialRequest(exchange, channel, data);
/* 180 */             channel.resumeReceives();
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http2\Http2UpgradeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */