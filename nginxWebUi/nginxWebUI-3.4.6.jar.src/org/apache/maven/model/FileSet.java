/*    */ package org.apache.maven.model;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FileSet
/*    */   extends PatternSet
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   private String directory;
/*    */   
/*    */   public FileSet clone() {
/*    */     try {
/* 46 */       FileSet copy = (FileSet)super.clone();
/*    */       
/* 48 */       return copy;
/*    */     }
/* 50 */     catch (Exception ex) {
/*    */       
/* 52 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getDirectory() {
/* 66 */     return this.directory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setDirectory(String directory) {
/* 78 */     this.directory = directory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 88 */     return "FileSet {directory: " + getDirectory() + ", " + super.toString() + "}";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\FileSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */