package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.client.ClientConnection;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ExclusivityChecker;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import io.undertow.util.AttachmentKey;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import org.xnio.IoUtils;

class ModClusterProxyClient implements ProxyClient {
   private final AttachmentKey<ExclusiveConnectionHolder> exclusiveConnectionKey = AttachmentKey.create(ExclusiveConnectionHolder.class);
   private final ExclusivityChecker exclusivityChecker;
   private final ModClusterContainer container;

   protected ModClusterProxyClient(ExclusivityChecker exclusivityChecker, ModClusterContainer container) {
      this.exclusivityChecker = exclusivityChecker;
      this.container = container;
   }

   public ProxyClient.ProxyTarget findTarget(HttpServerExchange exchange) {
      return this.container.findTarget(exchange);
   }

   public void getConnection(ProxyClient.ProxyTarget target, HttpServerExchange exchange, final ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
      final ExclusiveConnectionHolder holder = (ExclusiveConnectionHolder)exchange.getConnection().getAttachment(this.exclusiveConnectionKey);
      if (holder != null && holder.connection.getConnection().isOpen()) {
         callback.completed(exchange, holder.connection);
      } else if (!(target instanceof ModClusterProxyTarget)) {
         callback.couldNotResolveBackend(exchange);
      } else {
         ModClusterProxyTarget proxyTarget = (ModClusterProxyTarget)target;
         Context context = proxyTarget.resolveContext(exchange);
         if (context == null) {
            callback.couldNotResolveBackend(exchange);
         } else if (holder == null && (this.exclusivityChecker == null || !this.exclusivityChecker.isExclusivityRequired(exchange))) {
            context.handleRequest(proxyTarget, exchange, callback, timeout, timeUnit, false);
         } else {
            ProxyCallback<ProxyConnection> wrappedCallback = new ProxyCallback<ProxyConnection>() {
               public void completed(HttpServerExchange exchange, ProxyConnection result) {
                  if (holder != null) {
                     holder.connection = result;
                  } else {
                     final ExclusiveConnectionHolder newHolder = new ExclusiveConnectionHolder();
                     newHolder.connection = result;
                     ServerConnection connection = exchange.getConnection();
                     connection.putAttachment(ModClusterProxyClient.this.exclusiveConnectionKey, newHolder);
                     connection.addCloseListener(new ServerConnection.CloseListener() {
                        public void closed(ServerConnection connection) {
                           ClientConnection clientConnection = newHolder.connection.getConnection();
                           if (clientConnection.isOpen()) {
                              IoUtils.safeClose((Closeable)clientConnection);
                           }

                        }
                     });
                  }

                  callback.completed(exchange, result);
               }

               public void queuedRequestFailed(HttpServerExchange exchange) {
                  callback.queuedRequestFailed(exchange);
               }

               public void failed(HttpServerExchange exchange) {
                  callback.failed(exchange);
               }

               public void couldNotResolveBackend(HttpServerExchange exchange) {
                  callback.couldNotResolveBackend(exchange);
               }
            };
            context.handleRequest(proxyTarget, exchange, wrappedCallback, timeout, timeUnit, true);
         }

      }
   }

   private static class ExclusiveConnectionHolder {
      private ProxyConnection connection;

      private ExclusiveConnectionHolder() {
      }

      // $FF: synthetic method
      ExclusiveConnectionHolder(Object x0) {
         this();
      }
   }
}
