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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CiManagement
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private String system;
/*     */   private String url;
/*     */   private List<Notifier> notifiers;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public void addNotifier(Notifier notifier) {
/*  69 */     getNotifiers().add(notifier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CiManagement clone() {
/*     */     try {
/*  81 */       CiManagement copy = (CiManagement)super.clone();
/*     */       
/*  83 */       if (this.notifiers != null) {
/*     */         
/*  85 */         copy.notifiers = new ArrayList<Notifier>();
/*  86 */         for (Notifier item : this.notifiers)
/*     */         {
/*  88 */           copy.notifiers.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/*  92 */       if (copy.locations != null)
/*     */       {
/*  94 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  97 */       return copy;
/*     */     }
/*  99 */     catch (Exception ex) {
/*     */       
/* 101 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/* 114 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Notifier> getNotifiers() {
/* 124 */     if (this.notifiers == null)
/*     */     {
/* 126 */       this.notifiers = new ArrayList<Notifier>();
/*     */     }
/*     */     
/* 129 */     return this.notifiers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSystem() {
/* 140 */     return this.system;
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
/*     */   public String getUrl() {
/* 152 */     return this.url;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeNotifier(Notifier notifier) {
/* 162 */     getNotifiers().remove(notifier);
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
/* 173 */     if (location != null) {
/*     */       
/* 175 */       if (this.locations == null)
/*     */       {
/* 177 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 179 */       this.locations.put(key, location);
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
/*     */   public void setNotifiers(List<Notifier> notifiers) {
/* 193 */     this.notifiers = notifiers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSystem(String system) {
/* 204 */     this.system = system;
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
/*     */   public void setUrl(String url) {
/* 216 */     this.url = url;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\CiManagement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */