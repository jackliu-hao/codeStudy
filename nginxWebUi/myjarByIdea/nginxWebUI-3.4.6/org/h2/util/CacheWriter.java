package org.h2.util;

import org.h2.message.Trace;

public interface CacheWriter {
   void writeBack(CacheObject var1);

   void flushLog();

   Trace getTrace();
}
