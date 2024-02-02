/*     */ package io.undertow.server.handlers.proxy;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.PathMatcher;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
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
/*     */ public class HostTable<H>
/*     */ {
/*  40 */   private final Map<H, Set<Target>> hosts = (Map<H, Set<Target>>)new CopyOnWriteMap();
/*  41 */   private final Map<String, PathMatcher<Set<H>>> targets = (Map<String, PathMatcher<Set<H>>>)new CopyOnWriteMap();
/*     */   
/*     */   public synchronized HostTable addHost(H host) {
/*  44 */     if (this.hosts.containsKey(host)) {
/*  45 */       throw UndertowMessages.MESSAGES.hostAlreadyRegistered(host);
/*     */     }
/*  47 */     this.hosts.put(host, new CopyOnWriteArraySet<>());
/*  48 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized HostTable removeHost(H host) {
/*  52 */     Set<Target> targets = this.hosts.remove(host);
/*  53 */     for (Target target : targets) {
/*  54 */       removeRoute(host, target.virtualHost, target.contextPath);
/*     */     }
/*  56 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized HostTable addRoute(H host, String virtualHost, String contextPath) {
/*  60 */     Set<Target> hostData = this.hosts.get(host);
/*  61 */     if (hostData == null) {
/*  62 */       throw UndertowMessages.MESSAGES.hostHasNotBeenRegistered(host);
/*     */     }
/*  64 */     hostData.add(new Target(virtualHost, contextPath));
/*  65 */     PathMatcher<Set<H>> paths = this.targets.get(virtualHost);
/*  66 */     if (paths == null) {
/*  67 */       paths = new PathMatcher();
/*  68 */       this.targets.put(virtualHost, paths);
/*     */     } 
/*  70 */     Set<H> hostSet = (Set<H>)paths.getPrefixPath(contextPath);
/*  71 */     if (hostSet == null) {
/*  72 */       hostSet = new CopyOnWriteArraySet<>();
/*  73 */       paths.addPrefixPath(contextPath, hostSet);
/*     */     } 
/*  75 */     hostSet.add(host);
/*  76 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized HostTable removeRoute(H host, String virtualHost, String contextPath) {
/*  81 */     Set<Target> hostData = this.hosts.get(host);
/*  82 */     if (hostData != null) {
/*  83 */       hostData.remove(new Target(virtualHost, contextPath));
/*     */     }
/*  85 */     PathMatcher<Set<H>> paths = this.targets.get(virtualHost);
/*  86 */     if (paths == null) {
/*  87 */       return this;
/*     */     }
/*  89 */     Set<H> hostSet = (Set<H>)paths.getPrefixPath(contextPath);
/*  90 */     if (hostSet == null) {
/*  91 */       return this;
/*     */     }
/*  93 */     hostSet.remove(host);
/*  94 */     if (hostSet.isEmpty()) {
/*  95 */       paths.removePrefixPath(contextPath);
/*     */     }
/*  97 */     return this;
/*     */   }
/*     */   
/*     */   public Set<H> getHostsForTarget(String hostName, String path) {
/* 101 */     PathMatcher<Set<H>> matcher = this.targets.get(hostName);
/* 102 */     if (matcher == null) {
/* 103 */       return null;
/*     */     }
/* 105 */     return (Set<H>)matcher.match(path).getValue();
/*     */   }
/*     */   
/*     */   private static final class Target {
/*     */     final String virtualHost;
/*     */     final String contextPath;
/*     */     
/*     */     private Target(String virtualHost, String contextPath) {
/* 113 */       this.virtualHost = virtualHost;
/* 114 */       this.contextPath = contextPath;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 119 */       if (this == o) return true; 
/* 120 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 122 */       Target target = (Target)o;
/*     */       
/* 124 */       if ((this.contextPath != null) ? !this.contextPath.equals(target.contextPath) : (target.contextPath != null))
/* 125 */         return false; 
/* 126 */       if ((this.virtualHost != null) ? !this.virtualHost.equals(target.virtualHost) : (target.virtualHost != null)) {
/* 127 */         return false;
/*     */       }
/* 129 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 134 */       int result = (this.virtualHost != null) ? this.virtualHost.hashCode() : 0;
/* 135 */       result = 31 * result + ((this.contextPath != null) ? this.contextPath.hashCode() : 0);
/* 136 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\HostTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */