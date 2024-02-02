/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Notifier
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*  28 */   private String type = "mail";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean sendOnError = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean sendOnFailure = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean sendOnSuccess = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean sendOnWarning = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String address;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Properties configuration;
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
/*     */   public void addConfiguration(String key, String value) {
/*  83 */     getConfiguration().put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Notifier clone() {
/*     */     try {
/*  95 */       Notifier copy = (Notifier)super.clone();
/*     */       
/*  97 */       if (this.configuration != null)
/*     */       {
/*  99 */         copy.configuration = (Properties)this.configuration.clone();
/*     */       }
/*     */       
/* 102 */       if (copy.locations != null)
/*     */       {
/* 104 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/* 107 */       return copy;
/*     */     }
/* 109 */     catch (Exception ex) {
/*     */       
/* 111 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public String getAddress() {
/* 124 */     return this.address;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties getConfiguration() {
/* 134 */     if (this.configuration == null)
/*     */     {
/* 136 */       this.configuration = new Properties();
/*     */     }
/*     */     
/* 139 */     return this.configuration;
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
/* 150 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getType() {
/* 160 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSendOnError() {
/* 170 */     return this.sendOnError;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSendOnFailure() {
/* 180 */     return this.sendOnFailure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSendOnSuccess() {
/* 190 */     return this.sendOnSuccess;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSendOnWarning() {
/* 200 */     return this.sendOnWarning;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAddress(String address) {
/* 211 */     this.address = address;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConfiguration(Properties configuration) {
/* 222 */     this.configuration = configuration;
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
/* 233 */     if (location != null) {
/*     */       
/* 235 */       if (this.locations == null)
/*     */       {
/* 237 */         this.locations = new LinkedHashMap<Object, InputLocation>();
/*     */       }
/* 239 */       this.locations.put(key, location);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSendOnError(boolean sendOnError) {
/* 250 */     this.sendOnError = sendOnError;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSendOnFailure(boolean sendOnFailure) {
/* 260 */     this.sendOnFailure = sendOnFailure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSendOnSuccess(boolean sendOnSuccess) {
/* 270 */     this.sendOnSuccess = sendOnSuccess;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSendOnWarning(boolean sendOnWarning) {
/* 280 */     this.sendOnWarning = sendOnWarning;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setType(String type) {
/* 290 */     this.type = type;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Notifier.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */