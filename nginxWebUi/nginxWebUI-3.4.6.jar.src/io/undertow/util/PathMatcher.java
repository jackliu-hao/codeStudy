/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathMatcher<T>
/*     */ {
/*     */   private static final String STRING_PATH_SEPARATOR = "/";
/*     */   private volatile T defaultHandler;
/*  46 */   private final SubstringMap<T> paths = new SubstringMap<>();
/*  47 */   private final ConcurrentMap<String, T> exactPathMatches = new CopyOnWriteMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private volatile int[] lengths = new int[0];
/*     */   
/*     */   public PathMatcher(T defaultHandler) {
/*  55 */     this.defaultHandler = defaultHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public PathMatcher() {}
/*     */   
/*     */   public Set<String> getExactPathMatchesSet() {
/*  62 */     return Collections.unmodifiableSet(this.exactPathMatches.keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public Set<String> getPathMatchesSet() {
/*  67 */     return Collections.unmodifiableSet(this.paths.toMap().keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PathMatch<T> match(String path) {
/*  76 */     if (!this.exactPathMatches.isEmpty()) {
/*  77 */       T match = getExactPath(path);
/*  78 */       if (match != null) {
/*  79 */         UndertowLogger.REQUEST_LOGGER.debugf("Matched exact path %s", path);
/*  80 */         return new PathMatch<>(path, "", match);
/*     */       } 
/*     */     } 
/*     */     
/*  84 */     int length = path.length();
/*  85 */     int[] lengths = this.lengths;
/*  86 */     for (int i = 0; i < lengths.length; i++) {
/*  87 */       int pathLength = lengths[i];
/*  88 */       if (pathLength == length) {
/*  89 */         SubstringMap.SubstringMatch<T> next = this.paths.get(path, length);
/*  90 */         if (next != null) {
/*  91 */           UndertowLogger.REQUEST_LOGGER.debugf("Matched prefix path %s for path %s", next.getKey(), path);
/*  92 */           return new PathMatch<>(path, "", next.getValue());
/*     */         } 
/*  94 */       } else if (pathLength < length) {
/*  95 */         char c = path.charAt(pathLength);
/*  96 */         if (c == '/') {
/*     */           
/*  98 */           SubstringMap.SubstringMatch<T> next = this.paths.get(path, pathLength);
/*  99 */           if (next != null) {
/* 100 */             UndertowLogger.REQUEST_LOGGER.debugf("Matched prefix path %s for path %s", next.getKey(), path);
/* 101 */             return new PathMatch<>(next.getKey(), path.substring(pathLength), next.getValue());
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 106 */     UndertowLogger.REQUEST_LOGGER.debugf("Matched default handler path %s", path);
/* 107 */     return new PathMatch<>("", path, this.defaultHandler);
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
/*     */   public synchronized PathMatcher addPrefixPath(String path, T handler) {
/* 123 */     if (path.isEmpty()) {
/* 124 */       throw UndertowMessages.MESSAGES.pathMustBeSpecified();
/*     */     }
/*     */     
/* 127 */     String normalizedPath = URLUtils.normalizeSlashes(path);
/*     */     
/* 129 */     if ("/".equals(normalizedPath)) {
/* 130 */       this.defaultHandler = handler;
/* 131 */       return this;
/*     */     } 
/*     */     
/* 134 */     this.paths.put(normalizedPath, handler);
/*     */     
/* 136 */     buildLengths();
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized PathMatcher addExactPath(String path, T handler) {
/* 142 */     if (path.isEmpty()) {
/* 143 */       throw UndertowMessages.MESSAGES.pathMustBeSpecified();
/*     */     }
/* 145 */     this.exactPathMatches.put(URLUtils.normalizeSlashes(path), handler);
/* 146 */     return this;
/*     */   }
/*     */   
/*     */   public T getExactPath(String path) {
/* 150 */     return this.exactPathMatches.get(URLUtils.normalizeSlashes(path));
/*     */   }
/*     */ 
/*     */   
/*     */   public T getPrefixPath(String path) {
/* 155 */     String normalizedPath = URLUtils.normalizeSlashes(path);
/*     */ 
/*     */     
/* 158 */     SubstringMap.SubstringMatch<T> match = this.paths.get(normalizedPath);
/* 159 */     if ("/".equals(normalizedPath) && match == null) {
/* 160 */       return this.defaultHandler;
/*     */     }
/* 162 */     if (match == null) {
/* 163 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 167 */     return match.getValue();
/*     */   }
/*     */   
/*     */   private void buildLengths() {
/* 171 */     Set<Integer> lengths = new TreeSet<>(new Comparator<Integer>()
/*     */         {
/*     */           public int compare(Integer o1, Integer o2) {
/* 174 */             return -o1.compareTo(o2);
/*     */           }
/*     */         });
/* 177 */     for (String p : this.paths.keys()) {
/* 178 */       lengths.add(Integer.valueOf(p.length()));
/*     */     }
/*     */     
/* 181 */     int[] lengthArray = new int[lengths.size()];
/* 182 */     int pos = 0;
/* 183 */     for (Iterator<Integer> iterator = lengths.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/* 184 */       lengthArray[pos++] = i; }
/*     */     
/* 186 */     this.lengths = lengthArray;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public synchronized PathMatcher removePath(String path) {
/* 191 */     return removePrefixPath(path);
/*     */   }
/*     */   
/*     */   public synchronized PathMatcher removePrefixPath(String path) {
/* 195 */     if (path == null || path.isEmpty()) {
/* 196 */       throw UndertowMessages.MESSAGES.pathMustBeSpecified();
/*     */     }
/*     */     
/* 199 */     String normalizedPath = URLUtils.normalizeSlashes(path);
/*     */     
/* 201 */     if ("/".equals(normalizedPath)) {
/* 202 */       this.defaultHandler = null;
/* 203 */       return this;
/*     */     } 
/*     */     
/* 206 */     this.paths.remove(normalizedPath);
/*     */     
/* 208 */     buildLengths();
/* 209 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized PathMatcher removeExactPath(String path) {
/* 213 */     if (path == null || path.isEmpty()) {
/* 214 */       throw UndertowMessages.MESSAGES.pathMustBeSpecified();
/*     */     }
/*     */     
/* 217 */     this.exactPathMatches.remove(URLUtils.normalizeSlashes(path));
/*     */     
/* 219 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized PathMatcher clearPaths() {
/* 223 */     this.paths.clear();
/* 224 */     this.exactPathMatches.clear();
/* 225 */     this.lengths = new int[0];
/* 226 */     this.defaultHandler = null;
/* 227 */     return this;
/*     */   }
/*     */   
/*     */   public Map<String, T> getPaths() {
/* 231 */     return this.paths.toMap();
/*     */   }
/*     */   
/*     */   public static final class PathMatch<T> {
/*     */     private final String matched;
/*     */     private final String remaining;
/*     */     private final T value;
/*     */     
/*     */     public PathMatch(String matched, String remaining, T value) {
/* 240 */       this.matched = matched;
/* 241 */       this.remaining = remaining;
/* 242 */       this.value = value;
/*     */     }
/*     */     
/*     */     public String getRemaining() {
/* 246 */       return this.remaining;
/*     */     }
/*     */     
/*     */     public String getMatched() {
/* 250 */       return this.matched;
/*     */     }
/*     */     
/*     */     public T getValue() {
/* 254 */       return this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PathMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */