package io.undertow.server.protocol.http;

import io.undertow.UndertowMessages;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Protocols;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.channels.StreamSinkChannel;

public class HttpContinue {
   private static final Set<HttpString> COMPATIBLE_PROTOCOLS;
   public static final String CONTINUE = "100-continue";
   private static final AttachmentKey<Boolean> ALREADY_SENT;

   public static boolean requiresContinueResponse(HttpServerExchange exchange) {
      if (COMPATIBLE_PROTOCOLS.contains(exchange.getProtocol()) && !exchange.isResponseStarted() && exchange.getConnection().isContinueResponseSupported() && exchange.getAttachment(ALREADY_SENT) == null) {
         HeaderMap requestHeaders = exchange.getRequestHeaders();
         return requiresContinueResponse(requestHeaders);
      } else {
         return false;
      }
   }

   public static boolean requiresContinueResponse(HeaderMap requestHeaders) {
      List<String> expect = requestHeaders.get(Headers.EXPECT);
      if (expect != null) {
         Iterator var2 = expect.iterator();

         while(var2.hasNext()) {
            String header = (String)var2.next();
            if (header.equalsIgnoreCase("100-continue")) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isContinueResponseSent(HttpServerExchange exchange) {
      return exchange.getAttachment(ALREADY_SENT) != null;
   }

   public static void sendContinueResponse(HttpServerExchange exchange, IoCallback callback) {
      if (!exchange.isResponseChannelAvailable()) {
         callback.onException(exchange, (Sender)null, UndertowMessages.MESSAGES.cannotSendContinueResponse());
      } else {
         internalSendContinueResponse(exchange, callback);
      }
   }

   public static ContinueResponseSender createResponseSender(HttpServerExchange exchange) throws IOException {
      if (!exchange.isResponseChannelAvailable()) {
         throw UndertowMessages.MESSAGES.cannotSendContinueResponse();
      } else if (exchange.getAttachment(ALREADY_SENT) != null) {
         return new ContinueResponseSender() {
            public boolean send() throws IOException {
               return true;
            }

            public void awaitWritable() throws IOException {
            }

            public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
            }
         };
      } else {
         HttpServerExchange newExchange = exchange.getConnection().sendOutOfBandResponse(exchange);
         exchange.putAttachment(ALREADY_SENT, true);
         newExchange.setStatusCode(100);
         newExchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, 0L);
         final StreamSinkChannel responseChannel = newExchange.getResponseChannel();
         return new ContinueResponseSender() {
            boolean shutdown = false;

            public boolean send() throws IOException {
               if (!this.shutdown) {
                  this.shutdown = true;
                  responseChannel.shutdownWrites();
               }

               return responseChannel.flush();
            }

            public void awaitWritable() throws IOException {
               responseChannel.awaitWritable();
            }

            public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
               responseChannel.awaitWritable(time, timeUnit);
            }
         };
      }
   }

   public static void markContinueResponseSent(HttpServerExchange exchange) {
      exchange.putAttachment(ALREADY_SENT, true);
   }

   public static void sendContinueResponseBlocking(HttpServerExchange exchange) throws IOException {
      if (!exchange.isResponseChannelAvailable()) {
         throw UndertowMessages.MESSAGES.cannotSendContinueResponse();
      } else if (exchange.getAttachment(ALREADY_SENT) == null) {
         HttpServerExchange newExchange = exchange.getConnection().sendOutOfBandResponse(exchange);
         exchange.putAttachment(ALREADY_SENT, true);
         newExchange.setStatusCode(100);
         newExchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, 0L);
         newExchange.startBlocking();
         newExchange.getOutputStream().close();
         newExchange.getInputStream().close();
      }
   }

   public static void rejectExchange(HttpServerExchange exchange) {
      exchange.setStatusCode(417);
      exchange.setPersistent(false);
      exchange.endExchange();
   }

   private static void internalSendContinueResponse(final HttpServerExchange exchange, final IoCallback callback) {
      if (exchange.getAttachment(ALREADY_SENT) != null) {
         callback.onComplete(exchange, (Sender)null);
      } else {
         HttpServerExchange newExchange = exchange.getConnection().sendOutOfBandResponse(exchange);
         exchange.putAttachment(ALREADY_SENT, true);
         newExchange.setStatusCode(100);
         newExchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, 0L);
         StreamSinkChannel responseChannel = newExchange.getResponseChannel();

         try {
            responseChannel.shutdownWrites();
            if (!responseChannel.flush()) {
               responseChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
                  public void handleEvent(StreamSinkChannel channel) {
                     channel.suspendWrites();
                     exchange.dispatch(new HttpHandler() {
                        public void handleRequest(HttpServerExchange exchangex) throws Exception {
                           callback.onComplete(exchangex, (Sender)null);
                        }
                     });
                  }
               }, new ChannelExceptionHandler<Channel>() {
                  public void handleException(Channel channel, final IOException e) {
                     exchange.dispatch(new HttpHandler() {
                        public void handleRequest(HttpServerExchange exchangex) throws Exception {
                           callback.onException(exchangex, (Sender)null, e);
                        }
                     });
                  }
               }));
               responseChannel.resumeWrites();
               exchange.dispatch();
            } else {
               callback.onComplete(exchange, (Sender)null);
            }
         } catch (IOException var5) {
            callback.onException(exchange, (Sender)null, var5);
         }

      }
   }

   static {
      Set<HttpString> compat = new HashSet();
      compat.add(Protocols.HTTP_1_1);
      compat.add(Protocols.HTTP_2_0);
      COMPATIBLE_PROTOCOLS = Collections.unmodifiableSet(compat);
      ALREADY_SENT = AttachmentKey.create(Boolean.class);
   }

   public interface ContinueResponseSender {
      boolean send() throws IOException;

      void awaitWritable() throws IOException;

      void awaitWritable(long var1, TimeUnit var3) throws IOException;
   }
}
