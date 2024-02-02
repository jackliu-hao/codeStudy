/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.LinkedHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Activation
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private boolean activeByDefault = false;
/*     */   private String jdk;
/*     */   private ActivationOS os;
/*     */   private ActivationProperty property;
/*     */   private ActivationFile file;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public Activation clone() {
/*     */     try {
/*  88 */       Activation copy = (Activation)super.clone();
/*     */       
/*  90 */       if (this.os != null)
/*     */       {
/*  92 */         copy.os = this.os.clone();
/*     */       }
/*     */       
/*  95 */       if (this.property != null)
/*     */       {
/*  97 */         copy.property = this.property.clone();
/*     */       }
/*     */       
/* 100 */       if (this.file != null)
/*     */       {
/* 102 */         copy.file = this.file.clone();
/*     */       }
/*     */       
/* 105 */       if (copy.locations != null)
/*     */       {
/* 107 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/* 110 */       return copy;
/*     */     }
/* 112 */     catch (Exception ex) {
/*     */       
/* 114 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public ActivationFile getFile() {
/* 127 */     return this.file;
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
/*     */   public String getJdk() {
/* 142 */     return this.jdk;
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
/* 153 */     return (this.locations != null) ? this.locations.get(key) : null;
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
/*     */   public ActivationOS getOs() {
/* 165 */     return this.os;
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
/*     */   public ActivationProperty getProperty() {
/* 177 */     return this.property;
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
/*     */   public boolean isActiveByDefault() {
/* 191 */     return this.activeByDefault;
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
/*     */   public void setActiveByDefault(boolean activeByDefault) {
/* 205 */     this.activeByDefault = activeByDefault;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFile(ActivationFile file) {
/* 216 */     this.file = file;
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
/*     */   public void setJdk(String jdk) {
/* 231 */     this.jdk = jdk;
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
/* 242 */     if (location != null) {
/*     */       
/* 244 */       if (this.locations == null)
/*     */       {
/* 246 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 248 */       this.locations.put(key, location);
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
/*     */   public void setOs(ActivationOS os) {
/* 261 */     this.os = os;
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
/*     */   public void setProperty(ActivationProperty property) {
/* 273 */     this.property = property;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Activation.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */