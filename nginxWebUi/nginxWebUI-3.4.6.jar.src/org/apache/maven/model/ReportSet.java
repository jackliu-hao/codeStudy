/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
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
/*     */ public class ReportSet
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*  31 */   private String id = "default";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object configuration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String inherited;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<String> reports;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<Object, InputLocation> locations;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addReport(String string) {
/*  66 */     getReports().add(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReportSet clone() {
/*     */     try {
/*  78 */       ReportSet copy = (ReportSet)super.clone();
/*     */       
/*  80 */       if (this.configuration != null)
/*     */       {
/*  82 */         copy.configuration = new Xpp3Dom((Xpp3Dom)this.configuration);
/*     */       }
/*     */       
/*  85 */       if (this.reports != null) {
/*     */         
/*  87 */         copy.reports = new ArrayList<String>();
/*  88 */         copy.reports.addAll(this.reports);
/*     */       } 
/*     */       
/*  91 */       if (copy.locations != null)
/*     */       {
/*  93 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  96 */       return copy;
/*     */     }
/*  98 */     catch (Exception ex) {
/*     */       
/* 100 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public Object getConfiguration() {
/* 113 */     return this.configuration;
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
/*     */   public String getId() {
/* 125 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getInherited() {
/* 136 */     return this.inherited;
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
/* 147 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getReports() {
/* 157 */     if (this.reports == null)
/*     */     {
/* 159 */       this.reports = new ArrayList<String>();
/*     */     }
/*     */     
/* 162 */     return this.reports;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeReport(String string) {
/* 172 */     getReports().remove(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConfiguration(Object configuration) {
/* 183 */     this.configuration = configuration;
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
/*     */   public void setId(String id) {
/* 195 */     this.id = id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInherited(String inherited) {
/* 206 */     this.inherited = inherited;
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
/* 217 */     if (location != null) {
/*     */       
/* 219 */       if (this.locations == null)
/*     */       {
/* 221 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 223 */       this.locations.put(key, location);
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
/*     */   public void setReports(List<String> reports) {
/* 235 */     this.reports = reports;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean inheritanceApplied = true;
/*     */ 
/*     */   
/*     */   public void unsetInheritanceApplied() {
/* 244 */     this.inheritanceApplied = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInheritanceApplied() {
/* 249 */     return this.inheritanceApplied;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 255 */     return getId();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\ReportSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */