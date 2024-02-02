/*    */ package io.undertow.server.handlers.proxy.mod_cluster;
/*    */ 
/*    */ import io.undertow.util.HttpString;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ interface MCMPConstants
/*    */ {
/*    */   public static final String ALIAS_STRING = "Alias";
/*    */   public static final String BALANCER_STRING = "Balancer";
/*    */   public static final String CONTEXT_STRING = "Context";
/*    */   public static final String DOMAIN_STRING = "Domain";
/*    */   public static final String FLUSH_PACKET_STRING = "flushpackets";
/*    */   public static final String FLUSH_WAIT_STRING = "flushwait";
/*    */   public static final String HOST_STRING = "Host";
/*    */   public static final String JVMROUTE_STRING = "JVMRoute";
/*    */   public static final String LOAD_STRING = "Load";
/*    */   public static final String MAXATTEMPTS_STRING = "Maxattempts";
/*    */   public static final String PING_STRING = "ping";
/*    */   public static final String PORT_STRING = "Port";
/*    */   public static final String REVERSED_STRING = "Reversed";
/*    */   public static final String SCHEME_STRING = "Scheme";
/*    */   public static final String SMAX_STRING = "smax";
/*    */   public static final String STICKYSESSION_STRING = "StickySession";
/*    */   public static final String STICKYSESSIONCOOKIE_STRING = "StickySessionCookie";
/*    */   public static final String STICKYSESSIONPATH_STRING = "StickySessionPath";
/*    */   public static final String STICKYSESSIONREMOVE_STRING = "StickySessionRemove";
/*    */   public static final String STICKYSESSIONFORCE_STRING = "StickySessionForce";
/*    */   public static final String TIMEOUT_STRING = "Timeout";
/*    */   public static final String TTL_STRING = "ttl";
/*    */   public static final String TYPE_STRING = "Type";
/*    */   public static final String WAITWORKER_STRING = "WaitWorker";
/* 53 */   public static final HttpString ALIAS = new HttpString("Alias");
/* 54 */   public static final HttpString BALANCER = new HttpString("Balancer");
/* 55 */   public static final HttpString CONTEXT = new HttpString("Context");
/* 56 */   public static final HttpString DOMAIN = new HttpString("Domain");
/* 57 */   public static final HttpString FLUSH_PACKET = new HttpString("flushpackets");
/* 58 */   public static final HttpString FLUSH_WAIT = new HttpString("flushwait");
/* 59 */   public static final HttpString HOST = new HttpString("Host");
/* 60 */   public static final HttpString JVMROUTE = new HttpString("JVMRoute");
/* 61 */   public static final HttpString LOAD = new HttpString("Load");
/* 62 */   public static final HttpString MAXATTEMPTS = new HttpString("Maxattempts");
/* 63 */   public static final HttpString PING = new HttpString("ping");
/* 64 */   public static final HttpString PORT = new HttpString("Port");
/* 65 */   public static final HttpString REVERSED = new HttpString("Reversed");
/* 66 */   public static final HttpString SCHEME = new HttpString("Scheme");
/* 67 */   public static final HttpString SMAX = new HttpString("smax");
/* 68 */   public static final HttpString STICKYSESSION = new HttpString("StickySession");
/* 69 */   public static final HttpString STICKYSESSIONCOOKIE = new HttpString("StickySessionCookie");
/* 70 */   public static final HttpString STICKYSESSIONPATH = new HttpString("StickySessionPath");
/* 71 */   public static final HttpString STICKYSESSIONREMOVE = new HttpString("StickySessionRemove");
/* 72 */   public static final HttpString STICKYSESSIONFORCE = new HttpString("StickySessionForce");
/* 73 */   public static final HttpString TIMEOUT = new HttpString("Timeout");
/* 74 */   public static final HttpString TTL = new HttpString("ttl");
/* 75 */   public static final HttpString TYPE = new HttpString("Type");
/* 76 */   public static final HttpString WAITWORKER = new HttpString("WaitWorker");
/*    */   public static final String TYPESYNTAX = "SYNTAX";
/*    */   public static final String TYPEMEM = "MEM";
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\MCMPConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */