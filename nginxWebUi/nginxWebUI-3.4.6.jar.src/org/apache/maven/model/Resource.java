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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Resource
/*     */   extends FileSet
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   private String targetPath;
/*     */   private String filtering;
/*     */   private String mergeId;
/*     */   
/*     */   public Resource clone() {
/*     */     try {
/*  97 */       Resource copy = (Resource)super.clone();
/*     */       
/*  99 */       return copy;
/*     */     }
/* 101 */     catch (Exception ex) {
/*     */       
/* 103 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*     */ 
/*     */   
/*     */   public String getFiltering() {
/* 124 */     return this.filtering;
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
/*     */   public String getMergeId() {
/* 142 */     return this.mergeId;
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
/*     */ 
/*     */   
/*     */   public String getTargetPath() {
/* 164 */     return this.targetPath;
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
/*     */   public void setFiltering(String filtering) {
/* 183 */     this.filtering = filtering;
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
/*     */   public void setMergeId(String mergeId) {
/* 201 */     this.mergeId = mergeId;
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
/*     */ 
/*     */   
/*     */   public void setTargetPath(String targetPath) {
/* 223 */     this.targetPath = targetPath;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 228 */   private static int mergeIdCounter = 0;
/*     */ 
/*     */   
/*     */   public void initMergeId() {
/* 232 */     if (getMergeId() == null)
/*     */     {
/* 234 */       setMergeId("resource-" + mergeIdCounter++);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFiltering() {
/* 240 */     return (this.filtering != null) ? Boolean.parseBoolean(this.filtering) : false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFiltering(boolean filtering) {
/* 245 */     this.filtering = String.valueOf(filtering);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 253 */     return "Resource {targetPath: " + getTargetPath() + ", filtering: " + isFiltering() + ", " + super.toString() + "}";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\Resource.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */