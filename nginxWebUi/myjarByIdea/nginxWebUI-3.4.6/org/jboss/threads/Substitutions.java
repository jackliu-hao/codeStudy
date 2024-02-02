package org.jboss.threads;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import javax.management.ObjectInstance;

final class Substitutions {
   @TargetClass(EnhancedQueueExecutor.MBeanRegisterAction.class)
   static final class Target_EnhancedQueueExecutor_MBeanRegisterAction {
      @Substitute
      public ObjectInstance run() {
         return null;
      }
   }
}
