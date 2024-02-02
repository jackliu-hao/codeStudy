package org.h2.jmx;

public interface DatabaseInfoMBean {
   boolean isExclusive();

   boolean isReadOnly();

   String getMode();

   long getFileWriteCount();

   long getFileReadCount();

   long getFileSize();

   int getCacheSizeMax();

   void setCacheSizeMax(int var1);

   int getCacheSize();

   String getVersion();

   int getTraceLevel();

   void setTraceLevel(int var1);

   String listSettings();

   String listSessions();
}
