/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import java.io.IOException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Deque;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ class MCMPWebManager
/*     */   extends MCMPHandler
/*     */ {
/*     */   private final boolean checkNonce;
/*     */   private final boolean reduceDisplay;
/*     */   private final boolean allowCmd;
/*  50 */   private final Random r = new SecureRandom();
/*  51 */   private String nonce = null;
/*     */   
/*     */   MCMPWebManager(MCMPConfig.MCMPWebManagerConfig config, ModCluster modCluster, HttpHandler next) {
/*  54 */     super(config, modCluster, next);
/*  55 */     this.checkNonce = config.isCheckNonce();
/*  56 */     this.reduceDisplay = config.isReduceDisplay();
/*  57 */     this.allowCmd = config.isAllowCmd();
/*     */   }
/*     */   
/*     */   String getNonce() {
/*  61 */     return "nonce=" + getRawNonce();
/*     */   }
/*     */   
/*     */   synchronized String getRawNonce() {
/*  65 */     if (this.nonce == null) {
/*  66 */       byte[] nonce = new byte[16];
/*  67 */       this.r.nextBytes(nonce);
/*  68 */       this.nonce = "";
/*  69 */       for (int i = 0; i < 16; i += 2) {
/*  70 */         this.nonce = this.nonce.concat(Integer.toHexString(0xFF & nonce[i] * 16 + 255 & nonce[i + 1]));
/*     */       }
/*     */     } 
/*  73 */     return this.nonce;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleRequest(HttpString method, HttpServerExchange exchange) throws Exception {
/*  78 */     if (!Methods.GET.equals(method)) {
/*  79 */       super.handleRequest(method, exchange);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  84 */     processRequest(exchange);
/*     */   }
/*     */   
/*     */   protected boolean handlesMethod(HttpString method) {
/*  88 */     if (Methods.GET.equals(method)) {
/*  89 */       return true;
/*     */     }
/*  91 */     return super.handlesMethod(method);
/*     */   }
/*     */   
/*     */   private void processRequest(HttpServerExchange exchange) throws IOException {
/*  95 */     Map<String, Deque<String>> params = exchange.getQueryParameters();
/*  96 */     boolean hasNonce = params.containsKey("nonce");
/*  97 */     int refreshTime = 0;
/*  98 */     if (this.checkNonce)
/*     */     {
/* 100 */       if (hasNonce) {
/* 101 */         String receivedNonce = ((Deque<String>)params.get("nonce")).getFirst();
/* 102 */         if (receivedNonce.equals(getRawNonce())) {
/* 103 */           boolean refresh = params.containsKey("refresh");
/* 104 */           if (refresh) {
/* 105 */             String sval = ((Deque<String>)params.get("refresh")).getFirst();
/* 106 */             refreshTime = Integer.parseInt(sval);
/* 107 */             if (refreshTime < 10)
/* 108 */               refreshTime = 10; 
/* 109 */             exchange.getResponseHeaders().add(new HttpString("Refresh"), Integer.toString(refreshTime));
/*     */           } 
/* 111 */           boolean cmd = params.containsKey("Cmd");
/* 112 */           boolean range = params.containsKey("Range");
/* 113 */           if (cmd) {
/* 114 */             String scmd = ((Deque<String>)params.get("Cmd")).getFirst();
/* 115 */             if (scmd.equals("INFO")) {
/* 116 */               processInfo(exchange); return;
/*     */             } 
/* 118 */             if (scmd.equals("DUMP")) {
/* 119 */               processDump(exchange); return;
/*     */             } 
/* 121 */             if (scmd.equals("ENABLE-APP") && range) {
/* 122 */               String srange = ((Deque<String>)params.get("Range")).getFirst();
/* 123 */               MCMPHandler.RequestData data = buildRequestData(exchange, params);
/* 124 */               if (srange.equals("NODE")) {
/* 125 */                 processNodeCommand(exchange, data, MCMPHandler.MCMPAction.ENABLE);
/*     */               }
/* 127 */               if (srange.equals("DOMAIN")) {
/* 128 */                 boolean domain = params.containsKey("Domain");
/* 129 */                 if (domain) {
/* 130 */                   String sdomain = ((Deque<String>)params.get("Domain")).getFirst();
/* 131 */                   processDomainCmd(exchange, sdomain, MCMPHandler.MCMPAction.ENABLE);
/*     */                 } 
/*     */               } 
/* 134 */               if (srange.equals("CONTEXT")) {
/* 135 */                 processAppCommand(exchange, data, MCMPHandler.MCMPAction.ENABLE);
/*     */               }
/* 137 */             } else if (scmd.equals("DISABLE-APP") && range) {
/* 138 */               String srange = ((Deque<String>)params.get("Range")).getFirst();
/* 139 */               MCMPHandler.RequestData data = buildRequestData(exchange, params);
/* 140 */               if (srange.equals("NODE")) {
/* 141 */                 processNodeCommand(exchange, data, MCMPHandler.MCMPAction.DISABLE);
/*     */               }
/* 143 */               if (srange.equals("DOMAIN")) {
/* 144 */                 boolean domain = params.containsKey("Domain");
/* 145 */                 if (domain) {
/* 146 */                   String sdomain = ((Deque<String>)params.get("Domain")).getFirst();
/* 147 */                   processDomainCmd(exchange, sdomain, MCMPHandler.MCMPAction.DISABLE);
/*     */                 } 
/*     */               } 
/* 150 */               if (srange.equals("CONTEXT")) {
/* 151 */                 processAppCommand(exchange, data, MCMPHandler.MCMPAction.DISABLE);
/*     */               }
/*     */             } 
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 160 */     exchange.setStatusCode(200);
/* 161 */     exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "text/html; charset=ISO-8859-1");
/* 162 */     Sender resp = exchange.getResponseSender();
/*     */     
/* 164 */     StringBuilder buf = new StringBuilder();
/* 165 */     buf.append("<html><head>\n<title>Mod_cluster Status</title>\n</head><body>\n");
/* 166 */     buf.append("<h1>" + MOD_CLUSTER_EXPOSED_VERSION + "</h1>");
/*     */     
/* 168 */     String uri = exchange.getRequestPath();
/* 169 */     String nonce = getNonce();
/* 170 */     if (refreshTime <= 0) {
/* 171 */       buf.append("<a href=\"").append(uri).append("?").append(nonce).append("&refresh=").append(refreshTime).append("\">Auto Refresh</a>");
/*     */     }
/* 173 */     buf.append(" <a href=\"").append(uri).append("?").append(nonce).append("&Cmd=DUMP&Range=ALL").append("\">show DUMP output</a>");
/* 174 */     buf.append(" <a href=\"").append(uri).append("?").append(nonce).append("&Cmd=INFO&Range=ALL").append("\">show INFO output</a>");
/* 175 */     buf.append("\n");
/*     */ 
/*     */     
/* 178 */     Map<String, List<Node>> nodes = new LinkedHashMap<>();
/* 179 */     for (Node node : this.container.getNodes()) {
/* 180 */       String domain = (node.getNodeConfig().getDomain() != null) ? node.getNodeConfig().getDomain() : "";
/* 181 */       List<Node> list = nodes.get(domain);
/* 182 */       if (list == null) {
/* 183 */         list = new ArrayList<>();
/* 184 */         nodes.put(domain, list);
/*     */       } 
/* 186 */       list.add(node);
/*     */     } 
/*     */     
/* 189 */     for (Map.Entry<String, List<Node>> entry : nodes.entrySet()) {
/* 190 */       String groupName = entry.getKey();
/* 191 */       if (this.reduceDisplay) {
/* 192 */         buf.append("<br/><br/>LBGroup " + groupName + ": ");
/*     */       } else {
/* 194 */         buf.append("<h1> LBGroup " + groupName + ": ");
/*     */       } 
/* 196 */       if (this.allowCmd) {
/* 197 */         domainCommandString(buf, uri, MCMPHandler.MCMPAction.ENABLE, groupName);
/* 198 */         domainCommandString(buf, uri, MCMPHandler.MCMPAction.DISABLE, groupName);
/*     */       } 
/*     */       
/* 201 */       for (Node node : entry.getValue()) {
/* 202 */         NodeConfig nodeConfig = node.getNodeConfig();
/* 203 */         if (this.reduceDisplay) {
/* 204 */           buf.append("<br/><br/>Node " + nodeConfig.getJvmRoute());
/* 205 */           printProxyStat(buf, node, this.reduceDisplay);
/*     */         } else {
/* 207 */           buf.append("<h1> Node " + nodeConfig.getJvmRoute() + " (" + nodeConfig.getConnectionURI() + "): </h1>\n");
/*     */         } 
/*     */         
/* 210 */         if (this.allowCmd) {
/* 211 */           nodeCommandString(buf, uri, MCMPHandler.MCMPAction.ENABLE, nodeConfig.getJvmRoute());
/* 212 */           nodeCommandString(buf, uri, MCMPHandler.MCMPAction.DISABLE, nodeConfig.getJvmRoute());
/*     */         } 
/* 214 */         if (!this.reduceDisplay) {
/* 215 */           buf.append("<br/>\n");
/* 216 */           buf.append("Balancer: " + nodeConfig.getBalancer() + ",LBGroup: " + nodeConfig.getDomain());
/* 217 */           String flushpackets = "off";
/* 218 */           if (nodeConfig.isFlushPackets()) {
/* 219 */             flushpackets = "Auto";
/*     */           }
/* 221 */           buf.append(",Flushpackets: " + flushpackets + ",Flushwait: " + nodeConfig.getFlushwait() + ",Ping: " + nodeConfig.getPing() + " ,Smax: " + nodeConfig.getPing() + ",Ttl: " + TimeUnit.MILLISECONDS.toSeconds(nodeConfig.getTtl()));
/* 222 */           printProxyStat(buf, node, this.reduceDisplay);
/*     */         } else {
/* 224 */           buf.append("<br/>\n");
/*     */         } 
/* 226 */         buf.append("\n");
/*     */ 
/*     */         
/* 229 */         printInfoHost(buf, uri, this.reduceDisplay, this.allowCmd, node);
/*     */       } 
/*     */     } 
/* 232 */     buf.append("</body></html>\n");
/* 233 */     resp.send(buf.toString());
/*     */   }
/*     */   
/*     */   void nodeCommandString(StringBuilder buf, String uri, MCMPHandler.MCMPAction status, String jvmRoute) {
/* 237 */     switch (status) {
/*     */       case ENABLED:
/* 239 */         buf.append("<a href=\"" + uri + "?" + getNonce() + "&Cmd=ENABLE-APP&Range=NODE&JVMRoute=" + jvmRoute + "\">Enable Contexts</a> ");
/*     */         break;
/*     */       case DISABLED:
/* 242 */         buf.append("<a href=\"" + uri + "?" + getNonce() + "&Cmd=DISABLE-APP&Range=NODE&JVMRoute=" + jvmRoute + "\">Disable Contexts</a> ");
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   static void printProxyStat(StringBuilder buf, Node node, boolean reduceDisplay) {
/* 248 */     String status = "NOTOK";
/* 249 */     if (node.getStatus() == NodeStatus.NODE_UP)
/* 250 */       status = "OK"; 
/* 251 */     if (reduceDisplay) {
/* 252 */       buf.append(" " + status + " ");
/*     */     } else {
/* 254 */       buf.append(",Status: " + status + ",Elected: " + node.getElected() + ",Read: " + node.getConnectionPool().getClientStatistics().getRead() + ",Transferred: " + node.getConnectionPool().getClientStatistics().getWritten() + ",Connected: " + node
/* 255 */           .getConnectionPool().getOpenConnections() + ",Load: " + node.getLoad());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void domainCommandString(StringBuilder buf, String uri, MCMPHandler.MCMPAction status, String lbgroup) {
/* 261 */     switch (status) {
/*     */       case ENABLED:
/* 263 */         buf.append("<a href=\"" + uri + "?" + getNonce() + "&Cmd=ENABLE-APP&Range=DOMAIN&Domain=" + lbgroup + "\">Enable Nodes</a> ");
/*     */         break;
/*     */       case DISABLED:
/* 266 */         buf.append("<a href=\"" + uri + "?" + getNonce() + "&Cmd=DISABLE-APP&Range=DOMAIN&Domain=" + lbgroup + "\">Disable Nodes</a>");
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   void processDomainCmd(HttpServerExchange exchange, String domain, MCMPHandler.MCMPAction action) throws IOException {
/* 272 */     if (domain != null) {
/* 273 */       for (Node node : this.container.getNodes()) {
/* 274 */         if (domain.equals(node.getNodeConfig().getDomain())) {
/* 275 */           processNodeCommand(node.getJvmRoute(), action);
/*     */         }
/*     */       } 
/*     */     }
/* 279 */     processOK(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   private void printInfoHost(StringBuilder buf, String uri, boolean reduceDisplay, boolean allowCmd, Node node) {
/* 284 */     for (Node.VHostMapping host : node.getVHosts()) {
/* 285 */       if (!reduceDisplay) {
/* 286 */         buf.append("<h2> Virtual Host " + host.getId() + ":</h2>");
/*     */       }
/* 288 */       printInfoContexts(buf, uri, reduceDisplay, allowCmd, host.getId(), host, node);
/* 289 */       if (reduceDisplay) {
/* 290 */         buf.append("Aliases: ");
/* 291 */         for (String alias : host.getAliases())
/* 292 */           buf.append(alias + " "); 
/*     */         continue;
/*     */       } 
/* 295 */       buf.append("<h3>Aliases:</h3>");
/* 296 */       buf.append("<pre>");
/* 297 */       for (String alias : host.getAliases()) {
/* 298 */         buf.append(alias + "\n");
/*     */       }
/* 300 */       buf.append("</pre>");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void printInfoContexts(StringBuilder buf, String uri, boolean reduceDisplay, boolean allowCmd, long host, Node.VHostMapping vhost, Node node) {
/* 307 */     if (!reduceDisplay)
/* 308 */       buf.append("<h3>Contexts:</h3>"); 
/* 309 */     buf.append("<pre>");
/* 310 */     for (Context context : node.getContexts()) {
/* 311 */       if (context.getVhost() == vhost) {
/* 312 */         String status = "REMOVED";
/* 313 */         switch (context.getStatus()) {
/*     */           case ENABLED:
/* 315 */             status = "ENABLED";
/*     */             break;
/*     */           case DISABLED:
/* 318 */             status = "DISABLED";
/*     */             break;
/*     */           case STOPPED:
/* 321 */             status = "STOPPED";
/*     */             break;
/*     */         } 
/* 324 */         buf.append(context.getPath() + " , Status: " + status + " Request: " + context.getActiveRequests() + " ");
/* 325 */         if (allowCmd) {
/* 326 */           contextCommandString(buf, uri, context.getStatus(), context.getPath(), vhost.getAliases(), node.getJvmRoute());
/*     */         }
/* 328 */         buf.append("\n");
/*     */       } 
/*     */     } 
/* 331 */     buf.append("</pre>");
/*     */   }
/*     */ 
/*     */   
/*     */   void contextCommandString(StringBuilder buf, String uri, Context.Status status, String path, List<String> alias, String jvmRoute) {
/* 336 */     switch (status) {
/*     */       case DISABLED:
/* 338 */         buf.append("<a href=\"" + uri + "?" + getNonce() + "&Cmd=ENABLE-APP&Range=CONTEXT&");
/* 339 */         contextString(buf, path, alias, jvmRoute);
/* 340 */         buf.append("\">Enable</a> ");
/*     */         break;
/*     */       case ENABLED:
/* 343 */         buf.append("<a href=\"" + uri + "?" + getNonce() + "&Cmd=DISABLE-APP&Range=CONTEXT&");
/* 344 */         contextString(buf, path, alias, jvmRoute);
/* 345 */         buf.append("\">Disable</a> ");
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   static void contextString(StringBuilder buf, String path, List<String> alias, String jvmRoute) {
/* 351 */     buf.append("JVMRoute=" + jvmRoute + "&Alias=");
/* 352 */     boolean first = true;
/* 353 */     for (String a : alias) {
/* 354 */       if (first) {
/* 355 */         first = false;
/*     */       } else {
/* 357 */         buf.append(",");
/*     */       } 
/* 359 */       buf.append(a);
/*     */     } 
/* 361 */     buf.append("&Context=" + path);
/*     */   }
/*     */   
/*     */   static MCMPHandler.RequestData buildRequestData(HttpServerExchange exchange, Map<String, Deque<String>> params) {
/* 365 */     MCMPHandler.RequestData data = new MCMPHandler.RequestData();
/* 366 */     for (Map.Entry<String, Deque<String>> entry : params.entrySet()) {
/* 367 */       HttpString name = new HttpString(entry.getKey());
/* 368 */       data.addValues(name, entry.getValue());
/*     */     } 
/* 370 */     return data;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\MCMPWebManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */