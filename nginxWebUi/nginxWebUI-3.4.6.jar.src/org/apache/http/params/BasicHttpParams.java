/*     */ package org.apache.http.params;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ public class BasicHttpParams
/*     */   extends AbstractHttpParams
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private static final long serialVersionUID = -7086398485908701455L;
/*  57 */   private final Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getParameter(String name) {
/*  65 */     return this.parameters.get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams setParameter(String name, Object value) {
/*  70 */     if (name == null) {
/*  71 */       return this;
/*     */     }
/*  73 */     if (value != null) {
/*  74 */       this.parameters.put(name, value);
/*     */     } else {
/*  76 */       this.parameters.remove(name);
/*     */     } 
/*  78 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeParameter(String name) {
/*  84 */     if (this.parameters.containsKey(name)) {
/*  85 */       this.parameters.remove(name);
/*  86 */       return true;
/*     */     } 
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setParameters(String[] names, Object value) {
/*  98 */     for (String name : names) {
/*  99 */       setParameter(name, value);
/*     */     }
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
/*     */   public boolean isParameterSet(String name) {
/* 115 */     return (getParameter(name) != null);
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
/*     */   public boolean isParameterSetLocally(String name) {
/* 129 */     return (this.parameters.get(name) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 136 */     this.parameters.clear();
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
/*     */   public HttpParams copy() {
/*     */     try {
/* 151 */       return (HttpParams)clone();
/* 152 */     } catch (CloneNotSupportedException ex) {
/* 153 */       throw new UnsupportedOperationException("Cloning not supported");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 163 */     BasicHttpParams clone = (BasicHttpParams)super.clone();
/* 164 */     copyParams(clone);
/* 165 */     return clone;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void copyParams(HttpParams target) {
/* 176 */     for (Map.Entry<String, Object> me : this.parameters.entrySet()) {
/* 177 */       target.setParameter(me.getKey(), me.getValue());
/*     */     }
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
/*     */   public Set<String> getNames() {
/* 192 */     return new HashSet<String>(this.parameters.keySet());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 197 */     return "[parameters=" + this.parameters + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\BasicHttpParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */