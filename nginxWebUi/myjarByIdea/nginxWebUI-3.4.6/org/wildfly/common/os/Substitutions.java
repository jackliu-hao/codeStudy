package org.wildfly.common.os;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import java.io.File;
import org.graalvm.nativeimage.ProcessProperties;

final class Substitutions {
   static final class ProcessUtils {
      static String getProcessName() {
         String name = System.getProperty("jboss.process.name");
         if (name == null) {
            String exeName = ProcessProperties.getExecutableName();
            if (!exeName.isEmpty()) {
               int idx = exeName.lastIndexOf(File.separatorChar);
               name = idx == -1 ? exeName : (idx == exeName.length() - 1 ? null : exeName.substring(idx + 1));
            }
         }

         if (name == null) {
            name = "<unknown>";
         }

         return name;
      }
   }

   @TargetClass(
      className = "org.wildfly.common.os.GetProcessInfoAction"
   )
   static final class Target_org_wildfly_common_os_GetProcessInfoAction {
      @Substitute
      public Object[] run() {
         return new Object[]{ProcessProperties.getProcessID() & 4294967295L, Substitutions.ProcessUtils.getProcessName()};
      }
   }
}
