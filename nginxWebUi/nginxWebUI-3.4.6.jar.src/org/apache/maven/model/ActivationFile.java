/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ActivationFile
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private String missing;
/*     */   private String exists;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public ActivationFile clone() {
/*     */     try {
/*  60 */       ActivationFile copy = (ActivationFile)super.clone();
/*     */       
/*  62 */       if (copy.locations != null)
/*     */       {
/*  64 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  67 */       return copy;
/*     */     }
/*  69 */     catch (Exception ex) {
/*     */       
/*  71 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public String getExists() {
/*  84 */     return this.exists;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputLocation getLocation(Object key) {
/*  95 */     return (this.locations != null) ? this.locations.get(key) : null;
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
/*     */   public String getMissing() {
/* 107 */     return this.missing;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExists(String exists) {
/* 118 */     this.exists = exists;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLocation(Object key, InputLocation location) {
/* 129 */     if (location != null) {
/*     */       
/* 131 */       if (this.locations == null)
/*     */       {
/* 133 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 135 */       this.locations.put(key, location);
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
/*     */   public void setMissing(String missing) {
/* 148 */     this.missing = missing;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\ActivationFile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */