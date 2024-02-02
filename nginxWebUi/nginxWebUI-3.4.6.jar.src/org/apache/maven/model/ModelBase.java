/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ModelBase
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private List<String> modules;
/*     */   private DistributionManagement distributionManagement;
/*     */   private Properties properties;
/*     */   private DependencyManagement dependencyManagement;
/*     */   private List<Dependency> dependencies;
/*     */   private List<Repository> repositories;
/*     */   private List<Repository> pluginRepositories;
/*     */   private Object reports;
/*     */   private Reporting reporting;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public void addDependency(Dependency dependency) {
/* 117 */     getDependencies().add(dependency);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addModule(String string) {
/* 127 */     getModules().add(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addPluginRepository(Repository repository) {
/* 137 */     getPluginRepositories().add(repository);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProperty(String key, String value) {
/* 148 */     getProperties().put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRepository(Repository repository) {
/* 158 */     getRepositories().add(repository);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ModelBase clone() {
/*     */     try {
/* 170 */       ModelBase copy = (ModelBase)super.clone();
/*     */       
/* 172 */       if (this.modules != null) {
/*     */         
/* 174 */         copy.modules = new ArrayList<String>();
/* 175 */         copy.modules.addAll(this.modules);
/*     */       } 
/*     */       
/* 178 */       if (this.distributionManagement != null)
/*     */       {
/* 180 */         copy.distributionManagement = this.distributionManagement.clone();
/*     */       }
/*     */       
/* 183 */       if (this.properties != null)
/*     */       {
/* 185 */         copy.properties = (Properties)this.properties.clone();
/*     */       }
/*     */       
/* 188 */       if (this.dependencyManagement != null)
/*     */       {
/* 190 */         copy.dependencyManagement = this.dependencyManagement.clone();
/*     */       }
/*     */       
/* 193 */       if (this.dependencies != null) {
/*     */         
/* 195 */         copy.dependencies = new ArrayList<Dependency>();
/* 196 */         for (Dependency item : this.dependencies)
/*     */         {
/* 198 */           copy.dependencies.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 202 */       if (this.repositories != null) {
/*     */         
/* 204 */         copy.repositories = new ArrayList<Repository>();
/* 205 */         for (Repository item : this.repositories)
/*     */         {
/* 207 */           copy.repositories.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 211 */       if (this.pluginRepositories != null) {
/*     */         
/* 213 */         copy.pluginRepositories = new ArrayList<Repository>();
/* 214 */         for (Repository item : this.pluginRepositories)
/*     */         {
/* 216 */           copy.pluginRepositories.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 220 */       if (this.reports != null)
/*     */       {
/* 222 */         copy.reports = new Xpp3Dom((Xpp3Dom)this.reports);
/*     */       }
/*     */       
/* 225 */       if (this.reporting != null)
/*     */       {
/* 227 */         copy.reporting = this.reporting.clone();
/*     */       }
/*     */       
/* 230 */       if (copy.locations != null)
/*     */       {
/* 232 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/* 235 */       return copy;
/*     */     }
/* 237 */     catch (Exception ex) {
/*     */       
/* 239 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public List<Dependency> getDependencies() {
/* 251 */     if (this.dependencies == null)
/*     */     {
/* 253 */       this.dependencies = new ArrayList<Dependency>();
/*     */     }
/*     */     
/* 256 */     return this.dependencies;
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
/*     */   public DependencyManagement getDependencyManagement() {
/* 274 */     return this.dependencyManagement;
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
/*     */   public DistributionManagement getDistributionManagement() {
/* 287 */     return this.distributionManagement;
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
/* 298 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getModules() {
/* 308 */     if (this.modules == null)
/*     */     {
/* 310 */       this.modules = new ArrayList<String>();
/*     */     }
/*     */     
/* 313 */     return this.modules;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Repository> getPluginRepositories() {
/* 323 */     if (this.pluginRepositories == null)
/*     */     {
/* 325 */       this.pluginRepositories = new ArrayList<Repository>();
/*     */     }
/*     */     
/* 328 */     return this.pluginRepositories;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties getProperties() {
/* 338 */     if (this.properties == null)
/*     */     {
/* 340 */       this.properties = new Properties();
/*     */     }
/*     */     
/* 343 */     return this.properties;
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
/*     */   public Reporting getReporting() {
/* 360 */     return this.reporting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getReports() {
/* 370 */     return this.reports;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Repository> getRepositories() {
/* 380 */     if (this.repositories == null)
/*     */     {
/* 382 */       this.repositories = new ArrayList<Repository>();
/*     */     }
/*     */     
/* 385 */     return this.repositories;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeDependency(Dependency dependency) {
/* 395 */     getDependencies().remove(dependency);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeModule(String string) {
/* 405 */     getModules().remove(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removePluginRepository(Repository repository) {
/* 415 */     getPluginRepositories().remove(repository);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeRepository(Repository repository) {
/* 425 */     getRepositories().remove(repository);
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
/*     */ 
/*     */   
/*     */   public void setDependencies(List<Dependency> dependencies) {
/* 445 */     this.dependencies = dependencies;
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
/*     */   public void setDependencyManagement(DependencyManagement dependencyManagement) {
/* 463 */     this.dependencyManagement = dependencyManagement;
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
/*     */   public void setDistributionManagement(DistributionManagement distributionManagement) {
/* 476 */     this.distributionManagement = distributionManagement;
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
/* 487 */     if (location != null) {
/*     */       
/* 489 */       if (this.locations == null)
/*     */       {
/* 491 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 493 */       this.locations.put(key, location);
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
/*     */   public void setModules(List<String> modules) {
/* 507 */     this.modules = modules;
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
/*     */   public void setPluginRepositories(List<Repository> pluginRepositories) {
/* 519 */     this.pluginRepositories = pluginRepositories;
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
/*     */   public void setProperties(Properties properties) {
/* 533 */     this.properties = properties;
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
/*     */   public void setReporting(Reporting reporting) {
/* 550 */     this.reporting = reporting;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReports(Object reports) {
/* 560 */     this.reports = reports;
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
/*     */   public void setRepositories(List<Repository> repositories) {
/* 572 */     this.repositories = repositories;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\ModelBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */