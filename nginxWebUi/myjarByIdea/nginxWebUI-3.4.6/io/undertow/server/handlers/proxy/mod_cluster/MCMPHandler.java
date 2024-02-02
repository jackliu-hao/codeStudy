package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.Version;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.ssl.XnioSsl;

class MCMPHandler implements HttpHandler {
   public static final HttpString CONFIG = new HttpString("CONFIG");
   public static final HttpString ENABLE_APP = new HttpString("ENABLE-APP");
   public static final HttpString DISABLE_APP = new HttpString("DISABLE-APP");
   public static final HttpString STOP_APP = new HttpString("STOP-APP");
   public static final HttpString REMOVE_APP = new HttpString("REMOVE-APP");
   public static final HttpString STATUS = new HttpString("STATUS");
   public static final HttpString DUMP = new HttpString("DUMP");
   public static final HttpString INFO = new HttpString("INFO");
   public static final HttpString PING = new HttpString("PING");
   private static final Set<HttpString> HANDLED_METHODS;
   protected static final String VERSION_PROTOCOL = "0.2.1";
   protected static final String MOD_CLUSTER_EXPOSED_VERSION;
   private static final String CONTENT_TYPE = "text/plain; charset=ISO-8859-1";
   private static final String TYPESYNTAX = "SYNTAX";
   private static final String SCONBAD = "SYNTAX: Context without Alias";
   private static final String SBADFLD = "SYNTAX: Invalid field ";
   private static final String SBADFLD1 = " in message";
   private static final String SMISFLD = "SYNTAX: Mandatory field(s) missing in message";
   private final FormParserFactory parserFactory;
   private final MCMPConfig config;
   private final HttpHandler next;
   private final long creationTime = System.currentTimeMillis();
   private final ModCluster modCluster;
   protected final ModClusterContainer container;

   MCMPHandler(MCMPConfig config, ModCluster modCluster, HttpHandler next) {
      this.config = config;
      this.next = next;
      this.modCluster = modCluster;
      this.container = modCluster.getContainer();
      this.parserFactory = FormParserFactory.builder(false).addParser((new FormEncodedDataDefinition()).setForceCreation(true)).build();
      UndertowLogger.ROOT_LOGGER.mcmpHandlerCreated();
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      HttpString method = exchange.getRequestMethod();
      if (!this.handlesMethod(method)) {
         this.next.handleRequest(exchange);
      } else {
         InetSocketAddress addr = (InetSocketAddress)exchange.getConnection().getLocalAddress(InetSocketAddress.class);
         if ((addr.isUnresolved() || addr.getPort() == this.config.getManagementSocketAddress().getPort()) && Arrays.equals(addr.getAddress().getAddress(), this.config.getManagementSocketAddress().getAddress().getAddress())) {
            if (exchange.isInIoThread()) {
               exchange.dispatch((HttpHandler)this);
            } else {
               try {
                  this.handleRequest(method, exchange);
               } catch (Exception var6) {
                  UndertowLogger.ROOT_LOGGER.failedToProcessManagementReq(var6);
                  exchange.setStatusCode(500);
                  exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
                  Sender sender = exchange.getResponseSender();
                  sender.send("failed to process management request");
               }

            }
         } else {
            this.next.handleRequest(exchange);
         }
      }
   }

   protected boolean handlesMethod(HttpString method) {
      return HANDLED_METHODS.contains(method);
   }

   protected void handleRequest(HttpString method, HttpServerExchange exchange) throws Exception {
      RequestData requestData = this.parseFormData(exchange);
      boolean persistent = exchange.isPersistent();
      exchange.setPersistent(false);
      if (CONFIG.equals(method)) {
         this.processConfig(exchange, requestData);
      } else if (ENABLE_APP.equals(method)) {
         this.processCommand(exchange, requestData, MCMPHandler.MCMPAction.ENABLE);
      } else if (DISABLE_APP.equals(method)) {
         this.processCommand(exchange, requestData, MCMPHandler.MCMPAction.DISABLE);
      } else if (STOP_APP.equals(method)) {
         this.processCommand(exchange, requestData, MCMPHandler.MCMPAction.STOP);
      } else if (REMOVE_APP.equals(method)) {
         this.processCommand(exchange, requestData, MCMPHandler.MCMPAction.REMOVE);
      } else if (STATUS.equals(method)) {
         this.processStatus(exchange, requestData);
      } else if (INFO.equals(method)) {
         this.processInfo(exchange);
      } else if (DUMP.equals(method)) {
         this.processDump(exchange);
      } else if (PING.equals(method)) {
         this.processPing(exchange, requestData);
      } else {
         exchange.setPersistent(persistent);
         this.next.handleRequest(exchange);
      }

   }

   private void processConfig(HttpServerExchange exchange, RequestData requestData) {
      List<String> hosts = null;
      List<String> contexts = null;
      Balancer.BalancerBuilder balancer = Balancer.builder();
      NodeConfig.NodeBuilder node = NodeConfig.builder(this.modCluster);
      Iterator<HttpString> i = requestData.iterator();

      while(i.hasNext()) {
         HttpString name = (HttpString)i.next();
         String value = requestData.getFirst(name);
         UndertowLogger.ROOT_LOGGER.mcmpKeyValue(name, value);
         if (!checkString(value)) {
            processError("SYNTAX", "SYNTAX: Invalid field " + name + " in message", exchange);
            return;
         }

         if (MCMPConstants.BALANCER.equals(name)) {
            node.setBalancer(value);
            balancer.setName(value);
         } else if (MCMPConstants.MAXATTEMPTS.equals(name)) {
            balancer.setMaxRetries(Integer.parseInt(value));
         } else if (MCMPConstants.STICKYSESSION.equals(name)) {
            if ("No".equalsIgnoreCase(value)) {
               balancer.setStickySession(false);
            }
         } else if (MCMPConstants.STICKYSESSIONCOOKIE.equals(name)) {
            balancer.setStickySessionCookie(value);
         } else if (MCMPConstants.STICKYSESSIONPATH.equals(name)) {
            balancer.setStickySessionPath(value);
         } else if (MCMPConstants.STICKYSESSIONREMOVE.equals(name)) {
            if ("Yes".equalsIgnoreCase(value)) {
               balancer.setStickySessionRemove(true);
            }
         } else if (MCMPConstants.STICKYSESSIONFORCE.equals(name)) {
            if ("no".equalsIgnoreCase(value)) {
               balancer.setStickySessionForce(false);
            }
         } else if (MCMPConstants.JVMROUTE.equals(name)) {
            node.setJvmRoute(value);
         } else if (MCMPConstants.DOMAIN.equals(name)) {
            node.setDomain(value);
         } else if (MCMPConstants.HOST.equals(name)) {
            node.setHostname(value);
         } else if (MCMPConstants.PORT.equals(name)) {
            node.setPort(Integer.parseInt(value));
         } else if (MCMPConstants.TYPE.equals(name)) {
            node.setType(value);
         } else if (!MCMPConstants.REVERSED.equals(name)) {
            if (MCMPConstants.FLUSH_PACKET.equals(name)) {
               if ("on".equalsIgnoreCase(value)) {
                  node.setFlushPackets(true);
               } else if ("auto".equalsIgnoreCase(value)) {
                  node.setFlushPackets(true);
               }
            } else if (MCMPConstants.FLUSH_WAIT.equals(name)) {
               node.setFlushwait(Integer.parseInt(value));
            } else if (MCMPConstants.PING.equals(name)) {
               node.setPing(Integer.parseInt(value));
            } else if (MCMPConstants.SMAX.equals(name)) {
               node.setSmax(Integer.parseInt(value));
            } else if (MCMPConstants.TTL.equals(name)) {
               node.setTtl(TimeUnit.SECONDS.toMillis(Long.parseLong(value)));
            } else if (MCMPConstants.TIMEOUT.equals(name)) {
               node.setTimeout(Integer.parseInt(value));
            } else {
               String[] alias;
               if (MCMPConstants.CONTEXT.equals(name)) {
                  alias = value.split(",");
                  contexts = Arrays.asList(alias);
               } else if (MCMPConstants.ALIAS.equals(name)) {
                  alias = value.split(",");
                  hosts = Arrays.asList(alias);
               } else {
                  if (!MCMPConstants.WAITWORKER.equals(name)) {
                     processError("SYNTAX", "SYNTAX: Invalid field " + name + " in message", exchange);
                     return;
                  }

                  node.setWaitWorker(Integer.parseInt(value));
               }
            }
         }
      }

      try {
         NodeConfig config = node.build();
         if (this.container.addNode(config, balancer, exchange.getIoThread(), exchange.getConnection().getByteBufferPool())) {
            if (contexts != null && hosts != null) {
               Iterator var13 = contexts.iterator();

               while(var13.hasNext()) {
                  String context = (String)var13.next();
                  this.container.enableContext(context, config.getJvmRoute(), hosts);
               }
            }

            processOK(exchange);
         } else {
            processError(MCMPErrorCode.NODE_STILL_EXISTS, exchange);
         }
      } catch (Exception var11) {
         processError(MCMPErrorCode.CANT_UPDATE_NODE, exchange);
      }

   }

   void processCommand(HttpServerExchange exchange, RequestData requestData, MCMPAction action) throws IOException {
      if (!exchange.getRequestPath().equals("*") && !exchange.getRequestPath().endsWith("/*")) {
         this.processAppCommand(exchange, requestData, action);
      } else {
         this.processNodeCommand(exchange, requestData, action);
      }

   }

   void processNodeCommand(HttpServerExchange exchange, RequestData requestData, MCMPAction action) throws IOException {
      String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
      if (jvmRoute == null) {
         processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
      } else {
         if (this.processNodeCommand(jvmRoute, action)) {
            processOK(exchange);
         } else {
            processError(MCMPErrorCode.CANT_UPDATE_NODE, exchange);
         }

      }
   }

   boolean processNodeCommand(String jvmRoute, MCMPAction action) throws IOException {
      switch (action) {
         case ENABLE:
            return this.container.enableNode(jvmRoute);
         case DISABLE:
            return this.container.disableNode(jvmRoute);
         case STOP:
            return this.container.stopNode(jvmRoute);
         case REMOVE:
            return this.container.removeNode(jvmRoute) != null;
         default:
            return false;
      }
   }

   void processAppCommand(HttpServerExchange exchange, RequestData requestData, MCMPAction action) throws IOException {
      String contextPath = requestData.getFirst(MCMPConstants.CONTEXT);
      String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
      String aliases = requestData.getFirst(MCMPConstants.ALIAS);
      if (contextPath != null && jvmRoute != null && aliases != null) {
         List<String> virtualHosts = Arrays.asList(aliases.split(","));
         if (virtualHosts != null && !virtualHosts.isEmpty()) {
            String response = null;
            switch (action) {
               case ENABLE:
                  if (!this.container.enableContext(contextPath, jvmRoute, virtualHosts)) {
                     processError(MCMPErrorCode.CANT_UPDATE_CONTEXT, exchange);
                     return;
                  }
                  break;
               case DISABLE:
                  if (!this.container.disableContext(contextPath, jvmRoute, virtualHosts)) {
                     processError(MCMPErrorCode.CANT_UPDATE_CONTEXT, exchange);
                     return;
                  }
                  break;
               case STOP:
                  int i = this.container.stopContext(contextPath, jvmRoute, virtualHosts);
                  StringBuilder builder = new StringBuilder();
                  builder.append("Type=STOP-APP-RSP,JvmRoute=").append(jvmRoute);
                  builder.append("Alias=").append(aliases);
                  builder.append("Context=").append(contextPath);
                  builder.append("Requests=").append(i);
                  response = builder.toString();
                  break;
               case REMOVE:
                  if (!this.container.removeContext(contextPath, jvmRoute, virtualHosts)) {
                     processError(MCMPErrorCode.CANT_UPDATE_CONTEXT, exchange);
                     return;
                  }
                  break;
               default:
                  processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
                  return;
            }

            if (response != null) {
               sendResponse(exchange, response);
            } else {
               processOK(exchange);
            }

         } else {
            processError("SYNTAX", "SYNTAX: Context without Alias", exchange);
         }
      } else {
         processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
      }
   }

   void processStatus(final HttpServerExchange exchange, RequestData requestData) throws IOException {
      final String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
      String loadValue = requestData.getFirst(MCMPConstants.LOAD);
      if (loadValue != null && jvmRoute != null) {
         UndertowLogger.ROOT_LOGGER.receivedNodeLoad(jvmRoute, loadValue);
         final int load = Integer.parseInt(loadValue);
         final Node node;
         if (load <= 0 && load != -2) {
            if (load == 0) {
               node = this.container.getNode(jvmRoute);
               if (node != null) {
                  node.hotStandby();
                  sendResponse(exchange, "Type=STATUS-RSP&State=OK&JVMRoute=" + jvmRoute + "&id=" + this.creationTime);
               } else {
                  processError(MCMPErrorCode.CANT_READ_NODE, exchange);
               }
            } else if (load == -1) {
               node = this.container.getNode(jvmRoute);
               if (node != null) {
                  node.markInError();
                  sendResponse(exchange, "Type=STATUS-RSP&State=NOTOK&JVMRoute=" + jvmRoute + "&id=" + this.creationTime);
               } else {
                  processError(MCMPErrorCode.CANT_READ_NODE, exchange);
               }
            } else {
               processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
            }
         } else {
            node = this.container.getNode(jvmRoute);
            if (node == null) {
               processError(MCMPErrorCode.CANT_READ_NODE, exchange);
               return;
            }

            NodePingUtil.PingCallback callback = new NodePingUtil.PingCallback() {
               public void completed() {
                  String response = "Type=STATUS-RSP&State=OK&JVMRoute=" + jvmRoute + "&id=" + MCMPHandler.this.creationTime;

                  try {
                     if (load > 0) {
                        node.updateLoad(load);
                     }

                     MCMPHandler.sendResponse(exchange, response);
                  } catch (Exception var3) {
                     UndertowLogger.ROOT_LOGGER.failedToSendPingResponse(var3);
                  }

               }

               public void failed() {
                  String response = "Type=STATUS-RSP&State=NOTOK&JVMRoute=" + jvmRoute + "&id=" + MCMPHandler.this.creationTime;

                  try {
                     node.markInError();
                     MCMPHandler.sendResponse(exchange, response);
                  } catch (Exception var3) {
                     UndertowLogger.ROOT_LOGGER.failedToSendPingResponseDBG(var3, node.getJvmRoute(), jvmRoute);
                  }

               }
            };
            node.ping(exchange, callback);
         }

      } else {
         processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
      }
   }

   void processPing(final HttpServerExchange exchange, RequestData requestData) throws IOException {
      String jvmRoute = requestData.getFirst(MCMPConstants.JVMROUTE);
      String scheme = requestData.getFirst(MCMPConstants.SCHEME);
      String host = requestData.getFirst(MCMPConstants.HOST);
      String port = requestData.getFirst(MCMPConstants.PORT);
      final String OK = "Type=PING-RSP&State=OK&id=" + this.creationTime;
      final String NOTOK = "Type=PING-RSP&State=NOTOK&id=" + this.creationTime;
      if (jvmRoute != null) {
         final Node nodeConfig = this.container.getNode(jvmRoute);
         if (nodeConfig == null) {
            sendResponse(exchange, NOTOK);
         } else {
            NodePingUtil.PingCallback callback = new NodePingUtil.PingCallback() {
               public void completed() {
                  try {
                     MCMPHandler.sendResponse(exchange, OK);
                  } catch (Exception var2) {
                     var2.printStackTrace();
                  }

               }

               public void failed() {
                  try {
                     nodeConfig.markInError();
                     MCMPHandler.sendResponse(exchange, NOTOK);
                  } catch (Exception var2) {
                     var2.printStackTrace();
                  }

               }
            };
            nodeConfig.ping(exchange, callback);
         }
      } else if (scheme == null && host == null && port == null) {
         sendResponse(exchange, OK);
      } else if (host != null && port != null) {
         this.checkHostUp(scheme, host, Integer.parseInt(port), exchange, new NodePingUtil.PingCallback() {
            public void completed() {
               MCMPHandler.sendResponse(exchange, OK);
            }

            public void failed() {
               MCMPHandler.sendResponse(exchange, NOTOK);
            }
         });
      } else {
         processError("SYNTAX", "SYNTAX: Mandatory field(s) missing in message", exchange);
      }
   }

