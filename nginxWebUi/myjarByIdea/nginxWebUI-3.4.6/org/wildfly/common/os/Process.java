package org.wildfly.common.os;

import java.security.AccessController;

public final class Process {
   private static final long processId;
   private static final String processName;

   private Process() {
   }

   public static String getProcessName() {
      return processName;
   }

   public static long getProcessId() {
      return processId;
   }

   static {
      Object[] array = (Object[])AccessController.doPrivileged(new GetProcessInfoAction());
      processId = (Long)array[0];
      processName = (String)array[1];
   }
}
