/*     */ package org.apache.http.params;
/*     */ 
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public abstract class AbstractHttpParams
/*     */   implements HttpParams, HttpParamsNames
/*     */ {
/*     */   public long getLongParameter(String name, long defaultValue) {
/*  54 */     Object param = getParameter(name);
/*  55 */     if (param == null) {
/*  56 */       return defaultValue;
/*     */     }
/*  58 */     return ((Long)param).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams setLongParameter(String name, long value) {
/*  63 */     setParameter(name, Long.valueOf(value));
/*  64 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIntParameter(String name, int defaultValue) {
/*  69 */     Object param = getParameter(name);
/*  70 */     if (param == null) {
/*  71 */       return defaultValue;
/*     */     }
/*  73 */     return ((Integer)param).intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams setIntParameter(String name, int value) {
/*  78 */     setParameter(name, Integer.valueOf(value));
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public double getDoubleParameter(String name, double defaultValue) {
/*  84 */     Object param = getParameter(name);
/*  85 */     if (param == null) {
/*  86 */       return defaultValue;
/*     */     }
/*  88 */     return ((Double)param).doubleValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams setDoubleParameter(String name, double value) {
/*  93 */     setParameter(name, Double.valueOf(value));
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean getBooleanParameter(String name, boolean defaultValue) {
/*  99 */     Object param = getParameter(name);
/* 100 */     if (param == null) {
/* 101 */       return defaultValue;
/*     */     }
/* 103 */     return ((Boolean)param).booleanValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams setBooleanParameter(String name, boolean value) {
/* 108 */     setParameter(name, value ? Boolean.TRUE : Boolean.FALSE);
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParameterTrue(String name) {
/* 114 */     return getBooleanParameter(name, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParameterFalse(String name) {
/* 119 */     return !getBooleanParameter(name, false);
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
/*     */   public Set<String> getNames() {
/* 132 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\AbstractHttpParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */