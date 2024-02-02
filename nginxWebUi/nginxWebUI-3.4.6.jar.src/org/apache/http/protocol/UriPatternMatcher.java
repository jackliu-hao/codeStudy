/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
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
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class UriPatternMatcher<T>
/*     */ {
/*  63 */   private final Map<String, T> map = new LinkedHashMap<String, T>();
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
/*     */   public synchronized Set<Map.Entry<String, T>> entrySet() {
/*  75 */     return new HashSet<Map.Entry<String, T>>(this.map.entrySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void register(String pattern, T obj) {
/*  85 */     Args.notNull(pattern, "URI request pattern");
/*  86 */     this.map.put(pattern, obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void unregister(String pattern) {
/*  95 */     if (pattern == null) {
/*     */       return;
/*     */     }
/*  98 */     this.map.remove(pattern);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized void setHandlers(Map<String, T> map) {
/* 106 */     Args.notNull(map, "Map of handlers");
/* 107 */     this.map.clear();
/* 108 */     this.map.putAll(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized void setObjects(Map<String, T> map) {
/* 116 */     Args.notNull(map, "Map of handlers");
/* 117 */     this.map.clear();
/* 118 */     this.map.putAll(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public synchronized Map<String, T> getObjects() {
/* 126 */     return this.map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized T lookup(String path) {
/* 136 */     Args.notNull(path, "Request path");
/*     */     
/* 138 */     T obj = this.map.get(path);
/* 139 */     if (obj == null) {
/*     */       
/* 141 */       String bestMatch = null;
/* 142 */       for (String pattern : this.map.keySet()) {
/* 143 */         if (matchUriRequestPattern(pattern, path))
/*     */         {
/* 145 */           if (bestMatch == null || bestMatch.length() < pattern.length() || (bestMatch.length() == pattern.length() && pattern.endsWith("*"))) {
/*     */ 
/*     */             
/* 148 */             obj = this.map.get(pattern);
/* 149 */             bestMatch = pattern;
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/* 154 */     return obj;
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
/*     */   protected boolean matchUriRequestPattern(String pattern, String path) {
/* 166 */     if (pattern.equals("*")) {
/* 167 */       return true;
/*     */     }
/* 169 */     return ((pattern.endsWith("*") && path.startsWith(pattern.substring(0, pattern.length() - 1))) || (pattern.startsWith("*") && path.endsWith(pattern.substring(1, pattern.length()))));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 176 */     return this.map.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\UriPatternMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */