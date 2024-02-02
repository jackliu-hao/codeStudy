package io.undertow.server.protocol.http;

import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.conduits.ChunkedStreamSinkConduit;
import io.undertow.conduits.ChunkedStreamSourceConduit;
import io.undertow.conduits.ConduitListener;
import io.undertow.conduits.FinishableStreamSinkConduit;
import io.undertow.conduits.FixedLengthStreamSourceConduit;
import io.undertow.conduits.HeadStreamSinkConduit;
import io.undertow.conduits.PreChunkedStreamSinkConduit;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.DateUtils;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.jboss.logging.Logger;
import org.xnio.ChannelListener;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

class HttpTransferEncoding {
   private static final Logger log = Logger.getLogger("io.undertow.server.handler.transfer-encoding");

   private HttpTransferEncoding() {
   }

   public static void setupRequest(HttpServerExchange exchange) {
      HeaderMap requestHeaders = exchange.getRequestHeaders();
      String connectionHeader = requestHeaders.getFirst(Headers.CONNECTION);
      String transferEncodingHeader = requestHeaders.getLast(Headers.TRANSFER_ENCODING);
      String contentLengthHeader = requestHeaders.getFirst(Headers.CONTENT_LENGTH);
      HttpServerConnection connection = (HttpServerConnection)exchange.getConnection();
      PipeliningBufferingStreamSinkConduit pipeliningBuffer = connection.getPipelineBuffer();
      if (pipeliningBuffer != null) {
         pipeliningBuffer.setupPipelineBuffer(exchange);
      }

      ConduitStreamSourceChannel sourceChannel = connection.getChannel().getSourceChannel();
      sourceChannel.setConduit(connection.getReadDataStreamSourceConduit());
      boolean persistentConnection = persistentConnection(exchange, connectionHeader);
      if (transferEncodingHeader == null && contentLengthHeader == null) {
         if (persistentConnection && connection.getExtraBytes() != null && pipeliningBuffer == null && connection.getUndertowOptions().get(UndertowOptions.BUFFER_PIPELINED_DATA, false)) {
            pipeliningBuffer = new PipeliningBufferingStreamSinkConduit(connection.getOriginalSinkConduit(), connection.getByteBufferPool());
            connection.setPipelineBuffer(pipeliningBuffer);
            pipeliningBuffer.setupPipelineBuffer(exchange);
         }

         Connectors.terminateRequest(exchange);
      } else {
         persistentConnection = handleRequestEncoding(exchange, transferEncodingHeader, contentLengthHeader, connection, pipeliningBuffer, persistentConnection);
      }

      exchange.setPersistent(persistentConnection);
      if (!exchange.isRequestComplete() || connection.getExtraBytes() != null) {
         sourceChannel.setReadListener((ChannelListener)null);
         sourceChannel.suspendReads();
      }

   }

   private static boolean handleRequestEncoding(HttpServerExchange exchange, String transferEncodingHeader, String contentLengthHeader, HttpServerConnection connection, PipeliningBufferingStreamSinkConduit pipeliningBuffer, boolean persistentConnection) {
      HttpString transferEncoding = Headers.IDENTITY;
      if (transferEncodingHeader != null) {
         transferEncoding = new HttpString(transferEncodingHeader);
      }

      if (transferEncodingHeader != null && !transferEncoding.equals(Headers.IDENTITY)) {
         ConduitStreamSourceChannel sourceChannel = ((HttpServerConnection)exchange.getConnection()).getChannel().getSourceChannel();
         sourceChannel.setConduit(new ChunkedStreamSourceConduit(sourceChannel.getConduit(), exchange, chunkedDrainListener(exchange)));
      } else if (contentLengthHeader != null) {
         long contentLength = parsePositiveLong(contentLengthHeader);
         if (contentLength == 0L) {
            log.trace("No content, starting next request");
            Connectors.terminateRequest(exchange);
         } else {
            ConduitStreamSourceChannel sourceChannel = ((HttpServerConnection)exchange.getConnection()).getChannel().getSourceChannel();
            sourceChannel.setConduit(fixedLengthStreamSourceConduitWrapper(contentLength, sourceChannel.getConduit(), exchange));
         }
      } else if (transferEncodingHeader != null) {
         log.trace("Connection not persistent (no content length and identity transfer encoding)");
         persistentConnection = false;
      } else if (persistentConnection) {
         if (connection.getExtraBytes() != null && pipeliningBuffer == null && connection.getUndertowOptions().get(UndertowOptions.BUFFER_PIPELINED_DATA, false)) {
            pipeliningBuffer = new PipeliningBufferingStreamSinkConduit(connection.getOriginalSinkConduit(), connection.getByteBufferPool());
            connection.setPipelineBuffer(pipeliningBuffer);
            pipeliningBuffer.setupPipelineBuffer(exchange);
         }

         Connectors.terminateRequest(exchange);
      } else {
         Connectors.terminateRequest(exchange);
      }

      return persistentConnection;
   }

   private static boolean persistentConnection(HttpServerExchange exchange, String connectionHeader) {
      if (!exchange.isHttp11()) {
         if (exchange.isHttp10() && connectionHeader != null && Headers.KEEP_ALIVE.equals(new HttpString(connectionHeader))) {
            return true;
         } else {
            log.trace("Connection not persistent");
            return false;
         }
      } else {
         return connectionHeader == null || !Headers.CLOSE.equalToString(connectionHeader);
      }
   }

   private static StreamSourceConduit fixedLengthStreamSourceConduitWrapper(long contentLength, StreamSourceConduit conduit, HttpServerExchange exchange) {
      return new FixedLengthStreamSourceConduit(conduit, contentLength, fixedLengthDrainListener(exchange), exchange);
   }

   private static ConduitListener<FixedLengthStreamSourceConduit> fixedLengthDrainListener(final HttpServerExchange exchange) {
      return new ConduitListener<FixedLengthStreamSourceConduit>() {
         public void handleEvent(FixedLengthStreamSourceConduit fixedLengthConduit) {
            long remaining = fixedLengthConduit.getRemaining();
            if (remaining > 0L) {
               UndertowLogger.REQUEST_LOGGER.requestWasNotFullyConsumed();
               exchange.setPersistent(false);
            }

            Connectors.terminateRequest(exchange);
         }
      };
   }

   private static ConduitListener<ChunkedStreamSourceConduit> chunkedDrainListener(final HttpServerExchange exchange) {
      return new ConduitListener<ChunkedStreamSourceConduit>() {
         public void handleEvent(ChunkedStreamSourceConduit chunkedStreamSourceConduit) {
            if (!chunkedStreamSourceConduit.isFinished()) {
               UndertowLogger.REQUEST_LOGGER.requestWasNotFullyConsumed();
               exchange.setPersistent(false);
            }

            Connectors.terminateRequest(exchange);
         }
      };
   }

   private static ConduitListener<StreamSinkConduit> terminateResponseListener(final HttpServerExchange exchange) {
      return new ConduitListener<StreamSinkConduit>() {
         public void handleEvent(StreamSinkConduit channel) {
            Connectors.terminateResponse(exchange);
         }
      };
   }

   static StreamSinkConduit createSinkConduit(HttpServerExchange exchange) {
      DateUtils.addDateHeaderIfRequired(exchange);
      boolean headRequest = exchange.getRequestMethod().equals(Methods.HEAD);
      HttpServerConnection serverConnection = (HttpServerConnection)exchange.getConnection();
      HttpResponseConduit responseConduit = serverConnection.getResponseConduit();
      responseConduit.reset(exchange);
      StreamSinkConduit channel = responseConduit;
      if (headRequest) {
         channel = new HeadStreamSinkConduit(responseConduit, terminateResponseListener(exchange));
      } else if (!Connectors.isEntityBodyAllowed(exchange)) {
         exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
         exchange.getResponseHeaders().remove(Headers.TRANSFER_ENCODING);
         StreamSinkConduit channel = new HeadStreamSinkConduit(responseConduit, terminateResponseListener(exchange));
         return channel;
      }

      HeaderMap responseHeaders = exchange.getResponseHeaders();
      String connection = responseHeaders.getFirst(Headers.CONNECTION);
      if (exchange.getStatusCode() == 417) {
         exchange.setPersistent(false);
      }

      if (!exchange.isPersistent()) {
         responseHeaders.put(Headers.CONNECTION, Headers.CLOSE.toString());
      } else if (exchange.isPersistent() && connection != null) {
         if (HttpString.tryFromString(connection).equals(Headers.CLOSE)) {
            exchange.setPersistent(false);
         }
      } else if (exchange.getConnection().getUndertowOptions().get(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, true)) {
         responseHeaders.put(Headers.CONNECTION, Headers.KEEP_ALIVE.toString());
      }

      String transferEncodingHeader = responseHeaders.getLast(Headers.TRANSFER_ENCODING);
      if (transferEncodingHeader == null) {
         String contentLengthHeader = responseHeaders.getFirst(Headers.CONTENT_LENGTH);
         if (contentLengthHeader != null) {
            StreamSinkConduit res = handleFixedLength(exchange, headRequest, (StreamSinkConduit)channel, responseHeaders, contentLengthHeader, serverConnection);
            if (res != null) {
               return res;
            }
         }
      } else {
         responseHeaders.remove(Headers.CONTENT_LENGTH);
      }

      return handleResponseConduit(exchange, headRequest, (StreamSinkConduit)channel, responseHeaders, terminateResponseListener(exchange), transferEncodingHeader);
   }

   private static StreamSinkConduit handleFixedLength(HttpServerExchange exchange, boolean headRequest, StreamSinkConduit channel, HeaderMap responseHeaders, String contentLengthHeader, HttpServerConnection connection) {
      try {
         long contentLength = parsePositiveLong(contentLengthHeader);
         if (headRequest) {
            return channel;
         } else {
            ServerFixedLengthStreamSinkConduit fixed = connection.getFixedLengthStreamSinkConduit();
            fixed.reset(contentLength, exchange);
            return fixed;
         }
      } catch (NumberFormatException var9) {
         responseHeaders.remove(Headers.CONTENT_LENGTH);
         return null;
      }
   }

   private static StreamSinkConduit handleResponseConduit(HttpServerExchange exchange, boolean headRequest, StreamSinkConduit channel, HeaderMap responseHeaders, ConduitListener<StreamSinkConduit> finishListener, String transferEncodingHeader) {
      if (transferEncodingHeader == null) {
         if (exchange.isHttp11()) {
            if (exchange.isPersistent()) {
               responseHeaders.put(Headers.TRANSFER_ENCODING, Headers.CHUNKED.toString());
               return (StreamSinkConduit)(headRequest ? channel : new ChunkedStreamSinkConduit(channel, exchange.getConnection().getByteBufferPool(), true, !exchange.isPersistent(), responseHeaders, finishListener, exchange));
            } else {
               return (StreamSinkConduit)(headRequest ? channel : new FinishableStreamSinkConduit(channel, finishListener));
            }
         } else {
            exchange.setPersistent(false);
            responseHeaders.put(Headers.CONNECTION, Headers.CLOSE.toString());
            return (StreamSinkConduit)(headRequest ? channel : new FinishableStreamSinkConduit(channel, finishListener));
         }
      } else {
         return handleExplicitTransferEncoding(exchange, channel, finishListener, responseHeaders, transferEncodingHeader, headRequest);
      }
   }

   private static StreamSinkConduit handleExplicitTransferEncoding(HttpServerExchange exchange, StreamSinkConduit channel, ConduitListener<StreamSinkConduit> finishListener, HeaderMap responseHeaders, String transferEncodingHeader, boolean headRequest) {
      HttpString transferEncoding = new HttpString(transferEncodingHeader);
      if (transferEncoding.equals(Headers.CHUNKED)) {
         if (headRequest) {
            return channel;
         } else {
            Boolean preChunked = (Boolean)exchange.getAttachment(HttpAttachments.PRE_CHUNKED_RESPONSE);
            return (StreamSinkConduit)(preChunked != null && preChunked ? new PreChunkedStreamSinkConduit(channel, finishListener, exchange) : new ChunkedStreamSinkConduit(channel, exchange.getConnection().getByteBufferPool(), true, !exchange.isPersistent(), responseHeaders, finishListener, exchange));
         }
      } else if (headRequest) {
         return channel;
      } else {
         log.trace("Cancelling persistence because response is identity with no content length");
         exchange.setPersistent(false);
         responseHeaders.put(Headers.CONNECTION, Headers.CLOSE.toString());
         return new FinishableStreamSinkConduit(channel, terminateResponseListener(exchange));
      }
   }

   public static long parsePositiveLong(String str) {
      long value = 0L;
      int length = str.length();
      if (length == 0) {
         throw new NumberFormatException(str);
      } else {
         long multiplier = 1L;

         for(int i = length - 1; i >= 0; --i) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
               throw new NumberFormatException(str);
            }

            long digit = (long)(c - 48);
            value += digit * multiplier;
            multiplier *= 10L;
         }

         return value;
      }
   }
}
