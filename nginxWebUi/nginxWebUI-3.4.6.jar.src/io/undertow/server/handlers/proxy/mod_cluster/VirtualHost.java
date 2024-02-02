/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.PathMatcher;
/*     */ import io.undertow.util.URLUtils;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ConcurrentMap;
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
/*     */ public class VirtualHost
/*     */ {
/*     */   private static final String STRING_PATH_SEPARATOR = "/";
/*  42 */   private final HostEntry defaultHandler = new HostEntry("/");
/*  43 */   private final ConcurrentMap<String, HostEntry> contexts = (ConcurrentMap<String, HostEntry>)new CopyOnWriteMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   private volatile int[] lengths = new int[0];
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
/*     */   PathMatcher.PathMatch<HostEntry> match(String path) {
/*  61 */     int length = path.length();
/*  62 */     int[] lengths = this.lengths;
/*  63 */     for (int i = 0; i < lengths.length; i++) {
/*  64 */       int pathLength = lengths[i];
/*  65 */       if (pathLength == length) {
/*  66 */         HostEntry next = this.contexts.get(path);
/*  67 */         if (next != null) {
/*  68 */           return new PathMatcher.PathMatch(path, "", next);
/*     */         }
/*  70 */       } else if (pathLength < length) {
/*  71 */         char c = path.charAt(pathLength);
/*  72 */         if (c == '/') {
/*  73 */           String part = path.substring(0, pathLength);
/*  74 */           HostEntry next = this.contexts.get(part);
/*  75 */           if (next != null) {
/*  76 */             return new PathMatcher.PathMatch(part, path.substring(pathLength), next);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*  81 */     if (this.defaultHandler.contexts.isEmpty()) {
/*  82 */       return new PathMatcher.PathMatch("", path, null);
/*     */     }
/*  84 */     return new PathMatcher.PathMatch("", path, this.defaultHandler);
/*     */   }
/*     */   
/*     */   public synchronized void registerContext(String path, String jvmRoute, Context context) {
/*  88 */     if (path.isEmpty()) {
/*  89 */       throw UndertowMessages.MESSAGES.pathMustBeSpecified();
/*     */     }
/*     */     
/*  92 */     String normalizedPath = URLUtils.normalizeSlashes(path);
/*  93 */     if ("/".equals(normalizedPath)) {
/*  94 */       this.defaultHandler.contexts.put(jvmRoute, context);
/*     */       
/*     */       return;
/*     */     } 
/*  98 */     boolean rebuild = false;
/*  99 */     HostEntry hostEntry = this.contexts.get(normalizedPath);
/* 100 */     if (hostEntry == null) {
/* 101 */       rebuild = true;
/* 102 */       hostEntry = new HostEntry(normalizedPath);
/* 103 */       this.contexts.put(normalizedPath, hostEntry);
/*     */     } 
/* 105 */     assert !hostEntry.contexts.containsKey(jvmRoute);
/* 106 */     hostEntry.contexts.put(jvmRoute, context);
/* 107 */     if (rebuild) {
/* 108 */       buildLengths();
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void removeContext(String path, String jvmRoute, Context context) {
/* 113 */     if (path == null || path.isEmpty()) {
/* 114 */       throw UndertowMessages.MESSAGES.pathMustBeSpecified();
/*     */     }
/*     */     
/* 117 */     String normalizedPath = URLUtils.normalizeSlashes(path);
/* 118 */     if ("/".equals(normalizedPath)) {
/* 119 */       this.defaultHandler.contexts.remove(jvmRoute, context);
/*     */     }
/*     */     
/* 122 */     HostEntry hostEntry = this.contexts.get(normalizedPath);
/* 123 */     if (hostEntry != null && 
/* 124 */       hostEntry.contexts.remove(jvmRoute, context) && 
/* 125 */       hostEntry.contexts.isEmpty()) {
/* 126 */       this.contexts.remove(normalizedPath);
/* 127 */       buildLengths();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isEmpty() {
/* 134 */     return (this.contexts.isEmpty() && this.defaultHandler.contexts.isEmpty());
/*     */   }
/*     */   
/*     */   private void buildLengths() {
/* 138 */     Set<Integer> lengths = new TreeSet<>(new Comparator<Integer>()
/*     */         {
/*     */           public int compare(Integer o1, Integer o2) {
/* 141 */             return -o1.compareTo(o2);
/*     */           }
/*     */         });
/* 144 */     for (String p : this.contexts.keySet()) {
/* 145 */       lengths.add(Integer.valueOf(p.length()));
/*     */     }
/*     */     
/* 148 */     int[] lengthArray = new int[lengths.size()];
/* 149 */     int pos = 0;
/* 150 */     for (Iterator<Integer> iterator = lengths.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/* 151 */       lengthArray[pos++] = i; }
/*     */     
/* 153 */     this.lengths = lengthArray;
/*     */   }
/*     */ 
/*     */   
/*     */   static class HostEntry
/*     */   {
/* 159 */     private final ConcurrentMap<String, Context> contexts = (ConcurrentMap<String, Context>)new CopyOnWriteMap();
/*     */     private final String contextPath;
/*     */     
/*     */     HostEntry(String contextPath) {
/* 163 */       this.contextPath = contextPath;
/*     */     }
/*     */     
/*     */     protected String getContextPath() {
/* 167 */       return this.contextPath;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Context getContextForNode(String jvmRoute) {
/* 176 */       return this.contexts.get(jvmRoute);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Collection<String> getNodes() {
/* 183 */       return Collections.unmodifiableCollection(this.contexts.keySet());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Collection<Context> getContexts() {
/* 190 */       return Collections.unmodifiableCollection(this.contexts.values());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\VirtualHost.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */