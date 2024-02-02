package org.xnio.management;

import java.util.Set;

public interface XnioWorkerMXBean {
   String getProviderName();

   String getName();

   boolean isShutdownRequested();

   int getCoreWorkerPoolSize();

   int getMaxWorkerPoolSize();

   int getWorkerPoolSize();

   int getBusyWorkerThreadCount();

   int getIoThreadCount();

   int getWorkerQueueSize();

   Set<XnioServerMXBean> getServerMXBeans();
}
