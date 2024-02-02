package io.undertow.server.protocol.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.conduits.ConduitListener;
import io.undertow.conduits.HeadStreamSinkConduit;
import io.undertow.protocols.http2.AbstractHttp2StreamSourceChannel;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.protocols.http2.Http2DataStreamSinkChannel;
import io.undertow.protocols.http2.Http2HeadersStreamSinkChannel;
import io.undertow.protocols.http2.Http2StreamSourceChannel;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.Connectors;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.server.protocol.http.HttpContinue;
import io.undertow.server.protocol.http.HttpRequestParser;
import io.undertow.util.ConduitFactory;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.ImmediatePooledByteBuffer;
import io.undertow.util.Methods;
import io.undertow.util.ParameterLimitException;
import io.undertow.util.Protocols;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Supplier;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;

public class Http2ReceiveListener implements ChannelListener<Http2Channel> {
   private final HttpHandler rootHandler;
   private final long maxEntitySize;
   private final OptionMap undertowOptions;
   private final String encoding;
   private final boolean decode;
   private final StringBuilder decodeBuffer = new StringBuilder();
   private final boolean allowEncodingSlash;
   private final int bufferSize;
   private final int maxParameters;
   private final boolean recordRequestStartTime;
   private final boolean allowUnescapedCharactersInUrl;
   private final ConnectorStatisticsImpl connectorStatistics;

   public Http2ReceiveListener(HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize, ConnectorStatisticsImpl connectorStatistics) {
      this.rootHandler = rootHandler;
      this.undertowOptions = undertowOptions;
      this.bufferSize = bufferSize;
      this.connectorStatistics = connectorStatistics;
      this.maxEntitySize = undertowOptions.get(UndertowOptions.MAX_ENTITY_SIZE, -1L);
      this.allowEncodingSlash = undertowOptions.get(UndertowOptions.ALLOW_ENCODED_SLASH, false);
      this.decode = undertowOptions.get(UndertowOptions.DECODE_URL, true);
      this.maxParameters = undertowOptions.get(UndertowOptions.MAX_PARAMETERS, 1000);
      this.recordRequestStartTime = undertowOptions.get(UndertowOptions.RECORD_REQUEST_START_TIME, false);
      if (undertowOptions.get(UndertowOptions.DECODE_URL, true)) {
         this.encoding = (String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name());
      } else {
         this.encoding = null;
      }

      this.allowUnescapedCharactersInUrl = undertowOptions.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false);
   }

   public void handleEvent(Http2Channel channel) {
      try {
         AbstractHttp2StreamSourceChannel frame = (AbstractHttp2StreamSourceChannel)channel.receive();
         if (frame == null) {
            return;
         }

         if (frame instanceof Http2StreamSourceChannel) {
            this.handleRequests(channel, (Http2StreamSourceChannel)frame);
         }
      } catch (IOException var3) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var3);
         IoUtils.safeClose((Closeable)channel);
      } catch (Throwable var4) {
         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var4);
         IoUtils.safeClose((Closeable)channel);
      }

   }

   private void handleRequests(Http2Channel channel, Http2StreamSourceChannel frame) {
      Http2ServerConnection connection = new Http2ServerConnection(channel, frame, this.undertowOptions, this.bufferSize, this.rootHandler);
      if (!this.checkRequestHeaders(frame.getHeaders())) {
         channel.sendRstStream(frame.getStreamId(), 1);

         try {
            Channels.drain((StreamSourceChannel)frame, Long.MAX_VALUE);
         } catch (IOException var8) {
         }

      } else {
         final HttpServerExchange exchange = new HttpServerExchange(connection, frame.getHeaders(), frame.getResponseChannel().getHeaders(), this.maxEntitySize);
         frame.setTrailersHandler(new Http2StreamSourceChannel.TrailersHandler() {
            public void handleTrailers(HeaderMap headerMap) {
               exchange.putAttachment(HttpAttachments.REQUEST_TRAILERS, headerMap);
            }
         });
         connection.setExchange(exchange);
         frame.setMaxStreamSize(this.maxEntitySize);
         exchange.setRequestScheme(exchange.getRequestHeaders().getFirst(Http2Channel.SCHEME));
         exchange.setRequestMethod(Methods.fromString(exchange.getRequestHeaders().getFirst(Http2Channel.METHOD)));
         exchange.getRequestHeaders().put(Headers.HOST, exchange.getRequestHeaders().getFirst(Http2Channel.AUTHORITY));
         if (!Connectors.areRequestHeadersValid(exchange.getRequestHeaders())) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf("Invalid headers in HTTP/2 request, closing connection. Remote peer %s", connection.getPeerAddress());
            channel.sendGoAway(1);
         } else {
            String path = exchange.getRequestHeaders().getFirst(Http2Channel.PATH);
            if (path != null && !path.isEmpty()) {
               if (this.recordRequestStartTime) {
                  Connectors.setRequestStartTime(exchange);
               }

               this.handleCommonSetup(frame.getResponseChannel(), exchange, connection);
               if (!frame.isOpen()) {
                  Connectors.terminateRequest(exchange);
               } else {
                  frame.setCompletionListener(new ChannelListener<Http2StreamSourceChannel>() {
                     public void handleEvent(Http2StreamSourceChannel channel) {
                        Connectors.terminateRequest(exchange);
                     }
                  });
               }

               if (this.connectorStatistics != null) {
                  this.connectorStatistics.setup(exchange);
               }

               try {
                  Connectors.setExchangeRequestPath(exchange, path, this.encoding, this.decode, this.allowEncodingSlash, this.decodeBuffer, this.maxParameters);
               } catch (ParameterLimitException var9) {
                  UndertowLogger.REQUEST_IO_LOGGER.debug("Failed to set request path", var9);
                  exchange.setStatusCode(400);
                  exchange.endExchange();
                  return;
               }

               exchange.getRequestHeaders().remove(Http2Channel.AUTHORITY);
               exchange.getRequestHeaders().remove(Http2Channel.PATH);
               exchange.getRequestHeaders().remove(Http2Channel.SCHEME);
               exchange.getRequestHeaders().remove(Http2Channel.METHOD);
               Connectors.executeRootHandler(this.rootHandler, exchange);
            } else {
               UndertowLogger.REQUEST_IO_LOGGER.debugf("No :path header sent in HTTP/2 request, closing connection. Remote peer %s", connection.getPeerAddress());
               channel.sendGoAway(1);
            }
         }
      }
   }

   void handleInitialRequest(HttpServerExchange initial, Http2Channel channel, byte[] data) {
      Http2HeadersStreamSinkChannel sink = channel.createInitialUpgradeResponseStream();
      Http2ServerConnection connection = new Http2ServerConnection(channel, sink, this.undertowOptions, this.bufferSize, this.rootHandler);
      HeaderMap requestHeaders = new HeaderMap();
      Iterator var7 = initial.getRequestHeaders().iterator();

      while(var7.hasNext()) {
         HeaderValues hv = (HeaderValues)var7.next();
         requestHeaders.putAll(hv.getHeaderName(), hv);
      }

      HttpServerExchange exchange = new HttpServerExchange(connection, requestHeaders, sink.getHeaders(), this.maxEntitySize);
      if (initial.getRequestHeaders().contains(Headers.EXPECT)) {
         HttpContinue.markContinueResponseSent(exchange);
      }

      if (initial.getAttachment(HttpAttachments.REQUEST_TRAILERS) != null) {
         exchange.putAttachment(HttpAttachments.REQUEST_TRAILERS, initial.getAttachment(HttpAttachments.REQUEST_TRAILERS));
      }

      Connectors.setRequestStartTime(initial, exchange);
      connection.setExchange(exchange);
      exchange.setRequestScheme(initial.getRequestScheme());
      exchange.setRequestMethod(initial.getRequestMethod());
      exchange.setQueryString(initial.getQueryString());
      if (data != null) {
         Connectors.ungetRequestBytes(exchange, new ImmediatePooledByteBuffer(ByteBuffer.wrap(data)));
      }

      Connectors.terminateRequest(exchange);
      String uri = exchange.getQueryString().isEmpty() ? initial.getRequestURI() : initial.getRequestURI() + '?' + exchange.getQueryString();

      try {
         Connectors.setExchangeRequestPath(exchange, uri, this.encoding, this.decode, this.allowEncodingSlash, this.decodeBuffer, this.maxParameters);
      } catch (ParameterLimitException var10) {
         exchange.setStatusCode(400);
         exchange.endExchange();
         return;
      }

      this.handleCommonSetup(sink, exchange, connection);
      Connectors.executeRootHandler(this.rootHandler, exchange);
   }

   private void handleCommonSetup(Http2HeadersStreamSinkChannel sink, final HttpServerExchange exchange, Http2ServerConnection connection) {
      Http2Channel channel = (Http2Channel)sink.getChannel();
      SSLSession session = channel.getSslSession();
      if (session != null) {
         connection.setSslSessionInfo(new Http2SslSessionInfo(channel));
      }

      sink.setTrailersProducer(new Http2DataStreamSinkChannel.TrailersProducer() {
         public HeaderMap getTrailers() {
            Supplier<HeaderMap> supplier = (Supplier)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER);
            return supplier != null ? (HeaderMap)supplier.get() : (HeaderMap)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILERS);
         }
      });
      sink.setCompletionListener(new ChannelListener<Http2DataStreamSinkChannel>() {
         public void handleEvent(Http2DataStreamSinkChannel channel) {
            Connectors.terminateResponse(exchange);
         }
      });
      exchange.setProtocol(Protocols.HTTP_2_0);
      if (exchange.getRequestMethod().equals(Methods.HEAD)) {
         exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
            public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
               return new HeadStreamSinkConduit((StreamSinkConduit)factory.create(), (ConduitListener)null, true);
            }
         });
      }

   }

   private boolean checkRequestHeaders(HeaderMap headers) {
      if (headers.count(Http2Channel.METHOD) == 1 && !headers.contains(Headers.CONNECTION)) {
         if (headers.get(Http2Channel.METHOD).contains("CONNECT")) {
            if (headers.contains(Http2Channel.SCHEME) || headers.contains(Http2Channel.PATH) || headers.count(Http2Channel.AUTHORITY) != 1) {
               return false;
            }
         } else if (headers.count(Http2Channel.SCHEME) != 1 || headers.count(Http2Channel.PATH) != 1) {
            return false;
         }

         if (headers.contains(Headers.TE)) {
            Iterator var2 = headers.get(Headers.TE).iterator();

            while(var2.hasNext()) {
               String value = (String)var2.next();
               if (!value.equals("trailers")) {
                  return false;
               }
            }
         }

         int var4;
         byte b;
         byte[] var6;
         int var7;
         if (headers.contains(Http2Channel.PATH)) {
            var6 = headers.get(Http2Channel.PATH).getFirst().getBytes(StandardCharsets.ISO_8859_1);
            var7 = var6.length;

            for(var4 = 0; var4 < var7; ++var4) {
               b = var6[var4];
               if (!this.allowUnescapedCharactersInUrl && !HttpRequestParser.isTargetCharacterAllowed((char)b)) {
                  return false;
               }
            }
         }

         if (headers.contains(Http2Channel.SCHEME)) {
            var6 = headers.get(Http2Channel.SCHEME).getFirst().getBytes(StandardCharsets.ISO_8859_1);
            var7 = var6.length;

            for(var4 = 0; var4 < var7; ++var4) {
               b = var6[var4];
               if (!Connectors.isValidSchemeCharacter(b)) {
                  return false;
               }
            }
         }

         if (headers.contains(Http2Channel.AUTHORITY)) {
            var6 = headers.get(Http2Channel.AUTHORITY).getFirst().getBytes(StandardCharsets.ISO_8859_1);
            var7 = var6.length;

            for(var4 = 0; var4 < var7; ++var4) {
               b = var6[var4];
               if (!HttpRequestParser.isTargetCharacterAllowed((char)b)) {
                  return false;
               }
            }
         }

         if (headers.contains(Http2Channel.METHOD)) {
            var6 = headers.get(Http2Channel.METHOD).getFirst().getBytes(StandardCharsets.ISO_8859_1);
            var7 = var6.length;

            for(var4 = 0; var4 < var7; ++var4) {
               b = var6[var4];
               if (!Connectors.isValidTokenCharacter(b)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
