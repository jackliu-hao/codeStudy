package oshi.software.common;

import java.util.function.Supplier;
import oshi.software.os.OSThread;
import oshi.util.Memoizer;

public abstract class AbstractOSThread implements OSThread {
   private final Supplier<Double> cumulativeCpuLoad = Memoizer.memoize(this::queryCumulativeCpuLoad, Memoizer.defaultExpiration());
   private final int owningProcessId;

   protected AbstractOSThread(int processId) {
      this.owningProcessId = processId;
   }

   public int getOwningProcessId() {
      return this.owningProcessId;
   }

   public double getThreadCpuLoadCumulative() {
      return (Double)this.cumulativeCpuLoad.get();
   }

   private double queryCumulativeCpuLoad() {
      return (double)this.getUpTime() > 0.0 ? (double)(this.getKernelTime() + this.getUserTime()) / (double)this.getUpTime() : 0.0;
   }

   public double getThreadCpuLoadBetweenTicks(OSThread priorSnapshot) {
      return priorSnapshot != null && this.owningProcessId == priorSnapshot.getOwningProcessId() && this.getThreadId() == priorSnapshot.getThreadId() && this.getUpTime() > priorSnapshot.getUpTime() ? (double)(this.getUserTime() - priorSnapshot.getUserTime() + this.getKernelTime() - priorSnapshot.getKernelTime()) / (double)(this.getUpTime() - priorSnapshot.getUpTime()) : this.getThreadCpuLoadCumulative();
   }

   public String getName() {
      return "";
   }

   public long getStartMemoryAddress() {
      return 0L;
   }

   public long getContextSwitches() {
      return 0L;
   }

   public long getMinorFaults() {
      return 0L;
   }

   public long getMajorFaults() {
      return 0L;
   }

   public boolean updateAttributes() {
      return false;
   }

   public String toString() {
      return "OSThread [threadId=" + this.getThreadId() + ", owningProcessId=" + this.getOwningProcessId() + ", name=" + this.getName() + ", state=" + this.getState() + ", kernelTime=" + this.getKernelTime() + ", userTime=" + this.getUserTime() + ", upTime=" + this.getUpTime() + ", startTime=" + this.getStartTime() + ", startMemoryAddress=0x" + String.format("%x", this.getStartMemoryAddress()) + ", contextSwitches=" + this.getContextSwitches() + ", minorFaults=" + this.getMinorFaults() + ", majorFaults=" + this.getMajorFaults() + "]";
   }
}
