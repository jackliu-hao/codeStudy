/*    */ package org.jboss.threads;
/*    */ 
/*    */ import com.oracle.svm.core.annotate.Substitute;
/*    */ import com.oracle.svm.core.annotate.TargetClass;
/*    */ import javax.management.ObjectInstance;
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
/*    */ final class Substitutions
/*    */ {
/*    */   @TargetClass(EnhancedQueueExecutor.MBeanRegisterAction.class)
/*    */   static final class Target_EnhancedQueueExecutor_MBeanRegisterAction
/*    */   {
/*    */     @Substitute
/*    */     public ObjectInstance run() {
/* 32 */       return null;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\Substitutions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */