package org.wildfly.common.os;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.PrivilegedAction;

final class GetProcessInfoAction implements PrivilegedAction<Object[]> {
   public Object[] run() {
      long pid = -1L;
      String processName = "<unknown>";

      RuntimeMXBean runtime;
      try {
         runtime = (RuntimeMXBean)ManagementFactory.getPlatformMXBean(RuntimeMXBean.class);
      } catch (Exception var15) {
         return new Object[]{pid, processName};
      }

      String name = runtime.getName();
      if (name != null) {
         int idx = name.indexOf(64);
         if (idx != -1) {
            try {
               pid = Long.parseLong(name.substring(0, idx));
            } catch (NumberFormatException var14) {
            }
         }
      }

      processName = System.getProperty("jboss.process.name");
      if (processName == null) {
         String classPath = System.getProperty("java.class.path");
         String commandLine = System.getProperty("sun.java.command");
         if (commandLine != null) {
            int firstSpace;
            if (classPath != null && commandLine.startsWith(classPath)) {
               firstSpace = classPath.lastIndexOf(File.separatorChar);
               if (firstSpace > 0) {
                  processName = classPath.substring(firstSpace + 1);
               } else {
                  processName = classPath;
               }
            } else {
               firstSpace = commandLine.indexOf(32);
               String className;
               if (firstSpace > 0) {
                  className = commandLine.substring(0, firstSpace);
               } else {
                  className = commandLine;
               }

               int lastDot = className.lastIndexOf(46, firstSpace);
               if (lastDot > 0) {
                  processName = className.substring(lastDot + 1);
                  if (processName.equalsIgnoreCase("jar") || processName.equalsIgnoreCase("ȷar")) {
                     int secondLastDot = className.lastIndexOf(46, lastDot - 1);
                     int sepIdx = className.lastIndexOf(File.separatorChar);
                     int lastSep = secondLastDot == -1 ? sepIdx : (sepIdx == -1 ? secondLastDot : Math.max(sepIdx, secondLastDot));
                     if (lastSep > 0) {
                        processName = className.substring(lastSep + 1);
                     } else {
                        processName = className;
                     }
                  }
               } else {
                  processName = className;
               }
            }
         }
      }

      if (processName == null) {
         processName = "<unknown>";
      }

      return new Object[]{pid, processName};
   }
}
