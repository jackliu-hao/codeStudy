package io.undertow.server.handlers.proxy;

import io.undertow.UndertowLogger;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.AttachmentKey;
import io.undertow.util.AttachmentList;
import io.undertow.util.CopyOnWriteMap;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.ssl.XnioSsl;

public class LoadBalancingProxyClient implements ProxyClient {
   private final AttachmentKey<ExclusiveConnectionHolder> exclusiveConnectionKey;
   private static final AttachmentKey<AttachmentList<Host>> ATTEMPTED_HOSTS = AttachmentKey.createList(Host.class);
   private volatile int problemServerRetry;
   private final Set<String> sessionCookieNames;
   private volatile int connectionsPerThread;
   private volatile int maxQueueSize;
   private volatile int softMaxConnectionsPerThread;
   private volatile int ttl;
   private volatile Host[] hosts;
   private final HostSelector hostSelector;
   private final UndertowClient client;
   private final Map<String, Host> routes;
   private RouteIteratorFactory routeIteratorFactory;
   private final ExclusivityChecker exclusivityChecker;
   private static final ProxyClient.ProxyTarget PROXY_TARGET = new ProxyClient.ProxyTarget() {
   };

   public List<ProxyClient.ProxyTarget> getAllTargets() {
      List<ProxyClient.ProxyTarget> arr = new ArrayList();
      Host[] var2 = this.hosts;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Host host = var2[var4];
         ProxyClient.HostProxyTarget proxyTarget = new ProxyClient.HostProxyTarget() {
            Host host;

            public void setHost(Host host) {
               this.host = host;
            }

            public String toString() {
               return this.host.getUri().toString();
            }
         };
         proxyTarget.setHost(host);
         arr.add(proxyTarget);
      }

      return arr;
   }

   public LoadBalancingProxyClient() {
      this(UndertowClient.getInstance());
   }

   public LoadBalancingProxyClient(UndertowClient client) {
      this(client, (ExclusivityChecker)null, (HostSelector)null);
   }

   public LoadBalancingProxyClient(ExclusivityChecker client) {
      this(UndertowClient.getInstance(), client, (HostSelector)null);
   }

   public LoadBalancingProxyClient(UndertowClient client, ExclusivityChecker exclusivityChecker) {
      this(client, exclusivityChecker, (HostSelector)null);
   }

   public LoadBalancingProxyClient(UndertowClient client, ExclusivityChecker exclusivityChecker, HostSelector hostSelector) {
      this.exclusiveConnectionKey = AttachmentKey.create(ExclusiveConnectionHolder.class);
      this.problemServerRetry = 10;
      this.sessionCookieNames = new CopyOnWriteArraySet();
      this.connectionsPerThread = 10;
      this.maxQueueSize = 0;
      this.softMaxConnectionsPerThread = 5;
      this.ttl = -1;
      this.hosts = new Host[0];
      this.routes = new CopyOnWriteMap();
      this.routeIteratorFactory = new RouteIteratorFactory(RouteParsingStrategy.SINGLE, RouteIteratorFactory.ParsingCompatibility.MOD_JK);
      this.client = client;
      this.exclusivityChecker = exclusivityChecker;
      this.sessionCookieNames.add("JSESSIONID");
      if (hostSelector == null) {
         this.hostSelector = new RoundRobinHostSelector();
      } else {
         this.hostSelector = hostSelector;
      }

   }

   public LoadBalancingProxyClient addSessionCookieName(String sessionCookieName) {
      this.sessionCookieNames.add(sessionCookieName);
      return this;
   }

   public LoadBalancingProxyClient removeSessionCookieName(String sessionCookieName) {
      this.sessionCookieNames.remove(sessionCookieName);
      return this;
   }

   public LoadBalancingProxyClient setProblemServerRetry(int problemServerRetry) {
      this.problemServerRetry = problemServerRetry;
      return this;
   }

   public int getProblemServerRetry() {
      return this.problemServerRetry;
   }

   public int getConnectionsPerThread() {
      return this.connectionsPerThread;
   }

   public LoadBalancingProxyClient setConnectionsPerThread(int connectionsPerThread) {
      this.connectionsPerThread = connectionsPerThread;
      return this;
   }

   public int getMaxQueueSize() {
      return this.maxQueueSize;
   }

   public LoadBalancingProxyClient setMaxQueueSize(int maxQueueSize) {
      this.maxQueueSize = maxQueueSize;
      return this;
   }

   public LoadBalancingProxyClient setTtl(int ttl) {
      this.ttl = ttl;
      return this;
   }

   public LoadBalancingProxyClient setSoftMaxConnectionsPerThread(int softMaxConnectionsPerThread) {
      this.softMaxConnectionsPerThread = softMaxConnectionsPerThread;
      return this;
   }

   public LoadBalancingProxyClient setRouteParsingStrategy(RouteParsingStrategy routeParsingStrategy) {
      this.routeIteratorFactory = new RouteIteratorFactory(routeParsingStrategy, RouteIteratorFactory.ParsingCompatibility.MOD_JK, (String)null);
      return this;
   }

   public LoadBalancingProxyClient setRankedRoutingDelimiter(String rankedRoutingDelimiter) {
      this.routeIteratorFactory = new RouteIteratorFactory(RouteParsingStrategy.RANKED, RouteIteratorFactory.ParsingCompatibility.MOD_JK, rankedRoutingDelimiter);
      return this;
   }

   public synchronized LoadBalancingProxyClient addHost(URI host) {
      return this.addHost(host, (String)null, (XnioSsl)null);
   }

   public synchronized LoadBalancingProxyClient addHost(URI host, XnioSsl ssl) {
      return this.addHost(host, (String)null, ssl);
   }

   public synchronized LoadBalancingProxyClient addHost(URI host, String jvmRoute) {
      return this.addHost(host, jvmRoute, (XnioSsl)null);
   }

   public synchronized LoadBalancingProxyClient addHost(URI host, String jvmRoute, XnioSsl ssl) {
      Host h = new Host(jvmRoute, (InetSocketAddress)null, host, ssl, OptionMap.EMPTY);
      Host[] existing = this.hosts;
      Host[] newHosts = new Host[existing.length + 1];
      System.arraycopy(existing, 0, newHosts, 0, existing.length);
      newHosts[existing.length] = h;
      this.hosts = newHosts;
      if (jvmRoute != null) {
         this.routes.put(jvmRoute, h);
      }

      return this;
   }

   public synchronized LoadBalancingProxyClient addHost(URI host, String jvmRoute, XnioSsl ssl, OptionMap options) {
      return this.addHost((InetSocketAddress)null, host, jvmRoute, ssl, options);
   }

   public synchronized LoadBalancingProxyClient addHost(InetSocketAddress bindAddress, URI host, String jvmRoute, XnioSsl ssl, OptionMap options) {
      Host h = new Host(jvmRoute, bindAddress, host, ssl, options);
      Host[] existing = this.hosts;
      Host[] newHosts = new Host[existing.length + 1];
      System.arraycopy(existing, 0, newHosts, 0, existing.length);
      newHosts[existing.length] = h;
      this.hosts = newHosts;
      if (jvmRoute != null) {
         this.routes.put(jvmRoute, h);
      }

      return this;
   }

   public synchronized LoadBalancingProxyClient removeHost(URI uri) {
      int found = -1;
      Host[] existing = this.hosts;
      Host removedHost = null;

      for(int i = 0; i < existing.length; ++i) {
         if (existing[i].uri.equals(uri)) {
            found = i;
            removedHost = existing[i];
            break;
         }
      }

      if (found == -1) {
         return this;
      } else {
         Host[] newHosts = new Host[existing.length - 1];
         System.arraycopy(existing, 0, newHosts, 0, found);
         System.arraycopy(existing, found + 1, newHosts, found, existing.length - found - 1);
         this.hosts = newHosts;
         removedHost.connectionPool.close();
         if (removedHost.jvmRoute != null) {
            this.routes.remove(removedHost.jvmRoute);
         }

         return this;
      }
   }

   public ProxyClient.ProxyTarget findTarget(HttpServerExchange exchange) {
      return PROXY_TARGET;
   }

   public void getConnection(ProxyClient.ProxyTarget target, HttpServerExchange exchange, final ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
      final ExclusiveConnectionHolder holder = (ExclusiveConnectionHolder)exchange.getConnection().getAttachment(this.exclusiveConnectionKey);
      if (holder != null && holder.connection.getConnection().isOpen()) {
         callback.completed(exchange, holder.connection);
      } else {
         final Host host = this.selectHost(exchange);
         if (host == null) {
            callback.couldNotResolveBackend(exchange);
         } else {
            exchange.addToAttachmentList(ATTEMPTED_HOSTS, host);
            if (holder == null && (this.exclusivityChecker == null || !this.exclusivityChecker.isExclusivityRequired(exchange))) {
               host.connectionPool.connect(target, exchange, callback, timeout, timeUnit, false);
            } else {
               host.connectionPool.connect(target, exchange, new ProxyCallback<ProxyConnection>() {
                  public void completed(HttpServerExchange exchange, ProxyConnection result) {
                     if (holder != null) {
                        holder.connection = result;
                     } else {
                        final ExclusiveConnectionHolder newHolder = new ExclusiveConnectionHolder();
                        newHolder.connection = result;
                        ServerConnection connection = exchange.getConnection();
                        connection.putAttachment(LoadBalancingProxyClient.this.exclusiveConnectionKey, newHolder);
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
                     UndertowLogger.PROXY_REQUEST_LOGGER.proxyFailedToConnectToBackend(exchange.getRequestURI(), host.uri);
                     callback.failed(exchange);
                  }

                  public void couldNotResolveBackend(HttpServerExchange exchange) {
                     callback.couldNotResolveBackend(exchange);
                  }
               }, timeout, timeUnit, true);
            }
         }

      }
   }

   protected Host selectHost(HttpServerExchange exchange) {
      AttachmentList<Host> attempted = (AttachmentList)exchange.getAttachment(ATTEMPTED_HOSTS);
      Host[] hosts = this.hosts;
      if (hosts.length == 0) {
         return null;
      } else {
         Iterator<CharSequence> parsedRoutes = this.parseRoutes(exchange);

         Host host;
         do {
            do {
               if (!parsedRoutes.hasNext()) {
                  int host = this.hostSelector.selectHost(hosts);
                  int startHost = host;
                  Host full = null;
                  Host problem = null;

                  do {
                     Host selected = hosts[host];
                     if (attempted == null || !attempted.contains(selected)) {
                        ProxyConnectionPool.AvailabilityType available = selected.connectionPool.available();
                        if (available == ProxyConnectionPool.AvailabilityType.AVAILABLE) {
                           return selected;
                        }

                        if (available == ProxyConnectionPool.AvailabilityType.FULL && full == null) {
                           full = selected;
                        } else if ((available == ProxyConnectionPool.AvailabilityType.PROBLEM || available == ProxyConnectionPool.AvailabilityType.FULL_QUEUE) && problem == null) {
                           problem = selected;
                        }
                     }

                     host = (host + 1) % hosts.length;
                  } while(host != startHost);

                  if (full != null) {
                     return full;
                  }

                  if (problem != null) {
                     return problem;
                  }

                  return null;
               }

               host = (Host)this.routes.get(((CharSequence)parsedRoutes.next()).toString());
            } while(host == null);
         } while(attempted != null && attempted.contains(host));

         return host;
      }
   }

   protected Iterator<CharSequence> parseRoutes(HttpServerExchange exchange) {
      Iterator var2 = this.sessionCookieNames.iterator();

      while(var2.hasNext()) {
         String cookieName = (String)var2.next();
         Iterator var4 = exchange.requestCookies().iterator();

         while(var4.hasNext()) {
            Cookie cookie = (Cookie)var4.next();
            if (cookieName.equals(cookie.getName())) {
               return this.routeIteratorFactory.iterator(cookie.getValue());
            }
         }
      }

      return this.routeIteratorFactory.iterator((String)null);
   }

   public void closeCurrentConnections() {
      Host[] var1 = this.hosts;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Host host = var1[var3];
         host.closeCurrentConnections();
      }

   }

   static class RoundRobinHostSelector implements HostSelector {
      private final AtomicInteger currentHost = new AtomicInteger(0);

      public int selectHost(Host[] availableHosts) {
         return this.currentHost.incrementAndGet() % availableHosts.length;
      }
   }

   public interface HostSelector {
      int selectHost(Host[] var1);
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

   public final class Host extends ConnectionPoolErrorHandler.SimpleConnectionPoolErrorHandler implements ConnectionPoolManager {
      final ProxyConnectionPool connectionPool;
      final String jvmRoute;
      final URI uri;
      final XnioSsl ssl;

      private Host(String jvmRoute, InetSocketAddress bindAddress, URI uri, XnioSsl ssl, OptionMap options) {
         this.connectionPool = new ProxyConnectionPool(this, bindAddress, uri, ssl, LoadBalancingProxyClient.this.client, options);
         this.jvmRoute = jvmRoute;
         this.uri = uri;
         this.ssl = ssl;
      }

      public int getProblemServerRetry() {
         return LoadBalancingProxyClient.this.problemServerRetry;
      }

      public int getMaxConnections() {
         return LoadBalancingProxyClient.this.connectionsPerThread;
      }

      public int getMaxCachedConnections() {
         return LoadBalancingProxyClient.this.connectionsPerThread;
      }

      public int getSMaxConnections() {
         return LoadBalancingProxyClient.this.softMaxConnectionsPerThread;
      }

      public long getTtl() {
         return (long)LoadBalancingProxyClient.this.ttl;
      }

      public int getMaxQueueSize() {
         return LoadBalancingProxyClient.this.maxQueueSize;
      }

      public URI getUri() {
         return this.uri;
      }

      void closeCurrentConnections() {
         this.connectionPool.closeCurrentConnections();
      }

      // $FF: synthetic method
      Host(String x1, InetSocketAddress x2, URI x3, XnioSsl x4, OptionMap x5, Object x6) {
         this(x1, x2, x3, x4, x5);
      }
   }
}