   protected void processInfo(HttpServerExchange exchange) throws IOException {
      String data = this.processInfoString();
      exchange.getResponseHeaders().add(Headers.SERVER, MOD_CLUSTER_EXPOSED_VERSION);
      sendResponse(exchange, data);
   }

   protected String processInfoString() {
      StringBuilder builder = new StringBuilder();
      List<Node.VHostMapping> vHosts = new ArrayList();
      List<Context> contexts = new ArrayList();
      Collection<Node> nodes = this.container.getNodes();
      Iterator var5 = nodes.iterator();

      while(var5.hasNext()) {
         Node node = (Node)var5.next();
         MCMPInfoUtil.printInfo(node, builder);
         vHosts.addAll(node.getVHosts());
         contexts.addAll(node.getContexts());
      }

      var5 = vHosts.iterator();

      while(var5.hasNext()) {
         Node.VHostMapping vHost = (Node.VHostMapping)var5.next();
         MCMPInfoUtil.printInfo(vHost, builder);
      }

      var5 = contexts.iterator();

      while(var5.hasNext()) {
         Context context = (Context)var5.next();
         MCMPInfoUtil.printInfo(context, builder);
      }

      return builder.toString();
   }

   protected void processDump(HttpServerExchange exchange) throws IOException {
      String data = this.processDumpString();
      exchange.getResponseHeaders().add(Headers.SERVER, MOD_CLUSTER_EXPOSED_VERSION);
      sendResponse(exchange, data);
   }

   protected String processDumpString() {
      StringBuilder builder = new StringBuilder();
      Collection<Balancer> balancers = this.container.getBalancers();
      Iterator var3 = balancers.iterator();

      while(var3.hasNext()) {
         Balancer balancer = (Balancer)var3.next();
         MCMPInfoUtil.printDump(balancer, builder);
      }

      List<Node.VHostMapping> vHosts = new ArrayList();
      List<Context> contexts = new ArrayList();
      Collection<Node> nodes = this.container.getNodes();
      Iterator var6 = nodes.iterator();

      while(var6.hasNext()) {
         Node node = (Node)var6.next();
         MCMPInfoUtil.printDump(node, builder);
         vHosts.addAll(node.getVHosts());
         contexts.addAll(node.getContexts());
      }

      var6 = vHosts.iterator();

      while(var6.hasNext()) {
         Node.VHostMapping vHost = (Node.VHostMapping)var6.next();
         MCMPInfoUtil.printDump(vHost, builder);
      }

      var6 = contexts.iterator();

      while(var6.hasNext()) {
         Context context = (Context)var6.next();
         MCMPInfoUtil.printDump(context, builder);
      }

      return builder.toString();
   }

   protected void checkHostUp(String scheme, String host, int port, HttpServerExchange exchange, NodePingUtil.PingCallback callback) {
      XnioSsl xnioSsl = null;
      OptionMap options = OptionMap.builder().set(Options.TCP_NODELAY, true).getMap();

      try {
         if (!"ajp".equalsIgnoreCase(scheme) && !"http".equalsIgnoreCase(scheme)) {
            InetSocketAddress address = new InetSocketAddress(host, port);
            NodePingUtil.pingHost(address, exchange, callback, options);
         } else {
            URI uri = new URI(scheme, (String)null, host, port, "/", (String)null, (String)null);
            NodePingUtil.pingHttpClient(uri, callback, exchange, this.container.getClient(), (XnioSsl)xnioSsl, options);
         }
      } catch (URISyntaxException var9) {
         callback.failed();
      }

   }

   static void sendResponse(HttpServerExchange exchange, String response) {
      exchange.setStatusCode(200);
      exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
      Sender sender = exchange.getResponseSender();
      UndertowLogger.ROOT_LOGGER.mcmpSendingResponse(exchange.getSourceAddress(), exchange.getStatusCode(), exchange.getResponseHeaders(), response);
      sender.send(response);
   }

   static void processOK(HttpServerExchange exchange) throws IOException {
      exchange.setStatusCode(200);
      exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
      exchange.endExchange();
   }

   static void processError(MCMPErrorCode errorCode, HttpServerExchange exchange) {
      processError(errorCode.getType(), errorCode.getMessage(), exchange);
   }

   static void processError(String type, String errString, HttpServerExchange exchange) {
      exchange.setStatusCode(500);
      exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");
      exchange.getResponseHeaders().add(new HttpString("Version"), "0.2.1");
      exchange.getResponseHeaders().add(new HttpString("Type"), type);
      exchange.getResponseHeaders().add(new HttpString("Mess"), errString);
      exchange.endExchange();
      UndertowLogger.ROOT_LOGGER.mcmpProcessingError(type, errString);
   }

   RequestData parseFormData(HttpServerExchange exchange) throws IOException {
      FormDataParser parser = this.parserFactory.createParser(exchange);
      FormData formData = parser.parseBlocking();
      RequestData data = new RequestData();
      Iterator var5 = formData.iterator();

      while(var5.hasNext()) {
         String name = (String)var5.next();
         HttpString key = new HttpString(name);
         data.add(key, formData.get(name));
      }

      return data;
   }

   private static void checkStringForSuspiciousCharacters(String data) {
      for(int i = 0; i < data.length(); ++i) {
         char c = data.charAt(i);
         if (c == '>' || c == '<' || c == '\\' || c == '"' || c == '\n' || c == '\r') {
            throw UndertowMessages.MESSAGES.mcmpMessageRejectedDueToSuspiciousCharacters(data);
         }
      }

   }

   static boolean checkString(String value) {
      return value != null && value.length() > 0;
   }

   static {
      HANDLED_METHODS = Collections.unmodifiableSet(new HashSet(Arrays.asList(CONFIG, ENABLE_APP, DISABLE_APP, STOP_APP, REMOVE_APP, STATUS, INFO, DUMP, PING)));
      MOD_CLUSTER_EXPOSED_VERSION = "mod_cluster_undertow/" + Version.getVersionString();
   }

   static class RequestData {
      private final Map<HttpString, Deque<String>> values = new LinkedHashMap();

      Iterator<HttpString> iterator() {
         return this.values.keySet().iterator();
      }

      void add(HttpString name, Deque<FormData.FormValue> values) {
         MCMPHandler.checkStringForSuspiciousCharacters(name.toString());
         Iterator var3 = values.iterator();

         while(var3.hasNext()) {
            FormData.FormValue value = (FormData.FormValue)var3.next();
            this.add(name, value);
         }

      }

      void addValues(HttpString name, Deque<String> value) {
         Deque<String> values = (Deque)this.values.get(name);
         Iterator var4 = value.iterator();

         while(var4.hasNext()) {
            String i = (String)var4.next();
            MCMPHandler.checkStringForSuspiciousCharacters(i);
         }

         if (values == null) {
            this.values.put(name, value);
         } else {
            values.addAll(value);
         }

      }

      void add(HttpString name, FormData.FormValue value) {
         Deque<String> values = (Deque)this.values.get(name);
         if (values == null) {
            this.values.put(name, values = new ArrayDeque(1));
         }

         String stringVal = value.getValue();
         MCMPHandler.checkStringForSuspiciousCharacters(stringVal);
         ((Deque)values).add(stringVal);
      }

      String getFirst(HttpString name) {
         Deque<String> deque = (Deque)this.values.get(name);
         return deque == null ? null : (String)deque.peekFirst();
      }
   }

   static enum MCMPAction {
      ENABLE,
      DISABLE,
      STOP,
      REMOVE;
   }
}
