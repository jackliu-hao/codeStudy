/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.Version;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.form.FormData;
/*     */ import io.undertow.server.handlers.form.FormDataParser;
/*     */ import io.undertow.server.handlers.form.FormEncodedDataDefinition;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
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
/*     */ class MCMPHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   enum MCMPAction
/*     */   {
/*  89 */     ENABLE,
/*  90 */     DISABLE,
/*  91 */     STOP,
/*  92 */     REMOVE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  97 */   public static final HttpString CONFIG = new HttpString("CONFIG");
/*  98 */   public static final HttpString ENABLE_APP = new HttpString("ENABLE-APP");
/*  99 */   public static final HttpString DISABLE_APP = new HttpString("DISABLE-APP");
/* 100 */   public static final HttpString STOP_APP = new HttpString("STOP-APP");
/* 101 */   public static final HttpString REMOVE_APP = new HttpString("REMOVE-APP");
/* 102 */   public static final HttpString STATUS = new HttpString("STATUS");
/* 103 */   public static final HttpString DUMP = new HttpString("DUMP");
/* 104 */   public static final HttpString INFO = new HttpString("INFO");
/* 105 */   public static final HttpString PING = new HttpString("PING");
/*     */   
/* 107 */   private static final Set<HttpString> HANDLED_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new HttpString[] { CONFIG, ENABLE_APP, DISABLE_APP, STOP_APP, REMOVE_APP, STATUS, INFO, DUMP, PING })));
/*     */   
/*     */   protected static final String VERSION_PROTOCOL = "0.2.1";
/* 110 */   protected static final String MOD_CLUSTER_EXPOSED_VERSION = "mod_cluster_undertow/" + Version.getVersionString();
/*     */   
/*     */   private static final String CONTENT_TYPE = "text/plain; charset=ISO-8859-1";
/*     */   
/*     */   private static final String TYPESYNTAX = "SYNTAX";
/*     */   
/*     */   private static final String SCONBAD = "SYNTAX: Context without Alias";
/*     */   
/*     */   private static final String SBADFLD = "SYNTAX: Invalid field ";
/*     */   private static final String SBADFLD1 = " in message";
/*     */   private static final String SMISFLD = "SYNTAX: Mandatory field(s) missing in message";
/*     */   private final FormParserFactory parserFactory;
/*     */   private final MCMPConfig config;
/*     */   private final HttpHandler next;
/* 124 */   private final long creationTime = System.currentTimeMillis();
/*     */   private final ModCluster modCluster;
/*     */   protected final ModClusterContainer container;
/*     */   
/*     */   MCMPHandler(MCMPConfig config, ModCluster modCluster, HttpHandler next) {
/* 129 */     this.config = config;
/* 130 */     this.next = next;
/* 131 */     this.modCluster = modCluster;
/* 132 */     this.container = modCluster.getContainer();
/* 133 */     this.parserFactory = FormParserFactory.builder(false).addParser((FormParserFactory.ParserDefinition)(new FormEncodedDataDefinition()).setForceCreation(true)).build();
/* 134 */     UndertowLogger.ROOT_LOGGER.mcmpHandlerCreated();
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 139 */     HttpString method = exchange.getRequestMethod();
/* 140 */     if (!handlesMethod(method)) {
/* 141 */       this.next.handleRequest(exchange);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 148 */     InetSocketAddress addr = (InetSocketAddress)exchange.getConnection().getLocalAddress(InetSocketAddress.class);
/* 149 */     if ((!addr.isUnresolved() && addr.getPort() != this.config.getManagementSocketAddress().getPort()) || !Arrays.equals(addr.getAddress().getAddress(), this.config.getManagementSocketAddress().getAddress().getAddress())) {
/* 150 */       this.next.handleRequest(exchange);
/*     */       
/*     */       return;
/*     */     } 
/* 154 */     if (exchange.isInIoThread()) {
/*     */       
/* 156 */       exchange.dispatch(this);
/*     */       
/*     */       return;
/*     */     } 
/*     */     try {
/* 161 */       handleRequest(method, exchange);
/* 162 */     } catch (Exception e) {
/* 163 */       UndertowLogger.ROOT_LOGGER.failedToProcessManagementReq(e);
/* 164 */       exchange.setStatusCode(500);
/* 165 */       exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
/* 166 */       Sender sender = exchange.getResponseSender();
/* 167 */       sender.send("failed to process management request");
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean handlesMethod(HttpString method) {
/* 172 */     return HANDLED_METHODS.contains(method);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleRequest(HttpString method, HttpServerExchange exchange) throws Exception {
/* 183 */     RequestData requestData = parseFormData(exchange);
/* 184 */     boolean persistent = exchange.isPersistent();
/* 185 */     exchange.setPersistent(false);
/* 186 */     if (CONFIG.equals(method)) {
/* 187 */       processConfig(exchange, requestData);
/* 188 */     } else if (ENABLE_APP.equals(method)) {
/* 189 */       processCommand(exchange, requestData, MCMPAction.ENABLE);
/* 190 */     } else if (DISABLE_APP.equals(method)) {
/* 191 */       processCommand(exchange, requestData, MCMPAction.DISABLE);
/* 192 */     } else if (STOP_APP.equals(method)) {
/* 193 */       processCommand(exchange, requestData, MCMPAction.STOP);
/* 194 */     } else if (REMOVE_APP.equals(method)) {
/* 195 */       processCommand(exchange, requestData, MCMPAction.REMOVE);
/* 196 */     } else if (STATUS.equals(method)) {
/* 197 */       processStatus(exchange, requestData);
/* 198 */     } else if (INFO.equals(method)) {
/* 199 */       processInfo(exchange);
/* 200 */     } else if (DUMP.equals(method)) {
/* 201 */       processDump(exchange);
/* 202 */     } else if (PING.equals(method)) {
/* 203 */       processPing(exchange, requestData);
/*     */     } else {
/* 205 */       exchange.setPersistent(persistent);
/* 206 */       this.next.handleRequest(exchange);
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
/*     */   
/*     */   private void processConfig(HttpServerExchange exchange, RequestData requestData) {
/* 219 */     List<String> hosts = null;
/* 220 */     List<String> contexts = null;
/* 221 */     Balancer.BalancerBuilder balancer = Balancer.builder();
/* 222 */     NodeConfig.NodeBuilder node = NodeConfig.builder(this.modCluster);
/* 223 */     Iterator<HttpString> i = requestData.iterator();
/* 224 */     while (i.hasNext()) {
/* 225 */       HttpString name = i.next();
/* 226 */       String value = requestData.getFirst(name);
/*     */       
/* 228 */       UndertowLogger.ROOT_LOGGER.mcmpKeyValue(name, value);
/* 229 */       if (!checkString(value)) {
/* 230 */         processError("SYNTAX", "SYNTAX: Invalid field " + name + " in message", exchange);
/*     */         
/*     */         return;
/*     */       } 
/* 234 */       if (MCMPConstants.BALANCER.equals(name)) {
/* 235 */         node.setBalancer(value);
/* 236 */         balancer.setName(value); continue;
/* 237 */       }  if (MCMPConstants.MAXATTEMPTS.equals(name)) {
/* 238 */         balancer.setMaxRetries(Integer.parseInt(value)); continue;
/* 239 */       }  if (MCMPConstants.STICKYSESSION.equals(name)) {
/* 240 */         if ("No".equalsIgnoreCase(value))
/* 241 */           balancer.setStickySession(false);  continue;
/*     */       } 
/* 243 */       if (MCMPConstants.STICKYSESSIONCOOKIE.equals(name)) {
/* 244 */         balancer.setStickySessionCookie(value); continue;
/* 245 */       }  if (MCMPConstants.STICKYSESSIONPATH.equals(name)) {
/* 246 */         balancer.setStickySessionPath(value); continue;
/* 247 */       }  if (MCMPConstants.STICKYSESSIONREMOVE.equals(name)) {
/* 248 */         if ("Yes".equalsIgnoreCase(value))
/* 249 */           balancer.setStickySessionRemove(true);  continue;
/*     */       } 
/* 251 */       if (MCMPConstants.STICKYSESSIONFORCE.equals(name)) {
/* 252 */         if ("no".equalsIgnoreCase(value))
/* 253 */           balancer.setStickySessionForce(false);  continue;
/*     */       } 
/* 255 */       if (MCMPConstants.JVMROUTE.equals(name)) {
/* 256 */         node.setJvmRoute(value); continue;
/* 257 */       }  if (MCMPConstants.DOMAIN.equals(name)) {
/* 258 */         node.setDomain(value); continue;
/* 259 */       }  if (MCMPConstants.HOST.equals(name)) {
/* 260 */         node.setHostname(value); continue;
/* 261 */       }  if (MCMPConstants.PORT.equals(name)) {
/* 262 */         node.setPort(Integer.parseInt(value)); continue;
/* 263 */       }  if (MCMPConstants.TYPE.equals(name)) {
/* 264 */         node.setType(value); continue;
/* 265 */       }  if (MCMPConstants.REVERSED.equals(name))
/*     */         continue; 
/* 267 */       if (MCMPConstants.FLUSH_PACKET.equals(name)) {
/* 268 */         if ("on".equalsIgnoreCase(value)) {
/* 269 */           node.setFlushPackets(true); continue;
/* 270 */         }  if ("auto".equalsIgnoreCase(value))
/* 271 */           node.setFlushPackets(true);  continue;
/*     */       } 
/* 273 */       if (MCMPConstants.FLUSH_WAIT.equals(name)) {
/* 274 */         node.setFlushwait(Integer.parseInt(value)); continue;
/* 275 */       }  if (MCMPConstants.PING.equals(name)) {
/* 276 */         node.setPing(Integer.parseInt(value)); continue;
/* 277 */       }  if (MCMPConstants.SMAX.equals(name)) {
/* 278 */         node.setSmax(Integer.parseInt(value)); continue;
/* 279 */       }  if (MCMPConstants.TTL.equals(name)) {
/* 280 */         node.setTtl(TimeUnit.SECONDS.toMillis(Long.parseLong(value))); continue;
/* 281 */       }  if (MCMPConstants.TIMEOUT.equals(name)) {
/* 282 */         node.setTimeout(Integer.parseInt(value)); continue;
/* 283 */       }  if (MCMPConstants.CONTEXT.equals(name)) {
/* 284 */         String[] context = value.split(",");
/* 285 */         contexts = Arrays.asList(context); continue;
/* 286 */       }  if (MCMPConstants.ALIAS.equals(name)) {
/* 287 */         String[] alias = value.split(",");
/* 288 */         hosts = Arrays.asList(alias); continue;
/* 289 */       }  if (MCMPConstants.WAITWORKER.equals(name)) {
/* 290 */         node.setWaitWorker(Integer.parseInt(value)); continue;
/*     */       } 
/* 292 */       processError("SYNTAX", "SYNTAX: Invalid field " + name + " in message", exchange);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 300 */       NodeConfig config = node.build();
/* 301 */       if (this.container.addNode(config, balancer, exchange.getIoThread(), exchange.getConnection().getByteBufferPool())) {
/*     */         
/* 303 */         if (contexts != null && hosts != null) {
/* 304 */           for (String context : contexts) {
/* 305 */             this.container.enableContext(context, config.getJvmRoute(), hosts);
/*     */           }
/*     */         }
/* 308 */         processOK(exchange);
/*     */       } else {
/* 310 */         processError(MCMPErrorCode.NODE_STILL_EXISTS, exchange);
/*     */       } 
/* 312 */     } catch (Exception e) {
/* 313 */       processError(MCMPErrorCode.CANT_UPDATE_NODE, exchange);
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
/*     */   
/*     */   void processCommand(HttpServerExchange exchange, RequestData requestData, MCMPAction action) throws IOException {
/* 326 */     if (exchange.getRequestPath().equals("*") || exchange.getRequestPath().endsWith("/*")) {
/* 327 */       processNodeCommand(exchange, requestData, action);
/*     */     } else {
/* 329 */       processAppCommand(exchange, requestData, action);
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
/*     */   
/*     */   void processNodeCommand(HttpServerExchange exchange, RequestData requestData, MCMPAction action) throws IOException {
/* 342 */     String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
/* 343 */     if (jvmRoute == null) {
/* 344 */       processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
/*     */       return;
/*     */     } 
/* 347 */     if (processNodeCommand(jvmRoute, action)) {
/* 348 */       processOK(exchange);
/*     */     } else {
/* 350 */       processError(MCMPErrorCode.CANT_UPDATE_NODE, exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   boolean processNodeCommand(String jvmRoute, MCMPAction action) throws IOException {
/* 355 */     switch (action) {
/*     */       case ENABLE:
/* 357 */         return this.container.enableNode(jvmRoute);
/*     */       case DISABLE:
/* 359 */         return this.container.disableNode(jvmRoute);
/*     */       case STOP:
/* 361 */         return this.container.stopNode(jvmRoute);
/*     */       case REMOVE:
/* 363 */         return (this.container.removeNode(jvmRoute) != null);
/*     */     } 
/* 365 */     return false;
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
/*     */   void processAppCommand(HttpServerExchange exchange, RequestData requestData, MCMPAction action) throws IOException {
/*     */     int i;
/*     */     StringBuilder builder;
/* 379 */     String contextPath = requestData.getFirst(MCMPConstants.CONTEXT);
/* 380 */     String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
/* 381 */     String aliases = requestData.getFirst(MCMPConstants.ALIAS);
/*     */     
/* 383 */     if (contextPath == null || jvmRoute == null || aliases == null) {
/* 384 */       processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
/*     */       return;
/*     */     } 
/* 387 */     List<String> virtualHosts = Arrays.asList(aliases.split(","));
/* 388 */     if (virtualHosts == null || virtualHosts.isEmpty()) {
/* 389 */       processError("SYNTAX", "SYNTAX: Context without Alias", exchange);
/*     */       
/*     */       return;
/*     */     } 
/* 393 */     String response = null;
/* 394 */     switch (action) {
/*     */       case ENABLE:
/* 396 */         if (!this.container.enableContext(contextPath, jvmRoute, virtualHosts)) {
/* 397 */           processError(MCMPErrorCode.CANT_UPDATE_CONTEXT, exchange);
/*     */           return;
/*     */         } 
/*     */         break;
/*     */       case DISABLE:
/* 402 */         if (!this.container.disableContext(contextPath, jvmRoute, virtualHosts)) {
/* 403 */           processError(MCMPErrorCode.CANT_UPDATE_CONTEXT, exchange);
/*     */           return;
/*     */         } 
/*     */         break;
/*     */       case STOP:
/* 408 */         i = this.container.stopContext(contextPath, jvmRoute, virtualHosts);
/* 409 */         builder = new StringBuilder();
/* 410 */         builder.append("Type=STOP-APP-RSP,JvmRoute=").append(jvmRoute);
/* 411 */         builder.append("Alias=").append(aliases);
/* 412 */         builder.append("Context=").append(contextPath);
/* 413 */         builder.append("Requests=").append(i);
/* 414 */         response = builder.toString();
/*     */         break;
/*     */       case REMOVE:
/* 417 */         if (!this.container.removeContext(contextPath, jvmRoute, virtualHosts)) {
/* 418 */           processError(MCMPErrorCode.CANT_UPDATE_CONTEXT, exchange);
/*     */           return;
/*     */         } 
/*     */         break;
/*     */       default:
/* 423 */         processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
/*     */         return;
/*     */     } 
/*     */     
/* 427 */     if (response != null) {
/* 428 */       sendResponse(exchange, response);
/*     */     } else {
/* 430 */       processOK(exchange);
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
/*     */   
/*     */   void processStatus(final HttpServerExchange exchange, RequestData requestData) throws IOException {
/* 443 */     final String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
/* 444 */     String loadValue = requestData.getFirst(MCMPConstants.LOAD);
/*     */     
/* 446 */     if (loadValue == null || jvmRoute == null) {
/* 447 */       processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
/*     */       
/*     */       return;
/*     */     } 
/* 451 */     UndertowLogger.ROOT_LOGGER.receivedNodeLoad(jvmRoute, loadValue);
/* 452 */     final int load = Integer.parseInt(loadValue);
/* 453 */     if (load > 0 || load == -2) {
/*     */       
/* 455 */       final Node node = this.container.getNode(jvmRoute);
/* 456 */       if (node == null) {
/* 457 */         processError(MCMPErrorCode.CANT_READ_NODE, exchange);
/*     */         
/*     */         return;
/*     */       } 
/* 461 */       NodePingUtil.PingCallback callback = new NodePingUtil.PingCallback()
/*     */         {
/*     */           public void completed() {
/* 464 */             String response = "Type=STATUS-RSP&State=OK&JVMRoute=" + jvmRoute + "&id=" + MCMPHandler.this.creationTime;
/*     */             try {
/* 466 */               if (load > 0) {
/* 467 */                 node.updateLoad(load);
/*     */               }
/* 469 */               MCMPHandler.sendResponse(exchange, response);
/* 470 */             } catch (Exception e) {
/* 471 */               UndertowLogger.ROOT_LOGGER.failedToSendPingResponse(e);
/*     */             } 
/*     */           }
/*     */ 
/*     */           
/*     */           public void failed() {
/* 477 */             String response = "Type=STATUS-RSP&State=NOTOK&JVMRoute=" + jvmRoute + "&id=" + MCMPHandler.this.creationTime;
/*     */             try {
/* 479 */               node.markInError();
/* 480 */               MCMPHandler.sendResponse(exchange, response);
/* 481 */             } catch (Exception e) {
/* 482 */               UndertowLogger.ROOT_LOGGER.failedToSendPingResponseDBG(e, node.getJvmRoute(), jvmRoute);
/*     */             } 
/*     */           }
/*     */         };
/*     */ 
/*     */       
/* 488 */       node.ping(exchange, callback);
/*     */     }
/* 490 */     else if (load == 0) {
/* 491 */       final Node node = this.container.getNode(jvmRoute);
/* 492 */       if (node != null) {
/* 493 */         node.hotStandby();
/* 494 */         sendResponse(exchange, "Type=STATUS-RSP&State=OK&JVMRoute=" + jvmRoute + "&id=" + this.creationTime);
/*     */       } else {
/* 496 */         processError(MCMPErrorCode.CANT_READ_NODE, exchange);
/*     */       } 
/* 498 */     } else if (load == -1) {
/*     */       
/* 500 */       final Node node = this.container.getNode(jvmRoute);
/* 501 */       if (node != null) {
/* 502 */         node.markInError();
/* 503 */         sendResponse(exchange, "Type=STATUS-RSP&State=NOTOK&JVMRoute=" + jvmRoute + "&id=" + this.creationTime);
/*     */       } else {
/* 505 */         processError(MCMPErrorCode.CANT_READ_NODE, exchange);
/*     */       } 
/*     */     } else {
/* 508 */       processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
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
/*     */   
/*     */   void processPing(final HttpServerExchange exchange, RequestData requestData) throws IOException {
/* 521 */     String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
/* 522 */     String scheme = requestData.getFirst(MCMPConstants.SCHEME);
/* 523 */     String host = requestData.getFirst(MCMPConstants.HOST);
/* 524 */     String port = requestData.getFirst(MCMPConstants.PORT);
/*     */     
/* 526 */     final String OK = "Type=PING-RSP&State=OK&id=" + this.creationTime;
/* 527 */     final String NOTOK = "Type=PING-RSP&State=NOTOK&id=" + this.creationTime;
/*     */     
/* 529 */     if (jvmRoute != null) {
/*     */       
/* 531 */       final Node nodeConfig = this.container.getNode(jvmRoute);
/* 532 */       if (nodeConfig == null) {
/* 533 */         sendResponse(exchange, NOTOK);
/*     */         return;
/*     */       } 
/* 536 */       NodePingUtil.PingCallback callback = new NodePingUtil.PingCallback()
/*     */         {
/*     */           public void completed() {
/*     */             try {
/* 540 */               MCMPHandler.sendResponse(exchange, OK);
/* 541 */             } catch (Exception e) {
/* 542 */               e.printStackTrace();
/*     */             } 
/*     */           }
/*     */ 
/*     */           
/*     */           public void failed() {
/*     */             try {
/* 549 */               nodeConfig.markInError();
/* 550 */               MCMPHandler.sendResponse(exchange, NOTOK);
/* 551 */             } catch (Exception e) {
/* 552 */               e.printStackTrace();
/*     */             } 
/*     */           }
/*     */         };
/* 556 */       nodeConfig.ping(exchange, callback);
/*     */     } else {
/* 558 */       if (scheme == null && host == null && port == null) {
/* 559 */         sendResponse(exchange, OK);
/*     */         return;
/*     */       } 
/* 562 */       if (host == null || port == null) {
/* 563 */         processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
/*     */         
/*     */         return;
/*     */       } 
/* 567 */       checkHostUp(scheme, host, Integer.parseInt(port), exchange, new NodePingUtil.PingCallback()
/*     */           {
/*     */             public void completed() {
/* 570 */               MCMPHandler.sendResponse(exchange, OK);
/*     */             }
/*     */ 
/*     */             
/*     */             public void failed() {
/* 575 */               MCMPHandler.sendResponse(exchange, NOTOK);
/*     */             }
/*     */           });
/*     */       return;
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
/*     */   protected void processInfo(HttpServerExchange exchange) throws IOException {
/* 600 */     String data = processInfoString();
/* 601 */     exchange.getResponseHeaders().add(Headers.SERVER, MOD_CLUSTER_EXPOSED_VERSION);
/* 602 */     sendResponse(exchange, data);
/*     */   }
/*     */   
/*     */   protected String processInfoString() {
/* 606 */     StringBuilder builder = new StringBuilder();
/* 607 */     List<Node.VHostMapping> vHosts = new ArrayList<>();
/* 608 */     List<Context> contexts = new ArrayList<>();
/* 609 */     Collection<Node> nodes = this.container.getNodes();
/* 610 */     for (Node node : nodes) {
/* 611 */       MCMPInfoUtil.printInfo(node, builder);
/* 612 */       vHosts.addAll(node.getVHosts());
/* 613 */       contexts.addAll(node.getContexts());
/*     */     } 
/* 615 */     for (Node.VHostMapping vHost : vHosts) {
/* 616 */       MCMPInfoUtil.printInfo(vHost, builder);
/*     */     }
/* 618 */     for (Context context : contexts) {
/* 619 */       MCMPInfoUtil.printInfo(context, builder);
/*     */     }
/* 621 */     return builder.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void processDump(HttpServerExchange exchange) throws IOException {
/* 643 */     String data = processDumpString();
/* 644 */     exchange.getResponseHeaders().add(Headers.SERVER, MOD_CLUSTER_EXPOSED_VERSION);
/* 645 */     sendResponse(exchange, data);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String processDumpString() {
/* 650 */     StringBuilder builder = new StringBuilder();
/* 651 */     Collection<Balancer> balancers = this.container.getBalancers();
/* 652 */     for (Balancer balancer : balancers) {
/* 653 */       MCMPInfoUtil.printDump(balancer, builder);
/*     */     }
/*     */     
/* 656 */     List<Node.VHostMapping> vHosts = new ArrayList<>();
/* 657 */     List<Context> contexts = new ArrayList<>();
/* 658 */     Collection<Node> nodes = this.container.getNodes();
/* 659 */     for (Node node : nodes) {
/* 660 */       MCMPInfoUtil.printDump(node, builder);
/* 661 */       vHosts.addAll(node.getVHosts());
/* 662 */       contexts.addAll(node.getContexts());
/*     */     } 
/* 664 */     for (Node.VHostMapping vHost : vHosts) {
/* 665 */       MCMPInfoUtil.printDump(vHost, builder);
/*     */     }
/* 667 */     for (Context context : contexts) {
/* 668 */       MCMPInfoUtil.printDump(context, builder);
/*     */     }
/* 670 */     return builder.toString();
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
/*     */   protected void checkHostUp(String scheme, String host, int port, HttpServerExchange exchange, NodePingUtil.PingCallback callback) {
/* 684 */     XnioSsl xnioSsl = null;
/*     */ 
/*     */     
/* 687 */     OptionMap options = OptionMap.builder().set(Options.TCP_NODELAY, true).getMap();
/*     */ 
/*     */     
/*     */     try {
/* 691 */       if ("ajp".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)) {
/* 692 */         URI uri = new URI(scheme, null, host, port, "/", null, null);
/* 693 */         NodePingUtil.pingHttpClient(uri, callback, exchange, this.container.getClient(), xnioSsl, options);
/*     */       } else {
/* 695 */         InetSocketAddress address = new InetSocketAddress(host, port);
/* 696 */         NodePingUtil.pingHost(address, exchange, callback, options);
/*     */       } 
/* 698 */     } catch (URISyntaxException e) {
/* 699 */       callback.failed();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void sendResponse(HttpServerExchange exchange, String response) {
/* 710 */     exchange.setStatusCode(200);
/* 711 */     exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
/* 712 */     Sender sender = exchange.getResponseSender();
/* 713 */     UndertowLogger.ROOT_LOGGER.mcmpSendingResponse(exchange.getSourceAddress(), exchange.getStatusCode(), exchange.getResponseHeaders(), response);
/* 714 */     sender.send(response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void processOK(HttpServerExchange exchange) throws IOException {
/* 723 */     exchange.setStatusCode(200);
/* 724 */     exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
/* 725 */     exchange.endExchange();
/*     */   }
/*     */   
/*     */   static void processError(MCMPErrorCode errorCode, HttpServerExchange exchange) {
/* 729 */     processError(errorCode.getType(), errorCode.getMessage(), exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void processError(String type, String errString, HttpServerExchange exchange) {
/* 740 */     exchange.setStatusCode(500);
/* 741 */     exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
/* 742 */     exchange.getResponseHeaders().add(new HttpString("Version"), "0.2.1");
/* 743 */     exchange.getResponseHeaders().add(new HttpString("Type"), type);
/* 744 */     exchange.getResponseHeaders().add(new HttpString("Mess"), errString);
/* 745 */     exchange.endExchange();
/* 746 */     UndertowLogger.ROOT_LOGGER.mcmpProcessingError(type, errString);
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
/*     */   RequestData parseFormData(HttpServerExchange exchange) throws IOException {
/* 759 */     FormDataParser parser = this.parserFactory.createParser(exchange);
/* 760 */     FormData formData = parser.parseBlocking();
/* 761 */     RequestData data = new RequestData();
/* 762 */     for (String name : formData) {
/* 763 */       HttpString key = new HttpString(name);
/* 764 */       data.add(key, formData.get(name));
/*     */     } 
/* 766 */     return data;
/*     */   }
/*     */   
/*     */   private static void checkStringForSuspiciousCharacters(String data) {
/* 770 */     for (int i = 0; i < data.length(); i++) {
/* 771 */       char c = data.charAt(i);
/* 772 */       if (c == '>' || c == '<' || c == '\\' || c == '"' || c == '\n' || c == '\r') {
/* 773 */         throw UndertowMessages.MESSAGES.mcmpMessageRejectedDueToSuspiciousCharacters(data);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   static class RequestData
/*     */   {
/* 780 */     private final Map<HttpString, Deque<String>> values = new LinkedHashMap<>();
/*     */     
/*     */     Iterator<HttpString> iterator() {
/* 783 */       return this.values.keySet().iterator();
/*     */     }
/*     */     
/*     */     void add(HttpString name, Deque<FormData.FormValue> values) {
/* 787 */       MCMPHandler.checkStringForSuspiciousCharacters(name.toString());
/* 788 */       for (FormData.FormValue value : values) {
/* 789 */         add(name, value);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void addValues(HttpString name, Deque<String> value) {
/* 796 */       Deque<String> values = this.values.get(name);
/* 797 */       for (String i : value) {
/* 798 */         MCMPHandler.checkStringForSuspiciousCharacters(i);
/*     */       }
/* 800 */       if (values == null) {
/* 801 */         this.values.put(name, value);
/*     */       } else {
/* 803 */         values.addAll(value);
/*     */       } 
/*     */     }
/*     */     
/*     */     void add(HttpString name, FormData.FormValue value) {
/* 808 */       Deque<String> values = this.values.get(name);
/* 809 */       if (values == null) {
/* 810 */         this.values.put(name, values = new ArrayDeque<>(1));
/*     */       }
/* 812 */       String stringVal = value.getValue();
/* 813 */       MCMPHandler.checkStringForSuspiciousCharacters(stringVal);
/* 814 */       values.add(stringVal);
/*     */     }
/*     */     
/*     */     String getFirst(HttpString name) {
/* 818 */       Deque<String> deque = this.values.get(name);
/* 819 */       return (deque == null) ? null : deque.peekFirst();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean checkString(String value) {
/* 825 */     return (value != null && value.length() > 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\MCMPHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */