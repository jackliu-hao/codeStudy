/*    */ package org.wildfly.common.lock;
/*    */ 
/*    */ import com.oracle.svm.core.annotate.Alias;
/*    */ import com.oracle.svm.core.annotate.Substitute;
/*    */ import com.oracle.svm.core.annotate.TargetClass;
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
/*    */   @TargetClass(JDKSpecific.class)
/*    */   static final class Target_JDKSpecific
/*    */   {
/*    */     @Substitute
/*    */     static void onSpinWait() {
/* 30 */       Substitutions.Target_PauseNode.pause();
/*    */     }
/*    */   }
/*    */   
/*    */   @TargetClass(className = "org.graalvm.compiler.nodes.PauseNode")
/*    */   static final class Target_PauseNode {
/*    */     @Alias
/*    */     public static native void pause();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\lock\Substitutions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */