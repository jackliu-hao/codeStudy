package io.undertow.server.protocol.ajp;

import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.conduits.ConduitListener;
import io.undertow.conduits.EmptyStreamSourceConduit;
import io.undertow.conduits.ReadDataStreamSourceConduit;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.AbstractServerConnection;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.protocol.ParseTimeoutUpdater;
import io.undertow.util.BadRequestException;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.conduits.WriteReadyHandler;

final class AjpReadListener implements ChannelListener<StreamSourceChannel> {
   private static final byte[] CPONG = new byte[]{65, 66, 0, 1, 9};
   private static final byte[] SEND_HEADERS_INTERNAL_SERVER_ERROR_MSG = new byte[]{65, 66, 0, 8, 4, 1, -12, 0, 0, 0, 0, 0};
   private static final byte[] SEND_HEADERS_BAD_REQUEST_MSG = new byte[]{65, 66, 0, 8, 4, 1, -112, 0, 0, 0, 0, 0};
   private static final byte[] END_RESPONSE = new byte[]{65, 66, 0, 2, 5, 1};
   private final AjpServerConnection connection;
   private final String scheme;
   private final boolean recordRequestStartTime;
   private AjpRequestParseState state = new AjpRequestParseState();
   private HttpServerExchange httpServerExchange;
   private volatile int read = 0;
   private final int maxRequestSize;
   private final long maxEntitySize;
   private final AjpRequestParser parser;
   private final ConnectorStatisticsImpl connectorStatistics;
   private WriteReadyHandler.ChannelListenerHandler<ConduitStreamSinkChannel> writeReadyHandler;
   private ParseTimeoutUpdater parseTimeoutUpdater;

   AjpReadListener(AjpServerConnection connection, String scheme, AjpRequestParser parser, ConnectorStatisticsImpl connectorStatistics) {
      this.connection = connection;
      this.scheme = scheme;
      this.parser = parser;
      this.connectorStatistics = connectorStatistics;
      this.maxRequestSize = connection.getUndertowOptions().get(UndertowOptions.MAX_HEADER_SIZE, 1048576);
      this.maxEntitySize = connection.getUndertowOptions().get(UndertowOptions.MAX_ENTITY_SIZE, -1L);
      this.writeReadyHandler = new WriteReadyHandler.ChannelListenerHandler(connection.getChannel().getSinkChannel());
      this.recordRequestStartTime = connection.getUndertowOptions().get(UndertowOptions.RECORD_REQUEST_START_TIME, false);
      int requestParseTimeout = connection.getUndertowOptions().get(UndertowOptions.REQUEST_PARSE_TIMEOUT, -1);
      int requestIdleTimeout = connection.getUndertowOptions().get(UndertowOptions.NO_REQUEST_TIMEOUT, -1);
      if (requestIdleTimeout < 0 && requestParseTimeout < 0) {
         this.parseTimeoutUpdater = null;
      } else {
         this.parseTimeoutUpdater = new ParseTimeoutUpdater(connection, (long)requestParseTimeout, (long)requestIdleTimeout);
         connection.addCloseListener(this.parseTimeoutUpdater);
      }

   }

   public void startRequest() {
      this.connection.resetChannel();
      this.state = new AjpRequestParseState();
      this.read = 0;
      if (this.parseTimeoutUpdater != null) {
         this.parseTimeoutUpdater.connectionIdle();
      }

      this.connection.setCurrentExchange((HttpServerExchange)null);
   }

   public void handleEvent(StreamSourceChannel channel) {
      if (!this.connection.getOriginalSinkConduit().isWriteShutdown() && !this.connection.getOriginalSourceConduit().isReadShutdown()) {
         PooledByteBuffer existing = this.connection.getExtraBytes();
         PooledByteBuffer pooled = existing == null ? this.connection.getByteBufferPool().allocate() : existing;
         ByteBuffer buffer = pooled.getBuffer();
         boolean free = true;
         boolean bytesRead = false;

         try {
            try {
               do {
                  int res;
                  if (existing == null) {
                     buffer.clear();
                     res = channel.read(buffer);
                  } else {
                     res = buffer.remaining();
                  }

                  if (res == 0) {
                     if (bytesRead && this.parseTimeoutUpdater != null) {
                        this.parseTimeoutUpdater.failedParse();
                     }

                     if (!channel.isReadResumed()) {
                        channel.getReadSetter().set(this);
                        channel.resumeReads();
                     }

                     return;
                  }

                  if (res == -1) {
                     channel.shutdownReads();
                     StreamSinkChannel responseChannel = this.connection.getChannel().getSinkChannel();
                     responseChannel.shutdownWrites();
                     IoUtils.safeClose((Closeable)this.connection);
                     return;
                  }

                  bytesRead = true;
                  if (existing != null) {
                     existing = null;
                     this.connection.setExtraBytes((PooledByteBuffer)null);
                  } else {
                     buffer.flip();
                  }

                  int begin = buffer.remaining();
                  if (this.httpServerExchange == null) {
                     this.httpServerExchange = new HttpServerExchange(this.connection, this.maxEntitySize);
                  }

                  this.parser.parse(buffer, this.state, this.httpServerExchange);
                  this.read += begin - buffer.remaining();
                  if (buffer.hasRemaining()) {
                     free = false;
                     this.connection.setExtraBytes(pooled);
                  }

                  if (this.read > this.maxRequestSize) {
                     UndertowLogger.REQUEST_LOGGER.requestHeaderWasTooLarge(this.connection.getPeerAddress(), this.maxRequestSize);
                     IoUtils.safeClose((Closeable)this.connection);
                     return;
                  }
               } while(!this.state.isComplete());

               if (this.parseTimeoutUpdater != null) {
                  this.parseTimeoutUpdater.requestStarted();
               }

               if (this.state.prefix != 2) {
                  if (this.state.prefix == 10) {
                     UndertowLogger.REQUEST_LOGGER.debug("Received CPING, sending CPONG");
                     this.handleCPing();
                     return;
                  } else {
                     if (this.state.prefix == 9) {
                        UndertowLogger.REQUEST_LOGGER.debug("Received CPONG, starting next request");
                        this.state = new AjpRequestParseState();
                        channel.getReadSetter().set(this);
                        channel.resumeReads();
                     } else {
                        UndertowLogger.REQUEST_LOGGER.ignoringAjpRequestWithPrefixCode(this.state.prefix);
                        IoUtils.safeClose((Closeable)this.connection);
                     }

                     return;
                  }
               }

               channel.getReadSetter().set((ChannelListener)null);
               channel.suspendReads();
               final HttpServerExchange httpServerExchange = this.httpServerExchange;
               AjpServerResponseConduit responseConduit = new AjpServerResponseConduit(this.connection.getChannel().getSinkChannel().getConduit(), this.connection.getByteBufferPool(), httpServerExchange, new ConduitListener<AjpServerResponseConduit>() {
                  public void handleEvent(AjpServerResponseConduit channel) {
                     Connectors.terminateResponse(httpServerExchange);
                  }
               }, httpServerExchange.getRequestMethod().equals(Methods.HEAD));
               this.connection.getChannel().getSinkChannel().setConduit(responseConduit);
               this.connection.getChannel().getSourceChannel().setConduit(this.createSourceConduit(this.connection.getChannel().getSourceChannel().getConduit(), responseConduit, httpServerExchange));
               responseConduit.setWriteReadyHandler(this.writeReadyHandler);
               this.connection.setSSLSessionInfo(this.state.createSslSessionInfo());
               httpServerExchange.setSourceAddress(this.state.createPeerAddress());
               httpServerExchange.setDestinationAddress(this.state.createDestinationAddress());
               if (this.scheme != null) {
                  httpServerExchange.setRequestScheme(this.scheme);
               }

               if (this.state.attributes != null) {
                  httpServerExchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, this.state.attributes);
               }

               AjpRequestParseState oldState = this.state;
               this.state = null;
               this.httpServerExchange = null;
               httpServerExchange.setPersistent(true);
               if (this.recordRequestStartTime) {
                  Connectors.setRequestStartTime(httpServerExchange);
               }

               this.connection.setCurrentExchange(httpServerExchange);
               if (this.connectorStatistics != null) {
                  this.connectorStatistics.setup(httpServerExchange);
               }

               if (!Connectors.areRequestHeadersValid(httpServerExchange.getRequestHeaders())) {
                  oldState.badRequest = true;
                  UndertowLogger.REQUEST_IO_LOGGER.debugf("Invalid AJP request from %s, request contained invalid headers", this.connection.getPeerAddress());
               }

               if (oldState.badRequest) {
                  httpServerExchange.setStatusCode(400);
                  httpServerExchange.endExchange();
                  this.handleBadRequest();
                  IoUtils.safeClose((Closeable)this.connection);
               } else {
                  Connectors.executeRootHandler(this.connection.getRootHandler(), httpServerExchange);
               }

               return;
            } catch (BadRequestException var16) {
               UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest(var16);
               this.handleBadRequest();
               IoUtils.safeClose((Closeable)this.connection);
            } catch (IOException var17) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(var17);
               this.handleInternalServerError();
               IoUtils.safeClose((Closeable)this.connection);
            } catch (Throwable var18) {
               UndertowLogger.REQUEST_LOGGER.exceptionProcessingRequest(var18);
               this.handleInternalServerError();
               IoUtils.safeClose((Closeable)this.connection);
            }

         } finally {
            if (free) {
               pooled.close();
            }

         }
      } else {
         IoUtils.safeClose((Closeable)this.connection);
         channel.suspendReads();
      }
   }

   private void handleInternalServerError() {
      this.sendMessages(SEND_HEADERS_INTERNAL_SERVER_ERROR_MSG, END_RESPONSE);
   }

   private void handleBadRequest() {
      this.sendMessages(SEND_HEADERS_BAD_REQUEST_MSG, END_RESPONSE);
   }

   private void handleCPing() {
      if (this.sendMessages(CPONG)) {
         this.handleEvent((StreamSourceChannel)this.connection.getChannel().getSourceChannel());
      }

   }

   private boolean sendMessages(byte[]... rawMessages) {
      this.state = new AjpRequestParseState();
      final StreamConnection underlyingChannel = this.connection.getChannel();
      underlyingChannel.getSourceChannel().suspendReads();
      int bufferSize = 0;

      for(int i = 0; i < rawMessages.length; ++i) {
         bufferSize += rawMessages[i].length;
      }

      final ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

      int res;
      for(res = 0; res < rawMessages.length; ++res) {
         buffer.put(rawMessages[res]);
      }

      buffer.flip();

      try {
         do {
            res = underlyingChannel.getSinkChannel().write(buffer);
            if (res == 0) {
               underlyingChannel.getSinkChannel().setWriteListener(new ChannelListener<ConduitStreamSinkChannel>() {
                  public void handleEvent(ConduitStreamSinkChannel channel) {
                     do {
                        try {
                           int res = channel.write(buffer);
                           if (res == 0) {
                              return;
                           }
                        } catch (IOException var4) {
                           UndertowLogger.REQUEST_IO_LOGGER.ioException(var4);
                           IoUtils.safeClose((Closeable)AjpReadListener.this.connection);
                        }
                     } while(buffer.hasRemaining());

                     channel.suspendWrites();
                     AjpReadListener.this.handleEvent((StreamSourceChannel)underlyingChannel.getSourceChannel());
                  }
               });
               underlyingChannel.getSinkChannel().resumeWrites();
               return false;
            }
         } while(buffer.hasRemaining());

         return true;
      } catch (IOException var7) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var7);
         IoUtils.safeClose((Closeable)this.connection);
         return false;
      }
   }

   public void exchangeComplete(HttpServerExchange exchange) {
      if (!exchange.isUpgrade() && exchange.isPersistent()) {
         this.startRequest();
         ConduitStreamSourceChannel channel = ((AjpServerConnection)exchange.getConnection()).getChannel().getSourceChannel();
         channel.getReadSetter().set(this);
         channel.wakeupReads();
      } else if (!exchange.isPersistent()) {
         IoUtils.safeClose((Closeable)exchange.getConnection());
      }

   }

   private StreamSourceConduit createSourceConduit(StreamSourceConduit underlyingConduit, AjpServerResponseConduit responseConduit, final HttpServerExchange exchange) throws BadRequestException {
      ReadDataStreamSourceConduit conduit = new ReadDataStreamSourceConduit(underlyingConduit, (AbstractServerConnection)exchange.getConnection());
      HeaderMap requestHeaders = exchange.getRequestHeaders();
      HttpString transferEncoding = Headers.IDENTITY;
      String teHeader = requestHeaders.getLast(Headers.TRANSFER_ENCODING);
      boolean hasTransferEncoding = teHeader != null;
      if (hasTransferEncoding) {
         transferEncoding = new HttpString(teHeader);
      }

      String requestContentLength = requestHeaders.getFirst(Headers.CONTENT_LENGTH);
      Long length;
      if (hasTransferEncoding && !transferEncoding.equals(Headers.IDENTITY)) {
         length = null;
      } else {
         if (requestContentLength == null) {
            UndertowLogger.REQUEST_LOGGER.trace("No content length or transfer coding, starting next request");
            Connectors.terminateRequest(exchange);
            return new EmptyStreamSourceConduit(conduit.getReadThread());
         }

         try {
            long contentLength = Long.parseLong(requestContentLength);
            if (contentLength == 0L) {
               UndertowLogger.REQUEST_LOGGER.trace("No content, starting next request");
               Connectors.terminateRequest(this.httpServerExchange);
               return new EmptyStreamSourceConduit(conduit.getReadThread());
            }

            length = contentLength;
         } catch (NumberFormatException var13) {
            throw new BadRequestException("Invalid Content-Length header", var13);
         }
      }

      return new AjpServerRequestConduit(conduit, exchange, responseConduit, length, new ConduitListener<AjpServerRequestConduit>() {
         public void handleEvent(AjpServerRequestConduit channel) {
            Connectors.terminateRequest(exchange);
         }
      });
   }
}
