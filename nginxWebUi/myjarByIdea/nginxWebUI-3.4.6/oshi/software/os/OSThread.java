package oshi.software.os;

public interface OSThread {
   int getThreadId();

   String getName();

   OSProcess.State getState();

   double getThreadCpuLoadCumulative();

   double getThreadCpuLoadBetweenTicks(OSThread var1);

   int getOwningProcessId();

   long getStartMemoryAddress();

   long getContextSwitches();

   long getMinorFaults();

   long getMajorFaults();

   long getKernelTime();

   long getUserTime();

   long getUpTime();

   long getStartTime();

   int getPriority();

   boolean updateAttributes();
}
