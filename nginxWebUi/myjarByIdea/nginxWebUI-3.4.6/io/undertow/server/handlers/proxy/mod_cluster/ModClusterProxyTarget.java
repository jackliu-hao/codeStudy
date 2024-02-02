package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.proxy.ProxyClient;
import java.util.Iterator;

public interface ModClusterProxyTarget extends ProxyClient.ProxyTarget, ProxyClient.MaxRetriesProxyTarget {
   Context resolveContext(HttpServerExchange var1);

   public static class BasicTarget implements ModClusterProxyTarget {
      private final VirtualHost.HostEntry entry;
      private final ModClusterContainer container;
      private Context resolved;

      public BasicTarget(VirtualHost.HostEntry entry, ModClusterContainer container) {
         this.entry = entry;
         this.container = container;
      }

      public int getMaxRetries() {
         if (this.resolved == null) {
            this.resolveNode();
         }

         if (this.resolved == null) {
            return 0;
         } else {
            Balancer balancer = this.resolved.getNode().getBalancer();
            return balancer == null ? 0 : balancer.getMaxRetries();
         }
      }

      public Context resolveContext(HttpServerExchange exchange) {
         if (this.resolved == null) {
            this.resolveNode();
         }

         return this.resolved;
      }

      private void resolveNode() {
         this.resolved = this.container.findNewNode(this.entry);
      }
   }

   public static class ExistingSessionTarget implements ModClusterProxyTarget {
      private final String session;
      private final Iterator<CharSequence> routes;
      private final VirtualHost.HostEntry entry;
      private final boolean forceStickySession;
      private final ModClusterContainer container;
      private boolean resolved;
      private Context resolvedContext;

      public ExistingSessionTarget(String session, Iterator<CharSequence> routes, VirtualHost.HostEntry entry, ModClusterContainer container, boolean forceStickySession) {
         this.session = session;
         this.routes = routes;
         this.entry = entry;
         this.container = container;
         this.forceStickySession = forceStickySession;
      }

      public Context resolveContext(HttpServerExchange exchange) {
         this.resolveContextIfUnresolved();
         return this.resolvedContext;
      }

      void resolveContextIfUnresolved() {
         if (!this.resolved) {
            this.resolved = true;
            boolean firstResolved = false;
            String firstRoute = null;
            String firstRouteDomain = null;

            while(this.routes.hasNext()) {
               String jvmRoute = ((CharSequence)this.routes.next()).toString();
               Context context = this.entry.getContextForNode(jvmRoute);
               if (context != null && context.checkAvailable(true)) {
                  Node node = context.getNode();
                  node.elected();
                  this.resolvedContext = context;
                  return;
               }

               if (!firstResolved) {
                  firstResolved = true;
                  firstRoute = jvmRoute;
                  firstRouteDomain = context != null ? context.getNode().getNodeConfig().getDomain() : null;
               }
            }

            this.resolvedContext = this.container.findFailoverNode(this.entry, firstRouteDomain, this.session, firstRoute, this.forceStickySession);
         }
      }

      public int getMaxRetries() {
         this.resolveContextIfUnresolved();
         if (this.resolvedContext == null) {
            return 0;
         } else {
            Balancer balancer = this.resolvedContext.getNode().getBalancer();
            return balancer == null ? 0 : balancer.getMaxRetries();
         }
      }
   }
}
