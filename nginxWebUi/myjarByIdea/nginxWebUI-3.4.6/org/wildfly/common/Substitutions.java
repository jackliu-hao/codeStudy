package org.wildfly.common;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.AlwaysInline;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

final class Substitutions {
   @TargetClass(
      className = "org.graalvm.compiler.api.directives.GraalDirectives"
   )
   static final class Target_GraalDirectives {
      @Alias
      public static double LIKELY_PROBABILITY;
      @Alias
      public static double UNLIKELY_PROBABILITY;
      @Alias
      public static double SLOWPATH_PROBABILITY;
      @Alias
      public static double FASTPATH_PROBABILITY;

      @Alias
      public static boolean injectBranchProbability(double probability, boolean condition) {
         assert probability >= 0.0 && probability <= 1.0;

         return condition;
      }
   }

   @TargetClass(Branch.class)
   static final class Target_Branch {
      @AlwaysInline("Straight call to GraalVM")
      @Substitute
      public static boolean veryLikely(boolean expr) {
         return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.FASTPATH_PROBABILITY, expr);
      }

      @AlwaysInline("Straight call to GraalVM")
      @Substitute
      public static boolean veryUnlikely(boolean expr) {
         return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.SLOWPATH_PROBABILITY, expr);
      }

      @AlwaysInline("Straight call to GraalVM")
      @Substitute
      public static boolean likely(boolean expr) {
         return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.LIKELY_PROBABILITY, expr);
      }

      @AlwaysInline("Straight call to GraalVM")
      @Substitute
      public static boolean unlikely(boolean expr) {
         return Substitutions.Target_GraalDirectives.injectBranchProbability(Substitutions.Target_GraalDirectives.UNLIKELY_PROBABILITY, expr);
      }

      @AlwaysInline("Straight call to GraalVM")
      @Substitute
      public static boolean probability(float prob, boolean expr) {
         return Substitutions.Target_GraalDirectives.injectBranchProbability((double)prob, expr);
      }
   }
}
