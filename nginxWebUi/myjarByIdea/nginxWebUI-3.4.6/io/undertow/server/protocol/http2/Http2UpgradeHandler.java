package io.undertow.server.protocol.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.io.Receiver;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.Connectors;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.protocol.http.HttpContinue;
import io.undertow.util.FlexBase64;
import io.undertow.util.Headers;
import io.undertow.util.ImmediatePooledByteBuffer;
import io.undertow.util.Protocols;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;

public class Http2UpgradeHandler implements HttpHandler {
   private final HttpHandler next;
   private final Set<String> upgradeStrings;

   public Http2UpgradeHandler(HttpHandler next) {
      this.next = next;
      this.upgradeStrings = Collections.singleton("h2c");
   }

   public Http2UpgradeHandler(HttpHandler next, String... upgradeStrings) {
      this.next = next;
      this.upgradeStrings = new HashSet(Arrays.asList(upgradeStrings));
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String upgrade = exchange.getRequestHeaders().getFirst(Headers.UPGRADE);
      String settings = exchange.getRequestHeaders().getFirst("HTTP2-Settings");
      if (settings != null && upgrade != null && this.upgradeStrings.contains(upgrade)) {
         if (HttpContinue.requiresContinueResponse(exchange)) {
         }

         this.handleUpgradeBody(exchange, upgrade, settings);
      } else {
         this.next.handleRequest(exchange);
      }
   }

   private void handleUpgradeBody(HttpServerExchange exchange, final String upgrade, final String settings) throws Exception {
      if (exchange.isRequestComplete()) {
         this.handleHttp2Upgrade(exchange, upgrade, settings, (byte[])null);
      } else {
         final int maxBufferedSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
         if (exchange.getRequestContentLength() > (long)maxBufferedSize) {
            this.next.handleRequest(exchange);
         } else if (exchange.getRequestContentLength() > 0L && exchange.getRequestContentLength() < (long)maxBufferedSize) {
            exchange.getRequestReceiver().receiveFullBytes(new Receiver.FullBytesCallback() {
               public void handle(HttpServerExchange exchange, byte[] message) {
                  try {
                     Http2UpgradeHandler.this.handleHttp2Upgrade(exchange, upgrade, settings, message);
                  } catch (IOException var4) {
                     UndertowLogger.REQUEST_IO_LOGGER.ioException(var4);
                     exchange.endExchange();
                  }

               }
            });
         } else {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            exchange.getRequestReceiver().receivePartialBytes(new Receiver.PartialBytesCallback() {
               public void handle(HttpServerExchange exchange, byte[] message, boolean last) {
                  try {
                     outputStream.write(message);
                     if (last) {
                        Http2UpgradeHandler.this.handleHttp2Upgrade(exchange, upgrade, settings, outputStream.toByteArray());
                     } else if (outputStream.size() >= maxBufferedSize) {
                        exchange.getRequestReceiver().pause();
                        Connectors.ungetRequestBytes(exchange, new ImmediatePooledByteBuffer(ByteBuffer.wrap(outputStream.toByteArray())));
                        Connectors.resetRequestChannel(exchange);
                        Http2UpgradeHandler.this.next.handleRequest(exchange);
                     }
                  } catch (IOException var5) {
                     UndertowLogger.REQUEST_IO_LOGGER.ioException(var5);
                     exchange.endExchange();
                  } catch (RuntimeException var6) {
                     throw var6;
                  } catch (Exception var7) {
                     throw new RuntimeException(var7);
                  }

               }
            });
         }
      }

   }

   private void handleHttp2Upgrade(HttpServerExchange exchange, final String upgrade, String settings, final byte[] data) throws IOException {
      final ByteBuffer settingsFrame = FlexBase64.decodeURL(settings);
      exchange.getResponseHeaders().put(Headers.UPGRADE, upgrade);
      exchange.upgradeChannel(new HttpUpgradeListener() {
         public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
            OptionMap undertowOptions = exchange.getConnection().getUndertowOptions();
            Http2Channel channel = new Http2Channel(streamConnection, upgrade, exchange.getConnection().getByteBufferPool(), (PooledByteBuffer)null, false, true, true, settingsFrame, undertowOptions);
            Http2ReceiveListener receiveListener = new Http2ReceiveListener(new HttpHandler() {
               public void handleRequest(HttpServerExchange exchange) throws Exception {
                  if (exchange.getRequestHeaders().contains("X-HTTP2-connect-only")) {
                     exchange.endExchange();
                  } else {
                     exchange.setProtocol(Protocols.HTTP_2_0);
                     Http2UpgradeHandler.this.next.handleRequest(exchange);
                  }
               }
            }, undertowOptions, exchange.getConnection().getBufferSize(), (ConnectorStatisticsImpl)null);
            channel.getReceiveSetter().set(receiveListener);
            receiveListener.handleInitialRequest(exchange, channel, data);
            channel.resumeReceives();
         }
      });
   }
}
