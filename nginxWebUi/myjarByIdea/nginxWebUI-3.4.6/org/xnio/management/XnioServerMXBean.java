package org.xnio.management;

public interface XnioServerMXBean {
   String getProviderName();

   String getWorkerName();

   String getBindAddress();

   int getConnectionCount();

   int getConnectionLimitHighWater();

   int getConnectionLimitLowWater();
}
