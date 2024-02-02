/*     */ package org.jboss.threads.management;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface StandardThreadPoolMXBean
/*     */ {
/*     */   float getGrowthResistance();
/*     */   
/*     */   void setGrowthResistance(float paramFloat);
/*     */   
/*     */   boolean isGrowthResistanceSupported();
/*     */   
/*     */   int getCorePoolSize();
/*     */   
/*     */   void setCorePoolSize(int paramInt);
/*     */   
/*     */   boolean isCorePoolSizeSupported();
/*     */   
/*     */   boolean prestartCoreThread();
/*     */   
/*     */   int prestartAllCoreThreads();
/*     */   
/*     */   boolean isCoreThreadPrestartSupported();
/*     */   
/*     */   int getMaximumPoolSize();
/*     */   
/*     */   void setMaximumPoolSize(int paramInt);
/*     */   
/*     */   int getPoolSize();
/*     */   
/*     */   int getLargestPoolSize();
/*     */   
/*     */   int getActiveCount();
/*     */   
/*     */   boolean isAllowCoreThreadTimeOut();
/*     */   
/*     */   void setAllowCoreThreadTimeOut(boolean paramBoolean);
/*     */   
/*     */   long getKeepAliveTimeSeconds();
/*     */   
/*     */   void setKeepAliveTimeSeconds(long paramLong);
/*     */   
/*     */   int getMaximumQueueSize();
/*     */   
/*     */   void setMaximumQueueSize(int paramInt);
/*     */   
/*     */   int getQueueSize();
/*     */   
/*     */   int getLargestQueueSize();
/*     */   
/*     */   boolean isQueueBounded();
/*     */   
/*     */   boolean isQueueSizeModifiable();
/*     */   
/*     */   boolean isShutdown();
/*     */   
/*     */   boolean isTerminating();
/*     */   
/*     */   boolean isTerminated();
/*     */   
/*     */   long getSubmittedTaskCount();
/*     */   
/*     */   long getRejectedTaskCount();
/*     */   
/*     */   long getCompletedTaskCount();
/*     */   
/*     */   default long getSpinMissCount() {
/* 275 */     return 0L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\management\StandardThreadPoolMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */