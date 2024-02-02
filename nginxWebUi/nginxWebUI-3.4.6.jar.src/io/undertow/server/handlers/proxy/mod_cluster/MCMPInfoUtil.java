/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
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
/*     */ class MCMPInfoUtil
/*     */ {
/*     */   private static final String NEWLINE = "\n";
/*     */   
/*     */   static void printDump(Balancer balancer, StringBuilder builder) {
/*  31 */     builder.append("balancer: [").append(balancer.getId()).append("]")
/*  32 */       .append(" Name: ").append(balancer.getName())
/*  33 */       .append(" Sticky: ").append(toStringOneZero(balancer.isStickySession()))
/*  34 */       .append(" [").append(balancer.getStickySessionCookie()).append("]/[").append(balancer.getStickySessionPath()).append("]")
/*  35 */       .append(" remove: ").append(toStringOneZero(balancer.isStickySessionRemove()))
/*  36 */       .append(" force: ").append(toStringOneZero(balancer.isStickySessionForce()))
/*  37 */       .append(" Timeout: ").append(balancer.getWaitWorker())
/*  38 */       .append(" maxAttempts: ").append(balancer.getMaxRetries())
/*  39 */       .append("\n");
/*     */   }
/*     */ 
/*     */   
/*     */   static void printInfo(Node.VHostMapping host, StringBuilder builder) {
/*  44 */     for (String alias : host.getAliases()) {
/*  45 */       builder.append("Vhost: [")
/*  46 */         .append(host.getNode().getId()).append(":")
/*  47 */         .append(host.getId()).append(":")
/*  48 */         .append(-1)
/*  49 */         .append("], Alias: ").append(alias)
/*  50 */         .append("\n");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static void printDump(Node.VHostMapping host, StringBuilder builder) {
/*  56 */     for (String alias : host.getAliases()) {
/*  57 */       builder.append("host: ").append(host.getId())
/*  58 */         .append(" [").append(alias).append("]")
/*  59 */         .append(" vhost: ").append(host.getId())
/*  60 */         .append(" node: ").append(host.getNode().getId())
/*  61 */         .append("\n");
/*     */     }
/*     */   }
/*     */   
/*     */   static void printInfo(Context context, StringBuilder builder) {
/*  66 */     builder.append("Context: [")
/*  67 */       .append(context.getNode().getId()).append(":")
/*  68 */       .append(context.getVhost().getId()).append(":")
/*  69 */       .append(context.getId())
/*  70 */       .append("]")
/*  71 */       .append(", Context: ").append(context.getPath())
/*  72 */       .append(", Status: ").append(context.getStatus())
/*  73 */       .append("\n");
/*     */   }
/*     */   
/*     */   static void printDump(Context context, StringBuilder builder) {
/*  77 */     builder.append("context: ").append(context.getId())
/*  78 */       .append(" [").append(context.getPath()).append("]")
/*  79 */       .append(" vhost: ").append(context.getVhost().getId())
/*  80 */       .append(" node: ").append(context.getNode().getId())
/*  81 */       .append(" status: ").append(formatStatus(context.getStatus()))
/*  82 */       .append("\n");
/*     */   }
/*     */   
/*     */   static void printInfo(Node node, StringBuilder builder) {
/*  86 */     builder.append("Node: ")
/*  87 */       .append("[").append(node.getId()).append("]")
/*  88 */       .append(",Name: ").append(node.getJvmRoute())
/*  89 */       .append(",Balancer: ").append(node.getNodeConfig().getBalancer())
/*  90 */       .append(",LBGroup: ").append(formatString(node.getNodeConfig().getDomain()))
/*  91 */       .append(",Host: ").append(node.getNodeConfig().getConnectionURI().getHost())
/*  92 */       .append(",Port: ").append(node.getNodeConfig().getConnectionURI().getPort())
/*  93 */       .append(",Type: ").append(node.getNodeConfig().getConnectionURI().getScheme())
/*  94 */       .append(",Flushpackets: ").append(toStringOnOff(node.getNodeConfig().isFlushPackets()))
/*  95 */       .append(",Flushwait: ").append(node.getNodeConfig().getFlushwait())
/*  96 */       .append(",Ping: ").append(node.getNodeConfig().getPing())
/*  97 */       .append(",Smax: ").append(node.getNodeConfig().getSmax())
/*  98 */       .append(",Ttl: ").append(TimeUnit.MILLISECONDS.toSeconds(node.getNodeConfig().getTtl()))
/*  99 */       .append(",Elected: ").append(node.getElected())
/* 100 */       .append(",Read: ").append(node.getConnectionPool().getClientStatistics().getRead())
/* 101 */       .append(",Transfered: ").append(node.getConnectionPool().getClientStatistics().getWritten())
/* 102 */       .append(",Connected: ").append(node.getConnectionPool().getOpenConnections())
/* 103 */       .append(",Load: ").append(node.getLoad())
/* 104 */       .append("\n");
/*     */   }
/*     */   
/*     */   static void printDump(Node node, StringBuilder builder) {
/* 108 */     builder.append("node: [")
/* 109 */       .append(node.getBalancer().getId()).append(":")
/* 110 */       .append(node.getId()).append("]")
/* 111 */       .append(",Balancer: ").append(node.getNodeConfig().getBalancer())
/* 112 */       .append(",JVMRoute: ").append(node.getJvmRoute())
/* 113 */       .append(",LBGroup: [").append(formatString(node.getNodeConfig().getDomain())).append("]")
/* 114 */       .append(",Host: ").append(node.getNodeConfig().getConnectionURI().getHost())
/* 115 */       .append(",Port: ").append(node.getNodeConfig().getConnectionURI().getPort())
/* 116 */       .append(",Type: ").append(node.getNodeConfig().getConnectionURI().getScheme())
/* 117 */       .append(",flushpackets: ").append(toStringOneZero(node.getNodeConfig().isFlushPackets()))
/* 118 */       .append(",flushwait: ").append(node.getNodeConfig().getFlushwait())
/* 119 */       .append(",ping: ").append(node.getNodeConfig().getPing())
/* 120 */       .append(",smax: ").append(node.getNodeConfig().getSmax())
/* 121 */       .append(",ttl: ").append(TimeUnit.MILLISECONDS.toSeconds(node.getNodeConfig().getTtl()))
/* 122 */       .append(",timeout: ").append(node.getNodeConfig().getTimeout())
/* 123 */       .append("\n");
/*     */   }
/*     */   
/*     */   static String toStringOneZero(boolean bool) {
/* 127 */     return bool ? "1" : "0";
/*     */   }
/*     */   
/*     */   static String toStringOnOff(boolean bool) {
/* 131 */     return bool ? "On" : "Off";
/*     */   }
/*     */   
/*     */   static String formatString(String str) {
/* 135 */     return (str == null) ? "" : str;
/*     */   }
/*     */ 
/*     */   
/*     */   static int formatStatus(Context.Status status) {
/* 140 */     return (status == Context.Status.ENABLED) ? 1 : ((status == Context.Status.DISABLED) ? 2 : ((status == Context.Status.STOPPED) ? 3 : -1));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\MCMPInfoUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */