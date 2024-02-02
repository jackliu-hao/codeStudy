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
/*     */ public class Exclusion
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private String artifactId;
/*     */   private String groupId;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public Exclusion clone() {
/*     */     try {
/*  59 */       Exclusion copy = (Exclusion)super.clone();
/*     */       
/*  61 */       if (copy.locations != null)
/*     */       {
/*  63 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  66 */       return copy;
/*     */     }
/*  68 */     catch (Exception ex) {
/*     */       
/*  70 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public String getArtifactId() {
/*  82 */     return this.artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getGroupId() {
/*  92 */     return this.groupId;
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
/* 103 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setArtifactId(String artifactId) {
/* 113 */     this.artifactId = artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGroupId(String groupId) {
/* 123 */     this.groupId = groupId;
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
/* 134 */     if (location != null) {
/*     */       
/* 136 */       if (this.locations == null)
/*     */       {
/* 138 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 140 */       this.locations.put(key, location);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Exclusion.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */