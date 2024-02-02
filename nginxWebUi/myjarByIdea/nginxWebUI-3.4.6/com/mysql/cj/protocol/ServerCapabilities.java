package com.mysql.cj.protocol;

import com.mysql.cj.ServerVersion;

public interface ServerCapabilities {
   int getCapabilityFlags();

   void setCapabilityFlags(int var1);

   ServerVersion getServerVersion();

   long getThreadId();

   void setThreadId(long var1);

   boolean serverSupportsFracSecs();

   int getServerDefaultCollationIndex();
}
