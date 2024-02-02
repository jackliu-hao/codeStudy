package org.wildfly.common.net;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.UnsignedWord;
import org.graalvm.word.WordFactory;

final class Substitutions {
   static final class ProcessSubstitutions {
      static final int SIZE = 512;
   }

   static final class NativeInfoDirectives implements CContext.Directives {
      public List<String> getHeaderFiles() {
         return Collections.singletonList("<unistd.h>");
      }
   }

   @CContext(NativeInfoDirectives.class)
   @Platforms({Platform.DARWIN.class, Platform.LINUX.class})
   static final class NativeInfo {
      @CFunction
      static native int gethostname(CCharPointer var0, UnsignedWord var1);
   }

   @TargetClass(
      className = "org.wildfly.common.net.GetHostInfoAction"
   )
   @Platforms({Platform.DARWIN.class, Platform.LINUX.class})
   static final class Target_org_wildfly_common_net_GetHostInfoAction {
      @Substitute
      public String[] run() {
         String qualifiedHostName = System.getProperty("jboss.qualified.host.name");
         String providedHostName = System.getProperty("jboss.host.name");
         String providedNodeName = System.getProperty("jboss.node.name");
         if (qualifiedHostName == null) {
            qualifiedHostName = providedHostName;
            if (providedHostName == null) {
               CCharPointer nameBuf = (CCharPointer)StackValue.get(512);
               int res = Substitutions.NativeInfo.gethostname(nameBuf, WordFactory.unsigned(512));
               if (res != -1 && res > 0) {
                  if (res == 512) {
                     nameBuf.write(511, (byte)0);
                  }

                  qualifiedHostName = CTypeConversion.toJavaString(nameBuf);
               }
            }

            if (qualifiedHostName == null) {
               qualifiedHostName = System.getenv("HOSTNAME");
            }

            if (qualifiedHostName == null) {
               qualifiedHostName = System.getenv("COMPUTERNAME");
            }

            if (qualifiedHostName == null) {
               try {
                  qualifiedHostName = HostName.getLocalHost().getHostName();
               } catch (UnknownHostException var6) {
                  qualifiedHostName = null;
               }
            }

            if (qualifiedHostName != null && (Inet.isInet4Address(qualifiedHostName) || Inet.isInet6Address(qualifiedHostName))) {
               qualifiedHostName = null;
            }

            if (qualifiedHostName == null) {
               qualifiedHostName = "unknown-host.unknown-domain";
            } else {
               qualifiedHostName = qualifiedHostName.trim().toLowerCase();
            }
         }

         if (providedHostName == null) {
            int idx = qualifiedHostName.indexOf(46);
            providedHostName = idx == -1 ? qualifiedHostName : qualifiedHostName.substring(0, idx);
         }

         if (providedNodeName == null) {
            providedNodeName = providedHostName;
         }

         return new String[]{providedHostName, qualifiedHostName, providedNodeName};
      }
   }
}
