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
/*    */ public class PluginManagement
/*    */   extends PluginContainer
/*    */   implements Serializable, Cloneable
/*    */ {
/*    */   public PluginManagement clone() {
/*    */     try {
/* 36 */       PluginManagement copy = (PluginManagement)super.clone();
/*    */       
/* 38 */       return copy;
/*    */     }
/* 40 */     catch (Exception ex) {
/*    */       
/* 42 */       throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()")).initCause(ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\maven\model\PluginManagement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */