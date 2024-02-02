/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BuildBase
/*     */   extends PluginConfiguration
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private String defaultGoal;
/*     */   private List<Resource> resources;
/*     */   private List<Resource> testResources;
/*     */   private String directory;
/*     */   private String finalName;
/*     */   private List<String> filters;
/*     */   
/*     */   public void addFilter(String string) {
/*  83 */     getFilters().add(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addResource(Resource resource) {
/*  93 */     getResources().add(resource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTestResource(Resource resource) {
/* 103 */     getTestResources().add(resource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BuildBase clone() {
/*     */     try {
/* 115 */       BuildBase copy = (BuildBase)super.clone();
/*     */       
/* 117 */       if (this.resources != null) {
/*     */         
/* 119 */         copy.resources = new ArrayList<Resource>();
/* 120 */         for (Resource item : this.resources)
/*     */         {
/* 122 */           copy.resources.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 126 */       if (this.testResources != null) {
/*     */         
/* 128 */         copy.testResources = new ArrayList<Resource>();
/* 129 */         for (Resource item : this.testResources)
/*     */         {
/* 131 */           copy.testResources.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 135 */       if (this.filters != null) {
/*     */         
/* 137 */         copy.filters = new ArrayList<String>();
/* 138 */         copy.filters.addAll(this.filters);
/*     */       } 
/*     */       
/* 141 */       return copy;
/*     */     }
/* 143 */     catch (Exception ex) {
/*     */       
/* 145 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public String getDefaultGoal() {
/* 164 */     return this.defaultGoal;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getDirectory() {
/* 175 */     return this.directory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getFilters() {
/* 185 */     if (this.filters == null)
/*     */     {
/* 187 */       this.filters = new ArrayList<String>();
/*     */     }
/*     */     
/* 190 */     return this.filters;
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
/*     */   public String getFinalName() {
/* 204 */     return this.finalName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Resource> getResources() {
/* 214 */     if (this.resources == null)
/*     */     {
/* 216 */       this.resources = new ArrayList<Resource>();
/*     */     }
/*     */     
/* 219 */     return this.resources;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Resource> getTestResources() {
/* 229 */     if (this.testResources == null)
/*     */     {
/* 231 */       this.testResources = new ArrayList<Resource>();
/*     */     }
/*     */     
/* 234 */     return this.testResources;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeFilter(String string) {
/* 244 */     getFilters().remove(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeResource(Resource resource) {
/* 254 */     getResources().remove(resource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTestResource(Resource resource) {
/* 264 */     getTestResources().remove(resource);
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
/*     */   public void setDefaultGoal(String defaultGoal) {
/* 281 */     this.defaultGoal = defaultGoal;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDirectory(String directory) {
/* 292 */     this.directory = directory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFilters(List<String> filters) {
/* 303 */     this.filters = filters;
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
/*     */   public void setFinalName(String finalName) {
/* 317 */     this.finalName = finalName;
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
/*     */   public void setResources(List<Resource> resources) {
/* 331 */     this.resources = resources;
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
/*     */   public void setTestResources(List<Resource> testResources) {
/* 343 */     this.testResources = testResources;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\BuildBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */