package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import io.undertow.client.UndertowClient;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.cache.LRUCache;
import io.undertow.server.handlers.proxy.ExclusivityChecker;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.RouteIteratorFactory;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.Headers;
import io.undertow.util.PathMatcher;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.xnio.OptionMap;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.ssl.XnioSsl;

class ModClusterContainer implements ModClusterController {
   private final ConcurrentMap<String, Balancer> balancers = new CopyOnWriteMap();
   private final ConcurrentMap<String, Node> nodes = new CopyOnWriteMap();
   private final ConcurrentMap<String, VirtualHost> hosts = new CopyOnWriteMap();
   private final LRUCache<String, String> failoverDomains = new LRUCache(100, 300000);
   private final ConcurrentMap<XnioIoThread, HealthCheckTask> healthChecks = new CopyOnWriteMap();
   private final UpdateLoadTask updateLoadTask = new UpdateLoadTask();
   private final XnioSsl xnioSsl;
   private final UndertowClient client;
   private final ProxyClient proxyClient;
   private final ModCluster modCluster;
   private final NodeHealthChecker healthChecker;
   private final long removeBrokenNodesThreshold;
   private RouteIteratorFactory routeIteratorFactory;
   private final OptionMap clientOptions;

   ModClusterContainer(ModCluster modCluster, XnioSsl xnioSsl, UndertowClient client, OptionMap clientOptions) {
      this.xnioSsl = xnioSsl;
      this.client = client;
      this.modCluster = modCluster;
      this.clientOptions = clientOptions;
      this.healthChecker = modCluster.getHealthChecker();
      this.proxyClient = new ModClusterProxyClient((ExclusivityChecker)null, this);
      this.removeBrokenNodesThreshold = removeThreshold(modCluster.getHealthCheckInterval(), modCluster.getRemoveBrokenNodes());
      this.routeIteratorFactory = new RouteIteratorFactory(modCluster.routeParsingStrategy(), RouteIteratorFactory.ParsingCompatibility.MOD_CLUSTER, modCluster.rankedAffinityDelimiter());
   }

   String getServerID() {
      return this.modCluster.getServerID();
   }

   UndertowClient getClient() {
      return this.client;
   }

   XnioSsl getXnioSsl() {
      return this.xnioSsl;
   }

   public ProxyClient getProxyClient() {
      return this.proxyClient;
   }

   Collection<Balancer> getBalancers() {
      return Collections.unmodifiableCollection(this.balancers.values());
   }

   Collection<Node> getNodes() {
      return Collections.unmodifiableCollection(this.nodes.values());
   }

   Node getNode(String jvmRoute) {
      return (Node)this.nodes.get(jvmRoute);
   }

   public ModClusterProxyTarget findTarget(HttpServerExchange exchange) {
      PathMatcher.PathMatch<VirtualHost.HostEntry> entry = this.mapVirtualHost(exchange);
      if (entry == null) {
         return null;
      } else {
         Iterator var3 = this.balancers.values().iterator();

         while(true) {
            Balancer balancer;
            do {
               if (!var3.hasNext()) {
                  return new ModClusterProxyTarget.BasicTarget((VirtualHost.HostEntry)entry.getValue(), this);
               }

               balancer = (Balancer)var3.next();
            } while(!balancer.isStickySession());

            Iterator var5 = exchange.requestCookies().iterator();

            while(var5.hasNext()) {
               Cookie cookie = (Cookie)var5.next();
               if (balancer.getStickySessionCookie().equals(cookie.getName())) {
                  String sessionId = cookie.getValue();
                  Iterator<CharSequence> routes = this.parseRoutes(sessionId);
                  if (routes.hasNext()) {
                     return new ModClusterProxyTarget.ExistingSessionTarget(sessionId, routes, (VirtualHost.HostEntry)entry.getValue(), this, balancer.isStickySessionForce());
                  }
               }
            }

            if (exchange.getPathParameters().containsKey(balancer.getStickySessionPath())) {
               String sessionId = (String)((Deque)exchange.getPathParameters().get(balancer.getStickySessionPath())).getFirst();
               Iterator<CharSequence> jvmRoute = this.parseRoutes(sessionId);
               if (jvmRoute.hasNext()) {
                  return new ModClusterProxyTarget.ExistingSessionTarget(sessionId, jvmRoute, (VirtualHost.HostEntry)entry.getValue(), this, balancer.isStickySessionForce());
               }
            }
         }
      }
   }

   public synchronized boolean addNode(NodeConfig config, Balancer.BalancerBuilder balancerConfig, XnioIoThread ioThread, ByteBufferPool bufferPool) {
      String jvmRoute = config.getJvmRoute();
      Node existing = (Node)this.nodes.get(jvmRoute);
      if (existing != null) {
         if (config.getConnectionURI().equals(existing.getNodeConfig().getConnectionURI())) {
            existing.resetState();
            return true;
         }

         existing.markRemoved();
         this.removeNode(existing);
         if (!existing.isInErrorState()) {
            return false;
         }
      }

      String balancerRef = config.getBalancer();
      Balancer balancer = (Balancer)this.balancers.get(balancerRef);
      if (balancer != null) {
         UndertowLogger.ROOT_LOGGER.debugf("Balancer %s already exists, replacing", balancerRef);
      }

      balancer = balancerConfig.build();
      this.balancers.put(balancerRef, balancer);
      Node node = new Node(config, balancer, ioThread, bufferPool, this);
      this.nodes.put(jvmRoute, node);
      this.scheduleHealthCheck(node, ioThread);
      if (this.updateLoadTask.cancelKey == null) {
         this.updateLoadTask.cancelKey = ioThread.executeAtInterval(this.updateLoadTask, this.modCluster.getHealthCheckInterval(), TimeUnit.MILLISECONDS);
      }

      this.failoverDomains.remove(node.getJvmRoute());
      UndertowLogger.ROOT_LOGGER.registeringNode(jvmRoute, config.getConnectionURI());
      return true;
   }

   public synchronized boolean enableNode(String jvmRoute) {
      Node node = (Node)this.nodes.get(jvmRoute);
      if (node == null) {
         return false;
      } else {
         Iterator var3 = node.getContexts().iterator();

         while(var3.hasNext()) {
            Context context = (Context)var3.next();
            context.enable();
         }

         return true;
      }
   }

   public synchronized boolean disableNode(String jvmRoute) {
      Node node = (Node)this.nodes.get(jvmRoute);
      if (node == null) {
         return false;
      } else {
         Iterator var3 = node.getContexts().iterator();

         while(var3.hasNext()) {
            Context context = (Context)var3.next();
            context.disable();
         }

         return true;
      }
   }

   public synchronized boolean stopNode(String jvmRoute) {
      Node node = (Node)this.nodes.get(jvmRoute);
      if (node == null) {
         return false;
      } else {
         Iterator var3 = node.getContexts().iterator();

         while(var3.hasNext()) {
            Context context = (Context)var3.next();
            context.stop();
         }

         return true;
      }
   }

   public synchronized Node removeNode(String jvmRoute) {
      Node node = (Node)this.nodes.get(jvmRoute);
      if (node != null) {
         this.removeNode(node);
      }

      return node;
   }

   protected void removeNode(Node node) {
      this.removeNode(node, false);
   }

   protected synchronized void removeNode(Node node, boolean onlyInError) {
      if (!onlyInError || node.isInErrorState()) {
         String jvmRoute = node.getJvmRoute();
         node.markRemoved();
         if (this.nodes.remove(jvmRoute, node)) {
            UndertowLogger.ROOT_LOGGER.removingNode(jvmRoute);
            node.markRemoved();
            this.removeHealthCheck(node, node.getIoThread());
            Iterator var4 = node.getContexts().iterator();

            while(var4.hasNext()) {
               Context context = (Context)var4.next();
               this.removeContext(context.getPath(), node, context.getVirtualHosts());
            }

            String domain = node.getNodeConfig().getDomain();
            if (domain != null) {
               this.failoverDomains.add(node.getJvmRoute(), domain);
            }

            String balancerName = node.getBalancer().getName();
            Iterator var6 = this.nodes.values().iterator();

            while(var6.hasNext()) {
               Node other = (Node)var6.next();
               if (other.getBalancer().getName().equals(balancerName)) {
                  return;
               }
            }

            this.balancers.remove(balancerName);
         }

         if (this.nodes.size() == 0) {
            this.updateLoadTask.cancelKey.remove();
            this.updateLoadTask.cancelKey = null;
         }

      }
   }

   public synchronized boolean enableContext(String contextPath, String jvmRoute, List<String> aliases) {
      Node node = (Node)this.nodes.get(jvmRoute);
      if (node != null) {
         Context context = this.getOrRegisterContext(contextPath, jvmRoute, aliases, node);
         context.enable();
         return true;
      } else {
         return false;
      }
   }

   public synchronized boolean disableContext(String contextPath, String jvmRoute, List<String> aliases) {
      Node node = (Node)this.nodes.get(jvmRoute);
      if (node != null) {
         Context context = this.getOrRegisterContext(contextPath, jvmRoute, aliases, node);
         context.disable();
         return true;
      } else {
         return false;
      }
   }

   synchronized int stopContext(String contextPath, String jvmRoute, List<String> aliases) {
      Node node = (Node)this.nodes.get(jvmRoute);
      if (node != null) {
         Context context = this.getOrRegisterContext(contextPath, jvmRoute, aliases, node);
         context.stop();
         return context.getActiveRequests();
      } else {
         return -1;
      }
   }

   synchronized boolean removeContext(String contextPath, String jvmRoute, List<String> aliases) {
      Node node = (Node)this.nodes.get(jvmRoute);
      return node != null ? this.removeContext(contextPath, node, aliases) : false;
   }

   public synchronized boolean removeContext(String contextPath, Node node, List<String> aliases) {
      if (node == null) {
         return false;
      } else {
         String jvmRoute = node.getJvmRoute();
         UndertowLogger.ROOT_LOGGER.unregisteringContext(contextPath, jvmRoute);
         Context context = node.removeContext(contextPath, aliases);
         if (context == null) {
            return false;
         } else {
            context.stop();
            Iterator var6 = context.getVirtualHosts().iterator();

            while(var6.hasNext()) {
               String alias = (String)var6.next();
               VirtualHost virtualHost = (VirtualHost)this.hosts.get(alias);
               if (virtualHost != null) {
                  virtualHost.removeContext(contextPath, jvmRoute, context);
                  if (virtualHost.isEmpty()) {
                     this.hosts.remove(alias);
                  }
               }
            }

            return true;
         }
      }
   }

   private Context getOrRegisterContext(String contextPath, String jvmRoute, List<String> aliases, Node node) {
      Context context = node.getContext(contextPath, aliases);
      if (context == null) {
         context = node.registerContext(contextPath, aliases);
         UndertowLogger.ROOT_LOGGER.registeringContext(contextPath, jvmRoute);
         UndertowLogger.ROOT_LOGGER.registeringContext(contextPath, jvmRoute, aliases);

         VirtualHost virtualHost;
         for(Iterator var6 = aliases.iterator(); var6.hasNext(); virtualHost.registerContext(contextPath, jvmRoute, context)) {
            String alias = (String)var6.next();
            virtualHost = (VirtualHost)this.hosts.get(alias);
            if (virtualHost == null) {
               virtualHost = new VirtualHost();
               this.hosts.put(alias, virtualHost);
            }
         }
      }

      return context;
   }

   Context findNewNode(VirtualHost.HostEntry entry) {
      return electNode(entry.getContexts(), false, (String)null);
   }

   Context findFailoverNode(VirtualHost.HostEntry entry, String domain, String session, String jvmRoute, boolean forceStickySession) {
      if (!this.modCluster.isDeterministicFailover()) {
         String failOverDomain = null;
         if (domain == null) {
            Node node = (Node)this.nodes.get(jvmRoute);
            if (node != null) {
               failOverDomain = node.getNodeConfig().getDomain();
            }

            if (failOverDomain == null) {
               failOverDomain = (String)this.failoverDomains.get(jvmRoute);
            }
         } else {
            failOverDomain = domain;
         }

         Collection<Context> contexts = entry.getContexts();
         if (failOverDomain != null) {
            Context context = electNode(contexts, true, failOverDomain);
            if (context != null) {
               return context;
            }
         }

         return forceStickySession ? null : electNode(contexts, false, (String)null);
      } else {
         List<String> candidates = new ArrayList(entry.getNodes().size());
         Iterator var7 = entry.getNodes().iterator();

         String route;
         Node node;
         while(var7.hasNext()) {
            route = (String)var7.next();
            node = (Node)this.nodes.get(route);
            if (node != null && !node.isInErrorState() && !node.isHotStandby()) {
               candidates.add(route);
            }
         }

         if (candidates.isEmpty()) {
            var7 = entry.getNodes().iterator();

            while(var7.hasNext()) {
               route = (String)var7.next();
               node = (Node)this.nodes.get(route);
               if (node != null && !node.isInErrorState() && node.isHotStandby()) {
                  candidates.add(route);
               }
            }
         }

         if (candidates.isEmpty()) {
            return null;
         } else {
            String sessionId = session.substring(0, session.indexOf(46));
            int index = (int)(Math.abs((long)sessionId.hashCode()) % (long)candidates.size());
            Collections.sort(candidates);
            String electedRoute = (String)candidates.get(index);
            UndertowLogger.ROOT_LOGGER.debugf("Using deterministic failover target: %s", electedRoute);
            return entry.getContextForNode(electedRoute);
         }
      }
   }

   private PathMatcher.PathMatch<VirtualHost.HostEntry> mapVirtualHost(HttpServerExchange exchange) {
      String context = exchange.getRelativePath();
      if (this.modCluster.isUseAlias()) {
         String hostName = exchange.getRequestHeaders().getFirst(Headers.HOST);
         if (hostName != null) {
            int i = hostName.indexOf(":");
            VirtualHost host;
            if (i > 0) {
               host = (VirtualHost)this.hosts.get(hostName.substring(0, i));
               if (host == null) {
                  host = (VirtualHost)this.hosts.get(hostName);
               }
            } else {
               host = (VirtualHost)this.hosts.get(hostName);
            }

            if (host == null) {
               return null;
            }

            PathMatcher.PathMatch<VirtualHost.HostEntry> result = host.match(context);
            if (result.getValue() == null) {
               return null;
            }

            return result;
         }
      } else {
         Iterator var7 = this.hosts.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry<String, VirtualHost> host = (Map.Entry)var7.next();
            PathMatcher.PathMatch<VirtualHost.HostEntry> result = ((VirtualHost)host.getValue()).match(context);
            if (result.getValue() != null) {
               return result;
            }
         }
      }

      return null;
   }

   OptionMap getClientOptions() {
      return this.clientOptions;
   }

   private Iterator<CharSequence> parseRoutes(String sessionId) {
      return this.routeIteratorFactory.iterator(sessionId);
   }

   static Context electNode(Iterable<Context> contexts, boolean existingSession, String domain) {
      Context elected = null;
      Node candidate = null;
      boolean candidateHotStandby = false;
      Iterator var6 = contexts.iterator();

      while(true) {
         Context context;
         Node node;
         boolean hotStandby;
         do {
            do {
               if (!var6.hasNext()) {
                  if (candidate != null) {
                     candidate.elected();
                  }

                  return elected;
               }

               context = (Context)var6.next();
            } while(!context.checkAvailable(existingSession));

            node = context.getNode();
            hotStandby = node.isHotStandby();
         } while(domain != null && !domain.equals(node.getNodeConfig().getDomain()));

         if (candidate != null) {
            if (candidateHotStandby) {
               if (hotStandby) {
                  if (candidate.getElectedDiff() > node.getElectedDiff()) {
                     candidate = node;
                     elected = context;
                  }
               } else {
                  candidate = node;
                  elected = context;
                  candidateHotStandby = hotStandby;
               }
            } else if (!hotStandby) {
               int lbStatus1 = candidate.getLoadStatus();
               int lbStatus2 = node.getLoadStatus();
               if (lbStatus1 > lbStatus2) {
                  candidate = node;
                  elected = context;
                  candidateHotStandby = false;
               }
            }
         } else {
            candidate = node;
            elected = context;
            candidateHotStandby = hotStandby;
         }
      }
   }

   void scheduleHealthCheck(Node node, XnioIoThread ioThread) {
      assert Thread.holdsLock(this);

      HealthCheckTask task = (HealthCheckTask)this.healthChecks.get(ioThread);
      if (task == null) {
         task = new HealthCheckTask(this.removeBrokenNodesThreshold, this.healthChecker);
         this.healthChecks.put(ioThread, task);
         task.cancelKey = ioThread.executeAtInterval(task, this.modCluster.getHealthCheckInterval(), TimeUnit.MILLISECONDS);
      }

      task.nodes.add(node);
   }

   void removeHealthCheck(Node node, XnioIoThread ioThread) {
      assert Thread.holdsLock(this);

      HealthCheckTask task = (HealthCheckTask)this.healthChecks.get(ioThread);
      if (task != null) {
         task.nodes.remove(node);
         if (task.nodes.size() == 0) {
            this.healthChecks.remove(ioThread);
            task.cancelKey.remove();
         }

      }
   }

   static long removeThreshold(long healthChecks, long removeBrokenNodes) {
      if (healthChecks > 0L && removeBrokenNodes > 0L) {
         long threshold = removeBrokenNodes / healthChecks;
         if (threshold > 1000L) {
            return 1000L;
         } else {
            return threshold < 1L ? 1L : threshold;
         }
      } else {
         return -1L;
      }
   }

   public ModClusterStatus getStatus() {
      List<ModClusterStatus.LoadBalancer> balancers = new ArrayList();

      Map.Entry bentry;
      ArrayList nodes;
      label34:
      for(Iterator var2 = this.balancers.entrySet().iterator(); var2.hasNext(); balancers.add(new BalancerImpl((Balancer)bentry.getValue(), nodes))) {
         bentry = (Map.Entry)var2.next();
         nodes = new ArrayList();
         Iterator var5 = this.getNodes().iterator();

         while(true) {
            Node node;
            do {
               if (!var5.hasNext()) {
                  continue label34;
               }

               node = (Node)var5.next();
            } while(!node.getBalancer().getName().equals(bentry.getKey()));

            List<ModClusterStatus.Context> contexts = new ArrayList();
            Iterator var8 = node.getContexts().iterator();

            while(var8.hasNext()) {
               Context i = (Context)var8.next();
               contexts.add(new ContextImpl(i));
            }

            nodes.add(new NodeImpl(node, contexts));
         }
      }

      return new ModClusterStatusImpl(balancers);
   }

   private static class ContextImpl implements ModClusterStatus.Context {
      private final Context context;

      private ContextImpl(Context context) {
         this.context = context;
      }

      public String getName() {
         return this.context.getPath();
      }

      public boolean isEnabled() {
         return this.context.isEnabled();
      }

      public boolean isStopped() {
         return this.context.isStopped();
      }

      public int getRequests() {
         return this.context.getActiveRequests();
      }

      public void enable() {
         this.context.enable();
      }

      public void disable() {
         this.context.disable();
      }

      public void stop() {
         this.context.stop();
      }

      // $FF: synthetic method
      ContextImpl(Context x0, Object x1) {
         this(x0);
      }
   }

   private static class NodeImpl implements ModClusterStatus.Node {
      private final Node node;
      private final List<ModClusterStatus.Context> contexts;

      private NodeImpl(Node node, List<ModClusterStatus.Context> contexts) {
         this.node = node;
         this.contexts = contexts;
      }

      public String getName() {
         return this.node.getJvmRoute();
      }

      public URI getUri() {
         return this.node.getConnectionPool().getUri();
      }

      public List<ModClusterStatus.Context> getContexts() {
         return Collections.unmodifiableList(this.contexts);
      }

      public ModClusterStatus.Context getContext(String name) {
         Iterator var2 = this.contexts.iterator();

         ModClusterStatus.Context i;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            i = (ModClusterStatus.Context)var2.next();
         } while(!i.getName().equals(name));

         return i;
      }

      public int getLoad() {
         return this.node.getLoad();
      }

      public NodeStatus getStatus() {
         return this.node.getStatus();
      }

      public int getOpenConnections() {
         return this.node.getConnectionPool().getOpenConnections();
      }

      public long getTransferred() {
         return this.node.getConnectionPool().getClientStatistics().getWritten();
      }

      public long getRead() {
         return this.node.getConnectionPool().getClientStatistics().getRead();
      }

      public int getElected() {
         return this.node.getElected();
      }

      public int getCacheConnections() {
         return this.node.getNodeConfig().getCacheConnections();
      }

      public String getJvmRoute() {
         return this.node.getNodeConfig().getJvmRoute();
      }

      public String getDomain() {
         return this.node.getNodeConfig().getDomain();
      }

      public int getFlushWait() {
         return this.node.getNodeConfig().getFlushwait();
      }

      public int getMaxConnections() {
         return this.node.getNodeConfig().getMaxConnections();
      }

      public int getPing() {
         return this.node.getNodeConfig().getPing();
      }

      public int getRequestQueueSize() {
         return this.node.getNodeConfig().getRequestQueueSize();
      }

      public int getTimeout() {
         return this.node.getNodeConfig().getTimeout();
      }

      public long getTtl() {
         return this.node.getNodeConfig().getTtl();
      }

      public boolean isFlushPackets() {
         return this.node.getNodeConfig().isFlushPackets();
      }

      public boolean isQueueNewRequests() {
         return this.node.getNodeConfig().isQueueNewRequests();
      }

      public List<String> getAliases() {
         List<String> ret = new ArrayList();
         Iterator var2 = this.node.getVHosts().iterator();

         while(var2.hasNext()) {
            Node.VHostMapping host = (Node.VHostMapping)var2.next();
            ret.addAll(host.getAliases());
         }

         return ret;
      }

      public void resetStatistics() {
         this.node.getConnectionPool().getClientStatistics().reset();
      }

      // $FF: synthetic method
      NodeImpl(Node x0, List x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class BalancerImpl implements ModClusterStatus.LoadBalancer {
      private final Balancer balancer;
      private final List<ModClusterStatus.Node> nodes;

      private BalancerImpl(Balancer balancer, List<ModClusterStatus.Node> nodes) {
         this.balancer = balancer;
         this.nodes = nodes;
      }

      public String getName() {
         return this.balancer.getName();
      }

      public List<ModClusterStatus.Node> getNodes() {
         return this.nodes;
      }

      public ModClusterStatus.Node getNode(String name) {
         Iterator var2 = this.nodes.iterator();

         ModClusterStatus.Node i;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            i = (ModClusterStatus.Node)var2.next();
         } while(!i.getName().equals(name));

         return i;
      }

      public boolean isStickySession() {
         return this.balancer.isStickySession();
      }

      public String getStickySessionCookie() {
         return this.balancer.getStickySessionCookie();
      }

      public String getStickySessionPath() {
         return null;
      }

      public boolean isStickySessionRemove() {
         return this.balancer.isStickySessionRemove();
      }

      public boolean isStickySessionForce() {
         return this.balancer.isStickySessionForce();
      }

      public int getWaitWorker() {
         return this.balancer.getWaitWorker();
      }

      public int getMaxRetries() {
         return this.balancer.getMaxRetries();
      }

      /** @deprecated */
      @Deprecated
      public int getMaxAttempts() {
         return this.balancer.getMaxRetries();
      }

      // $FF: synthetic method
      BalancerImpl(Balancer x0, List x1, Object x2) {
         this(x0, x1);
      }
   }

   private static class ModClusterStatusImpl implements ModClusterStatus {
      private final List<ModClusterStatus.LoadBalancer> balancers;

      private ModClusterStatusImpl(List<ModClusterStatus.LoadBalancer> balancers) {
         this.balancers = balancers;
      }

      public List<ModClusterStatus.LoadBalancer> getLoadBalancers() {
         return this.balancers;
      }

      public ModClusterStatus.LoadBalancer getLoadBalancer(String name) {
         Iterator var2 = this.balancers.iterator();

         ModClusterStatus.LoadBalancer b;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            b = (ModClusterStatus.LoadBalancer)var2.next();
         } while(!b.getName().equals(name));

         return b;
      }

      // $FF: synthetic method
      ModClusterStatusImpl(List x0, Object x1) {
         this(x0);
      }
   }

   class UpdateLoadTask implements Runnable {
      private volatile XnioExecutor.Key cancelKey;

      public void run() {
         Iterator var1 = ModClusterContainer.this.nodes.values().iterator();

         while(var1.hasNext()) {
            Node node = (Node)var1.next();
            node.resetLbStatus();
         }

      }
   }

   static class HealthCheckTask implements Runnable {
      private final long threshold;
      private final NodeHealthChecker healthChecker;
      private final ArrayList<Node> nodes = new ArrayList();
      private volatile XnioExecutor.Key cancelKey;

      HealthCheckTask(long threshold, NodeHealthChecker healthChecker) {
         this.threshold = threshold;
         this.healthChecker = healthChecker;
      }

      public void run() {
         Iterator var1 = this.nodes.iterator();

         while(var1.hasNext()) {
            Node node = (Node)var1.next();
            node.checkHealth(this.threshold, this.healthChecker);
         }

      }
   }
}
