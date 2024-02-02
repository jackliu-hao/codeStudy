package org.jboss.threads.management;

public interface StandardThreadPoolMXBean {
   float getGrowthResistance();

   void setGrowthResistance(float var1);

   boolean isGrowthResistanceSupported();

   int getCorePoolSize();

   void setCorePoolSize(int var1);

   boolean isCorePoolSizeSupported();

   boolean prestartCoreThread();

   int prestartAllCoreThreads();

   boolean isCoreThreadPrestartSupported();

   int getMaximumPoolSize();

   void setMaximumPoolSize(int var1);

   int getPoolSize();

   int getLargestPoolSize();

   int getActiveCount();

   boolean isAllowCoreThreadTimeOut();

   void setAllowCoreThreadTimeOut(boolean var1);

   long getKeepAliveTimeSeconds();

   void setKeepAliveTimeSeconds(long var1);

   int getMaximumQueueSize();

   void setMaximumQueueSize(int var1);

   int getQueueSize();

   int getLargestQueueSize();

   boolean isQueueBounded();

   boolean isQueueSizeModifiable();

   boolean isShutdown();

   boolean isTerminating();

   boolean isTerminated();

   long getSubmittedTaskCount();

   long getRejectedTaskCount();

   long getCompletedTaskCount();

   default long getSpinMissCount() {
      return 0L;
   }
}
