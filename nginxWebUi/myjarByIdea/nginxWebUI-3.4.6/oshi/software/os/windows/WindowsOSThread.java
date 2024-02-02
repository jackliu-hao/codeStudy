package oshi.software.os.windows;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import oshi.driver.windows.registry.ThreadPerformanceData;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;

public class WindowsOSThread extends AbstractOSThread {
   private final int threadId;
   private String name;
   private OSProcess.State state;
   private long startMemoryAddress;
   private long contextSwitches;
   private long kernelTime;
   private long userTime;
   private long startTime;
   private long upTime;
   private int priority;

   public WindowsOSThread(int pid, int tid, String procName, ThreadPerformanceData.PerfCounterBlock pcb) {
      super(pid);
      this.threadId = tid;
      this.updateAttributes(procName, pcb);
   }

   public int getThreadId() {
      return this.threadId;
   }

   public String getName() {
      return this.name;
   }

   public OSProcess.State getState() {
      return this.state;
   }

   public long getStartMemoryAddress() {
      return this.startMemoryAddress;
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

   public long getStartTime() {
      return this.startTime;
   }

   public long getUpTime() {
      return this.upTime;
   }

   public int getPriority() {
      return this.priority;
   }

   public boolean updateAttributes() {
      Set<Integer> pids = Collections.singleton(this.getOwningProcessId());
      Map<Integer, ThreadPerformanceData.PerfCounterBlock> threads = ThreadPerformanceData.buildThreadMapFromRegistry(pids);
      if (threads == null) {
         threads = ThreadPerformanceData.buildThreadMapFromPerfCounters(pids);
      }

      return this.updateAttributes(this.name.split("/")[0], (ThreadPerformanceData.PerfCounterBlock)threads.get(this.getThreadId()));
   }

   private boolean updateAttributes(String procName, ThreadPerformanceData.PerfCounterBlock pcb) {
      if (pcb == null) {
         this.state = OSProcess.State.INVALID;
         return false;
      } else {
         if (!pcb.getName().contains("/") && !procName.isEmpty()) {
            this.name = procName + "/" + pcb.getName();
         } else {
            this.name = pcb.getName();
         }

         switch (pcb.getThreadState()) {
            case 0:
               this.state = OSProcess.State.NEW;
               break;
            case 1:
            case 6:
               this.state = OSProcess.State.WAITING;
               break;
            case 2:
            case 3:
               this.state = OSProcess.State.RUNNING;
               break;
            case 4:
               this.state = OSProcess.State.STOPPED;
               break;
            case 5:
               this.state = OSProcess.State.SLEEPING;
               break;
            case 7:
            default:
               this.state = OSProcess.State.OTHER;
         }

         this.startMemoryAddress = pcb.getStartAddress();
         this.contextSwitches = (long)pcb.getContextSwitches();
         this.kernelTime = pcb.getKernelTime();
         this.userTime = pcb.getUserTime();
         this.startTime = pcb.getStartTime();
         this.upTime = System.currentTimeMillis() - pcb.getStartTime();
         this.priority = pcb.getPriority();
         return true;
      }
   }
}
