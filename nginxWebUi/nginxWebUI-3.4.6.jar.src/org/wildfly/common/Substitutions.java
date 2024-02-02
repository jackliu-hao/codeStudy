/*    */ package org.wildfly.common;
/*    */ 
/*    */ import com.oracle.svm.core.annotate.Alias;
/*    */ import com.oracle.svm.core.annotate.AlwaysInline;
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
/*    */ 
/*    */ 
/*    */ final class Substitutions
/*    */ {
/*    */   @TargetClass(Branch.class)
/*    */   static final class Target_Branch
/*    */   {
/*    */     @AlwaysInline("Straight call to GraalVM")
/*    */     @Substitute
/*    */     public static boolean veryLikely(boolean expr) {
/* 34 */       return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.FASTPATH_PROBABILITY, expr);
/*    */     }
/*    */     
/*    */     @AlwaysInline("Straight call to GraalVM")
/*    */     @Substitute
/*    */     public static boolean veryUnlikely(boolean expr) {
/* 40 */       return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.SLOWPATH_PROBABILITY, expr);
/*    */     }
/*    */     
/*    */     @AlwaysInline("Straight call to GraalVM")
/*    */     @Substitute
/*    */     public static boolean likely(boolean expr) {
/* 46 */       return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.LIKELY_PROBABILITY, expr);
/*    */     }
/*    */     
/*    */     @AlwaysInline("Straight call to GraalVM")
/*    */     @Substitute
/*    */     public static boolean unlikely(boolean expr) {
/* 52 */       return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.UNLIKELY_PROBABILITY, expr);
/*    */     }
/*    */     
/*    */     @AlwaysInline("Straight call to GraalVM")
/*    */     @Substitute
/*    */     public static boolean probability(float prob, boolean expr) {
/* 58 */       return Substitutions.Target_GraalDirectives.injectBranchProbability(prob, expr);
/*    */     }
/*    */   }
/*    */   
/*    */   @TargetClass(className = "org.graalvm.compiler.api.directives.GraalDirectives")
/*    */   static final class Target_GraalDirectives
/*    */   {
/*    */     @Alias
/*    */     public static double LIKELY_PROBABILITY;
/*    */     @Alias
/*    */     public static double UNLIKELY_PROBABILITY;
/*    */     @Alias
/*    */     public static double SLOWPATH_PROBABILITY;
/*    */     @Alias
/*    */     public static double FASTPATH_PROBABILITY;
/*    */     
/*    */     @Alias
/*    */     public static boolean injectBranchProbability(double probability, boolean condition) {
/* 76 */       assert probability >= 0.0D && probability <= 1.0D;
/* 77 */       return condition;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\Substitutions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */