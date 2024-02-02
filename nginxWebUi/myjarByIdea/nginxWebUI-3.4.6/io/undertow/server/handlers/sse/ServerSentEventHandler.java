package io.undertow.server.handlers.sse;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.PathTemplateMatch;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;

public class ServerSentEventHandler implements HttpHandler {
   private static final HttpString LAST_EVENT_ID = new HttpString("Last-Event-ID");
   private final ServerSentEventConnectionCallback callback;
   private final Set<ServerSentEventConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap());

   public ServerSentEventHandler(ServerSentEventConnectionCallback callback) {
      this.callback = callback;
   }

   public ServerSentEventHandler() {
      this.callback = null;
   }

   public void handleRequest(final HttpServerExchange exchange) throws Exception {
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/event-stream; charset=UTF-8");
      exchange.setPersistent(false);
      final StreamSinkChannel sink = exchange.getResponseChannel();
      if (!sink.flush()) {
         sink.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
            public void handleEvent(StreamSinkChannel channel) {
               ServerSentEventHandler.this.handleConnect(channel, exchange);
            }
         }, new ChannelExceptionHandler<StreamSinkChannel>() {
            public void handleException(StreamSinkChannel channel, IOException exception) {
               IoUtils.safeClose((Closeable)exchange.getConnection());
            }
         }));
         sink.resumeWrites();
      } else {
         exchange.dispatch(exchange.getIoThread(), (Runnable)(new Runnable() {
            public void run() {
               ServerSentEventHandler.this.handleConnect(sink, exchange);
            }
         }));
      }

   }

   private void handleConnect(StreamSinkChannel channel, HttpServerExchange exchange) {
      UndertowLogger.REQUEST_LOGGER.debugf("Opened SSE connection to %s", exchange);
      final ServerSentEventConnection connection = new ServerSentEventConnection(exchange, channel);
      PathTemplateMatch pt = (PathTemplateMatch)exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
      if (pt != null) {
         Iterator var5 = pt.getParameters().entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry<String, String> p = (Map.Entry)var5.next();
            connection.setParameter((String)p.getKey(), (String)p.getValue());
         }
      }

      this.connections.add(connection);
      connection.addCloseTask(new ChannelListener<ServerSentEventConnection>() {
         public void handleEvent(ServerSentEventConnection channel) {
            ServerSentEventHandler.this.connections.remove(connection);
         }
      });
      if (this.callback != null) {
         this.callback.connected(connection, exchange.getRequestHeaders().getLast(LAST_EVENT_ID));
      }

   }

   public Set<ServerSentEventConnection> getConnections() {
      return Collections.unmodifiableSet(this.connections);
   }
}
