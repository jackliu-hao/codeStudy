/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
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
/*     */ public class DependencyManagement
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private List<Dependency> dependencies;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public void addDependency(Dependency dependency) {
/*  48 */     getDependencies().add(dependency);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DependencyManagement clone() {
/*     */     try {
/*  60 */       DependencyManagement copy = (DependencyManagement)super.clone();
/*     */       
/*  62 */       if (this.dependencies != null) {
/*     */         
/*  64 */         copy.dependencies = new ArrayList<Dependency>();
/*  65 */         for (Dependency item : this.dependencies)
/*     */         {
/*  67 */           copy.dependencies.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/*  71 */       if (copy.locations != null)
/*     */       {
/*  73 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  76 */       return copy;
/*     */     }
/*  78 */     catch (Exception ex) {
/*     */       
/*  80 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*  92 */     if (this.dependencies == null)
/*     */     {
/*  94 */       this.dependencies = new ArrayList<Dependency>();
/*     */     }
/*     */     
/*  97 */     return this.dependencies;
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
/* 108 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeDependency(Dependency dependency) {
/* 118 */     getDependencies().remove(dependency);
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
/*     */   public void setDependencies(List<Dependency> dependencies) {
/* 132 */     this.dependencies = dependencies;
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
/* 143 */     if (location != null) {
/*     */       
/* 145 */       if (this.locations == null)
/*     */       {
/* 147 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 149 */       this.locations.put(key, location);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\DependencyManagement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */