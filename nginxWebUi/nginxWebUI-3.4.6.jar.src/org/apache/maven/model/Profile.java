/*     */ package org.apache.maven.model;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Profile
/*     */   extends ModelBase
/*     */   implements Serializable, Cloneable
/*     */ {
/*  34 */   private String id = "default";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Activation activation;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BuildBase build;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SOURCE_POM = "pom";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SOURCE_SETTINGS = "settings.xml";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Profile clone() {
/*     */     try {
/*  62 */       Profile copy = (Profile)super.clone();
/*     */       
/*  64 */       if (this.activation != null)
/*     */       {
/*  66 */         copy.activation = this.activation.clone();
/*     */       }
/*     */       
/*  69 */       if (this.build != null)
/*     */       {
/*  71 */         copy.build = this.build.clone();
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
/*     */   
/*     */   public Activation getActivation() {
/*  92 */     return this.activation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BuildBase getBuild() {
/* 102 */     return this.build;
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
/*     */   public String getId() {
/* 115 */     return this.id;
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
/*     */   public void setActivation(Activation activation) {
/* 127 */     this.activation = activation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBuild(BuildBase build) {
/* 137 */     this.build = build;
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
/*     */   public void setId(String id) {
/* 150 */     this.id = id;
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
/* 161 */   private String source = "pom";
/*     */ 
/*     */   
/*     */   public void setSource(String source) {
/* 165 */     this.source = source;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSource() {
/* 170 */     return this.source;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 178 */     return "Profile {id: " + getId() + ", source: " + getSource() + "}";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Profile.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */