/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ public class ReportPlugin
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*  32 */   private String groupId = "org.apache.maven.plugins";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String artifactId;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String version;
/*     */ 
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
/*     */   private Object configuration;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<ReportSet> reportSets;
/*     */ 
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
/*     */   
/*     */   public void addReportSet(ReportSet reportSet) {
/*  86 */     getReportSets().add(reportSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ReportPlugin clone() {
/*     */     try {
/*  98 */       ReportPlugin copy = (ReportPlugin)super.clone();
/*     */       
/* 100 */       if (this.configuration != null)
/*     */       {
/* 102 */         copy.configuration = new Xpp3Dom((Xpp3Dom)this.configuration);
/*     */       }
/*     */       
/* 105 */       if (this.reportSets != null) {
/*     */         
/* 107 */         copy.reportSets = new ArrayList<ReportSet>();
/* 108 */         for (ReportSet item : this.reportSets)
/*     */         {
/* 110 */           copy.reportSets.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 114 */       if (copy.locations != null)
/*     */       {
/* 116 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/* 119 */       return copy;
/*     */     }
/* 121 */     catch (Exception ex) {
/*     */       
/* 123 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public String getArtifactId() {
/* 136 */     return this.artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getConfiguration() {
/* 146 */     return this.configuration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getGroupId() {
/* 156 */     return this.groupId;
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
/*     */   public String getInherited() {
/* 172 */     return this.inherited;
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
/* 183 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<ReportSet> getReportSets() {
/* 193 */     if (this.reportSets == null)
/*     */     {
/* 195 */       this.reportSets = new ArrayList<ReportSet>();
/*     */     }
/*     */     
/* 198 */     return this.reportSets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getVersion() {
/* 208 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeReportSet(ReportSet reportSet) {
/* 218 */     getReportSets().remove(reportSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setArtifactId(String artifactId) {
/* 229 */     this.artifactId = artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConfiguration(Object configuration) {
/* 239 */     this.configuration = configuration;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGroupId(String groupId) {
/* 249 */     this.groupId = groupId;
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
/*     */   public void setInherited(String inherited) {
/* 265 */     this.inherited = inherited;
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
/* 276 */     if (location != null) {
/*     */       
/* 278 */       if (this.locations == null)
/*     */       {
/* 280 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 282 */       this.locations.put(key, location);
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
/*     */   public void setReportSets(List<ReportSet> reportSets) {
/* 296 */     this.reportSets = reportSets;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVersion(String version) {
/* 306 */     this.version = version;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 311 */   private Map<String, ReportSet> reportSetMap = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushReportSetMap() {
/* 318 */     this.reportSetMap = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, ReportSet> getReportSetsAsMap() {
/* 327 */     if (this.reportSetMap == null) {
/*     */       
/* 329 */       this.reportSetMap = new LinkedHashMap<String, ReportSet>();
/* 330 */       if (getReportSets() != null)
/*     */       {
/* 332 */         for (Iterator<ReportSet> i = getReportSets().iterator(); i.hasNext(); ) {
/*     */           
/* 334 */           ReportSet reportSet = i.next();
/* 335 */           this.reportSetMap.put(reportSet.getId(), reportSet);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 340 */     return this.reportSetMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getKey() {
/* 348 */     return constructKey(this.groupId, this.artifactId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String constructKey(String groupId, String artifactId) {
/* 358 */     return groupId + ":" + artifactId;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInherited() {
/* 363 */     return (this.inherited != null) ? Boolean.parseBoolean(this.inherited) : true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setInherited(boolean inherited) {
/* 368 */     this.inherited = String.valueOf(inherited);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean inheritanceApplied = true;
/*     */   
/*     */   public void unsetInheritanceApplied() {
/* 375 */     this.inheritanceApplied = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInheritanceApplied() {
/* 380 */     return this.inheritanceApplied;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\ReportPlugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */