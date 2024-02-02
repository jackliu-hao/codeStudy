package org.wildfly.common.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import org.wildfly.common.Assert;

public final class HostName {
   private static final Object lock = new Object();
   private static volatile String hostName;
   private static volatile String qualifiedHostName;
   private static volatile String nodeName;

   private HostName() {
   }

   static InetAddress getLocalHost() throws UnknownHostException {
      InetAddress addr;
      try {
         addr = InetAddress.getLocalHost();
      } catch (ArrayIndexOutOfBoundsException var2) {
         addr = InetAddress.getByName((String)null);
      }

      return addr;
   }

   public static String getHostName() {
      return hostName;
   }

   public static String getQualifiedHostName() {
      return qualifiedHostName;
   }

   public static String getNodeName() {
      return nodeName;
   }

   public static void setQualifiedHostName(String qualifiedHostName) {
      Assert.checkNotNullParam("qualifiedHostName", qualifiedHostName);
      synchronized(lock) {
         HostName.qualifiedHostName = qualifiedHostName;
         int idx = qualifiedHostName.indexOf(46);
         hostName = idx == -1 ? qualifiedHostName : qualifiedHostName.substring(0, idx);
      }
   }

   public static void setNodeName(String nodeName) {
      Assert.checkNotNullParam("nodeName", nodeName);
      HostName.nodeName = nodeName;
   }

   static {
      String[] names = (String[])AccessController.doPrivileged(new GetHostInfoAction());
      hostName = names[0];
      qualifiedHostName = names[1];
      nodeName = names[2];
   }
}
