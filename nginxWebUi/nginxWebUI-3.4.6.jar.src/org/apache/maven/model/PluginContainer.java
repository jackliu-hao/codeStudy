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
/*     */ public class PluginContainer
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private List<Plugin> plugins;
/*     */   private Map<Object, InputLocation> locations;
/*     */   Map<String, Plugin> pluginMap;
/*     */   
/*     */   public void addPlugin(Plugin plugin) {
/*  46 */     getPlugins().add(plugin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PluginContainer clone() {
/*     */     try {
/*  58 */       PluginContainer copy = (PluginContainer)super.clone();
/*     */       
/*  60 */       if (this.plugins != null) {
/*     */         
/*  62 */         copy.plugins = new ArrayList<Plugin>();
/*  63 */         for (Plugin item : this.plugins)
/*     */         {
/*  65 */           copy.plugins.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/*  69 */       if (copy.locations != null)
/*     */       {
/*  71 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  74 */       return copy;
/*     */     }
/*  76 */     catch (Exception ex) {
/*     */       
/*  78 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public InputLocation getLocation(Object key) {
/*  91 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Plugin> getPlugins() {
/* 101 */     if (this.plugins == null)
/*     */     {
/* 103 */       this.plugins = new ArrayList<Plugin>();
/*     */     }
/*     */     
/* 106 */     return this.plugins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removePlugin(Plugin plugin) {
/* 116 */     getPlugins().remove(plugin);
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
/* 127 */     if (location != null) {
/*     */       
/* 129 */       if (this.locations == null)
/*     */       {
/* 131 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 133 */       this.locations.put(key, location);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPlugins(List<Plugin> plugins) {
/* 144 */     this.plugins = plugins;
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
/*     */   public synchronized void flushPluginMap() {
/* 156 */     this.pluginMap = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized Map<String, Plugin> getPluginsAsMap() {
/* 165 */     if (this.pluginMap == null) {
/*     */       
/* 167 */       this.pluginMap = new LinkedHashMap<String, Plugin>();
/* 168 */       if (this.plugins != null)
/*     */       {
/* 170 */         for (Iterator<Plugin> it = this.plugins.iterator(); it.hasNext(); ) {
/*     */           
/* 172 */           Plugin plugin = it.next();
/* 173 */           this.pluginMap.put(plugin.getKey(), plugin);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 178 */     return this.pluginMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\PluginContainer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */