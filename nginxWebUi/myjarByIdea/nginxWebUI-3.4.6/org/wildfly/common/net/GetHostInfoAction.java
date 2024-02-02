package org.wildfly.common.net;

import java.net.UnknownHostException;
import java.security.PrivilegedAction;
import java.util.regex.Pattern;

final class GetHostInfoAction implements PrivilegedAction<String[]> {
   public String[] run() {
      String qualifiedHostName = System.getProperty("jboss.qualified.host.name");
      String providedHostName = System.getProperty("jboss.host.name");
      String providedNodeName = System.getProperty("jboss.node.name");
      if (qualifiedHostName == null) {
         qualifiedHostName = providedHostName;
         if (providedHostName == null) {
            qualifiedHostName = System.getenv("HOSTNAME");
         }

         if (qualifiedHostName == null) {
            qualifiedHostName = System.getenv("COMPUTERNAME");
         }

         if (qualifiedHostName == null) {
            try {
               qualifiedHostName = HostName.getLocalHost().getHostName();
            } catch (UnknownHostException var5) {
               qualifiedHostName = null;
            }
         }

         if (qualifiedHostName != null && Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$|:").matcher(qualifiedHostName).find()) {
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
