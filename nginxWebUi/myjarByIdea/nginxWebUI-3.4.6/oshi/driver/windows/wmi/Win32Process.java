package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Process {
   private static final String WIN32_PROCESS = "Win32_Process";

   private Win32Process() {
   }

   public static WbemcliUtil.WmiResult<CommandLineProperty> queryCommandLines(Set<Integer> pidsToQuery) {
      StringBuilder sb = new StringBuilder("Win32_Process");
      if (pidsToQuery != null) {
         boolean first = true;

         Integer pid;
         for(Iterator var3 = pidsToQuery.iterator(); var3.hasNext(); sb.append(pid)) {
            pid = (Integer)var3.next();
            if (first) {
               sb.append(" WHERE ProcessID=");
               first = false;
            } else {
               sb.append(" OR ProcessID=");
            }
         }
      }

      WbemcliUtil.WmiQuery<CommandLineProperty> commandLineQuery = new WbemcliUtil.WmiQuery(sb.toString(), CommandLineProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(commandLineQuery);
   }

   public static WbemcliUtil.WmiResult<ProcessXPProperty> queryProcesses(Collection<Integer> pids) {
      StringBuilder sb = new StringBuilder("Win32_Process");
      if (pids != null) {
         boolean first = true;

         Integer pid;
         for(Iterator var3 = pids.iterator(); var3.hasNext(); sb.append(pid)) {
            pid = (Integer)var3.next();
            if (first) {
               sb.append(" WHERE ProcessID=");
               first = false;
            } else {
               sb.append(" OR ProcessID=");
            }
         }
      }

      WbemcliUtil.WmiQuery<ProcessXPProperty> processQueryXP = new WbemcliUtil.WmiQuery(sb.toString(), ProcessXPProperty.class);
      return WmiQueryHandler.createInstance().queryWMI(processQueryXP);
   }

   public static enum CommandLineProperty {
      PROCESSID,
      COMMANDLINE;
   }

   public static enum ProcessXPProperty {
      PROCESSID,
      NAME,
      KERNELMODETIME,
      USERMODETIME,
      THREADCOUNT,
      PAGEFILEUSAGE,
      HANDLECOUNT,
      EXECUTABLEPATH;
   }
}
