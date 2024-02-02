/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.client.UndertowClient;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.server.handlers.cache.LRUCache;
/*     */ import io.undertow.server.handlers.proxy.ProxyClient;
/*     */ import io.undertow.server.handlers.proxy.RouteIteratorFactory;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.PathMatcher;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.ssl.XnioSsl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ModClusterContainer
/*     */   implements ModClusterController
/*     */ {
/*  56 */   private final ConcurrentMap<String, Balancer> balancers = (ConcurrentMap<String, Balancer>)new CopyOnWriteMap();
/*     */ 
/*     */   
/*  59 */   private final ConcurrentMap<String, Node> nodes = (ConcurrentMap<String, Node>)new CopyOnWriteMap();
/*     */ 
/*     */   
/*  62 */   private final ConcurrentMap<String, VirtualHost> hosts = (ConcurrentMap<String, VirtualHost>)new CopyOnWriteMap();
/*     */ 
/*     */   
/*  65 */   private final LRUCache<String, String> failoverDomains = new LRUCache(100, 300000);
/*     */ 
/*     */   
/*  68 */   private final ConcurrentMap<XnioIoThread, HealthCheckTask> healthChecks = (ConcurrentMap<XnioIoThread, HealthCheckTask>)new CopyOnWriteMap();
/*  69 */   private final UpdateLoadTask updateLoadTask = new UpdateLoadTask();
/*     */   
/*     */   private final XnioSsl xnioSsl;
/*     */   
/*     */   private final UndertowClient client;
/*     */   private final ProxyClient proxyClient;
/*     */   private final ModCluster modCluster;
/*     */   private final NodeHealthChecker healthChecker;
/*     */   private final long removeBrokenNodesThreshold;
/*     */   private RouteIteratorFactory routeIteratorFactory;
/*     */   private final OptionMap clientOptions;
/*     */   
/*     */   ModClusterContainer(ModCluster modCluster, XnioSsl xnioSsl, UndertowClient client, OptionMap clientOptions) {
/*  82 */     this.xnioSsl = xnioSsl;
/*  83 */     this.client = client;
/*  84 */     this.modCluster = modCluster;
/*  85 */     this.clientOptions = clientOptions;
/*  86 */     this.healthChecker = modCluster.getHealthChecker();
/*  87 */     this.proxyClient = new ModClusterProxyClient(null, this);
/*  88 */     this.removeBrokenNodesThreshold = removeThreshold(modCluster.getHealthCheckInterval(), modCluster.getRemoveBrokenNodes());
/*  89 */     this.routeIteratorFactory = new RouteIteratorFactory(modCluster.routeParsingStrategy(), RouteIteratorFactory.ParsingCompatibility.MOD_CLUSTER, modCluster.rankedAffinityDelimiter());
/*     */   }
/*     */   
/*     */   String getServerID() {
/*  93 */     return this.modCluster.getServerID();
/*     */   }
/*     */   
/*     */   UndertowClient getClient() {
/*  97 */     return this.client;
/*     */   }
/*     */   
/*     */   XnioSsl getXnioSsl() {
/* 101 */     return this.xnioSsl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProxyClient getProxyClient() {
/* 110 */     return this.proxyClient;
/*     */   }
/*     */   
/*     */   Collection<Balancer> getBalancers() {
/* 114 */     return Collections.unmodifiableCollection(this.balancers.values());
/*     */   }
/*     */   
/*     */   Collection<Node> getNodes() {
/* 118 */     return Collections.unmodifiableCollection(this.nodes.values());
/*     */   }
/*     */   
/*     */   Node getNode(String jvmRoute) {
/* 122 */     return this.nodes.get(jvmRoute);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ModClusterProxyTarget findTarget(HttpServerExchange exchange) {
/* 133 */     PathMatcher.PathMatch<VirtualHost.HostEntry> entry = mapVirtualHost(exchange);
/* 134 */     if (entry == null) {
/* 135 */       return null;
/*     */     }
/* 137 */     for (Balancer balancer : this.balancers.values()) {
/* 138 */       if (balancer.isStickySession()) {
/* 139 */         for (Cookie cookie : exchange.requestCookies()) {
/* 140 */           if (balancer.getStickySessionCookie().equals(cookie.getName())) {
/* 141 */             String sessionId = cookie.getValue();
/* 142 */             Iterator<CharSequence> routes = parseRoutes(sessionId);
/* 143 */             if (routes.hasNext()) {
/* 144 */               return new ModClusterProxyTarget.ExistingSessionTarget(sessionId, routes, (VirtualHost.HostEntry)entry.getValue(), this, balancer.isStickySessionForce());
/*     */             }
/*     */           } 
/*     */         } 
/* 148 */         if (exchange.getPathParameters().containsKey(balancer.getStickySessionPath())) {
/* 149 */           String sessionId = ((Deque<String>)exchange.getPathParameters().get(balancer.getStickySessionPath())).getFirst();
/* 150 */           Iterator<CharSequence> jvmRoute = parseRoutes(sessionId);
/* 151 */           if (jvmRoute.hasNext()) {
/* 152 */             return new ModClusterProxyTarget.ExistingSessionTarget(sessionId, jvmRoute, (VirtualHost.HostEntry)entry.getValue(), this, balancer.isStickySessionForce());
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 157 */     return new ModClusterProxyTarget.BasicTarget((VirtualHost.HostEntry)entry.getValue(), this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean addNode(NodeConfig config, Balancer.BalancerBuilder balancerConfig, XnioIoThread ioThread, ByteBufferPool bufferPool) {
/* 171 */     String jvmRoute = config.getJvmRoute();
/* 172 */     Node existing = this.nodes.get(jvmRoute);
/* 173 */     if (existing != null) {
/* 174 */       if (config.getConnectionURI().equals(existing.getNodeConfig().getConnectionURI())) {
/*     */         
/* 176 */         existing.resetState();
/* 177 */         return true;
/*     */       } 
/* 179 */       existing.markRemoved();
/* 180 */       removeNode(existing);
/* 181 */       if (!existing.isInErrorState()) {
/* 182 */         return false;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 187 */     String balancerRef = config.getBalancer();
/* 188 */     Balancer balancer = this.balancers.get(balancerRef);
/* 189 */     if (balancer != null) {
/* 190 */       UndertowLogger.ROOT_LOGGER.debugf("Balancer %s already exists, replacing", balancerRef);
/*     */     }
/* 192 */     balancer = balancerConfig.build();
/* 193 */     this.balancers.put(balancerRef, balancer);
/*     */     
/* 195 */     Node node = new Node(config, balancer, ioThread, bufferPool, this);
/* 196 */     this.nodes.put(jvmRoute, node);
/*     */     
/* 198 */     scheduleHealthCheck(node, ioThread);
/*     */     
/* 200 */     if (this.updateLoadTask.cancelKey == null) {
/* 201 */       this.updateLoadTask.cancelKey = ioThread.executeAtInterval(this.updateLoadTask, this.modCluster.getHealthCheckInterval(), TimeUnit.MILLISECONDS);
/*     */     }
/*     */     
/* 204 */     this.failoverDomains.remove(node.getJvmRoute());
/* 205 */     UndertowLogger.ROOT_LOGGER.registeringNode(jvmRoute, config.getConnectionURI());
/* 206 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean enableNode(String jvmRoute) {
/* 216 */     Node node = this.nodes.get(jvmRoute);
/* 217 */     if (node != null) {
/* 218 */       for (Context context : node.getContexts()) {
/* 219 */         context.enable();
/*     */       }
/* 221 */       return true;
/*     */     } 
/* 223 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean disableNode(String jvmRoute) {
/* 233 */     Node node = this.nodes.get(jvmRoute);
/* 234 */     if (node != null) {
/* 235 */       for (Context context : node.getContexts()) {
/* 236 */         context.disable();
/*     */       }
/* 238 */       return true;
/*     */     } 
/* 240 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean stopNode(String jvmRoute) {
/* 250 */     Node node = this.nodes.get(jvmRoute);
/* 251 */     if (node != null) {
/* 252 */       for (Context context : node.getContexts()) {
/* 253 */         context.stop();
/*     */       }
/* 255 */       return true;
/*     */     } 
/* 257 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Node removeNode(String jvmRoute) {
/* 267 */     Node node = this.nodes.get(jvmRoute);
/* 268 */     if (node != null) {
/* 269 */       removeNode(node);
/*     */     }
/* 271 */     return node;
/*     */   }
/*     */   
/*     */   protected void removeNode(Node node) {
/* 275 */     removeNode(node, false);
/*     */   }
/*     */   
/*     */   protected synchronized void removeNode(Node node, boolean onlyInError) {
/* 279 */     if (onlyInError && !node.isInErrorState()) {
/*     */       return;
/*     */     }
/* 282 */     String jvmRoute = node.getJvmRoute();
/* 283 */     node.markRemoved();
/* 284 */     if (this.nodes.remove(jvmRoute, node)) {
/* 285 */       UndertowLogger.ROOT_LOGGER.removingNode(jvmRoute);
/* 286 */       node.markRemoved();
/*     */       
/* 288 */       removeHealthCheck(node, node.getIoThread());
/*     */       
/* 290 */       for (Context context : node.getContexts()) {
/* 291 */         removeContext(context.getPath(), node, context.getVirtualHosts());
/*     */       }
/* 293 */       String domain = node.getNodeConfig().getDomain();
/* 294 */       if (domain != null) {
/* 295 */         this.failoverDomains.add(node.getJvmRoute(), domain);
/*     */       }
/* 297 */       String balancerName = node.getBalancer().getName();
/* 298 */       for (Node other : this.nodes.values()) {
/* 299 */         if (other.getBalancer().getName().equals(balancerName)) {
/*     */           return;
/*     */         }
/*     */       } 
/* 303 */       this.balancers.remove(balancerName);
/*     */     } 
/* 305 */     if (this.nodes.size() == 0) {
/*     */       
/* 307 */       this.updateLoadTask.cancelKey.remove();
/* 308 */       this.updateLoadTask.cancelKey = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean enableContext(String contextPath, String jvmRoute, List<String> aliases) {
/* 320 */     Node node = this.nodes.get(jvmRoute);
/* 321 */     if (node != null) {
/* 322 */       Context context = getOrRegisterContext(contextPath, jvmRoute, aliases, node);
/* 323 */       context.enable();
/* 324 */       return true;
/*     */     } 
/* 326 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized boolean disableContext(String contextPath, String jvmRoute, List<String> aliases) {
/* 330 */     Node node = this.nodes.get(jvmRoute);
/* 331 */     if (node != null) {
/* 332 */       Context context = getOrRegisterContext(contextPath, jvmRoute, aliases, node);
/* 333 */       context.disable();
/* 334 */       return true;
/*     */     } 
/* 336 */     return false;
/*     */   }
/*     */   
/*     */   synchronized int stopContext(String contextPath, String jvmRoute, List<String> aliases) {
/* 340 */     Node node = this.nodes.get(jvmRoute);
/* 341 */     if (node != null) {
/* 342 */       Context context = getOrRegisterContext(contextPath, jvmRoute, aliases, node);
/* 343 */       context.stop();
/* 344 */       return context.getActiveRequests();
/*     */     } 
/* 346 */     return -1;
/*     */   }
/*     */   
/*     */   synchronized boolean removeContext(String contextPath, String jvmRoute, List<String> aliases) {
/* 350 */     Node node = this.nodes.get(jvmRoute);
/* 351 */     if (node != null) {
/* 352 */       return removeContext(contextPath, node, aliases);
/*     */     }
/* 354 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized boolean removeContext(String contextPath, Node node, List<String> aliases) {
/* 358 */     if (node == null) {
/* 359 */       return false;
/*     */     }
/* 361 */     String jvmRoute = node.getJvmRoute();
/* 362 */     UndertowLogger.ROOT_LOGGER.unregisteringContext(contextPath, jvmRoute);
/* 363 */     Context context = node.removeContext(contextPath, aliases);
/* 364 */     if (context == null) {
/* 365 */       return false;
/*     */     }
/* 367 */     context.stop();
/* 368 */     for (String alias : context.getVirtualHosts()) {
/* 369 */       VirtualHost virtualHost = this.hosts.get(alias);
/* 370 */       if (virtualHost != null) {
/* 371 */         virtualHost.removeContext(contextPath, jvmRoute, context);
/* 372 */         if (virtualHost.isEmpty()) {
/* 373 */           this.hosts.remove(alias);
/*     */         }
/*     */       } 
/*     */     } 
/* 377 */     return true;
/*     */   }
/*     */   
/*     */   private Context getOrRegisterContext(String contextPath, String jvmRoute, List<String> aliases, Node node) {
/* 381 */     Context context = node.getContext(contextPath, aliases);
/* 382 */     if (context == null) {
/* 383 */       context = node.registerContext(contextPath, aliases);
/* 384 */       UndertowLogger.ROOT_LOGGER.registeringContext(contextPath, jvmRoute);
/* 385 */       UndertowLogger.ROOT_LOGGER.registeringContext(contextPath, jvmRoute, aliases);
/* 386 */       for (String alias : aliases) {
/* 387 */         VirtualHost virtualHost = this.hosts.get(alias);
/* 388 */         if (virtualHost == null) {
/* 389 */           virtualHost = new VirtualHost();
/* 390 */           this.hosts.put(alias, virtualHost);
/*     */         } 
/* 392 */         virtualHost.registerContext(contextPath, jvmRoute, context);
/*     */       } 
/*     */     } 
/* 395 */     return context;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Context findNewNode(VirtualHost.HostEntry entry) {
/* 405 */     return electNode(entry.getContexts(), false, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Context findFailoverNode(VirtualHost.HostEntry entry, String domain, String session, String jvmRoute, boolean forceStickySession) {
/* 421 */     if (this.modCluster.isDeterministicFailover()) {
/* 422 */       List<String> candidates = new ArrayList<>(entry.getNodes().size());
/* 423 */       for (String route : entry.getNodes()) {
/* 424 */         Node node = this.nodes.get(route);
/* 425 */         if (node != null && !node.isInErrorState() && !node.isHotStandby()) {
/* 426 */           candidates.add(route);
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 431 */       if (candidates.isEmpty()) {
/* 432 */         for (String route : entry.getNodes()) {
/* 433 */           Node node = this.nodes.get(route);
/* 434 */           if (node != null && !node.isInErrorState() && node.isHotStandby()) {
/* 435 */             candidates.add(route);
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 440 */       if (candidates.isEmpty()) {
/* 441 */         return null;
/*     */       }
/*     */       
/* 444 */       String sessionId = session.substring(0, session.indexOf('.'));
/* 445 */       int index = (int)(Math.abs(sessionId.hashCode()) % candidates.size());
/* 446 */       Collections.sort(candidates);
/* 447 */       String electedRoute = candidates.get(index);
/* 448 */       UndertowLogger.ROOT_LOGGER.debugf("Using deterministic failover target: %s", electedRoute);
/* 449 */       return entry.getContextForNode(electedRoute);
/*     */     } 
/*     */     
/* 452 */     String failOverDomain = null;
/* 453 */     if (domain == null) {
/* 454 */       Node node = this.nodes.get(jvmRoute);
/* 455 */       if (node != null) {
/* 456 */         failOverDomain = node.getNodeConfig().getDomain();
/*     */       }
/* 458 */       if (failOverDomain == null) {
/* 459 */         failOverDomain = (String)this.failoverDomains.get(jvmRoute);
/*     */       }
/*     */     } else {
/* 462 */       failOverDomain = domain;
/*     */     } 
/* 464 */     Collection<Context> contexts = entry.getContexts();
/* 465 */     if (failOverDomain != null) {
/* 466 */       Context context = electNode(contexts, true, failOverDomain);
/* 467 */       if (context != null) {
/* 468 */         return context;
/*     */       }
/*     */     } 
/* 471 */     if (forceStickySession) {
/* 472 */       return null;
/*     */     }
/* 474 */     return electNode(contexts, false, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PathMatcher.PathMatch<VirtualHost.HostEntry> mapVirtualHost(HttpServerExchange exchange) {
/* 484 */     String context = exchange.getRelativePath();
/* 485 */     if (this.modCluster.isUseAlias()) {
/* 486 */       String hostName = exchange.getRequestHeaders().getFirst(Headers.HOST);
/* 487 */       if (hostName != null) {
/*     */         VirtualHost host;
/* 489 */         int i = hostName.indexOf(":");
/*     */         
/* 491 */         if (i > 0) {
/* 492 */           host = this.hosts.get(hostName.substring(0, i));
/* 493 */           if (host == null) {
/* 494 */             host = this.hosts.get(hostName);
/*     */           }
/*     */         } else {
/* 497 */           host = this.hosts.get(hostName);
/*     */         } 
/* 499 */         if (host == null) {
/* 500 */           return null;
/*     */         }
/* 502 */         PathMatcher.PathMatch<VirtualHost.HostEntry> result = host.match(context);
/* 503 */         if (result.getValue() == null) {
/* 504 */           return null;
/*     */         }
/* 506 */         return result;
/*     */       } 
/*     */     } else {
/* 509 */       for (Map.Entry<String, VirtualHost> host : this.hosts.entrySet()) {
/* 510 */         PathMatcher.PathMatch<VirtualHost.HostEntry> result = ((VirtualHost)host.getValue()).match(context);
/* 511 */         if (result.getValue() != null) {
/* 512 */           return result;
/*     */         }
/*     */       } 
/*     */     } 
/* 516 */     return null;
/*     */   }
/*     */   
/*     */   OptionMap getClientOptions() {
/* 520 */     return this.clientOptions;
/*     */   }
/*     */   
/*     */   private Iterator<CharSequence> parseRoutes(String sessionId) {
/* 524 */     return this.routeIteratorFactory.iterator(sessionId);
/*     */   }
/*     */   
/*     */   static Context electNode(Iterable<Context> contexts, boolean existingSession, String domain) {
/* 528 */     Context elected = null;
/* 529 */     Node candidate = null;
/* 530 */     boolean candidateHotStandby = false;
/* 531 */     for (Context context : contexts) {
/*     */       
/* 533 */       if (context.checkAvailable(existingSession)) {
/* 534 */         Node node = context.getNode();
/* 535 */         boolean hotStandby = node.isHotStandby();
/*     */         
/* 537 */         if (domain != null && !domain.equals(node.getNodeConfig().getDomain())) {
/*     */           continue;
/*     */         }
/* 540 */         if (candidate != null) {
/*     */           
/* 542 */           if (candidateHotStandby) {
/* 543 */             if (hotStandby) {
/* 544 */               if (candidate.getElectedDiff() > node.getElectedDiff()) {
/* 545 */                 candidate = node;
/* 546 */                 elected = context;
/*     */               }  continue;
/*     */             } 
/* 549 */             candidate = node;
/* 550 */             elected = context;
/* 551 */             candidateHotStandby = hotStandby; continue;
/*     */           } 
/* 553 */           if (hotStandby) {
/*     */             continue;
/*     */           }
/*     */           
/* 557 */           int lbStatus1 = candidate.getLoadStatus();
/* 558 */           int lbStatus2 = node.getLoadStatus();
/* 559 */           if (lbStatus1 > lbStatus2) {
/* 560 */             candidate = node;
/* 561 */             elected = context;
/* 562 */             candidateHotStandby = false;
/*     */           } 
/*     */           continue;
/*     */         } 
/* 566 */         candidate = node;
/* 567 */         elected = context;
/* 568 */         candidateHotStandby = hotStandby;
/*     */       } 
/*     */     } 
/*     */     
/* 572 */     if (candidate != null) {
/* 573 */       candidate.elected();
/*     */     }
/* 575 */     return elected;
/*     */   }
/*     */   
/*     */   void scheduleHealthCheck(Node node, XnioIoThread ioThread) {
/* 579 */     assert Thread.holdsLock(this);
/* 580 */     HealthCheckTask task = this.healthChecks.get(ioThread);
/* 581 */     if (task == null) {
/* 582 */       task = new HealthCheckTask(this.removeBrokenNodesThreshold, this.healthChecker);
/* 583 */       this.healthChecks.put(ioThread, task);
/* 584 */       task.cancelKey = ioThread.executeAtInterval(task, this.modCluster.getHealthCheckInterval(), TimeUnit.MILLISECONDS);
/*     */     } 
/* 586 */     task.nodes.add(node);
/*     */   }
/*     */   
/*     */   void removeHealthCheck(Node node, XnioIoThread ioThread) {
/* 590 */     assert Thread.holdsLock(this);
/* 591 */     HealthCheckTask task = this.healthChecks.get(ioThread);
/* 592 */     if (task == null) {
/*     */       return;
/*     */     }
/* 595 */     task.nodes.remove(node);
/* 596 */     if (task.nodes.size() == 0) {
/* 597 */       this.healthChecks.remove(ioThread);
/* 598 */       task.cancelKey.remove();
/*     */     } 
/*     */   }
/*     */   
/*     */   static long removeThreshold(long healthChecks, long removeBrokenNodes) {
/* 603 */     if (healthChecks > 0L && removeBrokenNodes > 0L) {
/* 604 */       long threshold = removeBrokenNodes / healthChecks;
/* 605 */       if (threshold > 1000L)
/* 606 */         return 1000L; 
/* 607 */       if (threshold < 1L) {
/* 608 */         return 1L;
/*     */       }
/* 610 */       return threshold;
/*     */     } 
/*     */     
/* 613 */     return -1L;
/*     */   }
/*     */   
/*     */   static class HealthCheckTask
/*     */     implements Runnable
/*     */   {
/*     */     private final long threshold;
/*     */     private final NodeHealthChecker healthChecker;
/* 621 */     private final ArrayList<Node> nodes = new ArrayList<>();
/*     */     private volatile XnioExecutor.Key cancelKey;
/*     */     
/*     */     HealthCheckTask(long threshold, NodeHealthChecker healthChecker) {
/* 625 */       this.threshold = threshold;
/* 626 */       this.healthChecker = healthChecker;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 631 */       for (Node node : this.nodes) {
/* 632 */         node.checkHealth(this.threshold, this.healthChecker);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class UpdateLoadTask
/*     */     implements Runnable
/*     */   {
/*     */     private volatile XnioExecutor.Key cancelKey;
/*     */     
/*     */     public void run() {
/* 643 */       for (Node node : ModClusterContainer.this.nodes.values()) {
/* 644 */         node.resetLbStatus();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ModClusterStatus getStatus() {
/* 652 */     List<ModClusterStatus.LoadBalancer> balancers = new ArrayList<>();
/* 653 */     for (Map.Entry<String, Balancer> bentry : this.balancers.entrySet()) {
/* 654 */       List<ModClusterStatus.Node> nodes = new ArrayList<>();
/* 655 */       for (Node node : getNodes()) {
/* 656 */         if (node.getBalancer().getName().equals(bentry.getKey())) {
/* 657 */           List<ModClusterStatus.Context> contexts = new ArrayList<>();
/*     */           
/* 659 */           for (Context i : node.getContexts()) {
/* 660 */             contexts.add(new ContextImpl(i));
/*     */           }
/*     */           
/* 663 */           nodes.add(new NodeImpl(node, contexts));
/*     */         } 
/*     */       } 
/*     */       
/* 667 */       balancers.add(new BalancerImpl(bentry.getValue(), nodes));
/*     */     } 
/* 669 */     return new ModClusterStatusImpl(balancers);
/*     */   }
/*     */   
/*     */   private static class ModClusterStatusImpl
/*     */     implements ModClusterStatus {
/*     */     private final List<ModClusterStatus.LoadBalancer> balancers;
/*     */     
/*     */     private ModClusterStatusImpl(List<ModClusterStatus.LoadBalancer> balancers) {
/* 677 */       this.balancers = balancers;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<ModClusterStatus.LoadBalancer> getLoadBalancers() {
/* 682 */       return this.balancers;
/*     */     }
/*     */ 
/*     */     
/*     */     public ModClusterStatus.LoadBalancer getLoadBalancer(String name) {
/* 687 */       for (ModClusterStatus.LoadBalancer b : this.balancers) {
/* 688 */         if (b.getName().equals(name)) {
/* 689 */           return b;
/*     */         }
/*     */       } 
/* 692 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BalancerImpl implements ModClusterStatus.LoadBalancer {
/*     */     private final Balancer balancer;
/*     */     private final List<ModClusterStatus.Node> nodes;
/*     */     
/*     */     private BalancerImpl(Balancer balancer, List<ModClusterStatus.Node> nodes) {
/* 701 */       this.balancer = balancer;
/* 702 */       this.nodes = nodes;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 707 */       return this.balancer.getName();
/*     */     }
/*     */ 
/*     */     
/*     */     public List<ModClusterStatus.Node> getNodes() {
/* 712 */       return this.nodes;
/*     */     }
/*     */ 
/*     */     
/*     */     public ModClusterStatus.Node getNode(String name) {
/* 717 */       for (ModClusterStatus.Node i : this.nodes) {
/* 718 */         if (i.getName().equals(name)) {
/* 719 */           return i;
/*     */         }
/*     */       } 
/* 722 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isStickySession() {
/* 727 */       return this.balancer.isStickySession();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getStickySessionCookie() {
/* 732 */       return this.balancer.getStickySessionCookie();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getStickySessionPath() {
/* 737 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isStickySessionRemove() {
/* 742 */       return this.balancer.isStickySessionRemove();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isStickySessionForce() {
/* 747 */       return this.balancer.isStickySessionForce();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getWaitWorker() {
/* 752 */       return this.balancer.getWaitWorker();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxRetries() {
/* 757 */       return this.balancer.getMaxRetries();
/*     */     }
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public int getMaxAttempts() {
/* 763 */       return this.balancer.getMaxRetries();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class NodeImpl
/*     */     implements ModClusterStatus.Node {
/*     */     private final Node node;
/*     */     private final List<ModClusterStatus.Context> contexts;
/*     */     
/*     */     private NodeImpl(Node node, List<ModClusterStatus.Context> contexts) {
/* 773 */       this.node = node;
/* 774 */       this.contexts = contexts;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 779 */       return this.node.getJvmRoute();
/*     */     }
/*     */ 
/*     */     
/*     */     public URI getUri() {
/* 784 */       return this.node.getConnectionPool().getUri();
/*     */     }
/*     */ 
/*     */     
/*     */     public List<ModClusterStatus.Context> getContexts() {
/* 789 */       return Collections.unmodifiableList(this.contexts);
/*     */     }
/*     */ 
/*     */     
/*     */     public ModClusterStatus.Context getContext(String name) {
/* 794 */       for (ModClusterStatus.Context i : this.contexts) {
/* 795 */         if (i.getName().equals(name)) {
/* 796 */           return i;
/*     */         }
/*     */       } 
/* 799 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getLoad() {
/* 804 */       return this.node.getLoad();
/*     */     }
/*     */ 
/*     */     
/*     */     public NodeStatus getStatus() {
/* 809 */       return this.node.getStatus();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getOpenConnections() {
/* 814 */       return this.node.getConnectionPool().getOpenConnections();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getTransferred() {
/* 819 */       return this.node.getConnectionPool().getClientStatistics().getWritten();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRead() {
/* 824 */       return this.node.getConnectionPool().getClientStatistics().getRead();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getElected() {
/* 829 */       return this.node.getElected();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getCacheConnections() {
/* 834 */       return this.node.getNodeConfig().getCacheConnections();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getJvmRoute() {
/* 839 */       return this.node.getNodeConfig().getJvmRoute();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getDomain() {
/* 844 */       return this.node.getNodeConfig().getDomain();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getFlushWait() {
/* 849 */       return this.node.getNodeConfig().getFlushwait();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMaxConnections() {
/* 854 */       return this.node.getNodeConfig().getMaxConnections();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getPing() {
/* 859 */       return this.node.getNodeConfig().getPing();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRequestQueueSize() {
/* 864 */       return this.node.getNodeConfig().getRequestQueueSize();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getTimeout() {
/* 869 */       return this.node.getNodeConfig().getTimeout();
/*     */     }
/*     */ 
/*     */     
/*     */     public long getTtl() {
/* 874 */       return this.node.getNodeConfig().getTtl();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFlushPackets() {
/* 879 */       return this.node.getNodeConfig().isFlushPackets();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isQueueNewRequests() {
/* 884 */       return this.node.getNodeConfig().isQueueNewRequests();
/*     */     }
/*     */ 
/*     */     
/*     */     public List<String> getAliases() {
/* 889 */       List<String> ret = new ArrayList<>();
/* 890 */       for (Node.VHostMapping host : this.node.getVHosts()) {
/* 891 */         ret.addAll(host.getAliases());
/*     */       }
/* 893 */       return ret;
/*     */     }
/*     */ 
/*     */     
/*     */     public void resetStatistics() {
/* 898 */       this.node.getConnectionPool().getClientStatistics().reset();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ContextImpl implements ModClusterStatus.Context {
/*     */     private final Context context;
/*     */     
/*     */     private ContextImpl(Context context) {
/* 906 */       this.context = context;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 911 */       return this.context.getPath();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEnabled() {
/* 916 */       return this.context.isEnabled();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isStopped() {
/* 921 */       return this.context.isStopped();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getRequests() {
/* 926 */       return this.context.getActiveRequests();
/*     */     }
/*     */ 
/*     */     
/*     */     public void enable() {
/* 931 */       this.context.enable();
/*     */     }
/*     */ 
/*     */     
/*     */     public void disable() {
/* 936 */       this.context.disable();
/*     */     }
/*     */ 
/*     */     
/*     */     public void stop() {
/* 941 */       this.context.stop();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\ModClusterContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */