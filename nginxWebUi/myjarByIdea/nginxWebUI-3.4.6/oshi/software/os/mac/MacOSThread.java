package oshi.software.os.mac;

import oshi.annotation.concurrent.Immutable;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;

@Immutable
public class MacOSThread extends AbstractOSThread {
   private final int threadId;
   private final OSProcess.State state;
   private final long kernelTime;
   private final long userTime;
   private final long startTime;
   private final long upTime;
   private final int priority;

   public MacOSThread(int pid, int threadId, OSProcess.State state, long kernelTime, long userTime, long startTime, long upTime, int priority) {
      super(pid);
      this.threadId = threadId;
      this.state = state;
      this.kernelTime = kernelTime;
      this.userTime = userTime;
      this.startTime = startTime;
      this.upTime = upTime;
      this.priority = priority;
   }

   public int getThreadId() {
      return this.threadId;
   }

   public OSProcess.State getState() {
      return this.state;
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
}
