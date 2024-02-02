package io.undertow.server.handlers;

import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.util.Methods;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.Transfer;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;

public class ConnectHandler implements HttpHandler {
   private final HttpHandler next;
   private final Predicate allowed;

   public ConnectHandler(HttpHandler next) {
      this(next, Predicates.truePredicate());
   }

   public ConnectHandler(HttpHandler next, Predicate allowed) {
      this.next = next;
      this.allowed = allowed;
   }

   public void handleRequest(final HttpServerExchange exchange) throws Exception {
      if (exchange.getRequestMethod().equals(Methods.CONNECT)) {
         if (!this.allowed.resolve(exchange)) {
            exchange.setStatusCode(405);
            return;
         }

         String[] parts = exchange.getRequestPath().split(":");
         if (parts.length != 2) {
            exchange.setStatusCode(400);
            return;
         }

         final String host = parts[0];
         final Integer port = Integer.parseInt(parts[1]);
         exchange.dispatch(SameThreadExecutor.INSTANCE, new Runnable() {
            public void run() {
               exchange.getConnection().getIoThread().openStreamConnection(new InetSocketAddress(host, port), new ChannelListener<StreamConnection>() {
                  public void handleEvent(final StreamConnection clientChannel) {
                     exchange.acceptConnectRequest(new HttpUpgradeListener() {
                        public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchangex) {
                           ClosingExceptionHandler handler = new ClosingExceptionHandler(new Closeable[]{streamConnection, clientChannel});
                           Transfer.initiateTransfer(clientChannel.getSourceChannel(), streamConnection.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, exchangex.getConnection().getByteBufferPool());
                           Transfer.initiateTransfer(streamConnection.getSourceChannel(), clientChannel.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, exchangex.getConnection().getByteBufferPool());
                        }
                     });
                     exchange.setStatusCode(200);
                     exchange.endExchange();
                  }
               }, OptionMap.create(Options.TCP_NODELAY, true)).addNotifier(new IoFuture.Notifier<StreamConnection, Object>() {
                  public void notify(IoFuture<? extends StreamConnection> ioFuture, Object attachment) {
                     if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
                        exchange.setStatusCode(503);
                        exchange.endExchange();
                     }

                  }
               }, (Object)null);
            }
         });
      } else {
         this.next.handleRequest(exchange);
      }

   }

   private static final class ClosingExceptionHandler implements ChannelExceptionHandler<Channel> {
      private final Closeable[] toClose;

      private ClosingExceptionHandler(Closeable... toClose) {
         this.toClose = toClose;
      }

      public void handleException(Channel channel, IOException exception) {
         IoUtils.safeClose((Closeable)channel);
         IoUtils.safeClose(this.toClose);
      }

      // $FF: synthetic method
      ClosingExceptionHandler(Closeable[] x0, Object x1) {
         this(x0);
      }
   }
}
