/*     */ package org.apache.http.params;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ @Deprecated
/*     */ public final class DefaultedHttpParams
/*     */   extends AbstractHttpParams
/*     */ {
/*     */   private final HttpParams local;
/*     */   private final HttpParams defaults;
/*     */   
/*     */   public DefaultedHttpParams(HttpParams local, HttpParams defaults) {
/*  60 */     this.local = (HttpParams)Args.notNull(local, "Local HTTP parameters");
/*  61 */     this.defaults = defaults;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpParams copy() {
/*  69 */     HttpParams clone = this.local.copy();
/*  70 */     return new DefaultedHttpParams(clone, this.defaults);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getParameter(String name) {
/*  80 */     Object obj = this.local.getParameter(name);
/*  81 */     if (obj == null && this.defaults != null) {
/*  82 */       obj = this.defaults.getParameter(name);
/*     */     }
/*  84 */     return obj;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeParameter(String name) {
/*  93 */     return this.local.removeParameter(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpParams setParameter(String name, Object value) {
/* 102 */     return this.local.setParameter(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpParams getDefaults() {
/* 110 */     return this.defaults;
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
/*     */   public Set<String> getNames() {
/* 126 */     Set<String> combined = new HashSet<String>(getNames(this.defaults));
/* 127 */     combined.addAll(getNames(this.local));
/* 128 */     return combined;
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
/*     */   public Set<String> getDefaultNames() {
/* 142 */     return new HashSet<String>(getNames(this.defaults));
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
/*     */   public Set<String> getLocalNames() {
/* 156 */     return new HashSet<String>(getNames(this.local));
/*     */   }
/*     */ 
/*     */   
/*     */   private Set<String> getNames(HttpParams params) {
/* 161 */     if (params instanceof HttpParamsNames) {
/* 162 */       return ((HttpParamsNames)params).getNames();
/*     */     }
/* 164 */     throw new UnsupportedOperationException("HttpParams instance does not implement HttpParamsNames");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\DefaultedHttpParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */