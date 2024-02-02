package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.proxy.ConnectionPoolManager;
import io.undertow.server.handlers.proxy.ProxyConnectionPool;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;
import org.xnio.OptionMap;
import org.xnio.XnioIoThread;

class Node {
   private final int id;
   private final String jvmRoute;
   private final ConnectionPoolManager connectionPoolManager;
   private final NodeConfig nodeConfig;
   private final Balancer balancerConfig;
   private final ProxyConnectionPool connectionPool;
   private final NodeLbStatus lbStatus = new NodeLbStatus();
   private final ModClusterContainer container;
   private final List<VHostMapping> vHosts = new CopyOnWriteArrayList();
   private final List<Context> contexts = new CopyOnWriteArrayList();
   private final XnioIoThread ioThread;
   private final ByteBufferPool bufferPool;
   private volatile int state = Integer.MIN_VALUE;
   private static final int ERROR = Integer.MIN_VALUE;
   private static final int REMOVED = 1073741824;
   private static final int HOT_STANDBY = 536870912;
   private static final int ACTIVE_PING = 268435456;
   private static final int ERROR_MASK = 1023;
   private static final AtomicInteger idGen = new AtomicInteger();
   private static final AtomicIntegerFieldUpdater<Node> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(Node.class, "state");
   static final AtomicInteger vHostIdGen = new AtomicInteger();

   protected Node(NodeConfig nodeConfig, Balancer balancerConfig, XnioIoThread ioThread, ByteBufferPool bufferPool, ModClusterContainer container) {
      this.id = idGen.incrementAndGet();
      this.jvmRoute = nodeConfig.getJvmRoute();
      this.nodeConfig = nodeConfig;
      this.ioThread = ioThread;
      this.bufferPool = bufferPool;
      this.balancerConfig = balancerConfig;
      this.container = container;
      this.connectionPoolManager = new NodeConnectionPoolManager();
      this.connectionPool = new ProxyConnectionPool(this.connectionPoolManager, nodeConfig.getConnectionURI(), container.getXnioSsl(), container.getClient(), container.getClientOptions());
   }

   public int getId() {
      return this.id;
   }

   public String getJvmRoute() {
      return this.jvmRoute;
   }

   public Balancer getBalancer() {
      return this.balancerConfig;
   }

   public NodeConfig getNodeConfig() {
      return this.nodeConfig;
   }

   public ProxyConnectionPool getConnectionPool() {
      return this.connectionPool;
   }

   XnioIoThread getIoThread() {
      return this.ioThread;
   }

   public NodeStatus getStatus() {
      int status = this.state;
      if (Bits.anyAreSet(status, Integer.MIN_VALUE)) {
         return NodeStatus.NODE_DOWN;
      } else {
         return Bits.anyAreSet(status, 536870912) ? NodeStatus.NODE_HOT_STANDBY : NodeStatus.NODE_UP;
      }
   }

   public int getElected() {
      return this.lbStatus.getElected();
   }

   int getElectedDiff() {
      return this.lbStatus.getElectedDiff();
   }

   public int getLoad() {
      int status = this.state;
      if (Bits.anyAreSet(status, Integer.MIN_VALUE)) {
         return -1;
      } else {
         return Bits.anyAreSet(status, 536870912) ? 0 : this.lbStatus.getLbFactor();
      }
   }

   public int getLoadStatus() {
      return this.lbStatus.getLbStatus();
   }

   void elected() {
      this.lbStatus.elected();
   }

   List<VHostMapping> getVHosts() {
      return Collections.unmodifiableList(this.vHosts);
   }

   Collection<Context> getContexts() {
      return Collections.unmodifiableCollection(this.contexts);
   }

   void resetLbStatus() {
      if (!Bits.allAreClear(this.state, Integer.MIN_VALUE) || !this.lbStatus.update()) {
         ;
      }
   }

   protected void checkHealth(long threshold, NodeHealthChecker healthChecker) {
      int state = this.state;
      if (!Bits.anyAreSet(state, 1342177280)) {
         this.healthCheckPing(threshold, healthChecker);
      }
   }

   void healthCheckPing(final long threshold, NodeHealthChecker healthChecker) {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         if ((oldState & 268435456) != 0) {
            return;
         }

         newState = oldState | 268435456;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

      NodePingUtil.internalPingNode(this, new NodePingUtil.PingCallback() {
         public void completed() {
            Node.this.clearActivePing();
         }

         public void failed() {
            if ((long)Node.this.healthCheckFailed() == threshold) {
               Node.this.ioThread.getWorker().execute(new Runnable() {
                  public void run() {
                     Node.this.container.removeNode(Node.this, true);
                     Node.this.clearActivePing();
                  }
               });
            } else {
               Node.this.clearActivePing();
            }

         }
      }, healthChecker, this.ioThread, this.bufferPool, this.container.getClient(), this.container.getXnioSsl(), OptionMap.EMPTY);
   }

   void ping(HttpServerExchange exchange, NodePingUtil.PingCallback callback) {
      NodePingUtil.pingNode(this, exchange, callback);
   }

   Context registerContext(String path, List<String> virtualHosts) {
      VHostMapping host = null;
      Iterator var4 = this.vHosts.iterator();

      while(var4.hasNext()) {
         VHostMapping vhost = (VHostMapping)var4.next();
         if (virtualHosts.equals(vhost.getAliases())) {
            host = vhost;
            break;
         }
      }

      if (host == null) {
         host = new VHostMapping(this, virtualHosts);
         this.vHosts.add(host);
      }

      Context context = new Context(path, host, this);
      this.contexts.add(context);
      return context;
   }

   Context getContext(String path, List<String> aliases) {
      VHostMapping host = null;
      Iterator var4 = this.vHosts.iterator();

      while(var4.hasNext()) {
         VHostMapping vhost = (VHostMapping)var4.next();
         if (aliases.equals(vhost.getAliases())) {
            host = vhost;
            break;
         }
      }

      if (host == null) {
         return null;
      } else {
         var4 = this.contexts.iterator();

         Context context;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            context = (Context)var4.next();
         } while(!context.getPath().equals(path) || context.getVhost() != host);

         return context;
      }
   }

   Context removeContext(String path, List<String> aliases) {
      Context context = this.getContext(path, aliases);
      if (context != null) {
         context.stop();
         this.contexts.remove(context);
         return context;
      } else {
         return null;
      }
   }

   protected void updateLoad(int i) {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState & 1610611712;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

      this.lbStatus.updateLoad(i);
   }

   protected void hotStandby() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState & 2147482624 | 536870912;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

      this.lbStatus.updateLoad(0);
   }

   protected void markRemoved() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState | 1073741824;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

      this.connectionPool.close();
   }

   protected void markInError() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState | Integer.MIN_VALUE;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

      UndertowLogger.ROOT_LOGGER.nodeIsInError(this.jvmRoute);
   }

   private void clearActivePing() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         newState = oldState & -268435457;
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

   }

   private int healthCheckFailed() {
      int oldState;
      int newState;
      do {
         oldState = this.state;
         if ((oldState & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            newState = oldState | Integer.MIN_VALUE;
            UndertowLogger.ROOT_LOGGER.nodeIsInError(this.jvmRoute);
         } else {
            if ((oldState & 1023) == 1023) {
               return 1023;
            }

            newState = oldState + 1;
         }
      } while(!stateUpdater.compareAndSet(this, oldState, newState));

      return newState & 1023;
   }

   protected void resetState() {
      this.state = Integer.MIN_VALUE;
      this.lbStatus.updateLoad(0);
   }

   protected boolean isInErrorState() {
      return (this.state & Integer.MIN_VALUE) == Integer.MIN_VALUE;
   }

   boolean isHotStandby() {
      return Bits.anyAreSet(this.state, 536870912);
   }

   protected boolean checkAvailable(boolean existingSession) {
      if (Bits.allAreClear(this.state, -1073741824)) {
         ProxyConnectionPool.AvailabilityType availability = this.connectionPool.available();
         if (availability == ProxyConnectionPool.AvailabilityType.AVAILABLE) {
            return true;
         }

         if (availability == ProxyConnectionPool.AvailabilityType.FULL) {
            if (existingSession) {
               return true;
            }

            if (this.nodeConfig.isQueueNewRequests()) {
               return true;
            }
         }
      }

      return false;
   }

   public String toString() {
      return "Node{jvmRoute='" + this.jvmRoute + '\'' + ", contexts=" + this.contexts + '}';
   }

   static class VHostMapping {
      private final int id;
      private final List<String> aliases;
      private final Node node;

      VHostMapping(Node node, List<String> aliases) {
         this.id = Node.vHostIdGen.incrementAndGet();
         this.aliases = aliases;
         this.node = node;
      }

      public int getId() {
         return this.id;
      }

      public List<String> getAliases() {
         return this.aliases;
      }

      Node getNode() {
         return this.node;
      }
   }

   private class NodeConnectionPoolManager implements ConnectionPoolManager {
      private NodeConnectionPoolManager() {
      }

      public boolean isAvailable() {
         return Bits.allAreClear(Node.this.state, -1073741824);
      }

      public boolean handleError() {
         Node.this.markInError();
         return false;
      }

      public boolean clearError() {
         return this.isAvailable();
      }

      public int getMaxConnections() {
         return Node.this.nodeConfig.getMaxConnections();
      }

      public int getMaxCachedConnections() {
         return Node.this.nodeConfig.getMaxConnections();
      }

      public int getSMaxConnections() {
         return Node.this.nodeConfig.getSmax();
      }

      public long getTtl() {
         return Node.this.nodeConfig.getTtl();
      }

      public int getMaxQueueSize() {
         return Node.this.nodeConfig.getRequestQueueSize();
      }

      public int getProblemServerRetry() {
         return -1;
      }

      // $FF: synthetic method
      NodeConnectionPoolManager(Object x1) {
         this();
      }
   }
}
