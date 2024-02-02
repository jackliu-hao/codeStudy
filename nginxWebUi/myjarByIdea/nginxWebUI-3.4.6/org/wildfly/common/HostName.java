package org.wildfly.common;

/** @deprecated */
public final class HostName {
   /** @deprecated */
   public static String getHostName() {
      return org.wildfly.common.net.HostName.getHostName();
   }

   /** @deprecated */
   public static String getQualifiedHostName() {
      return org.wildfly.common.net.HostName.getQualifiedHostName();
   }
}
