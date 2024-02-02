package oshi.software.os.unix.aix;

import java.util.Iterator;
import java.util.List;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

public class AixOSThread extends AbstractOSThread {
   private int threadId;
   private OSProcess.State state;
   private long contextSwitches;
   private long kernelTime;
   private long userTime;
   private long startTime;
   private long upTime;
   private int priority;

   public AixOSThread(int pid, String[] split) {
      super(pid);
      this.state = OSProcess.State.INVALID;
      this.updateAttributes(split);
   }

   public int getThreadId() {
      return this.threadId;
   }

   public OSProcess.State getState() {
      return this.state;
   }

   public long getContextSwitches() {
      return this.contextSwitches;
   }

   public long getKernelTime() {
      return this.kernelTime;
   }

   public long getUserTime() {
      return this.userTime;
   }

   public long getUpTime() {
      return this.upTime;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public int getPriority() {
      return this.priority;
   }

   public boolean updateAttributes() {
      List<String> threadListInfoPs = ExecutingCommand.runNative("ps -m -o THREAD -p " + this.getOwningProcessId());
      if (threadListInfoPs.size() > 2) {
         threadListInfoPs.remove(0);
         threadListInfoPs.remove(0);
         Iterator var2 = threadListInfoPs.iterator();

         while(var2.hasNext()) {
            String threadInfo = (String)var2.next();
            String[] threadInfoSplit = ParseUtil.whitespaces.split(threadInfo.trim());
            if (threadInfoSplit.length == 13 && threadInfoSplit[3].equals(String.valueOf(this.getThreadId()))) {
               String[] split = new String[]{threadInfoSplit[3], threadInfoSplit[4], threadInfoSplit[6]};
               this.updateAttributes(split);
            }
         }
      }

      this.state = OSProcess.State.INVALID;
      return false;
   }

   private boolean updateAttributes(String[] split) {
      this.threadId = ParseUtil.parseIntOrDefault(split[0], 0);
      this.state = AixOSProcess.getStateFromOutput(split[1].charAt(0));
      this.priority = ParseUtil.parseIntOrDefault(split[2], 0);
      return true;
   }
}
