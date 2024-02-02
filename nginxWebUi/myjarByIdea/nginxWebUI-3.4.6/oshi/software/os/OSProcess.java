package oshi.software.os;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface OSProcess {
   String getName();

   String getPath();

   String getCommandLine();

   String getCurrentWorkingDirectory();

   String getUser();

   String getUserID();

   String getGroup();

   String getGroupID();

   State getState();

   int getProcessID();

   int getParentProcessID();

   int getThreadCount();

   int getPriority();

   long getVirtualSize();

   long getResidentSetSize();

   long getKernelTime();

   long getUserTime();

   long getUpTime();

   long getStartTime();

   long getBytesRead();

   long getBytesWritten();

   long getOpenFiles();

   double getProcessCpuLoadCumulative();

   double getProcessCpuLoadBetweenTicks(OSProcess var1);

   int getBitness();

   long getAffinityMask();

   boolean updateAttributes();

   List<OSThread> getThreadDetails();

   long getMinorFaults();

   long getMajorFaults();

   public static enum State {
      NEW,
      RUNNING,
      SLEEPING,
      WAITING,
      ZOMBIE,
      STOPPED,
      OTHER,
      INVALID;
   }
}
