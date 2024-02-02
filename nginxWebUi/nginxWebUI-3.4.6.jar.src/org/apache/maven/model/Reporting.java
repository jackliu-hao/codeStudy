/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Reporting
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private String excludeDefaults;
/*     */   private String outputDirectory;
/*     */   private List<ReportPlugin> plugins;
/*     */   private Map<Object, InputLocation> locations;
/*     */   Map<String, ReportPlugin> reportPluginMap;
/*     */   
/*     */   public void addPlugin(ReportPlugin reportPlugin) {
/*  74 */     getPlugins().add(reportPlugin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Reporting clone() {
/*     */     try {
/*  86 */       Reporting copy = (Reporting)super.clone();
/*     */       
/*  88 */       if (this.plugins != null) {
/*     */         
/*  90 */         copy.plugins = new ArrayList<ReportPlugin>();
/*  91 */         for (ReportPlugin item : this.plugins)
/*     */         {
/*  93 */           copy.plugins.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/*  97 */       if (copy.locations != null)
/*     */       {
/*  99 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/* 102 */       return copy;
/*     */     }
/* 104 */     catch (Exception ex) {
/*     */       
/* 106 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExcludeDefaults() {
/* 125 */     return this.excludeDefaults;
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
/* 136 */     return (this.locations != null) ? this.locations.get(key) : null;
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
/*     */   public String getOutputDirectory() {
/* 149 */     return this.outputDirectory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<ReportPlugin> getPlugins() {
/* 159 */     if (this.plugins == null)
/*     */     {
/* 161 */       this.plugins = new ArrayList<ReportPlugin>();
/*     */     }
/*     */     
/* 164 */     return this.plugins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removePlugin(ReportPlugin reportPlugin) {
/* 174 */     getPlugins().remove(reportPlugin);
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
/*     */   
/*     */   public void setExcludeDefaults(String excludeDefaults) {
/* 191 */     this.excludeDefaults = excludeDefaults;
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
/* 202 */     if (location != null) {
/*     */       
/* 204 */       if (this.locations == null)
/*     */       {
/* 206 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 208 */       this.locations.put(key, location);
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
/*     */   public void setOutputDirectory(String outputDirectory) {
/* 222 */     this.outputDirectory = outputDirectory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPlugins(List<ReportPlugin> plugins) {
/* 232 */     this.plugins = plugins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExcludeDefaults() {
/* 239 */     return (this.excludeDefaults != null) ? Boolean.parseBoolean(this.excludeDefaults) : false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExcludeDefaults(boolean excludeDefaults) {
/* 244 */     this.excludeDefaults = String.valueOf(excludeDefaults);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void flushReportPluginMap() {
/* 254 */     this.reportPluginMap = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Map<String, ReportPlugin> getReportPluginsAsMap() {
/* 263 */     if (this.reportPluginMap == null) {
/*     */       
/* 265 */       this.reportPluginMap = new LinkedHashMap<String, ReportPlugin>();
/* 266 */       if (getPlugins() != null)
/*     */       {
/* 268 */         for (Iterator<ReportPlugin> it = getPlugins().iterator(); it.hasNext(); ) {
/*     */           
/* 270 */           ReportPlugin reportPlugin = it.next();
/* 271 */           this.reportPluginMap.put(reportPlugin.getKey(), reportPlugin);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 276 */     return this.reportPluginMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Reporting.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */