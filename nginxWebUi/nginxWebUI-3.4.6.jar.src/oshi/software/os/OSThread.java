package oshi.software.os;

public interface OSThread {
  int getThreadId();
  
  String getName();
  
  OSProcess.State getState();
  
  double getThreadCpuLoadCumulative();
  
  double getThreadCpuLoadBetweenTicks(OSThread paramOSThread);
  
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\OSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */