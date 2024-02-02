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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Build
/*     */   extends BuildBase
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private String sourceDirectory;
/*     */   private String scriptSourceDirectory;
/*     */   private String testSourceDirectory;
/*     */   private String outputDirectory;
/*     */   private String testOutputDirectory;
/*     */   private List<Extension> extensions;
/*     */   
/*     */   public void addExtension(Extension extension) {
/*  88 */     getExtensions().add(extension);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Build clone() {
/*     */     try {
/* 100 */       Build copy = (Build)super.clone();
/*     */       
/* 102 */       if (this.extensions != null) {
/*     */         
/* 104 */         copy.extensions = new ArrayList<Extension>();
/* 105 */         for (Extension item : this.extensions)
/*     */         {
/* 107 */           copy.extensions.add(item.clone());
/*     */         }
/*     */       } 
/*     */       
/* 111 */       return copy;
/*     */     }
/* 113 */     catch (Exception ex) {
/*     */       
/* 115 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */   public List<Extension> getExtensions() {
/* 127 */     if (this.extensions == null)
/*     */     {
/* 129 */       this.extensions = new ArrayList<Extension>();
/*     */     }
/*     */     
/* 132 */     return this.extensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOutputDirectory() {
/* 143 */     return this.outputDirectory;
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
/*     */   public String getScriptSourceDirectory() {
/* 159 */     return this.scriptSourceDirectory;
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
/*     */   public String getSourceDirectory() {
/* 174 */     return this.sourceDirectory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTestOutputDirectory() {
/* 184 */     return this.testOutputDirectory;
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
/*     */   public String getTestSourceDirectory() {
/* 199 */     return this.testSourceDirectory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeExtension(Extension extension) {
/* 209 */     getExtensions().remove(extension);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExtensions(List<Extension> extensions) {
/* 219 */     this.extensions = extensions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOutputDirectory(String outputDirectory) {
/* 230 */     this.outputDirectory = outputDirectory;
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
/*     */   public void setScriptSourceDirectory(String scriptSourceDirectory) {
/* 246 */     this.scriptSourceDirectory = scriptSourceDirectory;
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
/*     */   public void setSourceDirectory(String sourceDirectory) {
/* 261 */     this.sourceDirectory = sourceDirectory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTestOutputDirectory(String testOutputDirectory) {
/* 271 */     this.testOutputDirectory = testOutputDirectory;
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
/*     */   public void setTestSourceDirectory(String testSourceDirectory) {
/* 286 */     this.testSourceDirectory = testSourceDirectory;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Build.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */