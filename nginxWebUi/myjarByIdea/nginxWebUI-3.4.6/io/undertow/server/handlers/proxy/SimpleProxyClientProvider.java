package io.undertow.server.handlers.proxy;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.util.AttachmentKey;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.XnioIoThread;

/** @deprecated */
@Deprecated
public class SimpleProxyClientProvider implements ProxyClient {
   private final URI uri;
   private final AttachmentKey<ClientConnection> clientAttachmentKey = AttachmentKey.create(ClientConnection.class);
   private final UndertowClient client;
   private static final ProxyClient.ProxyTarget TARGET = new ProxyClient.ProxyTarget() {
   };

   public SimpleProxyClientProvider(URI uri) {
      this.uri = uri;
      this.client = UndertowClient.getInstance();
   }

   public ProxyClient.ProxyTarget findTarget(HttpServerExchange exchange) {
      return TARGET;
   }

   public void getConnection(ProxyClient.ProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
      ClientConnection existing = (ClientConnection)exchange.getConnection().getAttachment(this.clientAttachmentKey);
      if (existing != null) {
         if (existing.isOpen()) {
            callback.completed(exchange, new ProxyConnection(existing, this.uri.getPath() == null ? "/" : this.uri.getPath()));
            return;
         }

         exchange.getConnection().removeAttachment(this.clientAttachmentKey);
      }

      this.client.connect((ClientCallback)(new ConnectNotifier(callback, exchange)), (URI)this.uri, (XnioIoThread)exchange.getIoThread(), exchange.getConnection().getByteBufferPool(), OptionMap.EMPTY);
   }

   private final class ConnectNotifier implements ClientCallback<ClientConnection> {
      private final ProxyCallback<ProxyConnection> callback;
      private final HttpServerExchange exchange;

      private ConnectNotifier(ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange) {
         this.callback = callback;
         this.exchange = exchange;
      }

      public void completed(final ClientConnection connection) {
         final ServerConnection serverConnection = this.exchange.getConnection();
         serverConnection.putAttachment(SimpleProxyClientProvider.this.clientAttachmentKey, connection);
         serverConnection.addCloseListener(new ServerConnection.CloseListener() {
            public void closed(ServerConnection serverConnection) {
               IoUtils.safeClose((Closeable)connection);
            }
         });
         connection.getCloseSetter().set(new ChannelListener<Channel>() {
            public void handleEvent(Channel channel) {
               serverConnection.removeAttachment(SimpleProxyClientProvider.this.clientAttachmentKey);
            }
         });
         this.callback.completed(this.exchange, new ProxyConnection(connection, SimpleProxyClientProvider.this.uri.getPath() == null ? "/" : SimpleProxyClientProvider.this.uri.getPath()));
      }

      public void failed(IOException e) {
         this.callback.failed(this.exchange);
      }

      // $FF: synthetic method
      ConnectNotifier(ProxyCallback x1, HttpServerExchange x2, Object x3) {
         this(x1, x2);
      }
   }
}
