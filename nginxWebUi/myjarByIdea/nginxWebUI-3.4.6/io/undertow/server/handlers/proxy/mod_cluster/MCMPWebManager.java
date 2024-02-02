package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class MCMPWebManager extends MCMPHandler {
   private final boolean checkNonce;
   private final boolean reduceDisplay;
   private final boolean allowCmd;
   private final Random r = new SecureRandom();
   private String nonce = null;

   MCMPWebManager(MCMPConfig.MCMPWebManagerConfig config, ModCluster modCluster, HttpHandler next) {
      super(config, modCluster, next);
      this.checkNonce = config.isCheckNonce();
      this.reduceDisplay = config.isReduceDisplay();
      this.allowCmd = config.isAllowCmd();
   }

   String getNonce() {
      return "nonce=" + this.getRawNonce();
   }

   synchronized String getRawNonce() {
      if (this.nonce == null) {
         byte[] nonce = new byte[16];
         this.r.nextBytes(nonce);
         this.nonce = "";

         for(int i = 0; i < 16; i += 2) {
            this.nonce = this.nonce.concat(Integer.toHexString(255 & nonce[i] * 16 + 255 & nonce[i + 1]));
         }
      }

      return this.nonce;
   }

   protected void handleRequest(HttpString method, HttpServerExchange exchange) throws Exception {
      if (!Methods.GET.equals(method)) {
         super.handleRequest(method, exchange);
      } else {
         this.processRequest(exchange);
      }
   }

   protected boolean handlesMethod(HttpString method) {
      return Methods.GET.equals(method) ? true : super.handlesMethod(method);
   }

   private void processRequest(HttpServerExchange exchange) throws IOException {
      Map<String, Deque<String>> params = exchange.getQueryParameters();
      boolean hasNonce = params.containsKey("nonce");
      int refreshTime = 0;
      String uri;
      if (this.checkNonce && hasNonce) {
         String receivedNonce = (String)((Deque)params.get("nonce")).getFirst();
         if (receivedNonce.equals(this.getRawNonce())) {
            boolean refresh = params.containsKey("refresh");
            if (refresh) {
               uri = (String)((Deque)params.get("refresh")).getFirst();
               refreshTime = Integer.parseInt(uri);
               if (refreshTime < 10) {
                  refreshTime = 10;
               }

               exchange.getResponseHeaders().add(new HttpString("Refresh"), Integer.toString(refreshTime));
            }

            boolean cmd = params.containsKey("Cmd");
            boolean range = params.containsKey("Range");
            if (cmd) {
               String scmd = (String)((Deque)params.get("Cmd")).getFirst();
               if (scmd.equals("INFO")) {
                  this.processInfo(exchange);
                  return;
               }

               if (scmd.equals("DUMP")) {
                  this.processDump(exchange);
                  return;
               }

               String srange;
               MCMPHandler.RequestData data;
               boolean domain;
               String sdomain;
               if (scmd.equals("ENABLE-APP") && range) {
                  srange = (String)((Deque)params.get("Range")).getFirst();
                  data = buildRequestData(exchange, params);
                  if (srange.equals("NODE")) {
                     this.processNodeCommand(exchange, data, MCMPHandler.MCMPAction.ENABLE);
                  }

                  if (srange.equals("DOMAIN")) {
                     domain = params.containsKey("Domain");
                     if (domain) {
                        sdomain = (String)((Deque)params.get("Domain")).getFirst();
                        this.processDomainCmd(exchange, sdomain, MCMPHandler.MCMPAction.ENABLE);
                     }
                  }

                  if (srange.equals("CONTEXT")) {
                     this.processAppCommand(exchange, data, MCMPHandler.MCMPAction.ENABLE);
                  }
               } else if (scmd.equals("DISABLE-APP") && range) {
                  srange = (String)((Deque)params.get("Range")).getFirst();
                  data = buildRequestData(exchange, params);
                  if (srange.equals("NODE")) {
                     this.processNodeCommand(exchange, data, MCMPHandler.MCMPAction.DISABLE);
                  }

                  if (srange.equals("DOMAIN")) {
                     domain = params.containsKey("Domain");
                     if (domain) {
                        sdomain = (String)((Deque)params.get("Domain")).getFirst();
                        this.processDomainCmd(exchange, sdomain, MCMPHandler.MCMPAction.DISABLE);
                     }
                  }

                  if (srange.equals("CONTEXT")) {
                     this.processAppCommand(exchange, data, MCMPHandler.MCMPAction.DISABLE);
                  }
               }

               return;
            }
         }
      }

      exchange.setStatusCode(200);
      exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html; charset=ISO-8859-1");
      Sender resp = exchange.getResponseSender();
      StringBuilder buf = new StringBuilder();
      buf.append("<html><head>\n<title>Mod_cluster Status</title>\n</head><body>\n");
      buf.append("<h1>" + MOD_CLUSTER_EXPOSED_VERSION + "</h1>");
      uri = exchange.getRequestPath();
      String nonce = this.getNonce();
      if (refreshTime <= 0) {
         buf.append("<a href=\"").append(uri).append("?").append(nonce).append("&refresh=").append(refreshTime).append("\">Auto Refresh</a>");
      }

      buf.append(" <a href=\"").append(uri).append("?").append(nonce).append("&Cmd=DUMP&Range=ALL").append("\">show DUMP output</a>");
      buf.append(" <a href=\"").append(uri).append("?").append(nonce).append("&Cmd=INFO&Range=ALL").append("\">show INFO output</a>");
      buf.append("\n");
      Map<String, List<Node>> nodes = new LinkedHashMap();

      Iterator var10;
      Node node;
      String groupName;
      Object list;
      for(var10 = this.container.getNodes().iterator(); var10.hasNext(); ((List)list).add(node)) {
         node = (Node)var10.next();
         groupName = node.getNodeConfig().getDomain() != null ? node.getNodeConfig().getDomain() : "";
         list = (List)nodes.get(groupName);
         if (list == null) {
            list = new ArrayList();
            nodes.put(groupName, list);
         }
      }

      var10 = nodes.entrySet().iterator();

      while(var10.hasNext()) {
         Map.Entry<String, List<Node>> entry = (Map.Entry)var10.next();
         groupName = (String)entry.getKey();
         if (this.reduceDisplay) {
            buf.append("<br/><br/>LBGroup " + groupName + ": ");
         } else {
            buf.append("<h1> LBGroup " + groupName + ": ");
         }

         if (this.allowCmd) {
            this.domainCommandString(buf, uri, MCMPHandler.MCMPAction.ENABLE, groupName);
            this.domainCommandString(buf, uri, MCMPHandler.MCMPAction.DISABLE, groupName);
         }

         Iterator var26 = ((List)entry.getValue()).iterator();

         while(var26.hasNext()) {
            Node node = (Node)var26.next();
            NodeConfig nodeConfig = node.getNodeConfig();
            if (this.reduceDisplay) {
               buf.append("<br/><br/>Node " + nodeConfig.getJvmRoute());
               printProxyStat(buf, node, this.reduceDisplay);
            } else {
               buf.append("<h1> Node " + nodeConfig.getJvmRoute() + " (" + nodeConfig.getConnectionURI() + "): </h1>\n");
            }

            if (this.allowCmd) {
               this.nodeCommandString(buf, uri, MCMPHandler.MCMPAction.ENABLE, nodeConfig.getJvmRoute());
               this.nodeCommandString(buf, uri, MCMPHandler.MCMPAction.DISABLE, nodeConfig.getJvmRoute());
            }

            if (!this.reduceDisplay) {
               buf.append("<br/>\n");
               buf.append("Balancer: " + nodeConfig.getBalancer() + ",LBGroup: " + nodeConfig.getDomain());
               String flushpackets = "off";
               if (nodeConfig.isFlushPackets()) {
                  flushpackets = "Auto";
               }

               buf.append(",Flushpackets: " + flushpackets + ",Flushwait: " + nodeConfig.getFlushwait() + ",Ping: " + nodeConfig.getPing() + " ,Smax: " + nodeConfig.getPing() + ",Ttl: " + TimeUnit.MILLISECONDS.toSeconds(nodeConfig.getTtl()));
               printProxyStat(buf, node, this.reduceDisplay);
            } else {
               buf.append("<br/>\n");
            }

            buf.append("\n");
            this.printInfoHost(buf, uri, this.reduceDisplay, this.allowCmd, node);
         }
      }

      buf.append("</body></html>\n");
      resp.send(buf.toString());
   }

   void nodeCommandString(StringBuilder buf, String uri, MCMPHandler.MCMPAction status, String jvmRoute) {
      switch (status) {
         case ENABLE:
            buf.append("<a href=\"" + uri + "?" + this.getNonce() + "&Cmd=ENABLE-APP&Range=NODE&JVMRoute=" + jvmRoute + "\">Enable Contexts</a> ");
            break;
         case DISABLE:
            buf.append("<a href=\"" + uri + "?" + this.getNonce() + "&Cmd=DISABLE-APP&Range=NODE&JVMRoute=" + jvmRoute + "\">Disable Contexts</a> ");
      }

   }

   static void printProxyStat(StringBuilder buf, Node node, boolean reduceDisplay) {
      String status = "NOTOK";
      if (node.getStatus() == NodeStatus.NODE_UP) {
         status = "OK";
      }

      if (reduceDisplay) {
         buf.append(" " + status + " ");
      } else {
         buf.append(",Status: " + status + ",Elected: " + node.getElected() + ",Read: " + node.getConnectionPool().getClientStatistics().getRead() + ",Transferred: " + node.getConnectionPool().getClientStatistics().getWritten() + ",Connected: " + node.getConnectionPool().getOpenConnections() + ",Load: " + node.getLoad());
      }

   }

   void domainCommandString(StringBuilder buf, String uri, MCMPHandler.MCMPAction status, String lbgroup) {
      switch (status) {
         case ENABLE:
            buf.append("<a href=\"" + uri + "?" + this.getNonce() + "&Cmd=ENABLE-APP&Range=DOMAIN&Domain=" + lbgroup + "\">Enable Nodes</a> ");
            break;
         case DISABLE:
            buf.append("<a href=\"" + uri + "?" + this.getNonce() + "&Cmd=DISABLE-APP&Range=DOMAIN&Domain=" + lbgroup + "\">Disable Nodes</a>");
      }

   }

   void processDomainCmd(HttpServerExchange exchange, String domain, MCMPHandler.MCMPAction action) throws IOException {
      if (domain != null) {
         Iterator var4 = this.container.getNodes().iterator();

         while(var4.hasNext()) {
            Node node = (Node)var4.next();
            if (domain.equals(node.getNodeConfig().getDomain())) {
               this.processNodeCommand(node.getJvmRoute(), action);
            }
         }
      }

      processOK(exchange);
   }

   private void printInfoHost(StringBuilder buf, String uri, boolean reduceDisplay, boolean allowCmd, Node node) {
      Iterator var6 = node.getVHosts().iterator();

      while(true) {
         while(var6.hasNext()) {
            Node.VHostMapping host = (Node.VHostMapping)var6.next();
            if (!reduceDisplay) {
               buf.append("<h2> Virtual Host " + host.getId() + ":</h2>");
            }

            this.printInfoContexts(buf, uri, reduceDisplay, allowCmd, (long)host.getId(), host, node);
            Iterator var8;
            String alias;
            if (reduceDisplay) {
               buf.append("Aliases: ");
               var8 = host.getAliases().iterator();

               while(var8.hasNext()) {
                  alias = (String)var8.next();
                  buf.append(alias + " ");
               }
            } else {
               buf.append("<h3>Aliases:</h3>");
               buf.append("<pre>");
               var8 = host.getAliases().iterator();

               while(var8.hasNext()) {
                  alias = (String)var8.next();
                  buf.append(alias + "\n");
               }

               buf.append("</pre>");
            }
         }

         return;
      }
   }

   private void printInfoContexts(StringBuilder buf, String uri, boolean reduceDisplay, boolean allowCmd, long host, Node.VHostMapping vhost, Node node) {
      if (!reduceDisplay) {
         buf.append("<h3>Contexts:</h3>");
      }

      buf.append("<pre>");
      Iterator var9 = node.getContexts().iterator();

      while(var9.hasNext()) {
         Context context = (Context)var9.next();
         if (context.getVhost() == vhost) {
            String status = "REMOVED";
            switch (context.getStatus()) {
               case ENABLED:
                  status = "ENABLED";
                  break;
               case DISABLED:
                  status = "DISABLED";
                  break;
               case STOPPED:
                  status = "STOPPED";
            }

            buf.append(context.getPath() + " , Status: " + status + " Request: " + context.getActiveRequests() + " ");
            if (allowCmd) {
               this.contextCommandString(buf, uri, context.getStatus(), context.getPath(), vhost.getAliases(), node.getJvmRoute());
            }

            buf.append("\n");
         }
      }

      buf.append("</pre>");
   }

   void contextCommandString(StringBuilder buf, String uri, Context.Status status, String path, List<String> alias, String jvmRoute) {
      switch (status) {
         case ENABLED:
            buf.append("<a href=\"" + uri + "?" + this.getNonce() + "&Cmd=DISABLE-APP&Range=CONTEXT&");
            contextString(buf, path, alias, jvmRoute);
            buf.append("\">Disable</a> ");
            break;
         case DISABLED:
            buf.append("<a href=\"" + uri + "?" + this.getNonce() + "&Cmd=ENABLE-APP&Range=CONTEXT&");
            contextString(buf, path, alias, jvmRoute);
            buf.append("\">Enable</a> ");
      }

   }

   static void contextString(StringBuilder buf, String path, List<String> alias, String jvmRoute) {
      buf.append("JVMRoute=" + jvmRoute + "&Alias=");
      boolean first = true;

      String a;
      for(Iterator var5 = alias.iterator(); var5.hasNext(); buf.append(a)) {
         a = (String)var5.next();
         if (first) {
            first = false;
         } else {
            buf.append(",");
         }
      }

      buf.append("&Context=" + path);
   }

   static MCMPHandler.RequestData buildRequestData(HttpServerExchange exchange, Map<String, Deque<String>> params) {
      MCMPHandler.RequestData data = new MCMPHandler.RequestData();
      Iterator var3 = params.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<String, Deque<String>> entry = (Map.Entry)var3.next();
         HttpString name = new HttpString((String)entry.getKey());
         data.addValues(name, (Deque)entry.getValue());
      }

      return data;
   }
}
