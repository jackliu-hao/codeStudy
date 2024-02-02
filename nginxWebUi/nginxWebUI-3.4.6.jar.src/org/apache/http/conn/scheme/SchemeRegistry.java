/*     */ package org.apache.http.conn.scheme;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.HttpHost;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public final class SchemeRegistry
/*     */ {
/*  59 */   private final ConcurrentHashMap<String, Scheme> registeredSchemes = new ConcurrentHashMap<String, Scheme>();
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
/*     */   public final Scheme getScheme(String name) {
/*  73 */     Scheme found = get(name);
/*  74 */     if (found == null) {
/*  75 */       throw new IllegalStateException("Scheme '" + name + "' not registered.");
/*     */     }
/*     */     
/*  78 */     return found;
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
/*     */   public final Scheme getScheme(HttpHost host) {
/*  93 */     Args.notNull(host, "Host");
/*  94 */     return getScheme(host.getSchemeName());
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
/*     */   public final Scheme get(String name) {
/* 106 */     Args.notNull(name, "Scheme name");
/*     */ 
/*     */     
/* 109 */     Scheme found = this.registeredSchemes.get(name);
/* 110 */     return found;
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
/*     */   public final Scheme register(Scheme sch) {
/* 124 */     Args.notNull(sch, "Scheme");
/* 125 */     Scheme old = this.registeredSchemes.put(sch.getName(), sch);
/* 126 */     return old;
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
/*     */   public final Scheme unregister(String name) {
/* 138 */     Args.notNull(name, "Scheme name");
/*     */ 
/*     */     
/* 141 */     Scheme gone = this.registeredSchemes.remove(name);
/* 142 */     return gone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final List<String> getSchemeNames() {
/* 151 */     return new ArrayList<String>(this.registeredSchemes.keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setItems(Map<String, Scheme> map) {
/* 161 */     if (map == null) {
/*     */       return;
/*     */     }
/* 164 */     this.registeredSchemes.clear();
/* 165 */     this.registeredSchemes.putAll(map);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\SchemeRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */