package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.Connectors;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.server.protocol.http.HttpContinue;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;

public class RequestBufferingHandler implements HttpHandler {
   private final HttpHandler next;
   private final int maxBuffers;

   public RequestBufferingHandler(HttpHandler next, int maxBuffers) {
      this.next = next;
      this.maxBuffers = maxBuffers;
   }

   public void handleRequest(final HttpServerExchange exchange) throws Exception {
      if (!exchange.isRequestComplete() && !HttpContinue.requiresContinueResponse(exchange.getRequestHeaders())) {
         StreamSourceChannel channel = exchange.getRequestChannel();
         final int readBuffers = 0;
         final PooledByteBuffer[] bufferedData = new PooledByteBuffer[this.maxBuffers];
         final PooledByteBuffer buffer = exchange.getConnection().getByteBufferPool().allocate();

         try {
            while(true) {
               ByteBuffer b = buffer.getBuffer();
               int r = channel.read(b);
               if (r == -1) {
                  if (b.position() == 0) {
                     buffer.close();
                  } else {
                     b.flip();
                     bufferedData[readBuffers] = buffer;
                  }
                  break;
               }

               if (r == 0) {
                  channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
                     PooledByteBuffer buffer = buffer;
                     int readBuffers = readBuffers;

                     public void handleEvent(StreamSourceChannel channel) {
                        try {
                           while(true) {
                              ByteBuffer b = this.buffer.getBuffer();
                              int r = channel.read(b);
                              if (r == -1) {
                                 if (b.position() == 0) {
                                    this.buffer.close();
                                 } else {
                                    b.flip();
                                    bufferedData[this.readBuffers] = this.buffer;
                                 }

                                 Connectors.ungetRequestBytes(exchange, bufferedData);
                                 Connectors.resetRequestChannel(exchange);
                                 channel.getReadSetter().set((ChannelListener)null);
                                 channel.suspendReads();
                                 Connectors.executeRootHandler(RequestBufferingHandler.this.next, exchange);
                                 return;
                              }

                              if (r == 0) {
                                 return;
                              }

                              if (!b.hasRemaining()) {
                                 b.flip();
                                 bufferedData[this.readBuffers++] = this.buffer;
                                 if (this.readBuffers == RequestBufferingHandler.this.maxBuffers) {
                                    Connectors.ungetRequestBytes(exchange, bufferedData);
                                    Connectors.resetRequestChannel(exchange);
                                    channel.getReadSetter().set((ChannelListener)null);
                                    channel.suspendReads();
                                    Connectors.executeRootHandler(RequestBufferingHandler.this.next, exchange);
                                    return;
                                 }

                                 this.buffer = exchange.getConnection().getByteBufferPool().allocate();
                              }
                           }
                        } catch (Throwable var4) {
                           if (var4 instanceof IOException) {
                              UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)var4);
                           } else {
                              UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var4);
                           }

                           for(int i = 0; i < bufferedData.length; ++i) {
                              IoUtils.safeClose((Closeable)bufferedData[i]);
                           }

                           if (this.buffer != null && this.buffer.isOpen()) {
                              IoUtils.safeClose((Closeable)this.buffer);
                           }

                           exchange.endExchange();
                        }
                     }
                  });
                  channel.resumeReads();
                  return;
               }

               if (!b.hasRemaining()) {
                  b.flip();
                  bufferedData[readBuffers++] = buffer;
                  if (readBuffers == this.maxBuffers) {
                     break;
                  }

                  buffer = exchange.getConnection().getByteBufferPool().allocate();
               }
            }

            Connectors.ungetRequestBytes(exchange, bufferedData);
            Connectors.resetRequestChannel(exchange);
         } catch (Error | Exception var10) {
            for(int i = 0; i < bufferedData.length; ++i) {
               IoUtils.safeClose((Closeable)bufferedData[i]);
            }

            if (buffer != null && buffer.isOpen()) {
               IoUtils.safeClose((Closeable)buffer);
            }

            throw var10;
         }
      }

      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "buffer-request( " + this.maxBuffers + " )";
   }

   public static final class Builder implements HandlerBuilder {
      public String name() {
         return "buffer-request";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("buffers", Integer.class);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("buffers");
      }

      public String defaultParameter() {
         return "buffers";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((Integer)config.get("buffers"));
      }
   }

   public static final class Wrapper implements HandlerWrapper {
      private final int maxBuffers;

      public Wrapper(int maxBuffers) {
         this.maxBuffers = maxBuffers;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new RequestBufferingHandler(handler, this.maxBuffers);
      }
   }
}
