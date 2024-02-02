/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.codehaus.plexus.util.xml.Xpp3Dom;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigurationContainer
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private String inherited;
/*     */   private Object configuration;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public ConfigurationContainer clone() {
/*     */     try {
/*  63 */       ConfigurationContainer copy = (ConfigurationContainer)super.clone();
/*     */       
/*  65 */       if (this.configuration != null)
/*     */       {
/*  67 */         copy.configuration = new Xpp3Dom((Xpp3Dom)this.configuration);
/*     */       }
/*     */       
/*  70 */       if (copy.locations != null)
/*     */       {
/*  72 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  75 */       return copy;
/*     */     }
/*  77 */     catch (Exception ex) {
/*     */       
/*  79 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public Object getConfiguration() {
/*  91 */     return this.configuration;
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
/*     */   public String getInherited() {
/* 106 */     return this.inherited;
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
/* 117 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConfiguration(Object configuration) {
/* 127 */     this.configuration = configuration;
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
/*     */   public void setInherited(String inherited) {
/* 142 */     this.inherited = inherited;
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
/* 153 */     if (location != null) {
/*     */       
/* 155 */       if (this.locations == null)
/*     */       {
/* 157 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 159 */       this.locations.put(key, location);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInherited() {
/* 167 */     return (this.inherited != null) ? Boolean.parseBoolean(this.inherited) : true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInherited(boolean inherited) {
/* 172 */     this.inherited = String.valueOf(inherited);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean inheritanceApplied = true;
/*     */   
/*     */   public void unsetInheritanceApplied() {
/* 179 */     this.inheritanceApplied = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInheritanceApplied() {
/* 184 */     return this.inheritanceApplied;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\ConfigurationContainer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */