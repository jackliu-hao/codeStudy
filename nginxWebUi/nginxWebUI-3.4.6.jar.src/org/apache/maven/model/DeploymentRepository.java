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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DeploymentRepository
/*    */   extends Repository
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   private boolean uniqueVersion = true;
/*    */   
/*    */   public DeploymentRepository clone() {
/*    */     try {
/* 49 */       DeploymentRepository copy = (DeploymentRepository)super.clone();
/*    */       
/* 51 */       return copy;
/*    */     }
/* 53 */     catch (Exception ex) {
/*    */       
/* 55 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*    */   
/*    */   public boolean isUniqueVersion() {
/* 70 */     return this.uniqueVersion;
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
/*    */   public void setUniqueVersion(boolean uniqueVersion) {
/* 83 */     this.uniqueVersion = uniqueVersion;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\DeploymentRepository.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */