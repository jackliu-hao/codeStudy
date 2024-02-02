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
/*    */ 
/*    */ public class PluginConfiguration
/*    */   extends PluginContainer
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   private PluginManagement pluginManagement;
/*    */   
/*    */   public PluginConfiguration clone() {
/*    */     try {
/* 50 */       PluginConfiguration copy = (PluginConfiguration)super.clone();
/*    */       
/* 52 */       if (this.pluginManagement != null)
/*    */       {
/* 54 */         copy.pluginManagement = this.pluginManagement.clone();
/*    */       }
/*    */       
/* 57 */       return copy;
/*    */     }
/* 59 */     catch (Exception ex) {
/*    */       
/* 61 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
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
/*    */ 
/*    */ 
/*    */   
/*    */   public PluginManagement getPluginManagement() {
/* 79 */     return this.pluginManagement;
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
/*    */ 
/*    */   
/*    */   public void setPluginManagement(PluginManagement pluginManagement) {
/* 95 */     this.pluginManagement = pluginManagement;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\PluginConfiguration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */