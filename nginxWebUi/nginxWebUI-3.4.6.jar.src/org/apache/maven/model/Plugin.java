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
/*     */ public class Plugin
/*     */   extends ConfigurationContainer
/*     */   implements Serializable, Cloneable
/*     */ {
/*  33 */   private String groupId = "org.apache.maven.plugins";
/*     */ 
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
/*     */   
/*     */   private String version;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String extensions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<PluginExecution> executions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<Dependency> dependencies;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object goals;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDependency(Dependency dependency) {
/*  93 */     getDependencies().add(dependency);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addExecution(PluginExecution pluginExecution) {
/* 103 */     getExecutions().add(pluginExecution);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Plugin clone() {
/*     */     try {
/* 115 */       Plugin copy = (Plugin)super.clone();
/*     */       
/* 117 */       if (this.executions != null) {
/*     */         
/* 119 */         copy.executions = new ArrayList<PluginExecution>();
/* 120 */         for (PluginExecution item : this.executions)
/*     */         {
/* 122 */           copy.executions.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 126 */       if (this.dependencies != null) {
/*     */         
/* 128 */         copy.dependencies = new ArrayList<Dependency>();
/* 129 */         for (Dependency item : this.dependencies)
/*     */         {
/* 131 */           copy.dependencies.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 135 */       if (this.goals != null)
/*     */       {
/* 137 */         copy.goals = new Xpp3Dom((Xpp3Dom)this.goals);
/*     */       }
/*     */       
/* 140 */       return copy;
/*     */     }
/* 142 */     catch (Exception ex) {
/*     */       
/* 144 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/* 156 */     return this.artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Dependency> getDependencies() {
/* 166 */     if (this.dependencies == null)
/*     */     {
/* 168 */       this.dependencies = new ArrayList<Dependency>();
/*     */     }
/*     */     
/* 171 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<PluginExecution> getExecutions() {
/* 181 */     if (this.executions == null)
/*     */     {
/* 183 */       this.executions = new ArrayList<PluginExecution>();
/*     */     }
/*     */     
/* 186 */     return this.executions;
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
/*     */   public String getExtensions() {
/* 203 */     return this.extensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getGoals() {
/* 213 */     return this.goals;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getGroupId() {
/* 223 */     return this.groupId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getVersion() {
/* 234 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeDependency(Dependency dependency) {
/* 244 */     getDependencies().remove(dependency);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeExecution(PluginExecution pluginExecution) {
/* 254 */     getExecutions().remove(pluginExecution);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setArtifactId(String artifactId) {
/* 264 */     this.artifactId = artifactId;
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
/*     */   public void setDependencies(List<Dependency> dependencies) {
/* 276 */     this.dependencies = dependencies;
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
/*     */   public void setExecutions(List<PluginExecution> executions) {
/* 289 */     this.executions = executions;
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
/*     */   public void setExtensions(String extensions) {
/* 306 */     this.extensions = extensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGoals(Object goals) {
/* 316 */     this.goals = goals;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGroupId(String groupId) {
/* 326 */     this.groupId = groupId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVersion(String version) {
/* 337 */     this.version = version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isExtensions() {
/* 344 */     return (this.extensions != null) ? Boolean.parseBoolean(this.extensions) : false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExtensions(boolean extensions) {
/* 349 */     this.extensions = String.valueOf(extensions);
/*     */   }
/*     */   
/* 352 */   private Map<String, PluginExecution> executionMap = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushExecutionMap() {
/* 359 */     this.executionMap = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, PluginExecution> getExecutionsAsMap() {
/* 368 */     if (this.executionMap == null) {
/*     */       
/* 370 */       this.executionMap = new LinkedHashMap<String, PluginExecution>();
/* 371 */       if (getExecutions() != null)
/*     */       {
/* 373 */         for (Iterator<PluginExecution> i = getExecutions().iterator(); i.hasNext(); ) {
/*     */           
/* 375 */           PluginExecution exec = i.next();
/*     */           
/* 377 */           if (this.executionMap.containsKey(exec.getId()))
/*     */           {
/* 379 */             throw new IllegalStateException("You cannot have two plugin executions with the same (or missing) <id/> elements.\nOffending execution\n\nId: '" + exec.getId() + "'\nPlugin:'" + getKey() + "'\n\n");
/*     */           }
/*     */           
/* 382 */           this.executionMap.put(exec.getId(), exec);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 387 */     return this.executionMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getId() {
/* 397 */     StringBuilder id = new StringBuilder(128);
/*     */     
/* 399 */     id.append((getGroupId() == null) ? "[unknown-group-id]" : getGroupId());
/* 400 */     id.append(":");
/* 401 */     id.append((getArtifactId() == null) ? "[unknown-artifact-id]" : getArtifactId());
/* 402 */     id.append(":");
/* 403 */     id.append((getVersion() == null) ? "[unknown-version]" : getVersion());
/*     */     
/* 405 */     return id.toString();
/*     */   }
/*     */ 
/*     */   
/* 409 */   private String key = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getKey() {
/* 415 */     if (this.key == null)
/*     */     {
/* 417 */       this.key = constructKey(this.groupId, this.artifactId);
/*     */     }
/* 419 */     return this.key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String constructKey(String groupId, String artifactId) {
/* 429 */     return groupId + ":" + artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 437 */     if (other instanceof Plugin) {
/*     */       
/* 439 */       Plugin otherPlugin = (Plugin)other;
/*     */       
/* 441 */       return getKey().equals(otherPlugin.getKey());
/*     */     } 
/*     */     
/* 444 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 452 */     return getKey().hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 460 */     return "Plugin [" + getKey() + "]";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Plugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */