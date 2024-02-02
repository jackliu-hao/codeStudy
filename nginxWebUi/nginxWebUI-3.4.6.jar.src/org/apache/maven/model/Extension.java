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
/*     */ public class Extension
/*     */   implements Serializable, Cloneable, InputLocationTracker
/*     */ {
/*     */   private String groupId;
/*     */   private String artifactId;
/*     */   private String version;
/*     */   private Map<Object, InputLocation> locations;
/*     */   
/*     */   public Extension clone() {
/*     */     try {
/*  58 */       Extension copy = (Extension)super.clone();
/*     */       
/*  60 */       if (copy.locations != null)
/*     */       {
/*  62 */         copy.locations = new LinkedHashMap<Object, InputLocation>(copy.locations);
/*     */       }
/*     */       
/*  65 */       return copy;
/*     */     }
/*  67 */     catch (Exception ex) {
/*     */       
/*  69 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*  81 */     return this.artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getGroupId() {
/*  91 */     return this.groupId;
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
/* 102 */     return (this.locations != null) ? this.locations.get(key) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getVersion() {
/* 112 */     return this.version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setArtifactId(String artifactId) {
/* 122 */     this.artifactId = artifactId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setGroupId(String groupId) {
/* 132 */     this.groupId = groupId;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVersion(String version) {
/* 160 */     this.version = version;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 170 */     if (this == o)
/*     */     {
/* 172 */       return true;
/*     */     }
/*     */     
/* 175 */     if (!(o instanceof Extension))
/*     */     {
/* 177 */       return false;
/*     */     }
/*     */     
/* 180 */     Extension e = (Extension)o;
/*     */     
/* 182 */     if (!equal(e.getArtifactId(), getArtifactId()))
/*     */     {
/* 184 */       return false;
/*     */     }
/* 186 */     if (!equal(e.getGroupId(), getGroupId()))
/*     */     {
/* 188 */       return false;
/*     */     }
/* 190 */     if (!equal(e.getVersion(), getVersion()))
/*     */     {
/* 192 */       return false;
/*     */     }
/* 194 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T> boolean equal(T obj1, T obj2) {
/* 199 */     return (obj1 != null) ? obj1.equals(obj2) : ((obj2 == null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 207 */     int result = 17;
/* 208 */     result = 37 * result + ((getArtifactId() != null) ? getArtifactId().hashCode() : 0);
/* 209 */     result = 37 * result + ((getGroupId() != null) ? getGroupId().hashCode() : 0);
/* 210 */     result = 37 * result + ((getVersion() != null) ? getVersion().hashCode() : 0);
/* 211 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Extension.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */